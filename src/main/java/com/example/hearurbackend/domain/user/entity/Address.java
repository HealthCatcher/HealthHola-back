package com.example.hearurbackend.domain.user.entity;

import com.example.hearurbackend.domain.user.dto.AddressDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "username")
    private User user;
    private String address;
    private String detailAddress;
    private String zoneCode;

    public Address(User user, String address, String detailAddress, String zoneCode) {
        this.user = user;
        this.address = address;
        this.detailAddress = detailAddress;
        this.zoneCode = zoneCode;
    }

    public Address(User user, AddressDto addressRequestDto) {
        this.user = user;
        this.address = addressRequestDto.getAddress();
        this.detailAddress = addressRequestDto.getDetailAddress();
        this.zoneCode = addressRequestDto.getZoneCode();
    }

    public void update(AddressDto addressRequestDto) {
        this.address = addressRequestDto.getAddress();
        this.detailAddress = addressRequestDto.getDetailAddress();
        this.zoneCode = addressRequestDto.getZoneCode();
    }
}
