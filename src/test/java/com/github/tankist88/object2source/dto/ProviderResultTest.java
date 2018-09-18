package com.github.tankist88.object2source.dto;

import com.github.tankist88.object2source.SourceGenerator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

public class ProviderResultTest {
    private static final SourceGenerator SG = new SourceGenerator();

    @Test(dataProvider = "hashCodeEqualsDataProvider")
    public void hashCodeEqualsTest(ProviderResult r1, ProviderResult r2) {
        assertEquals(r1.hashCode(), r2.hashCode());
        Set<ProviderResult> hashSet = new HashSet<ProviderResult>();
        hashSet.add(r1);
        assertFalse(hashSet.add(r2));
    }

    @DataProvider
    public Object[][] hashCodeEqualsDataProvider() {
        return new Object[][] {
            {
                SG.createDataProviderMethod(getCalendarInstance("Europe/Moscow",1534600034819L)),
                SG.createDataProviderMethod(getCalendarInstance("Europe/Moscow",1534600034819L))
            },
            {
                SG.createDataProviderMethod(new Date(1534600034819L)),
                SG.createDataProviderMethod(new Date(1534600034819L))
            },
        };
    }

    @Test(dataProvider = "hashCodeNotEqualsDataProvider")
    public void hashCodeNotEqualsTest(ProviderResult r1, ProviderResult r2) {
        assertNotEquals(r1.hashCode(), r2.hashCode());
        Set<ProviderResult> hashSet = new HashSet<ProviderResult>();
        hashSet.add(r1);
        assertTrue(hashSet.add(r2));
    }

    @DataProvider
    public Object[][] hashCodeNotEqualsDataProvider() {
        return new Object[][] {
            {SG.createDataProviderMethod(Calendar.getInstance()), SG.createDataProviderMethod(new Date())},
            {SG.createDataProviderMethod(new Date()), SG.createDataProviderMethod(123)},
        };
    }

    @Test(dataProvider = "hashCodeEqualsDataProvider")
    public void equalsTest(ProviderResult r1, ProviderResult r2) {
        assertEquals(r1, r2);
        assertEquals(r2, r1);
        assertEquals(r1, r1);
        assertNotEquals(r1, null);
        assertNotEquals(null, r2);
        assertNotEquals(r1, "test string");
    }

    private static GregorianCalendar getCalendarInstance(String tzId, long mTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(tzId));
        cal.setTimeInMillis(mTime);
        return (GregorianCalendar) cal;
    }
}
