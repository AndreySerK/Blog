package main.api.response;

import lombok.Data;
import main.DTO.PostDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class PostsResponse {
    private int count;
    private List<PostDto> posts;
}
