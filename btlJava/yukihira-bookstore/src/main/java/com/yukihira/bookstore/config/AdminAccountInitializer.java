package com.yukihira.bookstore.config;

import com.yukihira.bookstore.user.Role;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                   @Value("${ADMIN_EMAIL:}") String adminEmail,
                                   @Value("${ADMIN_PASSWORD:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()
                || userRepository.existsByEmailIgnoreCase(adminEmail)) {
            return;
        }
        User admin = new User("Yukihira Admin", adminEmail.trim(), passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }
}
