package com.DreamOfDuck.goods.dto.response;

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
@Schema(description = "attendance response")
public class AttendanceResponse {
    @Schema(example="14")
    Integer curAttendance;
    @Schema(example="180")
    Integer longestAttendance;

    public static AttendanceResponse fromMember(Member member) {
        return AttendanceResponse.builder()
                .curAttendance(member.getCurStreak())
                .longestAttendance(member.getLongestStreak())
                .build();
    }
}
