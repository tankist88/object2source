package org.object2source.test;

class NotPublic extends AbstractPublic {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
