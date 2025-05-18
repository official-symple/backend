package com.DreamOfDuck.account.dto.response;

import com.DreamOfDuck.account.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    @Schema(example="1")
    Long memberId;
    @Schema(example="True")
    Boolean isMarketing;
    @Schema(example="기미진")
    String nickname;
    @Schema(example="2002-01-4")
    LocalDate birthday;
    @Schema(example="ROLE_GUEST")
    String role;
    @Schema(example="FEMALE")
    String gender;
    @Schema(example="1")
    Integer concern;
    @Schema(example="70")
    Integer blue;

    public static MemberResponse from(Member member){
        return MemberResponse.builder()
                .memberId(member.getId())
                .isMarketing(member.getIsMarketing())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .role(member.getRole().toString())
                .gender(member.getGender().toString())
                .concern(member.getConcern().getId())
                .blue(member.getBlue())
                .build();
    }
}
