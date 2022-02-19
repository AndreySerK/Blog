package main.controllers;

import main.DTO.PostByIdDto;
import main.api.response.PostsResponse;
import main.model.Post;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;
    private final PostsResponse postsResponse;

    public ApiPostController(PostService postService, PostsResponse postsResponse) {
        this.postService = postService;
        this.postsResponse = postsResponse;
    }

    @GetMapping("")
    public ResponseEntity<PostsResponse> getListOfPostDto (@RequestParam (defaultValue = "0") int offset,
                                                           @RequestParam (defaultValue = "10") int limit,
                                                           @RequestParam (defaultValue = "recent") String mode)
    {
        postsResponse.setCount(postService.getCountOfAllPosts());
        postsResponse.setPosts(postService.getSortedListOfPostDtoByMode(offset, limit, mode));
        if (postsResponse.getPosts().isEmpty()) {
            postsResponse.setCount(0);
            return new ResponseEntity<>(postsResponse,HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PostsResponse> getListOfPostDtoContainQuery (@RequestParam (defaultValue = "0") int offset,
                                                                       @RequestParam (defaultValue = "10") int limit,
                                                                       @RequestParam (defaultValue = "") String query)
    {
        if (query.trim().equals("")) {
            return getListOfPostDto(0, postService.getCountOfAllPosts(), "recent");
        }
        postsResponse.setCount (postService.getCountOfPostsWithQuery(query));
        postsResponse.setPosts(postService.getListOfPostDtoWithQuery(offset, limit, query));
        if (postsResponse.getPosts().isEmpty()) {
            postsResponse.setCount(0);
            return new ResponseEntity<>(postsResponse,HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostsResponse> getListOfPostDtoByDate (@RequestParam (defaultValue = "0") int offset,
                                                                 @RequestParam (defaultValue = "10") int limit,
                                                                 @RequestParam String date)
    {
        postsResponse.setCount(postService.getCountOfAllPosts());
        postsResponse.setPosts(postService.getListOfPostDtoByDate(offset, limit, date));
        if (postsResponse.getPosts().isEmpty()) {
            postsResponse.setCount(0);
            return new ResponseEntity<>(postsResponse,HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostsResponse> getListOfPostDtoByTag (@RequestParam (defaultValue = "0") int offset,
                                                                 @RequestParam (defaultValue = "10") int limit,
                                                                 @RequestParam String tag)
    {
        postsResponse.setCount(postService.getCountOfAllPosts());
        postsResponse.setPosts(postService.getListOfPostDtoByTag(offset, limit, tag));
        if (postsResponse.getPosts().isEmpty()) {
            postsResponse.setCount(0);
            return new ResponseEntity<>(postsResponse,HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity <PostByIdDto> getPostByIdDto (@PathVariable int id) {
        PostByIdDto postByIdDto = postService.getPostByIdDto(id);
        if (postByIdDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Post post = postService.getActiveAcceptedPostByIdBeforeCurrentTime(id);

        post.setViewCount(post.getViewCount() + 1);
        return new ResponseEntity<>(postByIdDto, HttpStatus.OK);
    }
}
