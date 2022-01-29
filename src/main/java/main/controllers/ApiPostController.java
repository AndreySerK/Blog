package main.controllers;

import main.api.response.PostsResponse;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;
    private final PostsResponse postsResponse;

    public ApiPostController(PostService postService, PostsResponse postsResponse) {
        this.postService = postService;
        this.postsResponse = postsResponse;
    }

    @GetMapping("/")
    public ResponseEntity<PostsResponse> getMainPageOfPostDto (@RequestParam (defaultValue = "0") int offset,
                                                               @RequestParam (defaultValue = "10") int limit,
                                                               @RequestParam (defaultValue = "recent") String mode)
    {
        postsResponse.setCount(postService.getCountOfPosts());
        postsResponse.setPosts(postService.getSortedListOfPostDtoByMode(offset, limit, mode));
        if (postsResponse.getPosts().isEmpty()) {
            postsResponse.setCount(0);
            return new ResponseEntity<>(postsResponse,HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }
}
