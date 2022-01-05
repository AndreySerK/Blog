package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
