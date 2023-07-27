package com.clothifystore.controller;

import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Admin;
import com.clothifystore.repository.AdminRepo;
import com.clothifystore.repository.UserRepo;
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
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping
    public ResponseEntity<CrudResponse> updateAdmin(@RequestParam(value = "name") String name,
                                                    @RequestParam(value = "password") String password,
                                                    @RequestParam(value = "email")String email){

        if (userRepo.existsByEmail(email)){
            return ResponseEntity.ok(new CrudResponse(false, "Duplicate Data"));
        }

        Admin admin1 = adminRepo.findByAdminID(1);
        admin1.setName(name);
        admin1.getUser().setEmail(email);
        admin1.getUser().setPassword(passwordEncoder.encode(password));
        adminRepo.save(admin1);
        return ResponseEntity.ok(new CrudResponse(true, "Admin Updated"));
    }

}
