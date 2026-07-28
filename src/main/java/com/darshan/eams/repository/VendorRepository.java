package com.darshan.eams.repository;

import com.darshan.eams.entity.Vendor;
import com.darshan.eams.enums.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    boolean existsByVendorNameIgnoreCase(String vendorName);

    boolean existsByEmailIgnoreCase(String email);

    List<Vendor> findByDeletedFalse();

    Page<Vendor> findByDeletedFalse(Pageable pageable);

    Page<Vendor> findByStatusAndDeletedFalse(VendorStatus status, Pageable pageable);

    @Query("""
            SELECT v FROM Vendor v
            WHERE v.deleted = false
            AND (LOWER(v.vendorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(v.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(v.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Vendor> searchVendors(@Param("keyword") String keyword, Pageable pageable);
}