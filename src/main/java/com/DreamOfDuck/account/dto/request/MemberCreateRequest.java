package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "member signup request")
public class MemberCreateRequest {
    @Schema(example="True")
    @NotNull
    Boolean isMarketing;
    @Schema(example="기미진")
    @NotNull
    String nickname;
    @Schema(example="2002-01-4")
    @NotNull
    LocalDate birthday;
    @Schema(
            example = "female",
            description = "female or male"
    )
    @NotNull
    String gender;
    @Schema(
            example = "Kor(Eng)",
            description = "Kor or Eng"
    )
    @Nullable
    String language;
    @Schema(example="1")
    @NotNull
    Integer concern;
    @Schema(example="[1, 1, 2, 3, 0, 3, 2, 3, 0]")
    @NotNull
    @Size(min = 9, max = 9, message = "status는 9개의 값이어야 합니다.")
    List<Integer> status;
}
