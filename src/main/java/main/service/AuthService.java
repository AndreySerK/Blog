package main.service;

import main.DTO.AuthUserDto;
import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.model.enums.Value;
import main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean isUserAuthorized (int id) {
        return true;
    }
    public AuthUserDto getAuthUserDto (int id) {
        User user = userRepository.findById(id).get();
        AuthUserDto authUserDto= new AuthUserDto();
        authUserDto.setEmail(user.getEmail());
        authUserDto.setId(user.getId());
        authUserDto.setName(user.getName());
        authUserDto.setPhoto(user.getPhoto());
        authUserDto.setModeration(user.getIsModerator());
        if(user.getIsModerator()) {
            authUserDto.setSettings(true);
            List<Post> newPosts = user.getPosts()
                    .stream()
                    .filter(post -> post.getModerationStatus().equals(ModerationStatus.NEW))
                    .collect(Collectors.toList());
            authUserDto.setModerationCount(newPosts.size());
        } else {
            authUserDto.setSettings(false);
            authUserDto.setModerationCount(0);
        }
        return authUserDto;
    }
}
