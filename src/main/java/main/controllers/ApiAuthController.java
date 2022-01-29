package main.controllers;

import main.api.response.AuthResponse;
import main.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;

    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/check")
    public AuthResponse authResponse() {
       if (authService.isUserAuthorized(1)) {
           return new AuthResponse(true, authService.getAuthUserDto(1));
       } else return new AuthResponse(false);
    }
}
