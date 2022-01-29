package main.controllers;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagResponse;
import main.model.User;
import main.service.SettingsService;
import main.service.TagService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final UserService userService;
    private final TagResponse tagResponse;
    private final TagService tagService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settings, UserService userService, TagService tagService, TagResponse tagResponse) {
        this.initResponse = initResponse;
        this.settingsService = settings;
        this.userService = userService;
        this.tagResponse = tagResponse;
        this.tagService = tagService;
    }

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
}
