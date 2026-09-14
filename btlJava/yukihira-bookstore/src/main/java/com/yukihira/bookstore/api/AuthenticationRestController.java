package com.yukihira.bookstore.api;

import com.yukihira.bookstore.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthenticationRestController(AuthenticationManager authenticationManager,
                                        UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @GetMapping("/csrf")
    public ResponseEntity<CsrfResponse> csrf(CsrfToken csrfToken) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new CsrfResponse(csrfToken.getHeaderName(), csrfToken.getParameterName(),
                        csrfToken.getToken()));
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest body,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        var authenticationRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(body.email().trim(), body.password());
        var authentication = authenticationManager.authenticate(authenticationRequest);
        var user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow();

        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        var strategy = SecurityContextHolder.getContextHolderStrategy();
        var context = strategy.createEmptyContext();
        context.setAuthentication(authentication);
        strategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new LoginResponse(user.getId(), user.getFullName(), user.getEmail(),
                        user.getRole(), "SESSION"));
    }
}
