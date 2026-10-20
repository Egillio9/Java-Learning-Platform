package com.himanshu.javalearn.auth;

import com.himanshu.javalearn.model.Learner;
import com.himanshu.javalearn.service.LearnerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LearnerService learnerService;
    private final SessionLogin sessionLogin;
    private final ObjectProvider<ClientRegistrationRepository> googleClients;

    public AuthController(LearnerService learnerService, SessionLogin sessionLogin,
                          ObjectProvider<ClientRegistrationRepository> googleClients) {
        this.learnerService = learnerService;
        this.sessionLogin = sessionLogin;
        this.googleClients = googleClients;
    }

    public record GuestLoginRequest(
            @NotBlank @Size(min = 2, max = 30)
            @Pattern(regexp = "[\\p{L}0-9 ._-]+", message = "may only contain letters, digits, spaces, '.', '_' and '-'")
            String name) {
    }

    @PostMapping("/guest")
    public AuthUser guestLogin(@Valid @RequestBody GuestLoginRequest body,
                               HttpServletRequest request, HttpServletResponse response) {
        Learner learner = learnerService.signInAsGuest(body.name());
        AuthUser user = AuthUser.of(learner);
        sessionLogin.login(user, request, response);
        return user;
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUser> me(@AuthenticationPrincipal AuthUser user) {
        return user == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.ok(user);
    }

    @GetMapping("/providers")
    public Map<String, Boolean> providers() {
        return Map.of("google", googleClients.getIfAvailable() != null);
    }
}
