package com.darshan.eams.mapper;

import com.darshan.eams.dto.request.AssetAllocationRequestDto;
import com.darshan.eams.dto.response.AssetAllocationResponseDto;
import com.darshan.eams.entity.AssetAllocation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssetAllocationMapper {

    public AssetAllocation toEntity(AssetAllocationRequestDto dto) {
        if (dto == null)
            return null;

        AssetAllocation allocation = new AssetAllocation();
        allocation.setAsset(null);
        allocation.setEmployee(null);
        allocation.setAllocationDate(dto.getAllocationDate() != null ? dto.getAllocationDate(): LocalDate.now());
        allocation.setExpectedReturnDate(dto.getExpectedReturnDate());
        allocation.setRemarks(dto.getRemarks());
        return allocation;
    }

    public AssetAllocationResponseDto toResponseDto(AssetAllocation allocation) {
        if (allocation == null)
            return null;

        return AssetAllocationResponseDto.builder()
                .id(allocation.getId())
                .assetId(allocation.getAsset() != null ? allocation.getAsset().getId() : null)
                .assetCode(allocation.getAsset() != null ? allocation.getAsset().getAssetCode() : null)
                .assetName(allocation.getAsset() != null ? allocation.getAsset().getAssetName() : null)
                .employeeId(allocation.getEmployee() != null ? allocation.getEmployee().getId() : null)
                .employeeName(allocation.getEmployee() != null ? allocation.getEmployee().getFirstName() + " " + allocation.getEmployee().getLastName() : null)
                .allocationDate(allocation.getAllocationDate())
                .expectedReturnDate(allocation.getExpectedReturnDate())
                .actualReturnDate(allocation.getActualReturnDate())
                .status(allocation.getStatus())
                .remarks(allocation.getRemarks())
                .createdAt(allocation.getCreatedAt())
                .updatedAt(allocation.getUpdatedAt())
                .build();
    }

    public List<AssetAllocationResponseDto> toResponseDtoList(List<AssetAllocation> allocations) {
        return allocations.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}