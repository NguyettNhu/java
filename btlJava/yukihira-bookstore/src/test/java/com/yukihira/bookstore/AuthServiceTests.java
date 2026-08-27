package com.yukihira.bookstore;

import com.yukihira.bookstore.auth.AuthService;
import com.yukihira.bookstore.auth.RegistrationForm;
import com.yukihira.bookstore.cart.CartRepository;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTests {

    @Autowired AuthService authService;
    @Autowired UserRepository userRepository;
    @Autowired CartRepository cartRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void registrationHashesPasswordAndCreatesCart() {
        RegistrationForm form = new RegistrationForm();
        form.setFullName("Trần Minh");
        form.setEmail("MINH@example.com");
        form.setPhone("0901234567");
        form.setPassword("securePass123");
        form.setConfirmPassword("securePass123");

        User user = authService.register(form);

        assertThat(user.getEmail()).isEqualTo("minh@example.com");
        assertThat(user.getPasswordHash()).isNotEqualTo("securePass123");
        assertThat(passwordEncoder.matches("securePass123", user.getPasswordHash())).isTrue();
        assertThat(cartRepository.findByUserId(user.getId())).isPresent();
        assertThat(userRepository.existsByEmailIgnoreCase("MINH@example.com")).isTrue();
    }
}
