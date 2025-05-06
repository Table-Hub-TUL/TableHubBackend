package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
