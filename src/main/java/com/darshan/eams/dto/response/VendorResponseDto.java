package com.darshan.eams.dto.response;

import com.darshan.eams.enums.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorResponseDto {

    private Long id;

    private String vendorName;

    private String contactPerson;

    private String email;

    private String phoneNumber;

    private String address;

    private BigDecimal rating;

    private VendorStatus status;

    private long suppliedAssetsCount;

    private long maintenanceJobsCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}