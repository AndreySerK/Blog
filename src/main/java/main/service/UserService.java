package main.service;

import lombok.RequiredArgsConstructor;
import main.model.User;
import main.repository.UserRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getUsers () {
        return (List<User>) userRepository.findAll();
    }

    public User getCurrentUser() {
//      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//      String currentPrincipalName = auth.getName();
        return null;
    }
}
