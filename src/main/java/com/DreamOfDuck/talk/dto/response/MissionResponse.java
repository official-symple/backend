package com.DreamOfDuck.talk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponse {
    @Schema(example="오늘 자격증 시험 보러가는 날이었는데, 아침에 늦게 일어나서 시험을 보러 가지 못했어.난 진짜 답이 없나봐... 다른 애들은 다 그냥 너무 멋지고 나도 빨리 따라가야 하는데 이번에 한번 실패하니까 다 포기하고 싶어")
    String mission;
}
