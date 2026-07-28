package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.common.PageResponse;
import com.darshan.eams.dto.request.MaintenanceRequestDto;
import com.darshan.eams.dto.response.MaintenanceResponseDto;
import com.darshan.eams.service.interfaces.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MaintenanceResponseDto> create(@Valid @RequestBody MaintenanceRequestDto requestDto) {
        MaintenanceResponseDto created = maintenanceService.create(requestDto);
        return ApiResponse.success("Maintenance ticket created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<MaintenanceResponseDto> update(@PathVariable Long id, @Valid @RequestBody MaintenanceRequestDto requestDto) {
        MaintenanceResponseDto updated = maintenanceService.update(id, requestDto);
        return ApiResponse.success("Maintenance ticket updated successfully", updated);
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<MaintenanceResponseDto> complete(@PathVariable Long id) {
        MaintenanceResponseDto updated = maintenanceService.complete(id);
        return ApiResponse.success("Maintenance ticket marked as completed", updated);
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<MaintenanceResponseDto> cancel(@PathVariable Long id) {
        MaintenanceResponseDto updated = maintenanceService.cancel(id);
        return ApiResponse.success("Maintenance ticket cancelled", updated);
    }

    @GetMapping("/{id}")
    public ApiResponse<MaintenanceResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(maintenanceService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<MaintenanceResponseDto>> getAll(@PageableDefault(size = 10, sort = "reportedDate") Pageable pageable) {
        Page<MaintenanceResponseDto> page = maintenanceService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/asset/{assetId}")
    public ApiResponse<PageResponse<MaintenanceResponseDto>> getByAsset(@PathVariable Long assetId, @PageableDefault(size = 10, sort = "reportedDate") Pageable pageable) {
        Page<MaintenanceResponseDto> page = maintenanceService.getByAsset(assetId, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }
}