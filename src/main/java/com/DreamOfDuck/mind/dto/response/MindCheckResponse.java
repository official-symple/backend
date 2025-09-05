package com.DreamOfDuck.mind.dto.response;

import com.DreamOfDuck.mind.entity.MindCheck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "mind check response")
public class MindCheckResponse {
    @Schema(example="1")
    Long mindCheckId;

    @Schema(example="true")
    boolean question1;

    @Schema(example="true")
    boolean question2;

    @Schema(example="true")
    boolean question3;

    @Schema(example="null")
    String positiveEmotion;

    @Schema(example="1")
    String negativeEmotion;

    public static MindCheckResponse fromMindCheck(MindCheck mindCheck) {
        return MindCheckResponse.builder()
                .mindCheckId(mindCheck.getId())
                .question1(mindCheck.isQuestion1())
                .question2(mindCheck.isQuestion2())
                .question3(mindCheck.isQuestion3())
                .negativeEmotion(mindCheck.getNegativeEmotion()==null?null:mindCheck.getNegativeEmotion().getText())
                .positiveEmotion(mindCheck.getPositiveEmotion()==null?null:mindCheck.getPositiveEmotion().getText())
                .build();
    }
}
