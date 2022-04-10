package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostModerateRequest {

    @JsonProperty("post_id")
    private Integer postId;
    private String decision;
}
