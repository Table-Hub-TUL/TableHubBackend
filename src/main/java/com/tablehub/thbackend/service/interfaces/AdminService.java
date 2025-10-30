package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.model.RoleName;

public interface AdminService {

    void assignRole(String username, RoleName role);
}
