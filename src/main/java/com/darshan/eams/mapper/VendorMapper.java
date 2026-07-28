package com.darshan.eams.mapper;

import com.darshan.eams.dto.request.VendorRequestDto;
import com.darshan.eams.dto.response.VendorResponseDto;
import com.darshan.eams.entity.Vendor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VendorMapper {

    public Vendor toEntity(VendorRequestDto dto) {
        if (dto == null)
            return null;

        Vendor vendor = new Vendor();
        vendor.setVendorName(dto.getVendorName());
        vendor.setContactPerson(dto.getContactPerson());
        vendor.setEmail(dto.getEmail());
        vendor.setPhoneNumber(dto.getPhoneNumber());
        vendor.setAddress(dto.getAddress());
        vendor.setRating(dto.getRating());
        return vendor;
    }

    public void updateEntity(Vendor vendor, VendorRequestDto dto) {
        vendor.setVendorName(dto.getVendorName());
        vendor.setContactPerson(dto.getContactPerson());
        vendor.setEmail(dto.getEmail());
        vendor.setPhoneNumber(dto.getPhoneNumber());
        vendor.setAddress(dto.getAddress());
        vendor.setRating(dto.getRating());
    }

    public VendorResponseDto toResponseDto(Vendor vendor) {
        return toResponseDto(vendor, 0L, 0L);
    }

    public VendorResponseDto toResponseDto(Vendor vendor, long suppliedAssetsCount, long maintenanceJobsCount) {
        if (vendor == null)
            return null;

        return VendorResponseDto.builder()
                .id(vendor.getId())
                .vendorName(vendor.getVendorName())
                .contactPerson(vendor.getContactPerson())
                .email(vendor.getEmail())
                .phoneNumber(vendor.getPhoneNumber())
                .address(vendor.getAddress())
                .rating(vendor.getRating())
                .status(vendor.getStatus())
                .suppliedAssetsCount(suppliedAssetsCount)
                .maintenanceJobsCount(maintenanceJobsCount)
                .createdAt(vendor.getCreatedAt())
                .updatedAt(vendor.getUpdatedAt())
                .build();
    }

    public List<VendorResponseDto> toResponseDtoList(List<Vendor> vendors) {
        return vendors.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}