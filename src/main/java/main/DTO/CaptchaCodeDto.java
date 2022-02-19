package main.DTO;

import lombok.Data;

@Data
public class CaptchaCodeDto {
    private String secret;
    private String image;
}
