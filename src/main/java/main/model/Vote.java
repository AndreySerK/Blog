package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "post_votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Column(name = "user_id", insertable = false, updatable =false)
    @NotNull
    private int userId;

    @Column(name = "post_id", insertable = false, updatable =false)
    @NotNull
    private int postId;


    @NotNull
    @Column(columnDefinition = "DATETIME")
    private Date time;

    @NotNull
    @Column(columnDefinition = "TINYINT")
    private int value;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
