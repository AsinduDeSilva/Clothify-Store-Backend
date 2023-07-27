package com.clothifystore.controller;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Admin;
import com.clothifystore.repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping()
    public ResponseEntity<CrudResponse> changePassword(@RequestBody ChangePasswordRequestDTO request){

        Admin admin1 = adminRepo.findByAdminID(1);
        admin1.getUser().setPassword(passwordEncoder.encode(request.getPassword()));
        adminRepo.save(admin1);
        return ResponseEntity.ok(new CrudResponse(true, "Admin Updated"));
    }

}
