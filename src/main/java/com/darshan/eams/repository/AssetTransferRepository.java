package com.darshan.eams.repository;

import com.darshan.eams.entity.AssetTransfer;
import com.darshan.eams.enums.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetTransferRepository extends JpaRepository<AssetTransfer, Long> {

    Page<AssetTransfer> findByStatusAndDeletedFalse(TransferStatus status, Pageable pageable);

    @Query("""
            SELECT t FROM AssetTransfer t
            JOIN FETCH t.asset a
            WHERE t.asset.id = :assetId AND t.deleted = false
            ORDER BY t.transferDate DESC
            """)
    Page<AssetTransfer> findTransferHistoryByAsset(@Param("assetId") Long assetId, Pageable pageable);

    Page<AssetTransfer> findByFromDepartmentIdOrToDepartmentIdAndDeletedFalse(Long fromDepartmentId, Long toDepartmentId, Pageable pageable);

    long countByStatusAndDeletedFalse(TransferStatus status);

}