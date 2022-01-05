package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @OneToMany (cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    @ToString.Exclude
    private List<Post> posts;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Vote vote;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private PostComment postComment;

    @Column (name = "is_moderator",columnDefinition = "TINYINT")
    @NotNull
    private Boolean isModerator;

    @Column (name = "reg_time",columnDefinition = "DATETIME")
    @NotNull
    private Date regTime;

    @NotNull
    @Column(columnDefinition="VARCHAR(255)")
    private String name;

    @NotNull
    @Column(columnDefinition="VARCHAR(255)")
    private String email;

    @NotNull
    @Column(columnDefinition="VARCHAR(255)")
    private String password;

    @Column(columnDefinition="VARCHAR(255)")
    private String code;

    @Column (columnDefinition = "TEXT")
    private String photo;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
