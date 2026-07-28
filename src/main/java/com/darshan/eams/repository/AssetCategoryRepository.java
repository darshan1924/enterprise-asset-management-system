package com.darshan.eams.repository;

import com.darshan.eams.entity.AssetCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    List<AssetCategory> findByDeletedFalse();

    Page<AssetCategory> findByDeletedFalse(Pageable pageable);

    List<AssetCategory> findByCategoryNameContainingIgnoreCaseAndDeletedFalse(String keyword);
}