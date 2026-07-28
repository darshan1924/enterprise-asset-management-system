package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.EmployeeRequestDto;
import com.darshan.eams.dto.response.EmployeeResponseDto;
import com.darshan.eams.entity.Department;
import com.darshan.eams.entity.Employee;
import com.darshan.eams.enums.AllocationStatus;
import com.darshan.eams.enums.EmployeeStatus;
import com.darshan.eams.exception.BusinessRuleViolationException;
import com.darshan.eams.exception.DuplicateResourceException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.EmployeeMapper;
import com.darshan.eams.repository.AssetAllocationRepository;
import com.darshan.eams.repository.DepartmentRepository;
import com.darshan.eams.repository.EmployeeRepository;
import com.darshan.eams.service.interfaces.AuditService;
import com.darshan.eams.service.interfaces.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

    private final AssetAllocationRepository assetAllocationRepository;

    private final EmployeeMapper employeeMapper;

    private final AuditService auditService;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponseDto create(EmployeeRequestDto requestDto) {
        if (employeeRepository.existsByEmployeeCodeIgnoreCase(requestDto.getEmployeeCode()))
            throw new DuplicateResourceException("Employee", "code", requestDto.getEmployeeCode());

        if (employeeRepository.existsByEmailIgnoreCase(requestDto.getEmail()))
            throw new DuplicateResourceException("Employee", "email", requestDto.getEmail());

        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", requestDto.getDepartmentId()));

        Employee employee = employeeMapper.toEntity(requestDto);
        employee.setDepartment(department);
        employee.setStatus(EmployeeStatus.ACTIVE);

        Employee saved = employeeRepository.save(employee);
        auditService.log("CREATE", "Employee", saved.getId(),
                "Created employee: " + saved.getFirstName() + " " + saved.getLastName());
        log.info("Employee created: id={}, code={}", saved.getId(), saved.getEmployeeCode());
        return employeeMapper.toResponseDto(saved, 0L);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponseDto update(Long id, EmployeeRequestDto requestDto) {
        Employee employee = findActiveEmployeeOrThrow(id);

        if (!employee.getEmployeeCode().equalsIgnoreCase(requestDto.getEmployeeCode())
                && employeeRepository.existsByEmployeeCodeIgnoreCase(requestDto.getEmployeeCode())) {
            throw new DuplicateResourceException("Employee", "code", requestDto.getEmployeeCode());
        }
        if (!employee.getEmail().equalsIgnoreCase(requestDto.getEmail())
                && employeeRepository.existsByEmailIgnoreCase(requestDto.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", requestDto.getEmail());
        }

        if (!employee.getDepartment().getId().equals(requestDto.getDepartmentId())) {
            Department newDepartment = departmentRepository.findById(requestDto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", requestDto.getDepartmentId()));
            employee.setDepartment(newDepartment);
        }

        employeeMapper.updateEntity(employee, requestDto);
        Employee updated = employeeRepository.save(employee);
        auditService.log("UPDATE", "Employee", updated.getId(),
                "Updated employee: " + updated.getFirstName() + " " + updated.getLastName());
        long allocatedCount = countActiveAllocations(updated.getId());
        log.info("Employee updated: id={}", updated.getId());
        return employeeMapper.toResponseDto(updated, allocatedCount);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getById(Long id) {
        Employee employee = findActiveEmployeeOrThrow(id);
        long allocatedCount = countActiveAllocations(id);
        return employeeMapper.toResponseDto(employee, allocatedCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> getAll(Pageable pageable) {
        return employeeRepository.findByDeletedFalse(pageable).map(this::mapWithAllocationCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> getByDepartment(Long departmentId, Pageable pageable) {
        return employeeRepository.findByDepartmentIdAndDeletedFalse(departmentId, pageable)
                .map(this::mapWithAllocationCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> getByStatus(EmployeeStatus status, Pageable pageable) {
        return employeeRepository.findByStatusAndDeletedFalse(status, pageable)
                .map(this::mapWithAllocationCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> search(String keyword, Pageable pageable) {
        return employeeRepository.searchEmployees(keyword, pageable).map(this::mapWithAllocationCount);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Employee employee = findActiveEmployeeOrThrow(id);

        long allocatedCount = countActiveAllocations(id);
        if (allocatedCount > 0) {
            throw new BusinessRuleViolationException(
                    "Cannot delete employee '" + employee.getFirstName() + " " + employee.getFirstName()
                            + "' because they currently hold " + allocatedCount + " allocated asset(s)");
        }

        employee.setDeleted(true);
        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
        auditService.log("DELETE", "Employee", id,
                "Deleted employee: " + employee.getFirstName() + " " + employee.getLastName());
        log.info("Employee soft-deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveEmployees() {
        return employeeRepository.countActiveEmployees();
    }

    private Employee findActiveEmployeeOrThrow(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        if (employee.isDeleted())
            throw new ResourceNotFoundException("Employee", "id", id);
        return employee;
    }

    private long countActiveAllocations(Long employeeId) {
        return assetAllocationRepository.findByEmployeeIdAndDeletedFalse(
                        employeeId, org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .filter(a -> a.getStatus() == AllocationStatus.ACTIVE)
                .count();
    }

    private EmployeeResponseDto mapWithAllocationCount(Employee employee) {
        long allocatedCount = countActiveAllocations(employee.getId());
        return employeeMapper.toResponseDto(employee, allocatedCount);
    }
}