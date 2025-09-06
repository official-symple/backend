package com.DreamOfDuck.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "fcm token response")
public class FcmTokenResponse {
    String deviceToken;
    boolean isSuccess;

    public static FcmTokenResponse of(String deviceToken) {
        return FcmTokenResponse.builder()
                .deviceToken(deviceToken)
                .isSuccess(true)
                .build();
    }
}
