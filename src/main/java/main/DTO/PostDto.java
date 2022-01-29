package main.DTO;

import lombok.Data;
import org.springframework.stereotype.Component;


@Data
public class PostDto {

    private int id;
    private long timestamp;
    private UserForPostDto user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;



}
