package main.controllers;

import lombok.RequiredArgsConstructor;
import main.DTO.LoadImageErrDto;
import main.api.request.AddCommentRequest;
import main.api.request.ChangeProfileRequest;
import main.api.request.PostModerateRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
import main.model.User;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final UploadImageService uploadImageService;

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("/tag")
    public ResponseEntity<TagResponse> getTags(@RequestParam(required = false) String query) {
        if (query != null) {
            tagResponse.setTags(tagService.getAllTagDtoByQuery(query));
            return new ResponseEntity<>(tagResponse, HttpStatus.OK);
        }
        tagResponse.setTags(tagService.getAllTagDto());
        return new ResponseEntity<>(tagResponse, HttpStatus.OK);
    }

    @GetMapping("/calendar")
    public CalendarOfPostsResponse getCalendarOfPostsResponse(
            @RequestParam(required = false) Integer year) {
        calendarOfPostsResponse.setYears(postService.getYearsOfPosts());
        if (year == null) {
            year = LocalDate.now().getYear();
            calendarOfPostsResponse.setPosts(postService.getPostsCountOnDate(year));
            return calendarOfPostsResponse;
        }
        calendarOfPostsResponse.setPosts(postService.getPostsCountOnDate(year));
        return calendarOfPostsResponse;
    }

    @PostMapping(value = "/image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> imageUpload(@RequestParam("image") MultipartFile image)
            throws IOException {
        return uploadImageService.uploadImage(image);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> addComment(@RequestBody AddCommentRequest addCommentRequest,
                                        Principal principal) {
        String getAddCommentStatus = postService.isCommentAddSuccess(addCommentRequest);
        switch (getAddCommentStatus) {
            case "OK":
                return ResponseEntity.ok(postService.getAddCommentResponse(addCommentRequest, principal));
            case "BAD_REQUEST":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(postService.getAddCommentErr(addCommentRequest));
        }
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultResponse> postModerate(
            @RequestBody PostModerateRequest postModerateRequest, Principal principal) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(postService.isPostModerateSuccess(postModerateRequest, principal));
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/profile/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> changeMyProfile(
            @ModelAttribute ChangeProfileRequest changeProfileRequest,
            Principal principal) throws IOException {
        return ResponseEntity.ok(userService.resultResponse(changeProfileRequest, principal));
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatResponse> getMyStatistics(Principal principal) {
        return ResponseEntity.ok(userService.getStatResponse(principal));
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<?> getAllStatistics() {
        if (postService.statisticsIsPublic()) {
            return ResponseEntity.ok(postService.getStatResponse());
        }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity saveGlobalSettings(@RequestBody SettingsRequest request) {
        settingsService.setGlobalSettings(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
