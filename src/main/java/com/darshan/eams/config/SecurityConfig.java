package com.darshan.eams.config;

import com.darshan.eams.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/error/**").permitAll()

                        // ADMIN API
                        .requestMatchers("/departments/**", "/api/departments/**").hasRole("ADMIN")
                        .requestMatchers("/employees/**", "/api/employees/**").hasRole("ADMIN")
                        .requestMatchers("/audit/**", "/api/audit/**").hasRole("ADMIN")
                        .requestMatchers("/settings/**").hasRole("ADMIN")
                        .requestMatchers("/users/**", "/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/roles/**", "/api/roles/**").hasRole("ADMIN")

                        // EMPLOYEE GET API
                        .requestMatchers(HttpMethod.GET,
                                "/vendors/**", "/categories/**", "/assets/**",
                                "/allocations/**", "/maintenance/**", "/transfers/**",
                                "/api/vendors/**", "/api/categories/**", "/api/assets/**",
                                "/api/allocations/**", "/api/maintenance/**", "/api/transfers/**",
                                "/dashboard", "/api/dashboard/**").authenticated()
                        .requestMatchers(
                                "/vendors/**", "/categories/**", "/assets/**",
                                "/allocations/**", "/maintenance/**", "/transfers/**",
                                "/api/vendors/**", "/api/categories/**", "/api/assets/**",
                                "/api/allocations/**", "/api/maintenance/**", "/api/transfers/**"
                        ).hasAnyRole("ADMIN", "ASSET_MANAGER")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403"))
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}