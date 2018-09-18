package com.github.tankist88.object2source.dto;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;

public class ProviderInfoTest {
    @Test(dataProvider = "dataProvider")
    public void hashCodeTest(String name, String body) {
        ProviderInfo o1 = new ProviderInfo();
        o1.setMethodBody(body);
        o1.setMethodName(name);
        ProviderInfo o2 = new ProviderInfo(name, body, false);
        assertEquals(o1.hashCode(), o2.hashCode());
        Set<ProviderInfo> hashSet = new HashSet<ProviderInfo>();
        hashSet.add(o1);
        assertFalse(hashSet.add(o2));
    }

    @Test(dataProvider = "dataProvider")
    public void equalsTest(String name, String body) {
        ProviderInfo o1 = new ProviderInfo();
        o1.setMethodBody(body);
        o1.setMethodName(name);
        ProviderInfo o2 = new ProviderInfo(name, body, false);
        assertEquals(o1, o2);
        assertEquals(o2, o1);
        assertEquals(o1, o1);
        assertNotEquals(o1, null);
        assertNotEquals(null, o2);
        assertNotEquals(o1, "test string");
    }

    @DataProvider
    public Object[][] dataProvider() {
        return new Object[][] {
                {"ggg", "bbb"},
                {null, "bbb"},
                {"ggg", null},
                {null, null}
        };
    }
}
