package com.DreamOfDuck.fcm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmRequest {
    @Schema(hidden=true)
    private String deviceToken;
    @Schema(example="title")
    @NotNull
    private String title;
    @Schema(example="body")
    @NotNull
    private String body;
    @Schema(example="deeplink")
    private String deeplink;
    @Builder(toBuilder = true)
    public FcmRequest(String title, String body, String deeplink) {
        this.title = title;
        this.body = body;
        this.deeplink = deeplink;
    }
}
