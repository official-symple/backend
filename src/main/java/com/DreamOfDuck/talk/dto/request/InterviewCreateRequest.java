package com.DreamOfDuck.talk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewCreateRequest {
    @Schema(example="접수면접 1 답변")
    @NotNull
    String question1;
    @Schema(example="접수면접 2 답변")
    @NotNull
    String question2;
    @Schema(example="접수면접 3 답변")
    @NotNull
    String question3;
    @Schema(example="접수면접 4 답변")
    @NotNull
    String question4;
    @Schema(example="접수면접 5 답변")
    @NotNull
    String question5;
    @Schema(example="접수면접 6 답변")
    @NotNull
    String question6;
    @Schema(example="접수면접 7 답변")
    @NotNull
    String question7;
    @Schema(example="접수면접 8 답변")
    @NotNull
    String question8;
    @Schema(example="접수면접 9 답변")
    @NotNull
    String question9;
}
