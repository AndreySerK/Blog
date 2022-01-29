package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import main.model.enums.Value;
import javax.persistence.*;

@Entity
@Data
@Table(name = "global_settings")
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @Column (columnDefinition = "VARCHAR(255)")
    @NotNull
    private String code;

    @Column (columnDefinition = "VARCHAR(255)")
    @NotNull
    private String name;

    @Column (columnDefinition = "VARCHAR(255)", insertable = false, updatable =false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Value value;

//    @JsonIgnore
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn (name = "value")
//    @ToString.Exclude
//    private User user;


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
