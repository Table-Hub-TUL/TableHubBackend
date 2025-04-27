package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
