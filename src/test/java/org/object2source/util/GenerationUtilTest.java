package org.object2source.util;

import org.object2source.extension.collections.UnmodCollectionExtension;
import org.object2source.test.IntHierarchyTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Test
    public void getParameterizedTypesTest() {
        List<Class> types = GenerationUtil.getParameterizedTypes((new ArrayList<Integer>()).getClass());
        for(Class c : types) {
            System.out.println(c.getName());
        }
    }

    @Test
    public void getOwnerParentClassTest() {
        String actual1 = GenerationUtil.getOwnerParentClass("org.home.MyClass$InnerClass$1");
        assertEquals(actual1, "org.home.MyClass");
        String actual2 = GenerationUtil.getOwnerParentClass("org.home.MyClass$InnerClass");
        assertEquals(actual2, "org.home.MyClass");
        String actual3 = GenerationUtil.getOwnerParentClass("org.home.MyClass$1");
        assertEquals(actual3, "org.home.MyClass");
        String actual4 = GenerationUtil.getOwnerParentClass("org.home.MyClass");
        assertEquals(actual4, "org.home.MyClass");
    }

    @Test
    public void getAnonymousCallerClassTest() {
        String actual1 = GenerationUtil.getAnonymousCallerClass("org.home.MyClass$InnerClass$1$2");
        assertEquals(actual1, "org.home.MyClass$InnerClass");
        String actual2 = GenerationUtil.getAnonymousCallerClass("org.home.MyClass$InnerClass$1");
        assertEquals(actual2, "org.home.MyClass$InnerClass");
        String actual3 = GenerationUtil.getAnonymousCallerClass("org.home.MyClass$1");
        assertEquals(actual3, "org.home.MyClass");
        String actual4 = GenerationUtil.getAnonymousCallerClass("org.home.MyClass");
        assertEquals(actual4, "org.home.MyClass");
    }

    @Test
    public void isAnonymousClassTest() {
        assertTrue(GenerationUtil.isAnonymousClass("org.home.MyClass$1"));
        assertFalse(GenerationUtil.isAnonymousClass("org.home.MyClass"));
    }

    @Test
    public void getClassHierarchyTest() {
        List<String> hierarchy = GenerationUtil.getClassHierarchyStr(UnmodCollectionExtension.class);
        List<String> controlValues = Arrays.asList(
                "org.object2source.extension.collections.UnmodCollectionExtension",
                "org.object2source.extension.collections.AbstractCollectionExtension",
                "org.object2source.extension.AbstractEmbeddedExtension"
        );
        assertEquals(hierarchy.size(), controlValues.size(),
                "Returned class hierarchy size not equal expected size.");
        for (String s : controlValues) {
            assertTrue(hierarchy.contains(s), "Class " + s + " not found.");
        }
    }

    @Test
    public void getInterfacesHierarchyTest() {
        List<String> hierarchy = GenerationUtil.getInterfacesHierarchyStr(IntHierarchyTest.class);
        List<String> controlValues = Arrays.asList(
                "org.object2source.test.Int1",
                "org.object2source.test.Int2",
                "org.object2source.test.Int3"
        );
        assertEquals(hierarchy.size(), controlValues.size(),
                "Returned interface hierarchy size not equal expected size.");
        for (String s : controlValues) {
            System.out.println(s);
            assertTrue(hierarchy.contains(s), "Interface " + s + " not found.");
        }
    }

    @Test
    public void getFirstClassNameTest() {
        String cn = GenerationUtil.getFirstClassName("com.sun.proxy.$Proxy117");
        assertEquals(cn, "Proxy117");
    }
}
