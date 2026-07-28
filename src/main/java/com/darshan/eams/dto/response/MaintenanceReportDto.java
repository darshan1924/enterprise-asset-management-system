package com.darshan.eams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceReportDto {

    private List<MaintenanceResponseDto> records;

    private long totalTicketCount;

    private BigDecimal totalCost;

    private BigDecimal averageCost;

    private Map<String, Long> countByStatus;

    private Map<String, BigDecimal> costByVendor;
}