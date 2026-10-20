package com.himanshu.javalearn.auth;

import com.himanshu.javalearn.model.Learner;
import com.himanshu.javalearn.service.LearnerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Maps a Google account onto a Learner, then swaps the OAuth principal for our own AuthUser. */
@Component
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LearnerService learnerService;
    private final SessionLogin sessionLogin;

    public GoogleLoginSuccessHandler(LearnerService learnerService, SessionLogin sessionLogin) {
        this.learnerService = learnerService;
        this.sessionLogin = sessionLogin;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();
        String email = googleUser.getAttribute("email");
        String name = googleUser.getAttribute("name");
        String picture = googleUser.getAttribute("picture");

        Learner learner = learnerService.signInWithGoogle(email, name, picture);
        sessionLogin.login(AuthUser.of(learner), request, response);
        response.sendRedirect("/app");
    }
}
