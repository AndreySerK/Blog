package main.DTO;

import lombok.Data;

@Data
public class CommentForPostByIdDto {
    private int id;
    private long timestamp;
    private String text;
    private UserForCommentDto user;
}
