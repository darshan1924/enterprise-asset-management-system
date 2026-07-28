package com.darshan.eams.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentReportDto {

    private Long departmentId;

    private String departmentName;

    private String departmentCode;

    private long employeeCount;

    private long allocatedAssetsCount;

    private BigDecimal allocatedAssetsValue;
}
