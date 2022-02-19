package main.DTO;

import lombok.Data;

@Data
public class RegisterDto {

    public RegisterDto(Boolean result) {
        this.result = result;
    }

    private Boolean result;
    private ErrorsDto errors;
}
