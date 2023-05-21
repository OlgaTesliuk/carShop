package com.example.carshop.security;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@AllArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private JwtFilter jwtFilter;

    @SneakyThrows
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/clients/save", "/clients/login", "/registration", "/car/**", "/pay/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/clients/**", "/car/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/clients/**", "/car/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                .requestMatchers(HttpMethod.POST, "/registration/CreateManager/**", "/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/**").hasAuthority("ROLE_ADMIN")
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList("GET", HttpMethod.POST.name()));
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
