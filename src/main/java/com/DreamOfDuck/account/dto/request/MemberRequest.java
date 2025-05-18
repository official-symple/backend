package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "session create request")
public class MemberRequest {
    @Schema(example="True")
    Boolean isMarketing;
    @Schema(example="기미진")
    String nickname;
    @Schema(example="2002-01-4")
    LocalDate birthday;
    @Schema(example="female or male")
    String gender;
    @Schema(example="1")
    Integer concern;
    @Schema(example="70")
    Integer blue;
}
