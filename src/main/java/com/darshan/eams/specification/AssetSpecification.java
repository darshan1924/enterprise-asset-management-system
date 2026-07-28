package com.darshan.eams.specification;

import com.darshan.eams.entity.Asset;
import com.darshan.eams.enums.AssetStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AssetSpecification {

    private AssetSpecification() {
    }

    public static Specification<Asset> withFilters(String keyword, Long categoryId, AssetStatus status, Long vendorId) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            if (keyword != null && !keyword.isBlank()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate nameMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("assetName")), likePattern);
                Predicate codeMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("assetCode")), likePattern);
                Predicate locationMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("location")), likePattern);
                predicates.add(criteriaBuilder.or(nameMatch, codeMatch, locationMatch));
            }

            if (categoryId != null)
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));

            if (status != null)
                predicates.add(criteriaBuilder.equal(root.get("status"), status));

            if (vendorId != null)
                predicates.add(criteriaBuilder.equal(root.get("vendor").get("id"), vendorId));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}