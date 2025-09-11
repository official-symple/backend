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
    @Schema(example="12")
    Integer responseRateOfQ1;
    @Schema(example="12")
    Integer responseRateOfQ2;
    @Schema(example="12")
    Integer responseRateOfQ3;
    @Schema(example="70")
    float dayResponseRate;
    @Schema(example="30")
    float nightResponseRate;
    @Schema(example="12")
    float responseRate;
    @Schema(example="40")
    float positiveEmotionRate;
    @Schema(example="60")
    float negativeEmotionRate;
    @Schema(example="{\n" +
            "      \"emotion\": \"즐거워\",\n" +
            "      \"ratio\": 9.090909\n" +
            "    },\n" +
            "    {\n" +
            "      \"emotion\": \"평온해\",\n" +
            "      \"ratio\": 9.090909\n" +
            "    },\n" +
            "    {\n" +
            "      \"emotion\": \"흥분돼\",\n" +
            "      \"ratio\": 9.090909\n" +
            "    }")
    private List<EmotionRatio> top3Emotions;
    @Schema(example="지난 2주와 비교해봤을 때 한 단계 상승했어요")
    private String resultTrend;
    @Schema(example="우울 빈도가 줄었고 스트레스 조절 어려움 빈도는 더 늘었어요. 스트레스 빈도는 그대로예요.")
    private String questionResponseTrend;
    @Schema(example = "부정 감정인 '언짢아'에서 부정 감정인 '우울해'로 달라졌어요.")
    private String topEmotionTrend;
    @Schema(example = "부정 감정의 비율이 7% 감소했어요.")
    private String negativeEmotionTrend;
    @Schema(example="전체 응답율은 40%로 그대로예요.\n아침 응답률은 20%가 늘었고, 밤 응답률은 50%가 늘었어요.")
    private String responseRateTrend;
    @Data
    @AllArgsConstructor
    public static class EmotionRatio {
        private String emotion;
        private float ratio;
    }
}
