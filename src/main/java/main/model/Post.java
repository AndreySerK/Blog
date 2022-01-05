package main.model;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import main.model.enums.ModerationStatus;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany (mappedBy = "post",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Vote> voteList;

    @OneToMany (mappedBy = "post",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<PostComment> postCommentList;

    @NotNull
    @Column (name = "is_active",columnDefinition = "TINYINT")
    private Boolean isActive;


    @Column(name = "moderation_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ModerationStatus moderationStatus = ModerationStatus.NEW;

    @Column(name = "moderator_id")
    private int moderatorId;

    @Column(name = "user_id", insertable = false, updatable = false)
    @NotNull
    private int userId;


    @Column(columnDefinition = "DATETIME")
    @NotNull
    private Date time;

    @Column(columnDefinition="VARCHAR(255)")
    @NotNull
    private String title;

    @Column (columnDefinition = "TEXT")
    @NotNull
    private String text;

    @Column(name = "view_count")
    @NotNull
    private int viewCount;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
