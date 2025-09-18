package com.DreamOfDuck.goods.controller;

import com.DreamOfDuck.account.dto.request.*;
import com.DreamOfDuck.goods.dto.response.AttendanceByMonthResponse;
import com.DreamOfDuck.goods.dto.response.AttendanceResponse;
import com.DreamOfDuck.goods.dto.request.DiaRequest;
import com.DreamOfDuck.goods.dto.response.FeatherRewardResponse;
import com.DreamOfDuck.goods.dto.response.HomeResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.goods.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goods")
public class GoodsController {
    private final MemberService memberService;
    private final GoodsService goodsService;

    @GetMapping("/attendance/month")
    @Operation(summary = "월별 출석 기록", description = "월별 출석 기록을 받는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "해당 월의 출석 내역",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = AttendanceByMonthResponse.class))
                    )
            )
    })
    public List<AttendanceByMonthResponse> getAttendanceByMonth(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam @DateTimeFormat(pattern="yyyy-MM") YearMonth yearMonth) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.getAttendanceByMonth(member, yearMonth);
    }
    @PostMapping("/attendance/ice/{date}")
    @Operation(summary = "얼음 깨기", description = "얼음을 깨는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = AttendanceResponse.class)
            )})
    })
    public AttendanceResponse breakIce(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.breakIce(member, date);
    }
    @PostMapping("/attendance")
    @Operation(summary = "최장 출석 일수 받기", description = "최장 출석 일수를 받는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = AttendanceResponse.class)
            )})
    })
    public AttendanceResponse getAttendance(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.getAttendance(member);
    }
    @PostMapping("/attendance/reward")
    @Operation(summary = "출석 깃털 보상 받기", description = "출석으로 인한 깃털 보상받는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = FeatherRewardResponse.class)
            )})
    })
    public FeatherRewardResponse getFeatherByAttendance(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.getFeatherByAttendance(member);
    }

    @PostMapping("/heart/ad")
    @Operation(summary = "광고,하트 업데이트", description = "광고보고 하트를 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateHeartByAd(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.plusHeart(member, 1);
    }
    @PostMapping("/heart/share")
    @Operation(summary = "공유하기,하트 업데이트", description = "공유하고 하트를 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateHeartByShare(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.plusHeart(member, 1);
    }
    @PostMapping("/heart/mission")
    @Operation(summary = "오늘의 미션 완료시 하트 지급 API", description = "오늘의 미션 완료시 하트를 지급하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateHeartByMission(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.plusHeart(member, 2);
    }

    @PostMapping("/feather/mission")
    @Operation(summary = "오늘의 미션 완료시 깃털 50개 지급 API", description = "오늘의 미션 완료시 깃털을 지급하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateFeatherByMission(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.plusFeather(member, 50);
    }
    @DeleteMapping("/heart/pang")
    @Operation(summary = "게임시 하트 사용", description = "게임시 하트를 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateHeartByGame(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.minusHeart(member, 1);
    }

    @PostMapping("/pang")
    @Operation(summary = "꽥팡 후, 다이아&깃털 업데이트", description = "꽥팡 후, 다이아&깃털을 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateDiaByPang(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid DFRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.plusDiaAndFeather(member, request);
    }

    @PostMapping("/dia/recharge")
    @Operation(summary = "다이아 구매", description = "다이아 구매시, 업데이트 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateDiaByStore(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid DiaRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.plusDia(member, request);
    }
    @DeleteMapping("/dia/consume")
    @Operation(summary = "게임 아이템/얼음깨기/오리 코스툼 구매", description = "게임 아이템/얼음깨기/오리 코스튬 구매시, 다이아 업데이트 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse deleteDiaByStore(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid DiaRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.minusDia(member, request);
    }

    @PostMapping("/duckname")
    @Operation(summary = "오리이름 업데이트", description = "오리이름을 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateDuckName(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid DucknameRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return goodsService.updateDuckname(member, request);
    }



}
