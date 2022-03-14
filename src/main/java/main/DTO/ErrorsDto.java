package main.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorsDto {
    private String email;
    private String name;
    private String password;
    private String captcha;
    private String photo;
    private String code;
}
