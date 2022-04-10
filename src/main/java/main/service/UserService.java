package main.service;

import lombok.RequiredArgsConstructor;
import main.DTO.ErrorsDto;
import main.api.request.ChangeProfileRequest;
import main.api.response.LoginResponse;
import main.api.response.ResultResponse;
import main.api.response.StatResponse;
import main.api.response.UserLoginResponse;
import main.config.SecurityConfig;
import main.mappers.UserMapper;
import main.model.Post;
import main.model.User;
import main.model.Vote;
import main.repository.UserRepository;
import main.repository.VoteRepository;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static main.service.PostService.setStatResponse;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final SecurityConfig securityConfig;
    private final PostService postService;

    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public LoginResponse getLoginResponse(String email) {
        LoginResponse loginResponse = new LoginResponse();
        User currentUser = userRepository.findByEmail(
                email).orElseThrow();
        UserLoginResponse userResponse = UserMapper.INSTANCE.userToUserResponse(currentUser);
        userResponse.setModeration(currentUser.getIsModerator() == 1);
        if (currentUser.getIsModerator() == 1) {
            userResponse.setModerationCount(postService.getCountOfNewPosts());
            userResponse.setSettings(true);
        }
        userResponse.setModerationCount(0);
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userResponse);
        return loginResponse;
    }

    public ResultResponse resultResponse(ChangeProfileRequest request, Principal principal)
            throws IOException {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(true);
        ErrorsDto errorsDto = new ErrorsDto();

        if (request.getEmail() != null) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()
                    && !request.getEmail().equals(principal.getName())) {
                errorsDto.setEmail("Этот e-mail уже зарегистрирован");
                resultResponse.setResult(false);
            } else {
                user.setEmail(request.getEmail());
            }
        }
        if (request.getName() != null) {
            if (request.getName().length() < 3
                    && !request.getName().matches("[A-Za-z0-9 _]+")) {
                errorsDto.setName("Имя указано неверно");
                resultResponse.setResult(false);
            } else {
                user.setName(request.getName());
            }
        }
        if (request.getPassword() != null) {
            if (request.getPassword().length() < 6) {
                errorsDto.setPassword("Пароль короче 6-ти символов");
                resultResponse.setResult(false);
            } else {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
        }
        if (request.getPhoto() != null) {
            if (request.getPhoto().getSize() > 5 * 1024 * 1024) {
                errorsDto.setPhoto("Фото слишком большое, нужно не более 5 Мб");
                resultResponse.setResult(false);
            } else {
                try {
                    BufferedImage bufferedImage = ImageIO.read(request.getPhoto().getInputStream());
                    BufferedImage resultImage = Scalr.resize(bufferedImage, 36, 36);
                    String toFile = "upload/" + user.getId() + "/" + request.getPhoto().getOriginalFilename();
                    Path path = Paths.get(toFile);
                    if (!path.toFile().exists()) {
                        Files.createDirectories(path.getParent());
                        Files.createFile(path);
                        String extension = FilenameUtils.getExtension(request.getPhoto().getOriginalFilename());
                        assert extension != null;
                        ImageIO.write(resultImage, extension, path.toFile());
                    }
                    user.setPhoto("/" + toFile.substring(toFile.lastIndexOf("upload")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (request.getRemovePhoto() != null && request.getRemovePhoto() == 1) {
            user.setPhoto(null);
        }
        if (resultResponse.getResult()) {
            userRepository.save(user);
            return resultResponse;
        }
        resultResponse.setErrors(errorsDto);
        return resultResponse;
    }

    public StatResponse getStatResponse(Principal principal) {
        StatResponse myStatResponse = new StatResponse();
        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<Post> posts = currentUser.getPosts();
        List<Vote> votes = voteRepository.getVotesByUser(currentUser);
        int likesCount = (int) votes.stream()
                .filter(vote -> vote.getValue() == 1)
                .count();
        int dislikeCount = (int) votes.stream()
                .filter(vote -> vote.getValue() == -1)
                .count();
        int viewCount = posts.stream().mapToInt(Post::getViewCount).sum();
        myStatResponse.setDislikesCount(dislikeCount);
        myStatResponse.setLikesCount(likesCount);
        myStatResponse.setPostsCount(posts.size());
        myStatResponse.setViewsCount(viewCount);
        return setStatResponse(myStatResponse, posts);
    }
}
