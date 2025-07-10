package com.DreamOfDuck.talk.dto.response;

import com.DreamOfDuck.talk.entity.Interview;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {
    @Schema(example = "1")
    private Long interviewId;

    @Schema(example = "접수면접 1 답변")
    private String question1;

    @Schema(example = "접수면접 2 답변")
    private String question2;

    @Schema(example = "접수면접 3 답변")
    private String question3;

    @Schema(example = "접수면접 3-2 답변")
    private String question3_2;

    @Schema(example = "접수면접 3-3 답변")
    private String question3_3;

    @Schema(example = "접수면접 4 답변")
    private String question4;

    @Schema(example = "접수면접 5 답변")
    private String question5;

    @Schema(example = "접수면접 5-2 답변")
    private String question5_2;

    @Schema(example = "접수면접 6 답변")
    private String question6;

    @Schema(example = "접수면접 6-2 답변")
    private String question6_2;

    @Schema(example = "접수면접 6-3 답변")
    private String question6_3;

    @Schema(example = "접수면접 7 답변")
    private String question7;

    @Schema(example = "접수면접 8 답변")
    private String question8;

    @Schema(example = "접수면접 8-2 답변")
    private String question8_2;

    @Schema(example = "접수면접 9 답변")
    private String question9;

    @Schema(example = "접수면접 9-2 답변")
    private String question9_2;

    @Schema(example = "접수면접 10 답변")
    private String question10;

    public static InterviewResponse from(Interview interview) {
        return InterviewResponse.builder()
                .interviewId(interview.getId())
                .question1(interview.getQuestion1())
                .question2(interview.getQuestion2())
                .question3(interview.getQuestion3())
                .question3_2(interview.getQuestion3_2())
                .question3_3(interview.getQuestion3_3())
                .question4(interview.getQuestion4())
                .question5(interview.getQuestion5())
                .question5_2(interview.getQuestion5_2())
                .question6(interview.getQuestion6())
                .question6_2(interview.getQuestion6_2())
                .question6_3(interview.getQuestion6_3())
                .question7(interview.getQuestion7())
                .question8(interview.getQuestion8())
                .question8_2(interview.getQuestion8_2())
                .question9(interview.getQuestion9())
                .question9_2(interview.getQuestion9_2())
                .question10(interview.getQuestion10())
                .build();
    }
}
