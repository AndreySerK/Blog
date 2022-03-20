package main.api;

import main.api.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<LoginResponse> handleException(AuthenticationException e) {
        LoginResponse response = new LoginResponse();
        response.setResult(false);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}