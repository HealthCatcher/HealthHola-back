package com.example.hearurbackend.repository;

import com.example.hearurbackend.entity.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
