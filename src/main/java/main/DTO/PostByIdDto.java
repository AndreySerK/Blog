package main.DTO;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class PostByIdDto {
    private int id;
    private long timestamp;
    private Boolean active;
    private UserForPostDto user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<CommentForPostByIdDto> comments;
    private List<TagForPostByIdDto> tags;
}
