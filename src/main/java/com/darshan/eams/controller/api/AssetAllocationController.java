package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.common.PageResponse;
import com.darshan.eams.dto.request.AssetAllocationRequestDto;
import com.darshan.eams.dto.response.AssetAllocationResponseDto;
import com.darshan.eams.service.interfaces.AssetAllocationService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AssetAllocationController {

    private final AssetAllocationService allocationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AssetAllocationResponseDto> allocate(@Valid @RequestBody AssetAllocationRequestDto requestDto) {
        AssetAllocationResponseDto created = allocationService.allocate(requestDto);
        return ApiResponse.success("Asset allocated successfully", created);
    }

    @PatchMapping("/{id}/return")
    public ApiResponse<AssetAllocationResponseDto> returnAsset(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        AssetAllocationResponseDto updated = allocationService.returnAsset(id, remarks);
        return ApiResponse.success("Asset returned successfully", updated);
    }

    @GetMapping("/{id}")
    public ApiResponse<AssetAllocationResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(allocationService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<AssetAllocationResponseDto>> getAll(@PageableDefault(size = 10, sort = "allocationDate") Pageable pageable) {
        Page<AssetAllocationResponseDto> page = allocationService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<PageResponse<AssetAllocationResponseDto>> getByEmployee(@PathVariable Long employeeId, @PageableDefault(size = 10, sort = "allocationDate") Pageable pageable) {
        Page<AssetAllocationResponseDto> page = allocationService.getByEmployee(employeeId, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/asset/{assetId}")
    public ApiResponse<PageResponse<AssetAllocationResponseDto>> getByAsset(@PathVariable Long assetId, @PageableDefault(size = 10, sort = "allocationDate") Pageable pageable) {
        Page<AssetAllocationResponseDto> page = allocationService.getByAsset(assetId, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }
}