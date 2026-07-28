package com.darshan.eams.repository;

import com.darshan.eams.entity.AssetAllocation;
import com.darshan.eams.enums.AllocationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetAllocationRepository extends JpaRepository<AssetAllocation, Long> {

    // Safely retrieves the latest active allocation in case duplicates exist
    Optional<AssetAllocation> findFirstByAssetIdAndStatusOrderByIdDesc(Long assetId, AllocationStatus status);

    Page<AssetAllocation> findByEmployeeIdAndDeletedFalse(Long employeeId, Pageable pageable);

    Page<AssetAllocation> findByAssetIdAndStatusAndDeletedFalse(Long assetId, AllocationStatus status, Pageable pageable);

    Page<AssetAllocation> findByAssetIdAndDeletedFalse(Long assetId, Pageable pageable);

    Page<AssetAllocation> findByStatusAndDeletedFalse(AllocationStatus status, Pageable pageable);

    boolean existsByAssetIdAndStatus(Long assetId, AllocationStatus status);

    @Query("""
            SELECT al FROM AssetAllocation al
            JOIN FETCH al.asset a
            JOIN FETCH al.employee e
            WHERE al.status = 'ACTIVE'
            AND al.expectedReturnDate IS NOT NULL
            AND al.expectedReturnDate < :today
            """)
    List<AssetAllocation> findOverdueAllocations(@Param("today") LocalDate today);

    @Query("SELECT COUNT(al) FROM AssetAllocation al WHERE al.status = 'ACTIVE'")
    long countActiveAllocations();
}