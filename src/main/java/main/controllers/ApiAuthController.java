package main.controllers;

import main.DTO.CaptchaCodeDto;
import main.DTO.RegisterDto;
import main.api.response.AuthResponse;
import main.service.AuthService;
import main.service.CaptchaCodeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;
    private final CaptchaCodeService codeService;

    public ApiAuthController(AuthService authService, CaptchaCodeService codeService) {
        this.authService = authService;
        this.codeService = codeService;
    }

    @GetMapping("/check")
    public AuthResponse authResponse() {
       if (authService.isUserAuthorized(1)) {
           return new AuthResponse(true, authService.getAuthUserDto(1));
       } else return new AuthResponse(false);
    }

    @GetMapping("/captcha")
    public CaptchaCodeDto captchaCodeDto () {
        return codeService.getCaptchaCodeDto();
    }

    @PostMapping("/register")
    public RegisterDto getRegisterResult (@RequestParam ("e_mail") String email,
                                          @RequestParam ("name") String name,
                                          @RequestParam ("password") String password,
                                          @RequestParam ("captcha") String code,
                                          @RequestParam ("captcha_secret") String secretCode)
    {

        return authService.getRegisterDto(email,name, password, code, secretCode);
    }
}
