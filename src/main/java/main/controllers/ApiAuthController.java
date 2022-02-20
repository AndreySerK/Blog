package main.controllers;

import lombok.RequiredArgsConstructor;
import main.DTO.AuthUserDto;
import main.DTO.CaptchaCodeDto;
import main.DTO.RegisterDto;
import main.api.response.AuthResponse;
import main.mappers.UserMapper;
import main.service.AuthService;
import main.service.CaptchaCodeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;
    private final CaptchaCodeService codeService;

    @GetMapping("/check")
    public AuthResponse authResponse() {
       if (authService.isUserAuthorized(1)) {
           AuthUserDto authUserDto = UserMapper.INSTANCE.userToAuthUserDto(authService.getAuthUser(1));
           authUserDto.setModerationCount(authService.getPostsForModerationCount());
           authUserDto.setSettings(true);
           return new AuthResponse(true, authUserDto);
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
