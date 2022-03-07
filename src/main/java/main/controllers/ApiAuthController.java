package main.controllers;

import lombok.RequiredArgsConstructor;
import main.DTO.CaptchaCodeDto;
import main.DTO.RegisterDto;
import main.api.request.RegisterRequest;
import main.api.response.AuthResponse;
import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.repository.UserRepository;
import main.service.AuthService;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;
    private final UserService userService;
    private final CaptchaCodeService codeService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check (Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(userService.loginResponse(principal.getName()));
    }

    @GetMapping("/captcha")
    public CaptchaCodeDto captchaCodeDto () {
        return codeService.getCaptchaCodeDto();
    }

    @PostMapping("/register")
    @Transactional
    public RegisterDto getRegisterResult (@RequestBody RegisterRequest registerRequest)
    {

        return authService.getRegisterDto(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest loginRequest) {

        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        return ResponseEntity.ok(userService.loginResponse(user.getUsername()));
    }

    @GetMapping("/logout")
    public ResponseEntity<AuthResponse> logout (HttpServletRequest request) throws ServletException, IOException {

        request.logout();
        if (request.getSession() != null) {
            request.getSession().invalidate();
        }
        return ResponseEntity.ok(new AuthResponse(true));
    }
}
