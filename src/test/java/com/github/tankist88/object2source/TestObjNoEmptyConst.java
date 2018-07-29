package com.github.tankist88.object2source;

public class TestObjNoEmptyConst {
    private int num;
    private String testStr;

    public TestObjNoEmptyConst(int num) {
        this.num = num;
    }

    public TestObjNoEmptyConst(int num, String testStr) {
        this.num = num;
        this.testStr = testStr;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTestStr() {
        return testStr;
    }

    public void setTestStr(String testStr) {
        this.testStr = testStr;
    }
}
