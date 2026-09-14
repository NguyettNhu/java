package com.yukihira.bookstore.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import static org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.pathPattern;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, com.yukihira.bookstore.user.UserRepository users) throws Exception {
        var apiRequest = pathPattern("/api/**");
        var webAccessDeniedHandler = new AccessDeniedHandlerImpl();
        webAccessDeniedHandler.setErrorPage("/access-denied");
        AccessDeniedHandler accessDeniedHandler = (request, response, exception) -> {
            if (!apiRequest.matches(request)) {
                webAccessDeniedHandler.handle(request, response, exception);
                return;
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"title\":\"Forbidden\",\"status\":403,"
                    + "\"detail\":\"Yêu cầu bị từ chối hoặc thiếu CSRF token.\"}");
        };
        http
                .addFilterAfter(new AccountStatusFilter(users),
                        org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/books/**", "/api/v1/**", "/register", "/login",
                                "/access-denied", "/css/**", "/js/**", "/images/**", "/error/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(new RoleLoginSuccessHandler())
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(accessDeniedHandler));
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
