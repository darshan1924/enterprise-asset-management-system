package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.common.PageResponse;
import com.darshan.eams.dto.request.VendorRequestDto;
import com.darshan.eams.dto.response.VendorResponseDto;
import com.darshan.eams.enums.VendorStatus;
import com.darshan.eams.service.interfaces.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<VendorResponseDto> create(@Valid @RequestBody VendorRequestDto requestDto) {
        VendorResponseDto created = vendorService.create(requestDto);
        return ApiResponse.success("Vendor created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<VendorResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody VendorRequestDto requestDto) {
        VendorResponseDto updated = vendorService.update(id, requestDto);
        return ApiResponse.success("Vendor updated successfully", updated);
    }

    @GetMapping("/{id}")
    public ApiResponse<VendorResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(vendorService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<VendorResponseDto>> getAll(@PageableDefault(size = 10, sort = "vendorName") Pageable pageable) {
        Page<VendorResponseDto> page = vendorService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<PageResponse<VendorResponseDto>> getByStatus(@PathVariable VendorStatus status, @PageableDefault(size = 10, sort = "vendorName") Pageable pageable) {
        Page<VendorResponseDto> page = vendorService.getByStatus(status, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<VendorResponseDto>> search(@RequestParam String keyword, @PageableDefault(size = 10, sort = "vendorName") Pageable pageable) {
        Page<VendorResponseDto> page = vendorService.search(keyword, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        vendorService.delete(id);
        return ApiResponse.success("Vendor deleted successfully", null);
    }
}