package com.darshan.eams.service;

import com.darshan.eams.dto.request.AssetAllocationRequestDto;
import com.darshan.eams.dto.request.AssetCategoryRequestDto;
import com.darshan.eams.dto.request.AssetRequestDto;
import com.darshan.eams.dto.request.DepartmentRequestDto;
import com.darshan.eams.dto.request.EmployeeRequestDto;
import com.darshan.eams.dto.request.VendorRequestDto; // Add this import
import com.darshan.eams.entity.Asset;
import com.darshan.eams.enums.AllocationStatus;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.repository.AssetAllocationRepository;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.service.interfaces.AssetAllocationService;
import com.darshan.eams.service.interfaces.AssetCategoryService;
import com.darshan.eams.service.interfaces.AssetService;
import com.darshan.eams.service.interfaces.DepartmentService;
import com.darshan.eams.service.interfaces.EmployeeService;
import com.darshan.eams.service.interfaces.VendorService; // Add this import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Rollback
@WithMockUser(username = "admin", roles = "ADMIN")
class TransactionRollbackTest {

    @Autowired
    private AssetAllocationService allocationService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetCategoryService categoryService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VendorService vendorService; // Inject VendorService

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetAllocationRepository allocationRepository;

    private Long assetId;
    private Long employeeId;

    @BeforeEach
    void setUp() {
        Long categoryId = categoryService.create(buildCategoryDto()).getId();
        Long departmentId = departmentService.create(buildDepartmentDto()).getId();
        Long vendorId = vendorService.create(buildVendorDto()).getId(); // Create Vendor dynamically

        employeeId = employeeService.create(buildEmployeeDto(departmentId)).getId();
        assetId = assetService.create(buildAssetDto(categoryId, vendorId)).getId(); // Pass dynamic vendorId
    }

    @Test
    void allocate_whenEmployeeIdInvalid_rollsBackWithNoAssetStatusChange() {
        AssetAllocationRequestDto requestDto = new AssetAllocationRequestDto();
        requestDto.setAssetId(assetId);
        requestDto.setEmployeeId(999999L);
        requestDto.setAllocationDate(LocalDate.now());

        assertThrows(RuntimeException.class, () -> allocationService.allocate(requestDto));

        Asset asset = assetRepository.findById(assetId).orElseThrow();
        assertEquals(AssetStatus.AVAILABLE, asset.getStatus());

        assertTrue(allocationRepository.findFirstByAssetIdAndStatusOrderByIdDesc(assetId, AllocationStatus.ACTIVE).isEmpty());
    }

    @Test
    void allocate_successThenDoubleAllocate_secondCallRollsBackCleanly() {
        AssetAllocationRequestDto firstRequest = new AssetAllocationRequestDto();
        firstRequest.setAssetId(assetId);
        firstRequest.setEmployeeId(employeeId);
        firstRequest.setAllocationDate(LocalDate.now());
        allocationService.allocate(firstRequest);

        Asset afterFirst = assetRepository.findById(assetId).orElseThrow();
        assertEquals(AssetStatus.ALLOCATED, afterFirst.getStatus());

        AssetAllocationRequestDto secondRequest = new AssetAllocationRequestDto();
        secondRequest.setAssetId(assetId);
        secondRequest.setEmployeeId(employeeId);
        secondRequest.setAllocationDate(LocalDate.now());

        assertThrows(RuntimeException.class, () -> allocationService.allocate(secondRequest));

        Asset afterSecondAttempt = assetRepository.findById(assetId).orElseThrow();
        assertEquals(AssetStatus.ALLOCATED, afterSecondAttempt.getStatus());

        long activeAllocationCount = allocationRepository
                .findFirstByAssetIdAndStatusOrderByIdDesc(assetId, AllocationStatus.ACTIVE)
                .map(a -> 1L)
                .orElse(0L);
        assertEquals(1L, activeAllocationCount);
    }

    private AssetCategoryRequestDto buildCategoryDto() {
        AssetCategoryRequestDto dto = new AssetCategoryRequestDto();
        dto.setCategoryName("Test-Laptops-" + System.nanoTime());
        return dto;
    }

    private DepartmentRequestDto buildDepartmentDto() {
        DepartmentRequestDto dto = new DepartmentRequestDto();
        dto.setDepartmentName("Test-Dept-" + System.nanoTime());
        dto.setDepartmentCode("TD" + (System.nanoTime() % 100000));
        return dto;
    }

    private EmployeeRequestDto buildEmployeeDto(Long departmentId) {
        EmployeeRequestDto dto = new EmployeeRequestDto();
        dto.setEmployeeCode("TEMP-" + System.nanoTime());
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setEmail("test" + System.nanoTime() + "@example.com");
        dto.setDepartmentId(departmentId);
        return dto;
    }

    private VendorRequestDto buildVendorDto() {
        VendorRequestDto dto = new VendorRequestDto();
        dto.setVendorName("Test Vendor " + System.nanoTime());
        dto.setContactPerson("John Doe");
        dto.setEmail("vendor" + System.nanoTime() + "@example.com");
        dto.setPhoneNumber("1234567890");
        return dto;
    }

    private AssetRequestDto buildAssetDto(Long categoryId, Long vendorId) {
        AssetRequestDto dto = new AssetRequestDto();
        dto.setAssetCode("TEST-AST-" + System.nanoTime());
        dto.setAssetName("Test Laptop");
        dto.setPurchaseDate(LocalDate.now().minusMonths(1));
        dto.setPurchaseCost(BigDecimal.valueOf(50000));
        dto.setCategoryId(categoryId);
        dto.setVendorId(vendorId); // Dynamically set valid vendorId
        return dto;
    }
}