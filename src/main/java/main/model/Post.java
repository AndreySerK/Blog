package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import main.model.enums.ModerationStatus;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Vote> voteList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<PostComment> postCommentList;

    @NotNull
    @Column(name = "is_active", columnDefinition = "TINYINT")
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

    @Column(columnDefinition = "VARCHAR(255)")
    @NotNull
    private String title;

    @Column(columnDefinition = "TEXT")
    @NotNull
    private String text;

    @Column(name = "view_count")
    @NotNull
    private int viewCount;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "tag2post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @ToString.Exclude
    public List<Tag> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Post post = (Post) o;
        return id != null && Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
