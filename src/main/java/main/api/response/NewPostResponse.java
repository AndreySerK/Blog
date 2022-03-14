package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.DTO.NewPostErrors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewPostResponse {
    private boolean result;
    private NewPostErrors errors;
}
