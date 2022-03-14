package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.DTO.ErrorsDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse {
    private Boolean result;
    private ErrorsDto errors;
}
