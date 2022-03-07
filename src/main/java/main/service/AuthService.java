package main.service;

import lombok.RequiredArgsConstructor;
import main.DTO.AuthUserDto;
import main.DTO.ErrorsDto;
import main.DTO.RegisterDto;
import main.api.request.RegisterRequest;
import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.repository.CaptchaCodeRepository;
import main.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public int getPostsForModerationCount () {
        return userRepository.getPostsForModerationCount();
    }

    public User getAuthUser (int id) {
        return userRepository.findById(id).orElseThrow();
    }

    private boolean isEmailAlreadyInUse (String email) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isValidName(String name) {
        String regex = "^[A-Za-zА-Яа-я]{3,29}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    private boolean isValidPassword (String password) {
        int minPasswordLength = 6;
        return password.length() >= minPasswordLength;
    }

    private boolean isValidCaptcha (String code, String secretCode) {
        if(captchaCodeRepository.findCaptchaCodeBySecretCode(secretCode).isEmpty()) {
            return false;
        } else
            return captchaCodeRepository.findCaptchaCodeBySecretCode(secretCode).get(0).getCode().equals(code);
    }

    public RegisterDto getRegisterDto (RegisterRequest registerRequest) {
        if (isValidName(registerRequest.getName())
                && !isEmailAlreadyInUse(registerRequest.getEmail())
                && isValidPassword(registerRequest.getPassword())
                && isValidCaptcha(registerRequest.getCaptcha(),registerRequest.getCaptchaSecret()))   {
            User newUser = new User();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setName(registerRequest.getName());
            newUser.setRegTime(new Date());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setIsModerator(0);
            userRepository.save(newUser);
            return new RegisterDto(true);
        }
        RegisterDto registerDtoErr = new RegisterDto(false);
        ErrorsDto errorsDto = new ErrorsDto();
        if (isEmailAlreadyInUse(registerRequest.getEmail())) {
            errorsDto.setEmail("Этот e-mail уже зарегистрирован!");
        }
        if (!isValidName(registerRequest.getName())) {
            errorsDto.setName("Имя указано неверно");
        }
        if (!isValidPassword(registerRequest.getPassword())) {
            errorsDto.setPassword("Пароль короче 6-ти символов");
        }
        if (!isValidCaptcha(registerRequest.getCaptcha(), registerRequest.getCaptchaSecret())) {
            errorsDto.setCaptcha("Код с картинки введён неверно");
        }
        registerDtoErr.setErrors(errorsDto);
        return registerDtoErr;
    }
}
