package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @NotNull
    @Column(columnDefinition = "DATETIME")
    private Date time;

    @Column (columnDefinition = "TINYTEXT")
    @NotNull
    private String code;

    @Column (name = "secret_code", columnDefinition = "TINYTEXT")
    @NotNull
    private String secretCode;

}
