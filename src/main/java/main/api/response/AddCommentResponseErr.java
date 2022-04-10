package main.api.response;

import lombok.Data;
import main.DTO.AddCommentErr;

@Data
public class AddCommentResponseErr {

    private boolean result;
    private AddCommentErr errors;
}
