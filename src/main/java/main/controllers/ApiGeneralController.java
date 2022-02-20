package main.controllers;
import lombok.RequiredArgsConstructor;
import main.api.response.CalendarOfPostsResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagResponse;
import main.model.User;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
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
    private InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    private SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/users")
    private List<User> getAllUsers () {
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
}
