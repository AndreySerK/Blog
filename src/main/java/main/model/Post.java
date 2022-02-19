package main.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.ToString;
import main.model.enums.ModerationStatus;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Entity
@Data
@Table(name = "posts")
public class Post {

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    public List <Tag> tags;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @JsonBackReference
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @OneToMany (mappedBy = "post",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Vote> voteList;

    @OneToMany (mappedBy = "post",cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PostComment> postCommentList;

    @NotNull
    @Column (name = "is_active",columnDefinition = "TINYINT")
    private int isActive;


    @Column(name = "moderation_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ModerationStatus moderationStatus = ModerationStatus.NEW;

    @Column(name = "moderator_id")
    private Integer moderatorId;

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
