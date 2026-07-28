package com.darshan.eams.entity;

import com.darshan.eams.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "roles",
        uniqueConstraints = @UniqueConstraint(name = "uk_role_name", columnNames = "role_name"))
public class Role extends BaseEntity {

    @NotNull(message = "Role name is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false, length = 30, unique = true)
    private UserRole roleName;

    @Column(name = "description", length = 150)
    private String description;
}