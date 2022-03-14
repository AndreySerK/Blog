package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @ToString.Exclude
    private Post post;

    @Column(name = "parent_id")
    private Integer parentId;

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
}
