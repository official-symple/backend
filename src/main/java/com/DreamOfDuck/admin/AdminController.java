package com.DreamOfDuck.admin;

import com.DreamOfDuck.account.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/report/search")
    public ResponseEntity<?> getReportBySearch(SearchRequest searchRequest){
        return ResponseEntity.ok(adminService.getReportsByFilter(searchRequest));
    }
    @GetMapping("/chat-detail/{id}")
    public ResponseEntity<?> getChatById(@PathVariable("id") Long id){
        return ResponseEntity.ok(adminService.getChatById(id));
    }
}
