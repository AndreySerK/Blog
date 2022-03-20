package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @OneToMany (cascade = CascadeType.ALL, mappedBy = "user")
    @ToString.Exclude
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Vote> votes;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private PostComment postComment;

    @Column (name = "is_moderator",columnDefinition = "TINYINT")
    @NotNull
    private int isModerator;

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

    public Role getRole() {
        return isModerator == 1 ? Role.MODERATOR : Role.USER;
    }
}
