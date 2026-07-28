package com.darshan.eams.repository;

import com.darshan.eams.entity.MaintenanceRecord;
import com.darshan.eams.enums.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    Page<MaintenanceRecord> findByAssetIdAndDeletedFalse(Long assetId, Pageable pageable);

    Page<MaintenanceRecord> findByVendorIdAndDeletedFalse(Long vendorId, Pageable pageable);

    Page<MaintenanceRecord> findByStatusAndDeletedFalse(MaintenanceStatus status, Pageable pageable);

    @Query("""
            SELECT m FROM MaintenanceRecord m
            WHERE m.deleted = false
            AND m.status IN ('PENDING', 'IN_PROGRESS')
            AND m.expectedCompletionDate IS NOT NULL
            AND m.expectedCompletionDate BETWEEN :startDate AND :endDate
            """)
    List<MaintenanceRecord> findUpcomingDueMaintenance(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT COALESCE(SUM(m.maintenanceCost), 0) FROM MaintenanceRecord m
            WHERE m.deleted = false AND m.status = 'COMPLETED'
            """)
    BigDecimal sumTotalMaintenanceCost();

    @Query("""
            SELECT m.asset.assetName AS assetName, COALESCE(SUM(m.maintenanceCost), 0) AS totalCost
            FROM MaintenanceRecord m
            WHERE m.deleted = false AND m.status = 'COMPLETED'
            GROUP BY m.asset.assetName
            """)
    List<Object[]> sumMaintenanceCostGroupedByAsset();

    long countByStatusAndDeletedFalse(MaintenanceStatus status);

    @Query("""
            SELECT m FROM MaintenanceRecord m
            WHERE m.deleted = false
            AND (:startDate IS NULL OR m.reportedDate >= :startDate)
            AND (:endDate IS NULL OR m.reportedDate <= :endDate)
            AND (:status IS NULL OR m.status = :status)
            ORDER BY m.reportedDate DESC
            """)
    List<MaintenanceRecord> findForReport(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") MaintenanceStatus status);
}