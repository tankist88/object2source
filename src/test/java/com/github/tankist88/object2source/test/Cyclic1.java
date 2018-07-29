package com.github.tankist88.object2source.test;

public class Cyclic1 {
    private String name;
    private Cyclic2 field;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cyclic2 getField() {
        return field;
    }

    public void setField(Cyclic2 field) {
        this.field = field;
    }
}
