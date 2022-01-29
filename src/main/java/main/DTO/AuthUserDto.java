package main.DTO;

import lombok.Data;

@Data
public class AuthUserDto {
    private int id;
    private String name;
    private String photo;
    private String email;
    private Boolean moderation;
    private int moderationCount;
    private Boolean settings;
}
