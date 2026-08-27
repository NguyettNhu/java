package com.yukihira.bookstore.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewModelAdvice {

    @ModelAttribute("signedIn")
    public boolean signedIn(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @ModelAttribute("currentEmail")
    public String currentEmail(Authentication authentication) {
        return signedIn(authentication) ? authentication.getName() : null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        return signedIn(authentication) && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
