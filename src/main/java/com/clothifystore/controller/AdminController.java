package com.clothifystore.controller;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Admin;
import com.clothifystore.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PutMapping
    public ResponseEntity<CrudResponse> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        adminService.changePassword(request);
        return ResponseEntity.ok(new CrudResponse(true, "Admin Password Changed"));
    }

    @GetMapping
    public ResponseEntity<Admin> getAdmin() {
        return ResponseEntity.ok(adminService.getAdmin());
    }
}
