package main.controllers;
import lombok.RequiredArgsConstructor;
import main.DTO.LoadImageErrDto;
import main.api.request.AddCommentRequest;
import main.api.request.ChangeProfileRequest;
import main.api.request.PostModerateRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
import main.model.User;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final UserService userService;
    private final TagResponse tagResponse;
    private final TagService tagService;
    private final CalendarOfPostsResponse calendarOfPostsResponse;
    private final PostService postService;

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/users")
    public List<User> getAllUsers () {
        return userService.getUsers();
    }

    @GetMapping("/tag")
    public ResponseEntity <TagResponse> getTags (@RequestParam(required = false) String query) {
        if (query != null) {
            tagResponse.setTags(tagService.getAllTagDtoByQuery(query));
            return new ResponseEntity<>(tagResponse, HttpStatus.OK);
        }
        tagResponse.setTags(tagService.getAllTagDto());
        return new ResponseEntity<>(tagResponse, HttpStatus.OK);
    }

    @GetMapping("/calendar")
    public CalendarOfPostsResponse getCalendarOfPostsResponse(@RequestParam(required = false) Integer year) {
        calendarOfPostsResponse.setYears(postService.getYearsOfPosts());
        if (year == null) {
            year = LocalDate.now().getYear();
            calendarOfPostsResponse.setPosts(postService.getPostsCountOnDate(year));
            return calendarOfPostsResponse;
        }
        calendarOfPostsResponse.setPosts(postService.getPostsCountOnDate(year));
        return calendarOfPostsResponse;
    }

    @PostMapping("/image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> fileUpload(@RequestParam("image") MultipartFile file) throws IOException{
        LoadImageResponse loadImageResponse = new LoadImageResponse();
        char[] text = new char[9];
        String characters = "1234567890ABCDEFG";
        Random rnd = new Random();
        for (int i = 0; i < 9; i++) {
            text[i] = characters.charAt(rnd.nextInt(characters.length()));
        }
        String randomName = new String(text);
        if (file.getSize() > 10485760) {
            loadImageResponse.setResult(false);
            LoadImageErrDto loadImageErrDto = new LoadImageErrDto();
            loadImageErrDto.setImage("Размер файла превышает допустимый размер");
            loadImageResponse.setErrors(loadImageErrDto);
            return new ResponseEntity<>(loadImageResponse,HttpStatus.BAD_REQUEST);
        }
        if (!file.getOriginalFilename().contains("jpg") && !file.getOriginalFilename().contains("png")) {
            loadImageResponse.setResult(false);
            LoadImageErrDto loadImageErrDto = new LoadImageErrDto();
            loadImageErrDto.setImage("Недопустимый тип изображения");
            loadImageResponse.setErrors(loadImageErrDto);
            return new ResponseEntity<>(loadImageResponse,HttpStatus.BAD_REQUEST);
        }
            byte[] bytes = file.getBytes();
            String uploadDir = "/upload/" + randomName.substring(0,2);
            new File(uploadDir).mkdirs();
            uploadDir = uploadDir + "/" + randomName.substring(3,5);
            new File(uploadDir).mkdirs();
            uploadDir = uploadDir + "/" + randomName.substring(6,8) +"/";
            new File(uploadDir).mkdirs();
            Path path = Paths.get(uploadDir + file.getOriginalFilename() + ".png");
            Files.write(path, bytes);

            return ResponseEntity.ok(path.toString());
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> addComment (@RequestBody AddCommentRequest addCommentRequest, Principal principal) {

        if (postService.isCommentAddSuccess(addCommentRequest).equals("200")) {
            return ResponseEntity.ok(postService.getAddCommentResponse(addCommentRequest, principal));
        }
        if (postService.isCommentAddSuccess(addCommentRequest).equals("400")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(postService.getAddCommentErr(addCommentRequest));
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultResponse> postModerate (@RequestBody PostModerateRequest postModerateRequest, Principal principal) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(postService.isPostModerateSuccess(postModerateRequest,principal));
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/profile/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> changeMyProfile (@ModelAttribute ChangeProfileRequest changeProfileRequest,
                                                           Principal principal) throws IOException {
        return ResponseEntity.ok(userService.resultResponse(changeProfileRequest, principal));
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatResponse> getMyStatistics (Principal principal) {
        return ResponseEntity.ok(userService.getStatResponse(principal));
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<?> getAllStatistics () {
        if (postService.statisticsIsPublic()) {
            return ResponseEntity.ok(postService.getStatResponse());
        }
        return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity saveGlobalSettings (@RequestBody SettingsRequest request) {
        settingsService.setGlobalSettings(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
