package com.tablehub.thbackend.dto.request;

import com.tablehub.thbackend.model.RoleName;
import lombok.Data;

@Data
public class AssignRoleRequest {
    private RoleName roleName;
}
