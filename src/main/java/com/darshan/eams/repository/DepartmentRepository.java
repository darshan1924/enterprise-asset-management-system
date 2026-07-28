package com.darshan.eams.repository;

import com.darshan.eams.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByDepartmentNameIgnoreCase(String departmentName);

    boolean existsByDepartmentCodeIgnoreCase(String departmentCode);

    Optional<Department> findByDepartmentCodeIgnoreCase(String departmentCode);

    List<Department> findByDeletedFalse();

    Page<Department> findByDeletedFalse(Pageable pageable);

    @Query("""
            SELECT d FROM Department d
            WHERE d.deleted = false
            AND (LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(d.departmentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(d.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Department> searchDepartments(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Department d WHERE d.deleted = false")
    long countActiveDepartments();
}