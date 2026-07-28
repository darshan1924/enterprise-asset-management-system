package com.darshan.eams.controller.web;

import com.darshan.eams.dto.response.AssetReportDto;
import com.darshan.eams.dto.response.DepartmentReportDto;
import com.darshan.eams.dto.response.MaintenanceReportDto;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.enums.MaintenanceStatus;
import com.darshan.eams.service.interfaces.AssetCategoryService;
import com.darshan.eams.service.interfaces.ReportService;
import com.darshan.eams.service.interfaces.VendorService;
import com.darshan.eams.util.CsvExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportWebController {

    private final ReportService reportService;
    private final AssetCategoryService categoryService;
    private final VendorService vendorService;

    @GetMapping
    public String index() {
        return "reports/index";
    }

    @GetMapping("/departments")
    public String departmentReport(Model model) {
        model.addAttribute("report", reportService.getDepartmentReport());
        return "reports/department-report";
    }

    @GetMapping("/departments/export")
    @ResponseBody
    public ResponseEntity<byte[]> exportDepartmentReport() {
        List<DepartmentReportDto> report = reportService.getDepartmentReport();

        List<String> headers = List.of("Department Name", "Code", "Employees", "Allocated Assets", "Allocated Asset Value");
        List<List<String>> rows = new ArrayList<>();
        for (DepartmentReportDto row : report) {
            rows.add(List.of(
                    row.getDepartmentName(),
                    row.getDepartmentCode(),
                    String.valueOf(row.getEmployeeCount()),
                    String.valueOf(row.getAllocatedAssetsCount()),
                    row.getAllocatedAssetsValue().toPlainString()
            ));
        }

        return buildCsvResponse(CsvExportUtil.toCsv(headers, rows), "department-report.csv");
    }

    @GetMapping("/assets")
    public String assetReport(@RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) AssetStatus status,
                              @RequestParam(required = false) Long vendorId,
                              Model model) {
        model.addAttribute("report", reportService.getAssetReport(categoryId, status, vendorId));
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("vendorId", vendorId);
        model.addAttribute("categories", categoryService.getAllList());
        model.addAttribute("vendors", vendorService.getAll(PageRequest.of(0, 1000, Sort.by("vendorName"))).getContent());
        model.addAttribute("statuses", AssetStatus.values());
        return "reports/asset-report";
    }

    @GetMapping("/assets/export")
    @ResponseBody
    public ResponseEntity<byte[]> exportAssetReport(@RequestParam(required = false) Long categoryId,
                                                    @RequestParam(required = false) AssetStatus status,
                                                    @RequestParam(required = false) Long vendorId) {
        AssetReportDto report = reportService.getAssetReport(categoryId, status, vendorId);

        List<String> headers = List.of("Asset Code", "Name", "Category", "Vendor", "Status", "Current Holder", "Purchase Cost");
        List<List<String>> rows = new ArrayList<>();
        report.getAssets().forEach(asset -> rows.add(List.of(
                asset.getAssetCode(),
                asset.getAssetName(),
                asset.getCategoryName() != null ? asset.getCategoryName() : "",
                asset.getVendorName() != null ? asset.getVendorName() : "",
                asset.getStatus().name(),
                asset.getCurrentHolderName() != null ? asset.getCurrentHolderName() : "",
                asset.getPurchaseCost().toPlainString()
        )));

        return buildCsvResponse(CsvExportUtil.toCsv(headers, rows), "asset-report.csv");
    }

    @GetMapping("/maintenance")
    public String maintenanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) MaintenanceStatus status,
            Model model) {
        model.addAttribute("report", reportService.getMaintenanceReport(startDate, endDate, status));
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("status", status);
        model.addAttribute("statuses", MaintenanceStatus.values());
        return "reports/maintenance-report";
    }

    @GetMapping("/maintenance/export")
    @ResponseBody
    public ResponseEntity<byte[]> exportMaintenanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) MaintenanceStatus status) {
        MaintenanceReportDto report = reportService.getMaintenanceReport(startDate, endDate, status);

        List<String> headers = List.of("Asset Code", "Issue", "Vendor", "Reported Date", "Completed Date", "Cost", "Status");
        List<List<String>> rows = new ArrayList<>();
        report.getRecords().forEach(m -> rows.add(List.of(
                m.getAssetCode(),
                m.getIssueDescription(),
                m.getVendorName() != null ? m.getVendorName() : "",
                m.getReportedDate() != null ? m.getReportedDate().toString() : "",
                m.getCompletedDate() != null ? m.getCompletedDate().toString() : "",
                m.getMaintenanceCost() != null ? m.getMaintenanceCost().toPlainString() : "",
                m.getStatus().name()
        )));

        return buildCsvResponse(CsvExportUtil.toCsv(headers, rows), "maintenance-report.csv");
    }

    private ResponseEntity<byte[]> buildCsvResponse(String csvContent, String filename) {
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] contentBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        byte[] fullContent = new byte[bom.length + contentBytes.length];
        System.arraycopy(bom, 0, fullContent, 0, bom.length);
        System.arraycopy(contentBytes, 0, fullContent, bom.length, contentBytes.length);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fullContent);
    }
}