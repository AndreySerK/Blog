package main.controllers;

import lombok.RequiredArgsConstructor;
import main.DTO.CaptchaCodeDto;
import main.DTO.RegisterDto;
import main.api.request.ChangePasswordRequest;
import main.api.request.RegisterRequest;
import main.api.request.RestorePasswordRequest;
import main.api.response.AuthResponse;
import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.ResultResponse;
import main.model.User;
import main.model.enums.Code;
import main.model.enums.Value;
import main.repository.GlobalSettingRepository;
import main.repository.UserRepository;
import main.service.*;
import org.springframework.http.HttpStatus;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;
    private final SettingsService settingsService;
    private final UserService userService;
    private final CaptchaCodeService codeService;
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
    public ResponseEntity<?> registerNewUser (@RequestBody RegisterRequest registerRequest)
    {
        if (settingsService.getGlobalSettingValueByCode(Code.MULTIUSER_MODE).equals(Value.NO)) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(authService.getRegisterDto(registerRequest));
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

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restorePassword (@RequestBody RestorePasswordRequest request) {

        return ResponseEntity.ok(authService.getPasswordRestoreResult(request));
    }

    @PostMapping("/password")
    public ResponseEntity<ResultResponse> changePassword (@RequestBody ChangePasswordRequest changePasswordRequest) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(false);
        if (authService.changePasswordErrors(changePasswordRequest) == null) {
            resultResponse.setResult(true);
            return ResponseEntity.ok(resultResponse);
        }
        resultResponse.setErrors(authService.changePasswordErrors(changePasswordRequest));
        return ResponseEntity.ok(resultResponse);
    }
}
