package main.model;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date time;

    @Column (columnDefinition = "TINYTEXT", nullable = false)
    private String code;

    @Column (name = "secret_code", columnDefinition = "TINYTEXT", nullable = false)
    private String secretCode;

}
