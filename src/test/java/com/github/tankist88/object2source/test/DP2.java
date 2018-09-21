package com.github.tankist88.object2source.test;

import com.github.tankist88.object2source.TestObj;

import java.util.List;
import java.util.Set;

public interface DP2 {
    String getString(List<Integer> list, List noGenList);
    TestObj getTestObj(List<TestObj> list);
    Integer getInteger();
    String getString(Set set1, Set set2);
}
