package com.DreamOfDuck.talk.dto.request;

import com.DreamOfDuck.talk.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "session create request")
public class SessionCreateRequest {
    @Schema(
            example="1",
            description="꽥둥이=1, 꽥돌이=2, 꽥둑이=3"
    )
    @NotNull(message="duckType은 필수 입력 값입니다.")
    Integer duckType;
    @Schema(
            example="true",
            description = "true or false"
    )
    @NotNull
    Boolean isFormal;
    @Schema(example="[1,10, 30]")
    @NotNull(message="emotion은 필수 입력 값입니다.")
    List<Integer> emotion;
    @Schema(example="1")
    @NotNull(message="emotion은 필수 입력 값입니다.")
    Integer cause;

}
