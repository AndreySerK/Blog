package main.service;

import lombok.RequiredArgsConstructor;
import main.api.response.LoginResponse;
import main.api.response.UserLoginResponse;
import main.mappers.UserMapper;
import main.model.User;
import main.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getUsers () {
        return (List<User>) userRepository.findAll();
    }

    public LoginResponse loginResponse (String email) {

        User currentUser = userRepository.findByEmail(
                email).orElseThrow(() -> new UsernameNotFoundException(email));
        UserLoginResponse userResponse = UserMapper.INSTANCE.userToUserResponse(currentUser);
        userResponse.setModeration(currentUser.getIsModerator() == 1);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userResponse);
        return loginResponse;
    }
}
