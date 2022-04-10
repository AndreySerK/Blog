package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.DTO.LoadImageErrDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoadImageResponse {

    private boolean result;
    private LoadImageErrDto errors;
}
