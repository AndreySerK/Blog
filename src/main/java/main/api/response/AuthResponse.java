package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.DTO.AuthUserDto;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private boolean result;
    private AuthUserDto user;

    public AuthResponse(boolean result) {
        this.result = result;
    }

    public AuthResponse(boolean result, AuthUserDto user) {
        this.result = result;
        this.user = user;
    }
}


