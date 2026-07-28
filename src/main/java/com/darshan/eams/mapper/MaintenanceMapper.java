package com.darshan.eams.mapper;

import com.darshan.eams.dto.request.MaintenanceRequestDto;
import com.darshan.eams.dto.response.MaintenanceResponseDto;
import com.darshan.eams.entity.MaintenanceRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaintenanceMapper {

    public MaintenanceRecord toEntity(MaintenanceRequestDto dto) {
        if (dto == null)
            return null;

        MaintenanceRecord record = new MaintenanceRecord();
        record.setTitle(dto.getTitle()); // Map title
        record.setIssueDescription(dto.getIssueDescription());
        record.setReportedDate(dto.getReportedDate());
        record.setExpectedCompletionDate(dto.getExpectedCompletionDate());
        record.setCompletedDate(dto.getCompletedDate());
        record.setMaintenanceCost(dto.getMaintenanceCost());
        record.setRemarks(dto.getRemarks());
        return record;
    }

    public void updateEntity(MaintenanceRecord record, MaintenanceRequestDto dto) {
        record.setTitle(dto.getTitle());
        record.setIssueDescription(dto.getIssueDescription());
        if (dto.getReportedDate() != null) {
            record.setReportedDate(dto.getReportedDate());
        }
        record.setExpectedCompletionDate(dto.getExpectedCompletionDate());
        record.setCompletedDate(dto.getCompletedDate());
        record.setMaintenanceCost(dto.getMaintenanceCost());
        record.setRemarks(dto.getRemarks());
    }

    public MaintenanceResponseDto toResponseDto(MaintenanceRecord record) {
        if (record == null)
            return null;

        return MaintenanceResponseDto.builder()
                .id(record.getId())
                .assetId(record.getAsset() != null ? record.getAsset().getId() : null)
                .assetCode(record.getAsset() != null ? record.getAsset().getAssetCode() : null)
                .assetName(record.getAsset() != null ? record.getAsset().getAssetName() : null)
                .vendorId(record.getVendor() != null ? record.getVendor().getId() : null)
                .vendorName(record.getVendor() != null ? record.getVendor().getVendorName() : null)
                .issueDescription(record.getIssueDescription())
                .reportedDate(record.getReportedDate())
                .expectedCompletionDate(record.getExpectedCompletionDate())
                .completedDate(record.getCompletedDate())
                .maintenanceCost(record.getMaintenanceCost())
                .status(record.getStatus())
                .remarks(record.getRemarks())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    public List<MaintenanceResponseDto> toResponseDtoList(List<MaintenanceRecord> records) {
        return records.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}