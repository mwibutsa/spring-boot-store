package com.mwibutsa.store.config;

import com.mwibutsa.store.entities.Role;
import com.mwibutsa.store.filters.JwtAuthenticationFilter;
import com.mwibutsa.store.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Authorize
        http
                .sessionManagement(
                        c -> c.
                                sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> c.
                        requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/carts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/checkout/webhook").permitAll()
                        .anyRequest().authenticated()
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        c -> {
                            c.authenticationEntryPoint(
                                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                            );
                            c.accessDeniedHandler((request, response, accessDenied) -> {
                                response.setStatus(HttpStatus.FORBIDDEN.value());
                            });
                        });

        return http.build();
    }
}
