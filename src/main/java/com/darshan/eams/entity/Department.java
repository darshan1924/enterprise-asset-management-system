package com.darshan.eams.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_department_name", columnNames = "department_name"),
                @UniqueConstraint(name = "uk_department_code", columnNames = "department_code")})
public class Department extends BaseEntity {

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    @NotBlank(message = "Department code is required")
    @Size(max = 20, message = "Department code must not exceed 20 characters")
    @Column(name = "department_code", nullable = false, length = 20)
    private String departmentCode;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Column(name = "location", length = 100)
    private String location;

    @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Employee> employees = new ArrayList<>();
}