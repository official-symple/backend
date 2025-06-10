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
    @Schema(example="1")
    Long interviewId;
    @Schema(example="접수면접 1 답변")
    String question1;
    @Schema(example="접수면접 2 답변")
    String question2;
    @Schema(example="접수면접 3 답변")
    String question3;
    @Schema(example="접수면접 4 답변")
    String question4;
    @Schema(example="접수면접 5 답변")
    String question5;
    @Schema(example="접수면접 6 답변")
    String question6;
    @Schema(example="접수면접 7 답변")
    String question7;
    @Schema(example="접수면접 8 답변")
    String question8;
    @Schema(example="접수면접 9 답변")
    String question9;

    public static InterviewResponse from(Interview interview) {
        return InterviewResponse.builder()
                .interviewId(interview.getId())
                .question1(interview.getQuestion1())
                .question2(interview.getQuestion2())
                .question3(interview.getQuestion3())
                .question4(interview.getQuestion4())
                .question5(interview.getQuestion5())
                .question6(interview.getQuestion6())
                .question7(interview.getQuestion7())
                .question8(interview.getQuestion8())
                .question9(interview.getQuestion9())
                .build();
    }
}
