package com.darshan.eams.config;

import com.darshan.eams.entity.Role;
import com.darshan.eams.entity.User;
import com.darshan.eams.enums.UserRole;
import com.darshan.eams.repository.RoleRepository;
import com.darshan.eams.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles(){
        for (UserRole roleName: UserRole.values()){
            if (!roleRepository.existsByRoleName(roleName)){
                Role role = new Role();
                role.setRoleName(roleName);
                role.setDescription("Default " + roleName.name() + " role");
                roleRepository.save(role);
                log.info("Seeded role: {}", roleName);
            }
        }
    }

    private void seedAdminUser(){
        if (userRepository.existsByUsernameIgnoreCase("admin"))
            return;

        Role adminRole = roleRepository.findByRoleName(UserRole.ADMIN)
                .orElseThrow(()-> new IllegalStateException("ADMIN role must be seeded before the admin user"));

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setEmail("admin@eams.local");
        admin.setEnabled(true);
        admin.setAccountNonLocked(true);
        admin.setRole(adminRole);

        userRepository.save(admin);
        log.info("Seeded default admin user (username: admin) — CHANGE THIS PASSWORD IMMEDIATELY");
    }
}
