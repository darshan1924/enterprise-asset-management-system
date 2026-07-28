package com.darshan.eams.mapper;

import com.darshan.eams.dto.request.AssetTransferRequestDto;
import com.darshan.eams.dto.response.AssetTransferResponseDto;
import com.darshan.eams.entity.AssetTransfer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssetTransferMapper {

    public AssetTransfer toEntity(AssetTransferRequestDto dto) {
        if (dto == null)
            return null;

        AssetTransfer transfer = new AssetTransfer();
        transfer.setTransferDate(dto.getTransferDate());
        transfer.setReason(dto.getReason());
        return transfer;
    }

    public AssetTransferResponseDto toResponseDto(AssetTransfer transfer) {
        if (transfer == null)
            return null;

        return AssetTransferResponseDto.builder()
                .id(transfer.getId())
                .assetId(transfer.getAsset() != null ? transfer.getAsset().getId() : null)
                .assetCode(transfer.getAsset() != null ? transfer.getAsset().getAssetCode() : null)
                .assetName(transfer.getAsset() != null ? transfer.getAsset().getAssetName() : null)
                .fromDepartmentId(transfer.getFromDepartment() != null ? transfer.getFromDepartment().getId() : null)
                .fromDepartmentName(transfer.getFromDepartment() != null ? transfer.getFromDepartment().getDepartmentName() : null)
                .toDepartmentId(transfer.getToDepartment() != null ? transfer.getToDepartment().getId() : null)
                .toDepartmentName(transfer.getToDepartment() != null ? transfer.getToDepartment().getDepartmentName() : null)
                .fromEmployeeId(transfer.getFromEmployee() != null ? transfer.getFromEmployee().getId() : null)
                .fromEmployeeName(transfer.getFromEmployee() != null
                        ? transfer.getFromEmployee().getFirstName() + " " + transfer.getFromEmployee().getLastName() : null)
                .toEmployeeId(transfer.getToEmployee() != null ? transfer.getToEmployee().getId() : null)
                .toEmployeeName(transfer.getToEmployee() != null
                        ? transfer.getToEmployee().getFirstName() + " " + transfer.getToEmployee().getLastName() : null)
                .transferDate(transfer.getTransferDate())
                .status(transfer.getStatus())
                .reason(transfer.getReason())
                .createdAt(transfer.getCreatedAt())
                .updatedAt(transfer.getUpdatedAt())
                .build();
    }

    public List<AssetTransferResponseDto> toResponseDtoList(List<AssetTransfer> transfers) {
        return transfers.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}