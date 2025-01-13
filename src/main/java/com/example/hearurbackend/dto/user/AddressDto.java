package com.example.hearurbackend.dto.user;

import lombok.Getter;

@Getter
public class AddressDto {
    private String address;
    private String detailAddress;
    private String zoneCode;

    public AddressDto(String address, String detailAddress, String zoneCode) {
        this.address = address;
        this.detailAddress = detailAddress;
        this.zoneCode = zoneCode;
    }
}
