package com.DreamOfDuck.account.dto.response;

import java.time.LocalDate;
import java.util.List;

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
@Schema(description = "member response")
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
    @Schema(example="kor")
    String language;
    @Schema(example="Asia/Seoul")
    String location;
    @Schema(example="1")
    Integer concern;

    @Schema(example="[1, 1, 2, 3, 0, 3, 2, 3, 0]")
    List<Integer> status;

    @Schema(example="심한 우울")
    String totalStatus;
    @Schema(example="1100")
    Integer maxScore;
    @Schema(example="free")
    String subscribe;

    public static MemberResponse from(Member member){
        return MemberResponse.builder()
                .memberId(member.getId())
                .isMarketing(member.getIsMarketing())
                .nickname(member.getNickname())
                // .birthday(member.getBirthday())
                .role(member.getRole() != null ? member.getRole().toString() : null)
                .gender(member.getGender() != null ? member.getGender().toString() : null)
                .language(member.getLanguage() != null ? member.getLanguage().toString().toLowerCase() : null)
                .location(member.getLocation().toString())
                .concern(member.getConcern() != null ? member.getConcern().getId() : null)
                .status(member.getStatus())
                .totalStatus(member.getTotalStatus())
                .maxScore(member.getMaxScore())
                .subscribe(member.getSubscribe()==null?"free":member.getSubscribe().toString().toLowerCase())
                .build();
    }
}
