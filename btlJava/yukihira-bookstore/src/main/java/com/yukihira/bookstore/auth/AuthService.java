package com.yukihira.bookstore.auth;

import com.yukihira.bookstore.cart.Cart;
import com.yukihira.bookstore.cart.CartRepository;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, CartRepository cartRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegistrationForm form) {
        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException();
        }
        User user = new User(form.getFullName().trim(), email, passwordEncoder.encode(form.getPassword()));
        user.setPhone(blankToNull(form.getPhone()));
        user = userRepository.save(user);
        cartRepository.save(new Cart(user));
        return user;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
