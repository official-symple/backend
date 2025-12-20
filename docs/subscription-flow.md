# Subscription / IAP Data Flow (Android / Apple)

This repo implements subscription as:
- **Source of truth**: `user_subscription` (ACTIVE + `expires_at` > now)
- **Audit/history**: `iap_transaction` (every verification attempt)
- **Compatibility flag**: `member.subscribe` (used by existing features; auto-synced from source of truth)

> **Important safety rule**
> `member.subscribe == PREMIUM` **does not** mean premium is valid. Premium is valid only when `user_subscription` says it’s active & not expired.

---

## Current Plans

We seed two plans on startup (see `SubscriptionPlanSeeder`):
- **FREE**
- **PREMIUM**

Plan benefit flags are stored as booleans in `subscription_plan`:
- `unlimited_talk`
- `ad_free`
- `daily_item`

---

## Data Model (Entities / Tables)

### `subscription_plan` (`SubscriptionPlan`)
**Purpose**: plan catalog & benefit booleans.
- `plan_code` (PK, string) — matches `Subscribe.name()` (e.g. `FREE`, `PREMIUM`)
- `display_name`
- `unlimited_talk`, `ad_free`, `daily_item` (boolean)

### `user_subscription` (`UserSubscription`)
**Purpose**: per-user current subscription state (1 row per user).
- `id` (PK)
- `member_id` (**UNIQUE**) — guarantees **one subscription row per member**
- `plan_code` — current plan code (currently set to `PREMIUM` when active)
- `status` — `ACTIVE | EXPIRED | CANCELED`
- `platform` — `APPLE | GOOGLE`
- `product_id`
- `store_subscription_id`, `store_transaction_id`
- `started_at`, `expires_at`, `auto_renewing`, `last_verified_at`

**Premium validity check**
- Premium is valid iff: `status == ACTIVE` AND `expires_at > now`

### `iap_transaction` (`IapTransaction`)
**Purpose**: immutable audit log for every verification (success/fail).
- `member_id`
- `platform`, `product_id`
- `store_subscription_id`, `store_transaction_id`
- `verification_status` — `VERIFIED | FAILED`
- `verified_at`
- `request_payload`, `raw_response` (LOB)

---

## “Effective plan” (how the app decides Free vs Premium)

### Recommended usage (most code)
Use:
- `SubscriptionReader.effectivePlan(member)`  
or:
- `SubscriptionService.myPlanAndSyncMemberFlag(member)` (returns plan + benefits + also syncs `member.subscribe`)

### Logic summary
1. Read `user_subscription` for member where `status=ACTIVE`
2. If `expires_at` is in the future → **PREMIUM**
3. Otherwise → **FREE** (even if `member.subscribe` says PREMIUM)
4. Optionally sync `member.subscribe`:
   - active premium → set `member.subscribe = PREMIUM`
   - not active → set `member.subscribe = FREE` (if it was PREMIUM)

---

## APIs

### `GET /api/subscription/me`
Returns:
- effective plan (`FREE` / `PREMIUM`)
- `premiumActive` (boolean)
- `expiresAt` (nullable)
- benefit booleans (`unlimitedTalk`, `adFree`, `dailyItem`)

Also: this endpoint uses `myPlanAndSyncMemberFlag`, so `member.subscribe` is kept consistent with reality.

### `POST /api/subscription/verify`
Body (`VerifySubscriptionRequest`):
- `platform` (`APPLE` or `GOOGLE`)
- `productId`
- Apple: `receiptData`
- Google: `purchaseToken`
- optional: `storeTransactionId`, `storeSubscriptionId`

Flow:
1. Verify with store-specific verifier
2. Insert audit row in `iap_transaction` (always)
3. If valid:
   - upsert the member’s single `user_subscription` row (activate premium + set expires)
   - set `member.subscribe = PREMIUM` (compat flag)
4. If invalid: throw `IAP_VERIFICATION_FAILED`

---

## Verifiers (Abstraction)

Interface:
- `StoreSubscriptionVerifier`
- resolved by `VerifierResolver` using `StorePlatform`

Current status:
- `AppleSubscriptionVerifier` and `GooglePlaySubscriptionVerifier` are **stubs** (shape validation + fake 30-day expiry).
- Replace stub internals with real store verification:
  - Apple: receipt validation / server-to-server (needs `iap.apple.sharedSecret`)
  - Google: Play Developer API (needs `iap.google.serviceAccountJson`, `iap.google.packageName`)

Configuration holder:
- `IapProperties` (`iap.*`)

---

## Lifecycle Scenarios

### Subscribe (first purchase)
- `POST /verify` (valid)
- `user_subscription` created (member_id unique)
- `member.subscribe` set to `PREMIUM`
- `iap_transaction` appended

### Renew (monthly)
- `POST /verify` again (or future: server notifications)
- same `user_subscription` row updated (`expires_at` pushed forward)
- `iap_transaction` appended

### Cancel / Expire
- If `expires_at` passes → effective plan becomes **FREE**
- `GET /me` will auto-sync: `member.subscribe` forced back to `FREE` if it was PREMIUM

### Restore (re-subscribe after cancel)
- `POST /verify` (valid)
- same `user_subscription` row updated back to ACTIVE with new `expires_at`
- `iap_transaction` appended

---

## Why `UserSubscription` is “one row per user”

We keep exactly one row per member for:
- simple and safe reads (no “which record is latest?” ambiguity)
- easy “effective plan” computation

History is handled by `iap_transaction`, not by duplicating subscription rows.


