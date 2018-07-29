package com.github.tankist88.object2source.test;

public class Cyclic2 {
    private Integer id;
    private Cyclic1 field;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cyclic1 getField() {
        return field;
    }

    public void setField(Cyclic1 field) {
        this.field = field;
    }
}
