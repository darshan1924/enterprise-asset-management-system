package com.darshan.eams.repository;

import com.darshan.eams.entity.Asset;
import com.darshan.eams.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    boolean existsByAssetCodeIgnoreCase(String assetCode);

    Optional<Asset> findByAssetCodeIgnoreCase(String assetCode);

    Page<Asset> findByDeletedFalse(Pageable pageable);

    Page<Asset> findByStatusAndDeletedFalse(AssetStatus status, Pageable pageable);

    Page<Asset> findByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);

    Page<Asset> findByVendorIdAndDeletedFalse(Long vendorId, Pageable pageable);

    @Query("""
            SELECT a FROM Asset a
            JOIN FETCH a.category c
            LEFT JOIN FETCH a.vendor v
            WHERE a.deleted = false
            AND (LOWER(a.assetName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(a.assetCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(a.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Asset> searchAssets(@Param("keyword") String keyword, Pageable pageable);

    long countByStatusAndDeletedFalse(AssetStatus status);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.deleted = false")
    long countTotalAssets();

    @Query("""
            SELECT a FROM Asset a
            WHERE a.deleted = false
            AND a.warrantyExpiryDate IS NOT NULL
            AND a.warrantyExpiryDate BETWEEN :startDate AND :endDate
            """)
    List<Asset> findAssetsWithWarrantyExpiringBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT a.category.categoryName AS categoryName, COUNT(a) AS total
            FROM Asset a
            WHERE a.deleted = false
            GROUP BY a.category.categoryName
            """)
    List<Object[]> countAssetsGroupedByCategory();

    @Query("""
            SELECT a.status AS status, COUNT(a) AS total
            FROM Asset a
            WHERE a.deleted = false
            GROUP BY a.status
            """)
    List<Object[]> countAssetsGroupedByStatus();

    @Query("""
            SELECT d.departmentName AS departmentName, COUNT(al) AS total
            FROM AssetAllocation al
            JOIN al.employee e
            JOIN e.department d
            WHERE al.status = 'ACTIVE' AND al.deleted = false
            GROUP BY d.departmentName
            """)
    List<Object[]> countAllocatedAssetsGroupedByDepartment();

    @Query("""
            SELECT d.id AS departmentId, COALESCE(SUM(a.purchaseCost), 0) AS totalValue
            FROM AssetAllocation al
            JOIN al.asset a
            JOIN al.employee e
            JOIN e.department d
            WHERE al.status = 'ACTIVE' AND al.deleted = false AND a.deleted = false
            GROUP BY d.id
            """)
    List<Object[]> sumAllocatedAssetValueGroupedByDepartment();

    @Query("""
            SELECT v.vendorName AS vendorName, COUNT(a) AS total
            FROM Asset a
            JOIN a.vendor v
            WHERE a.deleted = false
            GROUP BY v.vendorName
            """)
    List<Object[]> countAssetsGroupedByVendor();

    @Query("SELECT COALESCE(SUM(a.purchaseCost), 0) FROM Asset a WHERE a.deleted = false AND a.status != 'RETIRED'")
    java.math.BigDecimal sumTotalPurchaseCost();

}