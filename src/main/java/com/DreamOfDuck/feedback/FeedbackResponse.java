package com.DreamOfDuck.feedback;

import com.DreamOfDuck.talk.dto.response.AdviceResponse;
import com.DreamOfDuck.talk.entity.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    @Schema(example="id")
    private Long id;
    @Schema(example="4")
    private Integer star;
    @Schema(example="어쩔저쩔")
    private String content;

    public static FeedbackResponse from(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .star(feedback.getStar())
                .content(feedback.getContent())
                .build();
    }
}
