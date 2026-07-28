package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.DepartmentRequestDto;
import com.darshan.eams.dto.response.DepartmentResponseDto;
import com.darshan.eams.entity.Department;
import com.darshan.eams.exception.BusinessRuleViolationException;
import com.darshan.eams.exception.DuplicateResourceException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.DepartmentMapper;
import com.darshan.eams.repository.DepartmentRepository;
import com.darshan.eams.repository.EmployeeRepository;
import com.darshan.eams.service.interfaces.AuditService;
import com.darshan.eams.service.interfaces.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;

    private final EmployeeRepository employeeRepository;

    private final DepartmentMapper departmentMapper;

    private final AuditService auditService;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponseDto create(DepartmentRequestDto requestDto) {
        if (departmentRepository.existsByDepartmentNameIgnoreCase(requestDto.getDepartmentName()))
            throw new DuplicateResourceException("Department", "name", requestDto.getDepartmentName());

        if (departmentRepository.existsByDepartmentCodeIgnoreCase(requestDto.getDepartmentCode()))
            throw new DuplicateResourceException("Department", "code", requestDto.getDepartmentCode());

        Department department = departmentMapper.toEntity(requestDto);
        Department saved = departmentRepository.save(department);
        auditService.log("CREATE", "Department", saved.getId(), "Created department: " + saved.getDepartmentName());
        log.info("Department created: id={}, code={}", saved.getId(), saved.getDepartmentCode());
        return departmentMapper.toResponseDto(saved, 0L);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponseDto update(Long id, DepartmentRequestDto requestDto) {
        Department department = findActiveDepartmentOrThrow(id);

        if (!department.getDepartmentName().equalsIgnoreCase(requestDto.getDepartmentName())
                && departmentRepository.existsByDepartmentNameIgnoreCase(requestDto.getDepartmentName())) {
            throw new DuplicateResourceException("Department", "name", requestDto.getDepartmentName());
        }
        if (!department.getDepartmentCode().equalsIgnoreCase(requestDto.getDepartmentCode())
                && departmentRepository.existsByDepartmentCodeIgnoreCase(requestDto.getDepartmentCode())) {
            throw new DuplicateResourceException("Department", "code", requestDto.getDepartmentCode());
        }

        departmentMapper.updateEntity(department, requestDto);
        Department updated = departmentRepository.save(department);
        auditService.log("UPDATE", "Department", updated.getId(), "Updated department: " + updated.getDepartmentName());

        long employeeCount = employeeRepository.countByDepartmentIdAndDeletedFalse(updated.getId());
        log.info("Department updated: id={}", updated.getId());
        return departmentMapper.toResponseDto(updated, employeeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDto getById(Long id) {
        Department department = findActiveDepartmentOrThrow(id);
        long employeeCount = employeeRepository.countByDepartmentIdAndDeletedFalse(id);
        return departmentMapper.toResponseDto(department, employeeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponseDto> getAll(Pageable pageable) {
        Page<Department> page = departmentRepository.findByDeletedFalse(pageable);
        return page.map(this::mapWithEmployeeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponseDto> search(String keyword, Pageable pageable) {
        Page<Department> page = departmentRepository.searchDepartments(keyword, pageable);
        return page.map(this::mapWithEmployeeCount);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Department department = findActiveDepartmentOrThrow(id);

        long employeeCount = employeeRepository.countByDepartmentIdAndDeletedFalse(id);
        if (employeeCount > 0) {
            throw new BusinessRuleViolationException(
                    "Cannot delete department '" + department.getDepartmentName()
                            + "' because it still has " + employeeCount + " active employee(s)");
        }

        department.setDeleted(true);
        departmentRepository.save(department);
        auditService.log("DELETE", "Department", id, "Deleted department: " + department.getDepartmentName());
        log.info("Department soft-deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveDepartments() {
        return departmentRepository.countActiveDepartments();
    }

    private Department findActiveDepartmentOrThrow(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        if (department.isDeleted())
            throw new ResourceNotFoundException("Department", "id", id);
        return department;
    }

    private DepartmentResponseDto mapWithEmployeeCount(Department department) {
        long employeeCount = employeeRepository.countByDepartmentIdAndDeletedFalse(department.getId());
        return departmentMapper.toResponseDto(department, employeeCount);
    }
}