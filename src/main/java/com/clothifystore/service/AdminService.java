package com.clothifystore.service;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.entity.Admin;

public interface AdminService {
    void changePassword(ChangePasswordRequestDTO request);
    Admin getAdmin();
}
