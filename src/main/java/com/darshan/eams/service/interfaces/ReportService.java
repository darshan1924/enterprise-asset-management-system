package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.response.AssetReportDto;
import com.darshan.eams.dto.response.DepartmentReportDto;
import com.darshan.eams.dto.response.MaintenanceReportDto;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.enums.MaintenanceStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<DepartmentReportDto> getDepartmentReport();

    AssetReportDto getAssetReport(Long categoryId, AssetStatus status, Long vendorId);

    MaintenanceReportDto getMaintenanceReport(LocalDate startDate, LocalDate endDate, MaintenanceStatus status);
}
