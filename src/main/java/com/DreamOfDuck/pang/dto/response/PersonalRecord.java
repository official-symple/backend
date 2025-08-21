package com.DreamOfDuck.pang.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "personal response")
public class PersonalRecord {
    String nickname;
    Long score;
}
