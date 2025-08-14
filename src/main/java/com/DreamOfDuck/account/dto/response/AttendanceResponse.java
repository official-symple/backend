package com.DreamOfDuck.account.dto.response;

import com.DreamOfDuck.account.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "home screen response")
public class AttendanceResponse {
    @Schema(example="14")
    Integer curAttendance;
    @Schema(example="180")
    Integer longestAttendance;


}
