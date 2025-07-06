package com.DreamOfDuck.talk.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
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
public class AdviceResponse {
    @Schema(example="[\"그럴 때도 있지, 자신감을 갖고 차근차근 해보면 돼.\", \"네가 너무 쉽게 생각하는 거 아니야? 그렇게 안일하게 해도 되냐?\"]")
    List<String> advice;
}
