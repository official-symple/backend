# AI 서버 연동 가이드

## 개요

백엔드 서버와 AI 서버 간의 비동기 처리 방식을 개선하여 리소스 효율성을 높였습니다.

### 기존 방식의 문제점
- AI 서버의 응답을 기다리는 동안 비동기 스레드가 블로킹됨
- 타임아웃 설정이 없어 무한 대기 가능
- AI 처리 시간이 길어질 경우 스레드 풀 고갈

### 개선된 방식
- 백엔드 → AI: 즉시 200 응답 (요청 수신 확인)
- AI 서버가 처리 완료 후 백엔드의 콜백 API 호출
- 비동기 스레드를 AI 대기에 사용하지 않음

---

## 처리 흐름

### 1. 세션 종료 시 (SessionService.update)

```
Client → Backend: POST /api/session/update (LastEmotion 설정)
         ↓
Backend: Session 저장, Heart +2
         ↓
Backend → AI Server: POST /summary (즉시 200 응답)
Backend → AI Server: POST /advice (즉시 200 응답)
         ↓
Backend → Client: 200 OK (SessionResponse)
```

**주의**: Mission은 처음에 요청하지 않습니다. Summary 콜백 수신 후 자동으로 요청됩니다.

### 2. AI 서버 처리 완료 시 (비동기)

```
[Summary 처리]
AI Server: Summary 생성 완료
         ↓
AI Server → Backend: POST /api/ai/callback/summary
Backend: Session.problem, solutions 업데이트
Backend → AI Server: POST /mission (Summary 결과 포함, 즉시 200 응답)
         ↓
[Mission 처리]
AI Server: Mission 생성 완료
         ↓
AI Server → Backend: POST /api/ai/callback/mission
Backend: Session.mission 업데이트
         ↓
[Advice 처리 - 독립적]
AI Server: Advice 생성 완료
         ↓
AI Server → Backend: POST /api/ai/callback/advice
Backend: Session.advice 업데이트
```

**핵심**: Mission은 Summary 콜백 수신 시 백엔드가 자동으로 요청합니다. AI 서버는 단순히 주어진 데이터로 AI 결과만 생성하면 됩니다.

### 3. 클라이언트 조회 시

```
Client → Backend: GET /api/session/{sessionId}/report
Backend: Session.problem이 null이면 ErrorCode.NOT_EXIST_REPORT
         → AI 처리 미완료 상태

Client → Backend: GET /api/session/{sessionId}/mission
Backend: Session.mission이 null이면 ErrorCode.NOT_EXIST_MISSION
         → AI 처리 미완료 상태

Client → Backend: GET /api/session/{sessionId}/advice
Backend: Session.advice가 null이면 ErrorCode.NOT_EXIST_ADVICE
         → AI 처리 미완료 상태
```

---

## AI 서버 구현 요구사항

### 1. Summary 엔드포인트

**호출 시점:** 세션 종료 시 (최초)

**요청받는 엔드포인트:** `POST ${fastApi.summary.endpoint}`

**Request Body:**
```json
{
  "sessionId": 123,  // ← 백엔드에서 추가로 전달 (기존 필드 유지)
  "language": "kor",
  "messages": [...],
  "emotionCause": "...",
  "emotion": ["..."],
  "persona": "...",
  "formal": true
}
```

**Response:**
```json
HTTP 200 OK
{"status": "accepted"}
```

**처리 완료 후 콜백:**
```
POST http://backend-server/api/ai/callback/summary
Content-Type: application/json

{
  "sessionId": 123,
  "problem": "내가 맡은 모든 일들을 잘하고 싶은데 오점이 생겼다는 생각에 너무 화가 나",
  "solutions": [
    "'오점이 생겼다' 대신, '이번엔 실수가 있었지만 배울 기회가 생겼어'라고 생각해보면 어떨까?",
    "모든 일을 완벽히 해내야 한다는 목표보다는, 하루에 하나씩 작은 성취를 이루는 데 집중해보자."
  ]
}
```

**중요:** Summary 콜백 전송 후, 백엔드가 자동으로 Mission을 요청합니다!

---

### 2. Mission 엔드포인트

**호출 시점:** Summary 콜백 전송 직후 (백엔드가 자동으로 호출)

**요청받는 엔드포인트:** `POST ${fastApi.mission.endpoint}`

**Request Body:**
```json
{
  "sessionId": 123,  // ← 백엔드에서 추가로 전달
  "persona": "...",
  "language": "kor",
  "formal": true,
  "summary": "내가 맡은 모든 일들을 잘하고 싶은데 오점이 생겼다는 생각에 너무 화가 나",  // ← Summary 결과가 포함됨
  "nickname": "닉네임",
  "emotion_cause": "...",
  "emotion": ["..."]
}
```

**중요:**
- Mission은 **Summary 콜백을 백엔드에 보낸 후** 자동으로 요청됩니다
- AI 서버는 요청에 포함된 `summary` 값을 사용하면 됩니다
- **별도의 상태 관리나 대기 로직이 필요 없습니다**

**Response:**
```json
HTTP 200 OK
{"status": "accepted"}
```

**처리 완료 후 콜백:**
```
POST http://backend-server/api/ai/callback/mission
Content-Type: application/json

{
  "sessionId": 123,
  "mission": "오늘 하루 동안 자신에게 칭찬 3가지 말해보기"
}
```

---

### 3. Advice 엔드포인트

**호출 시점:** 세션 종료 시 (Summary와 동시에 요청됨, 독립적)

**요청받는 엔드포인트:** `POST ${fastApi.advice.endpoint}`

