package com.darshan.eams.repository;

import com.darshan.eams.entity.Employee;
import com.darshan.eams.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Employee> findByEmployeeCodeIgnoreCase(String employeeCode);

    List<Employee> findByDeletedFalse();

    Page<Employee> findByDeletedFalse(Pageable pageable);

    Page<Employee> findByDepartmentIdAndDeletedFalse(Long departmentId, Pageable pageable);

    Page<Employee> findByStatusAndDeletedFalse(EmployeeStatus status, Pageable pageable);

    long countByDepartmentIdAndDeletedFalse(Long departmentId);

    @Query("""
            SELECT e FROM Employee e
            JOIN FETCH e.department d
            WHERE e.deleted = false
            AND (LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Employee> searchEmployees(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = 'ACTIVE' AND e.deleted = false")
    long countActiveEmployees();
}