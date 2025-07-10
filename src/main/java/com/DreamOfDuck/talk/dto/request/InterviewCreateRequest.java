package com.DreamOfDuck.talk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewCreateRequest {
    @Schema(example = "접수면접 1 답변")
    @NotNull
    private String question1;

    @Schema(example = "접수면접 2 답변")
    @NotNull
    private String question2;

    @Schema(example = "접수면접 3 답변")
    @NotNull
    private String question3;

    @Schema(example = "접수면접 3-2 답변")
    @NotNull
    private String question3_2;

    @Schema(example = "접수면접 3-3 답변")
    @NotNull
    private String question3_3;

    @Schema(example = "접수면접 4 답변")
    @NotNull
    private String question4;

    @Schema(example = "접수면접 5 답변")
    @NotNull
    private String question5;

    @Schema(example = "접수면접 5-2 답변")
    @NotNull
    private String question5_2;

    @Schema(example = "접수면접 6 답변")
    @NotNull
    private String question6;

    @Schema(example = "접수면접 6-2 답변")
    @NotNull
    private String question6_2;

    @Schema(example = "접수면접 6-3 답변")
    @NotNull
    private String question6_3;

    @Schema(example = "접수면접 7 답변")
    @NotNull
    private String question7;

    @Schema(example = "접수면접 8 답변")
    @NotNull
    private String question8;

    @Schema(example = "접수면접 8-2 답변")
    @NotNull
    private String question8_2;

    @Schema(example = "접수면접 9 답변")
    @NotNull
    private String question9;

    @Schema(example = "접수면접 9-2 답변")
    @NotNull
    private String question9_2;

    @Schema(example = "접수면접 10 답변")
    @NotNull
    private String question10;

}
