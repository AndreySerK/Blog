package main.controllers;

import lombok.RequiredArgsConstructor;
import main.DTO.PostByIdDto;
import main.api.request.AddCommentRequest;
import main.api.request.NewPostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.NewPostResponse;
import main.api.response.PostsResponse;
import main.api.response.ResultResponse;
import main.model.Post;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;
    private final PostsResponse postsResponse;

    @GetMapping("")
    public ResponseEntity<PostsResponse> getListOfPostDto (@RequestParam (defaultValue = "0") int offset,
                                                           @RequestParam (defaultValue = "10") int limit,
                                                           @RequestParam (defaultValue = "recent") String mode)
    {
        postsResponse.setPosts(postService.getSortedListOfPostDtoByMode(offset, limit, mode));
        postsResponse.setCount(postsResponse.getPosts().size());
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
        postsResponse.setPosts(postService.getListOfPostDtoByDate(offset, limit, date));
        postsResponse.setCount(postsResponse.getPosts().size());
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
        postsResponse.setPosts(postService.getListOfPostDtoByTag(offset, limit, tag));
        postsResponse.setCount(postsResponse.getPosts().size());
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

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsResponse> getListOfPostDtoByModerationStatus
                                                                (@RequestParam (defaultValue = "0") int offset,
                                                                 @RequestParam (defaultValue = "10") int limit,
                                                                 @RequestParam String status)
    {
        if (status.equals("accepted")) {
            postsResponse.setPosts(postService.getListOfPostDtoByModerationStatus(offset, limit, status));
            postsResponse.setCount(postsResponse.getPosts().size());
        } else {
            postsResponse.setPosts(postService.getListOfPostDtoByModerationStatusAndId(offset, limit, status));
            postsResponse.setCount(postsResponse.getPosts().size());
        }

        if (postsResponse.getPosts().isEmpty()) {
            postsResponse.setCount(0);
            return new ResponseEntity<>(postsResponse,HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsResponse> getMyListOfPostDto
                                                                (@RequestParam (defaultValue = "0") int offset,
                                                                 @RequestParam (defaultValue = "10") int limit,
                                                                 @RequestParam String status)
    {
        if (status.equals("inactive")) {
            postsResponse.setPosts(postService.getListOfPostDtoByUserId(
                    postService.getListOfPostByUserId(offset, limit), 0));
            postsResponse.setCount(postsResponse.getPosts().size());
        }
        if (status.equals("pending")) {
            postsResponse.setPosts(postService.getListOfPostDtoByUserId(
                    postService.getListOfPostByUserId(offset, limit), 1, "new"));
            postsResponse.setCount(postsResponse.getPosts().size());
        }
        if (status.equals("declined")) {
            postsResponse.setPosts(postService.getListOfPostDtoByUserId(
                    postService.getListOfPostByUserId(offset, limit), 1, "declined"));
            postsResponse.setCount(postsResponse.getPosts().size());
        }
        if (status.equals("published")) {
            postsResponse.setPosts(postService.getListOfPostDtoByUserId(
                    postService.getListOfPostByUserId(offset, limit), 1, "accepted"));
            postsResponse.setCount(postsResponse.getPosts().size());
        }

        if (postsResponse.getPosts().isEmpty()) {
            postsResponse.setCount(0);
            return new ResponseEntity<>(postsResponse,HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<NewPostResponse> newPost(@RequestBody NewPostRequest newPostRequest, Principal principal) {

        return ResponseEntity.ok(postService.addNewPost(newPostRequest, principal));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<NewPostResponse> changePost(@PathVariable int id,
            @RequestBody NewPostRequest newPostRequest, Principal principal) {

        return ResponseEntity.ok(postService.changePost(newPostRequest, principal, id));
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> likeToPost (@RequestBody PostVoteRequest request, Principal principal) {
        return ResponseEntity.ok(postService.setVoteValue(request,principal,1));
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> dislikeToPost (@RequestBody PostVoteRequest request, Principal principal) {
        return ResponseEntity.ok(postService.setVoteValue(request,principal,-1));
    }

}
