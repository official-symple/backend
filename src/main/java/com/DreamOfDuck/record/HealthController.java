package com.DreamOfDuck.record;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.talk.dto.request.MessageCreateRequest;
import com.DreamOfDuck.talk.dto.response.MessageFormat;
import com.DreamOfDuck.talk.dto.response.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health")
public class HealthController {
    private final HealthService healthService;
    private final MemberService memberService;
    @PostMapping("")
    @Operation(summary = "건강 기록 생성", description = "건강기록을 생성할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HealthResponse.class)
            )})
    })
    public ResponseEntity<?> createRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody HealthCreateRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        HealthResponse response = healthService.save(member, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "건강기록 수정", description = "건강기록을 수정할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HealthResponse.class)
            )})
    })
    public ResponseEntity<?> updateRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody HealthUpdateRequest request, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(healthService.updateHealth(member, id, request));
    }
    @PutMapping("/diary/{id}")
    @Operation(summary = "일기 작성&수정", description = "일기를 작성하거나 수정할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HealthResponse.class)
            )})
    })
    public ResponseEntity<?> writeDiary(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody DiaryRequest request, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(healthService.updateDiary(member, id, request.getDiary()));
    }
    @PutMapping("/date/{id}")
    @Operation(summary = "날짜 변경", description = "날짜를 변경할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HealthResponse.class)
            )})
    })
    public ResponseEntity<?> updateDate(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody DateRequest request, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(healthService.updateDate(member, id, request.getDate()));
    }
    @GetMapping("/{id}")
    @Operation(summary = "특정 건강기록 조회", description = "특정 건강기록을 조회할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HealthResponse.class)
            )})
    })
    public ResponseEntity<?> getRecordById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(healthService.getRecordById(member, id));
    }
    @GetMapping("/date")
    @Operation(summary = "날짜별 건강기록 조회", description = "날짜별 건강기록을 조회할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HealthResponse.class)
            )})
    })
    public ResponseEntity<?> getRecordByDate(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(value="date") LocalDate date){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(healthService.getRecordByDate(member, date));
    }
    @GetMapping("/period")
    @Operation(summary = "특정 기간 건강기록 조회", description = "특정 기간 건강기록을 조회할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(array=@ArraySchema(schema= @Schema(implementation = HealthResponse.class))
            )})
    })
    public ResponseEntity<?> getRecordByDatePeriod(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(value="startDate") LocalDate startDate, @RequestParam(value="endDate") LocalDate endDate){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(healthService.getRecordsByDatePeriodAndHost(member, startDate,endDate));
    }
    @GetMapping("/")
    @Operation(summary = "유저의 모든 건강기록 조회", description = "유저의 모든 건강기록을 조회할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(array=@ArraySchema(schema= @Schema(implementation = HealthResponse.class))
            )})
    })
    public ResponseEntity<?> getRecordByHost(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(healthService.getRecordsByHost(member));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "특정 건강기록 삭제", description = "특정 건강기록을 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteRecordById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        healthService.delete(member, id);
        return ResponseEntity.ok("건강 기록이 정상적으로 삭제되었습니다.");
    }


}
