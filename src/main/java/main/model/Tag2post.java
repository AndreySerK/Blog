package main.model;


import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "tag2post")
public class Tag2post {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    private Post post;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", referencedColumnName = "id")
    private Tag tag;

    @NotNull
    @Column(name = "tag_id", insertable = false, updatable =false)
    private int tagId;

    @NotNull
    @Column(name = "post_id", insertable = false, updatable =false)
    private int postId;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
