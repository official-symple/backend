package com.DreamOfDuck.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "session create request")
public class FeedbackCreateRequest {
    @Schema(example="2")
    @NotNull
    Integer star;
    @Schema(example="어쩌구 저쩌구")
    String content;

}
