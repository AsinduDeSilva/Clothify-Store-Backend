package com.clothifystore.service;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.response.AdminResponseDTO;

public interface AdminService {
    void changePassword(ChangePasswordRequestDTO request);
    AdminResponseDTO getAdmin();
}
