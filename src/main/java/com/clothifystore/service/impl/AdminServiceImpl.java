package com.clothifystore.service.impl;

import com.clothifystore.service.AdminService;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.entity.Admin;
import com.clothifystore.repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void changePassword(ChangePasswordRequestDTO request) {
        Admin admin1 = adminRepo.findByAdminID(1);
        admin1.getUser().setPassword(passwordEncoder.encode(request.getPassword()));
        adminRepo.save(admin1);
    }

    public Admin getAdmin() {
        return adminRepo.findByAdminID(1);
    }
}
