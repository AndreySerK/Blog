package main.model;


import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private PostComment parentPostComment;

    @OneToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ToString.Exclude
    private PostComment childPostComment;

    @OneToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @Column(name = "parent_id", insertable = false, updatable =false)
    private int parentId;

    @Column(name = "user_id", insertable = false, updatable =false)
    @NotNull
    private int userId;

    @Column(name = "post_id", insertable = false, updatable =false)
    @NotNull
    private int postId;

    @NotNull
    @Column(columnDefinition = "DATETIME")
    private Date time;

    @Column (columnDefinition = "TEXT")
    @NotNull
    private String text;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
