package com.DreamOfDuck.talk.dto.response;

import com.DreamOfDuck.talk.entity.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    @Schema(example="1")
    Long sessionId;
    @Schema(example="다음 버튼이 안눌려요")
    String feedback;

    public static FeedbackResponse from(Session session) {
        return FeedbackResponse.builder()
                .sessionId(session.getId())
                .feedback(session.getFeedback())
                .build();
    }
}
