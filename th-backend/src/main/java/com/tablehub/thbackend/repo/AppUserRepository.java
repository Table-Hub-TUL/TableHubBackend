package com.tablehub.thbackend.repo;


import com.tablehub.thbackend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {}