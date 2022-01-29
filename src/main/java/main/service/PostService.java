package main.service;

import com.sun.istack.NotNull;
import main.DTO.PostDto;
import main.DTO.UserForPostDto;
import main.model.Post;
import main.model.enums.ModerationStatus;
import main.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page <Post> getPageOfPosts (int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return postRepository.findAll(nextPage);
    }
    public Integer getCountOfPosts () {
        return postRepository.findAll().size();
    }
    public List <PostDto> getPageOfPostDto(@NotNull Page<Post> pageOfPosts ) {
        List<PostDto> postDtoList = new ArrayList<>();
        pageOfPosts.stream()
                .filter(post -> post.getModerationStatus().equals(ModerationStatus.ACCEPTED) && post.getIsActive() == 1 && post.getTime().before(new Date()))
                .forEach(post -> {
            PostDto postDto = new PostDto();
            UserForPostDto user = new UserForPostDto();
            user.setId(post.getUserId());
            user.setName(post.getUser().getName());
            postDto.setUser(user);
            postDto.setId(post.getId());
            postDto.setTimestamp(post.getTime().getTime()/1000);
            postDto.setTitle(post.getTitle());
            postDto.setCommentCount(post.getPostCommentList().size());
            postDto.setDislikeCount
                    ((int) post.getVoteList()
                            .stream()
                            .filter(vote -> vote.getValue() < 0).count()
                    );
            postDto.setLikeCount
                    ((int) post.getVoteList()
                            .stream()
                            .filter(vote -> vote.getValue() > 0)
                            .count()
                    );
            postDto.setViewCount(post.getViewCount());
            String announce = post.getText();
            if (announce.length() < 150) {
                postDto.setAnnounce(announce);
            } else {
                postDto.setAnnounce(announce.substring(0,149) + "...");
            }
            postDtoList.add(postDto);
        });
        return postDtoList;
    }

    public List<PostDto> getSortedListOfPostDtoByMode (int offset, int limit, String mode) {
        Page<Post> postPage = getPageOfPosts(offset, limit);
        List<PostDto> postDtoList = getPageOfPostDto(postPage);
        if (mode.equals("early")) {
            postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp));
            return postDtoList;
        }
        if (mode.equals("popular")) {
            postDtoList.sort(Comparator.comparingInt(PostDto::getCommentCount).reversed());
            return postDtoList;
        }
        if (mode.equals("best")) {
            postDtoList.sort(Comparator.comparingInt(PostDto::getLikeCount).reversed());
            return postDtoList;
        }
        postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp).reversed());
        return postDtoList;
    }

}


