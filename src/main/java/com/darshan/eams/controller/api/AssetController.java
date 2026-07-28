package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.common.PageResponse;
import com.darshan.eams.dto.request.AssetRequestDto;
import com.darshan.eams.dto.response.AssetResponseDto;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.service.interfaces.AssetService;
import com.darshan.eams.validation.group.OnCreate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AssetResponseDto> create(@Validated(OnCreate.class) @RequestBody AssetRequestDto requestDto) {
        AssetResponseDto created = assetService.create(requestDto);
        return ApiResponse.success("Asset created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<AssetResponseDto> update(@PathVariable Long id, @Valid @RequestBody AssetRequestDto requestDto) {
        AssetResponseDto updated = assetService.update(id, requestDto);
        return ApiResponse.success("Asset updated successfully", updated);
    }

    @GetMapping("/{id}")
    public ApiResponse<AssetResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(assetService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<AssetResponseDto>> getAll(@PageableDefault(size = 10, sort = "assetName") Pageable pageable) {
        Page<AssetResponseDto> page = assetService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/filter")
    public ApiResponse<PageResponse<AssetResponseDto>> filter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) Long vendorId,
            @PageableDefault(size = 10, sort = "assetName") Pageable pageable) {
        Page<AssetResponseDto> page = assetService.filter(keyword, categoryId, status, vendorId, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @PatchMapping("/{id}/retire")
    public ApiResponse<Void> retire(@PathVariable Long id) {
        assetService.retire(id);
        return ApiResponse.success("Asset retired successfully", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ApiResponse.success("Asset deleted successfully", null);
    }
}