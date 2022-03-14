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
import org.imgscalr.Scalr;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final SecurityConfig securityConfig;

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

    public ResultResponse resultResponse (ChangeProfileRequest request, Principal principal) throws IOException {
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
            }else {
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
            if (request.getPhoto().getSize() > 5242880) {
                errorsDto.setPhoto("Фото слишком большое, нужно не более 5 Мб");
                resultResponse.setResult(false);
            } else {
                Path photoPath = Paths.get("src/main/resources/user_photo/" + request.getPhoto().getOriginalFilename());
                request.getPhoto().transferTo(photoPath);
                File file = new File(String.valueOf(photoPath));
                BufferedImage image = ImageIO.read(file);
                BufferedImage resizedImage = Scalr.resize(image, 36,36);
                ImageIO.write(resizedImage, "png", file);
                resizedImage.flush();
                user.setPhoto(file.getAbsolutePath());
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

    public StatResponse getStatResponse (Principal principal) {
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
        AtomicInteger viewCount = new AtomicInteger();
        posts.forEach(post -> {
            viewCount.addAndGet(post.getViewCount());
        });
        myStatResponse.setDislikesCount(dislikeCount);
        myStatResponse.setLikesCount(likesCount);
        myStatResponse.setPostsCount(posts.size());
        List<Date> dateList = new ArrayList<>();
        posts.forEach(post -> dateList.add(post.getTime()));
        dateList.sort(Comparator.naturalOrder());
        myStatResponse.setFirstPublication(dateList.get(0));
        return myStatResponse;
    }
}
