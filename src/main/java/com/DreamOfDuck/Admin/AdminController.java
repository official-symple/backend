package com.DreamOfDuck.Admin;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.AuthService;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.pang.dto.request.ItemCreateRequest;
import com.DreamOfDuck.pang.dto.response.ItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AuthService authService;
    private final AdminService adminService;

    @GetMapping("/report")
    public ResponseEntity<?> getAllReports(){
        return ResponseEntity.ok(adminService.getAllReport());
    }
    @GetMapping("/report-detail/{id}")
    public ResponseEntity<?> getReportById(@PathVariable("id") Long id){
        return ResponseEntity.ok(adminService.getReportById(id));
    }

}
