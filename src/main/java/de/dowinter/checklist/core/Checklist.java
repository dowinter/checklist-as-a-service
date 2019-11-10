package de.dowinter.checklist.core;

import javax.persistence.*;
import java.util.List;

@Entity
public class Checklist {
    @Id
    private String id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Checkable> checkables;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Checkable> getCheckables() {
        return checkables;
    }

    public void setCheckables(List<Checkable> checkables) {
        this.checkables = checkables;
    }
}
