package org.object2source;

import org.object2source.util.GenerationUtil;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

public class GenerationUtilTest {
    @Test
    public void getPackageTest() {
        assertEquals(GenerationUtil.getPackage(Collections.emptyList().getClass().getName()), "java.util");
        assertEquals(GenerationUtil.getPackage(Integer.class.getName()), "java.lang");
    }

    @Test
    public void getClassShortTest() {
        assertEquals(GenerationUtil.getClassShort(TestUtil.class.getName()), "GenerationUtilTest.TestUtil");
        assertEquals(GenerationUtil.getClassShort(GenerationUtilTest.class.getName()), "GenerationUtilTest");
    }

    private static class TestUtil {
        @SuppressWarnings("unused")
        void setList(List<Integer> intList) {
        }
        @SuppressWarnings("unused")
        <T> List<Integer> returnList(List<T> list) {
            return null;
        }
    }

    @Test
    public void getInstNameTest() {
        assertFalse(GenerationUtil.getInstName(TestUtil.class).contains("."));
    }

    @Test
    public void isPrimitiveTest() {
        assertTrue(GenerationUtil.isPrimitive("int"));
        assertTrue(GenerationUtil.isPrimitive("boolean"));
        assertTrue(GenerationUtil.isPrimitive("long"));
        assertTrue(GenerationUtil.isPrimitive("double"));
        assertTrue(GenerationUtil.isPrimitive("float"));
        assertTrue(GenerationUtil.isPrimitive("char"));
        assertTrue(GenerationUtil.isPrimitive("short"));
        assertTrue(GenerationUtil.isPrimitive("byte"));

        assertFalse(GenerationUtil.isPrimitive(String.class.getName()));
    }

    @Test
    public void isWrapperTest() {
        assertTrue(GenerationUtil.isWrapper(Integer.class.getName()));
        assertTrue(GenerationUtil.isWrapper(Boolean.class.getName()));
        assertTrue(GenerationUtil.isWrapper(Long.class.getName()));
        assertTrue(GenerationUtil.isWrapper(Double.class.getName()));
        assertTrue(GenerationUtil.isWrapper(Float.class.getName()));
        assertTrue(GenerationUtil.isWrapper(Character.class.getName()));
        assertTrue(GenerationUtil.isWrapper(Short.class.getName()));
        assertTrue(GenerationUtil.isWrapper(Byte.class.getName()));

        assertFalse(GenerationUtil.isWrapper(String.class.getName()));
    }

    @Test
    public void getMethodArgGenericTypesTest() {
        Method method0 = null;
        for(Method m : GenerationUtil.getAllMethodsOfClass(GenerationUtil.getClassHierarchy(TestUtil.class))) {
            if(m.getName().equals("setList")) {
                method0 = m;
                break;
            }
        }
        assertNotEquals(method0, null);
        if(method0 != null) {
            assertTrue(GenerationUtil.getMethodArgGenericTypes(method0, 0).contains(Integer.class));
        }

        Method method1 = null;
        for(Method m : GenerationUtil.getAllMethodsOfClass(GenerationUtil.getClassHierarchy(TestUtil.class))) {
            if(m.getName().equals("returnList")) {
                method1 = m;
                break;
            }
        }
        assertNotEquals(method1, null);
        if(method1 != null) {
            assertTrue(GenerationUtil.getMethodArgGenericTypes(method1, 0).contains(Integer.class));
        }
    }
}
