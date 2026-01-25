package com.project6.ecommerce.config;

import com.project6.ecommerce.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // API bez CSRF, widoki z CSRF (domyślnie włączone)
                .authorizeHttpRequests(auth -> auth
                        // dokumentacja api dostępna publicznie
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // strona główna, produkty, koszyk i kasa dostępne dla każdego
                        .requestMatchers(
                                "/",
                                "/product/**",
                                "/product-images/**",
                                "/cart/**",
                                "/checkout/**",
                                "/h2-console/**"
                        ).permitAll()
                        // publiczne endpointy rest api (tylko odczyt)
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/search").permitAll()
                        // logowanie i rejestracja
                        .requestMatchers("/api/v1/auth/**", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        // panel admina tylko dla administratora
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                // logowanie basic auth dla testów w postmanie
                .httpBasic(org.springframework.security.config.Customizer.withDefaults())
                // wymagane dla konsoli h2 (baza danych w pamięci)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .formLogin(form -> form
                        .loginPage("/login")
                        // przekierowanie po zalogowaniu na dodawanie produktu
                        .defaultSuccessUrl("/admin/products/add", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);// <-- TU WCHODZI Twój SERWIS
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
