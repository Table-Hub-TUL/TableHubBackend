package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.request.ChangePasswordRequest;

public interface UserService {
    void changePassword(String username, ChangePasswordRequest request);
}