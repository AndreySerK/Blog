package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.DTO.NewPostErrors;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewPostResponse {

    private boolean result;
    private Map<String, String> errors;
}
