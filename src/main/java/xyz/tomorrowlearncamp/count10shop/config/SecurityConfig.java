package xyz.tomorrowlearncamp.count10shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.util.JwtUtil;
import xyz.tomorrowlearncamp.count10shop.domain.user.enums.UserRole;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    // private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter(), SecurityContextHolderAwareRequestFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(request -> request.getRequestURI().startsWith("/auth")).permitAll()
                        .requestMatchers("/test").hasAuthority(UserRole.Authority.ADMIN)
                        .requestMatchers("/open").permitAll()
                        .requestMatchers("/api/v1/coupons").permitAll()
                        .requestMatchers("/api/v1/**").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }
}
