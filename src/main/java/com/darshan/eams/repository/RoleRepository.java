package com.darshan.eams.repository;

import com.darshan.eams.entity.Role;
import com.darshan.eams.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(UserRole roleName);

    boolean existsByRoleName(UserRole roleName);
}