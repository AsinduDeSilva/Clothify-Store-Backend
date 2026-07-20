package com.clothifystore.controller;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.response.AdminResponseDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PutMapping
    public ResponseEntity<CrudResponse> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        adminService.changePassword(request);
        return ResponseEntity.ok(new CrudResponse(true, "Admin Password Changed"));
    }

    @GetMapping
    public ResponseEntity<AdminResponseDTO> getAdmin() {
        return ResponseEntity.ok(adminService.getAdmin());
    }
}
