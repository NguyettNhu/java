package com.yukihira.bookstore.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileForm profile(String email) {
        User user = requireByEmail(email);
        ProfileForm form = new ProfileForm();
        form.setFullName(user.getFullName());
        form.setPhone(user.getPhone());
        form.setAddress(user.getAddress());
        return form;
    }

    @Transactional
    public void updateProfile(String email, ProfileForm form) {
        User user = requireByEmail(email);
        user.setFullName(form.getFullName().trim());
        user.setPhone(blankToNull(form.getPhone()));
        user.setAddress(blankToNull(form.getAddress()));
    }

    @Transactional(readOnly = true)
    public Page<UserView> searchCustomers(UserSearchQuery query, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return userRepository.findAll(UserSpecifications.customers(query), pageable).map(this::toView);
    }

    @Transactional
    public void updateCustomerStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        if (user.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Chỉ được cập nhật trạng thái tài khoản khách hàng");
        }
        user.setStatus(status);
    }

    private User requireByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(NoSuchElementException::new);
    }

    private UserView toView(User user) {
        return new UserView(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getAddress(),
                user.getStatus(), user.getCreatedAt());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
