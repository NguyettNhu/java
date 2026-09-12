package com.yukihira.bookstore.security;

import com.yukihira.bookstore.user.UserRepository;
import com.yukihira.bookstore.user.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/** Recheck customers so locking an account also ends an already-open session. */
public class AccountStatusFilter extends OncePerRequestFilter {
    private final UserRepository users;
    public AccountStatusFilter(UserRepository users) { this.users = users; }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)
                && auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_CUSTOMER"))
                && users.findByEmailIgnoreCase(auth.getName()).filter(user -> user.getStatus() == UserStatus.LOCKED).isPresent()) {
            var session = request.getSession(false);
            if (session != null) session.invalidate();
            SecurityContextHolder.clearContext();
            response.sendRedirect(request.getContextPath() + "/login?locked");
            return;
        }
        chain.doFilter(request, response);
    }
}
