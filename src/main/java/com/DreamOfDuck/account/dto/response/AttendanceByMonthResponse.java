package com.DreamOfDuck.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "attendance filtered by date response")
public class AttendanceByMonthResponse {
    @Schema(example="2025-08-25")
    LocalDate attendedDate;

}
