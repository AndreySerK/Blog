package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @NotNull
    @Column(columnDefinition = "VARCHAR(255)")
    private String name;

    @OneToMany (mappedBy = "tag",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Tag2post> tag2postList;


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
