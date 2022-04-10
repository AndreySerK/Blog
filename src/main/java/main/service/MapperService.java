package main.service;

import lombok.RequiredArgsConstructor;
import main.DTO.*;
import main.mappers.CommentMapper;
import main.mappers.PostMapper;
import main.mappers.UserMapper;
import main.model.Post;
import main.model.Tag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapperService {

    public PostByIdDto convertToPostByIdDto(Post post) {
        PostByIdDto postByIdDto = PostMapper.INSTANCE.postToPostByIdDto(post);
        UserForPostDto userForPostDto = UserMapper.INSTANCE.userToUserForPostDto(post.getUser());
        postByIdDto.setTimestamp(post.getTime().getTime() / 1000);
        postByIdDto.setActive(true);
        postByIdDto.setUser(userForPostDto);
        postByIdDto.setDislikeCount((int) post.getVoteList()
                .stream()
                .filter(vote -> vote.getValue() < 0)
                .count());
        postByIdDto.setLikeCount((int) post.getVoteList()
                .stream()
                .filter(vote -> vote.getValue() > 0)
                .count());
        List<CommentForPostByIdDto> commentsForPostByIdDtoList = new ArrayList<>();
        post.getPostCommentList()
                .forEach(postComment -> {
                    CommentForPostByIdDto comment = CommentMapper.INSTANCE.commentForPostByIdDto(postComment);
                    UserForCommentDto user = UserMapper.INSTANCE.userToUserForCommentsDto(post.getUser());
                    comment.setTimestamp(postComment.getTime().getTime() / 1000);
                    comment.setUser(user);
                    commentsForPostByIdDtoList.add(comment);
                });
        postByIdDto.setComments(commentsForPostByIdDtoList);
        List<String> tags = post.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        postByIdDto.setTags(tags);
        postByIdDto.setText(postByIdDto.getText());
        return postByIdDto;
    }

}
