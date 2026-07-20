package com.clothifystore.service.impl;

import com.clothifystore.service.AdminService;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.response.AdminResponseDTO;
import com.clothifystore.entity.Admin;
import com.clothifystore.repository.AdminRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @PostConstruct
    private void configureModelMapper() {
        // Map Admin -> AdminResponseDTO: pull email from nested User
        TypeMap<Admin, AdminResponseDTO> typeMap =
                modelMapper.createTypeMap(Admin.class, AdminResponseDTO.class);
        typeMap.addMapping(src -> src.getUser().getEmail(), AdminResponseDTO::setEmail);
    }

    public void changePassword(ChangePasswordRequestDTO request) {
        Admin admin = adminRepo.findByAdminID(1);
        admin.getUser().setPassword(passwordEncoder.encode(request.getPassword()));
        adminRepo.save(admin);
    }

    public AdminResponseDTO getAdmin() {
        Admin admin = adminRepo.findByAdminID(1);
        return modelMapper.map(admin, AdminResponseDTO.class);
    }
}
