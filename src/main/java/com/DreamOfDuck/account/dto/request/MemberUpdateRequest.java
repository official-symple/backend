package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "member signup request")
public class MemberUpdateRequest {
    @Schema(example="True")
    Boolean isMarketing;
    @Schema(example="기미진")
    String nickname;
    @Schema(example="2002-01-4")
    LocalDate birthday;
    @Schema(
            example = "female",
            description = "female or male"
    )
    String gender;
    @Schema(example="1")
    Integer concern;
    @Schema(example="[1, 1, 2, 3, 0, 3, 2, 3, 0]")
    List<Integer> status;
}
