package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.common.PageResponse;
import com.darshan.eams.dto.request.AssetTransferRequestDto;
import com.darshan.eams.dto.response.AssetTransferResponseDto;
import com.darshan.eams.service.interfaces.AssetTransferService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class AssetTransferController {

    private final AssetTransferService transferService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AssetTransferResponseDto> initiate(@Valid @RequestBody AssetTransferRequestDto requestDto) {
        AssetTransferResponseDto created = transferService.initiate(requestDto);
        return ApiResponse.success("Asset transfer initiated successfully", created);
    }

    @PatchMapping("/{id}/approve")
    public ApiResponse<AssetTransferResponseDto> approve(@PathVariable Long id) {
        AssetTransferResponseDto updated = transferService.approve(id);
        return ApiResponse.success("Asset transfer approved", updated);
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<AssetTransferResponseDto> complete(@PathVariable Long id) {
        AssetTransferResponseDto updated = transferService.complete(id);
        return ApiResponse.success("Asset transfer completed", updated);
    }

    @PatchMapping("/{id}/reject")
    public ApiResponse<AssetTransferResponseDto> reject(@PathVariable Long id) {
        AssetTransferResponseDto updated = transferService.reject(id);
        return ApiResponse.success("Asset transfer rejected", updated);
    }

    @GetMapping("/{id}")
    public ApiResponse<AssetTransferResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(transferService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<AssetTransferResponseDto>> getAll(@PageableDefault(size = 10, sort = "transferDate") Pageable pageable) {
        Page<AssetTransferResponseDto> page = transferService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/asset/{assetId}")
    public ApiResponse<PageResponse<AssetTransferResponseDto>> getHistoryByAsset(@PathVariable Long assetId, @PageableDefault(size = 10, sort = "transferDate") Pageable pageable) {
        Page<AssetTransferResponseDto> page = transferService.getHistoryByAsset(assetId, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }
}