**Request Body:**
```json
{
  "sessionId": 123,  // ← 백엔드에서 추가로 전달
  "messages": [...],
  "language": "kor",
  "persona": "...",
  "formal": true,
  "nickname": "닉네임"
}
```

**Response:**
```json
HTTP 200 OK
{"status": "accepted"}
```

**처리 완료 후 콜백:**
```
POST http://backend-server/api/ai/callback/advice
Content-Type: application/json

{
  "sessionId": 123,
  "advice": [
    "그럴 때도 있지, 자신감을 갖고 차근차근 해보면 돼.",
    "네가 너무 쉽게 생각하는 거 아니야? 그렇게 안일하게 해도 되냐?"
  ]
}
```

---

## AI 서버 구현 예시 (Python FastAPI)

```python
from fastapi import FastAPI, BackgroundTasks
import httpx

app = FastAPI()
BACKEND_CALLBACK_URL = "http://backend-server/api/ai/callback"

@app.post("/summary")
async def create_summary(request: SummaryRequest, background_tasks: BackgroundTasks):
    # 즉시 200 응답
    background_tasks.add_task(process_summary, request)
    return {"status": "accepted"}

async def process_summary(request: SummaryRequest):
    # AI 처리 (시간이 오래 걸림)
    problem, solutions = await generate_summary(request)

    # 백엔드 콜백 호출 (이 시점에 백엔드가 Mission을 자동 요청함)
    async with httpx.AsyncClient() as client:
        await client.post(
            f"{BACKEND_CALLBACK_URL}/summary",
            json={
                "sessionId": request.sessionId,
                "problem": problem,
                "solutions": solutions
            },
            timeout=10.0
        )

@app.post("/mission")
async def create_mission(request: MissionRequest, background_tasks: BackgroundTasks):
    # 즉시 200 응답
    background_tasks.add_task(process_mission, request)
    return {"status": "accepted"}

async def process_mission(request: MissionRequest):
    # request.summary에 Summary 결과가 포함되어 있음
    summary = request.summary

    # Mission 생성
    mission = await generate_mission(request, summary)

    # 백엔드 콜백 호출
    async with httpx.AsyncClient() as client:
        await client.post(
            f"{BACKEND_CALLBACK_URL}/mission",
            json={
                "sessionId": request.sessionId,
                "mission": mission
            },
            timeout=10.0
        )

@app.post("/advice")
async def create_advice(request: AdviceRequest, background_tasks: BackgroundTasks):
    # 즉시 200 응답
    background_tasks.add_task(process_advice, request)
    return {"status": "accepted"}

async def process_advice(request: AdviceRequest):
    # Advice 생성
    advice = await generate_advice(request)

    # 백엔드 콜백 호출
    async with httpx.AsyncClient() as client:
        await client.post(
            f"{BACKEND_CALLBACK_URL}/advice",
            json={
                "sessionId": request.sessionId,
                "advice": advice
            },
            timeout=10.0
        )
```

**핵심 차이점:**
- ❌ **기존**: AI 서버에서 Summary 결과를 저장하고 Mission에서 사용
- ✅ **개선**: 백엔드가 Summary 콜백 수신 후 Mission을 요청하며 summary를 포함하여 전송
- **장점**: AI 서버는 상태 관리 없이 단순히 주어진 입력으로 AI 결과만 생성

---

## 주의사항

### 1. sessionId 전달
- **중요:** AI 서버로 보내는 모든 Request에 `sessionId` 필드를 추가해야 합니다
- 백엔드에서 기존 Request DTO에 sessionId를 추가하여 전송합니다

### 2. Summary와 Mission 의존성
- Mission은 Summary 결과에 의존합니다
- **백엔드가 자동으로 처리합니다**
  1. AI 서버가 Summary 콜백을 백엔드에 전송
  2. 백엔드가 Summary를 DB에 저장
  3. 백엔드가 자동으로 Mission 요청 (summary 포함)
  4. AI 서버는 요청에 포함된 summary를 사용하여 Mission 생성
- **AI 서버는 별도의 상태 관리나 대기 로직이 필요 없습니다**

### 3. 타임아웃 설정
- 백엔드 → AI 서버: 10초 타임아웃 (즉시 응답이므로)
- AI 서버 → 백엔드 콜백: 10초 타임아웃 권장

### 4. 재시도 로직
- AI 서버에서 백엔드 콜백 실패 시 재시도 로직 구현 권장
- 3회 재시도 후 실패 로그 기록

### 5. 에러 처리
- 백엔드에서 AI 요청 실패해도 세션 업데이트는 성공 처리
- 클라이언트가 조회 시 AI 결과가 없으면 에러 응답

---

## 테스트 방법

### 1. 로컬 테스트
```bash
# AI 서버 콜백 테스트
curl -X POST http://localhost:8080/api/ai/callback/summary \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": 1,
    "problem": "테스트 문제",
    "solutions": ["해결방안1", "해결방안2"]
  }'
```

### 2. 통합 테스트
1. 세션 종료 API 호출
2. AI 서버 로그 확인 (요청 수신 확인)
3. AI 서버에서 콜백 호출
4. 백엔드 로그 확인 (콜백 수신 및 DB 업데이트)
5. 조회 API로 결과 확인

---

## 변경 이력

### 2025-01-14
- 이벤트 기반 비동기 처리 → 콜백 방식으로 변경
- RestTemplate 타임아웃 설정 추가 (연결: 5초, 읽기: 10초)
- LastEmotionEventHandler @Deprecated 처리
- AiCallbackController 추가
- AiCallbackService 추가
