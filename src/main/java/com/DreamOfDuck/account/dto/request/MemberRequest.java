package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "member signup request")
public class MemberRequest {
    @Schema(example="True")
    @NotNull
    Boolean isMarketing;
    @Schema(example="기미진")
    @NotNull
    String nickname;
    @Schema(example="2002-01-4")
    @NotNull
    LocalDate birthday;
    @Schema(example="female or male")
    @NotNull
    String gender;
    @Schema(example="1")
    @NotNull
    Integer concern;
    @Schema(example="70")
    @NotNull
    Integer blue;
}
