package com.DreamOfDuck.mind.dto.response;

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
@Schema(description = "mind check report response per 2 weeks, a month")
public class MindCheckReportPeriod {
    @Schema(example="위험")
    String result;
    Integer responseRateOfQ1;
    Integer responseRateOfQ2;
    Integer responseRateOfQ3;
    float dayResponseRate;
    float nightResponseRate;
    float responseRate;
    float positiveEmotionRate;
    float negativeEmotionRate;
    private List<EmotionRatio> top3Emotions;

    private String resultTrend;
    private String questionResponseTrend;
    private String topEmotionTrend;
    private String negativeEmotionTrend;
    private String responseRateTrend;
    @Data
    @AllArgsConstructor
    public static class EmotionRatio {
        private String emotion;
        private float ratio;
    }
}
