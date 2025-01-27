package com.example.hearurbackend.domain.user.repository;

import com.example.hearurbackend.domain.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
