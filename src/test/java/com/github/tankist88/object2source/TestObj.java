package com.github.tankist88.object2source;

import com.github.tankist88.object2source.dto.InstanceCreateData;

import java.math.BigDecimal;
import java.util.*;

public class TestObj {
    public enum TestEnum {
        V1,
        V2
    }

    private List<BigDecimal> testObjList;
    public List<BigDecimal> unmodifList;
    public List<BigDecimal> emptyList;
    public Set<BigDecimal> emptySet;
    public Map<String, BigDecimal> emptyMap;
    private Set<TestObj> testObjSet;
    public Set<TestObj> unmodifSet;
    private Map<String, TestObj> testObjMap;
    public Map<String, TestObj> unmodifMap;
    public SortedMap<String, TestObj> unmodifSortedMap;
    public List<Integer> arrayList;
    public List<List<Integer>> arrayListOfArrayList;
    public List<Double> doubleList;
    private int num;
    private Integer num2;
    private Long longNum;
    private String testPrivateStr;
    public Long noSetter;
    public UUID uuid;
    private Long privateField;
    private BigDecimal bigNum;
    private String name;
    private char ch;
    private GregorianCalendar calendar;
    public Calendar calendar2;
    private Date date;
    private TestObj testObj;
    private TestEnum testEnum;
    private int[] arr = new int[10];
    public char[] charArr = new char[10];
    private TestObj[] testObjArr = new TestObj[10];
    private InstanceCreateData instanceCreateData;
    public TestObjNoEmptyConst noConst;
    public TestObj2 testObj2;
    private long contentLength;
    private final String finalTest;

    public TestObj(){
        this.finalTest = "ggg";
    }

    public TestObj(Long privateField, String testPrivateStr) {
        this();
        this.privateField = privateField;
        this.testPrivateStr = testPrivateStr;
    }

    public TestObj[] getTestObjArr() {
        return testObjArr;
    }

    public void setTestObjArr(TestObj[] testObjArr) {
        this.testObjArr = testObjArr;
    }

    public List<BigDecimal> getTestObjList() {
        if(testObjList == null) {
            testObjList = new ArrayList<>();
        }
        return testObjList;
    }

    public void setTestObjList(List<BigDecimal> testObjList) {
        this.testObjList = testObjList;
    }

    public Set<TestObj> getTestObjSet() {
        if(testObjSet == null) {
            testObjSet = new HashSet<>();
        }
        return testObjSet;
    }

    public void setTestObjSet(Set<TestObj> testObjSet) {
        this.testObjSet = testObjSet;
    }

    public Map<String, TestObj> getTestObjMap() {
        if(testObjMap == null) {
            testObjMap = new HashMap<>();
        }
        return testObjMap;
    }

    public void setTestObjMap(Map<String, TestObj> testObjMap) {
        this.testObjMap = testObjMap;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Integer getNum2() {
        return num2;
    }

    public void setNum2(Integer num2) {
        this.num2 = num2;
    }

    public Long getLongNum() {
        return longNum;
    }

    public void setLongNum(Long longNum) {
        this.longNum = longNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GregorianCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(GregorianCalendar calendar) {
        this.calendar = calendar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TestObj getTestObj() {
        return testObj;
    }

    public void setTestObj(TestObj testObj) {
        this.testObj = testObj;
    }

    public TestEnum getTestEnum() {
        return testEnum;
    }

    public void setTestEnum(TestEnum testEnum) {
        this.testEnum = testEnum;
    }

    public InstanceCreateData getInstanceCreateData() {
        return instanceCreateData;
    }

    public void setInstanceCreateData(InstanceCreateData instanceCreateData) {
        this.instanceCreateData = instanceCreateData;
    }

    public BigDecimal getBigNum() {
        return bigNum;
    }

    public void setBigNum(BigDecimal bigNum) {
        this.bigNum = bigNum;
    }

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public int[] getArr() {
        return arr;
    }

    public void setArr(int[] arr) {
        this.arr = arr;
    }

    public void setContentLength(int len) {
        setContentLength((long) len);
    }

    private void setContentLength(long len) {
        this.contentLength = len;
    }
}
