package com.github.tankist88.object2source;

import com.github.tankist88.object2source.dto.InstanceCreateData;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.dto.ProviderResult;
import com.github.tankist88.object2source.exception.FillingNotSupportedException;
import com.github.tankist88.object2source.test.Cyclic1;
import com.github.tankist88.object2source.test.Cyclic2;
import com.github.tankist88.object2source.test.PrivateStaticClassTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.testng.Assert.*;

public class SourceGeneratorTest {
    private TestObj createTestObj() {
        String str = "text \"%ggg%\"<br />text<br />text";

        TestObj testObj = new TestObj(50000L, str);
        testObj.setNum(112);
        testObj.setLongNum(7777777L);
        testObj.setName("т\"ес\\т()|%" + testObj.getNum() + "%");
        testObj.noSetter = 10000L;
        testObj.uuid = UUID.randomUUID();
        testObj.unmodifList = Collections.unmodifiableList(new ArrayList<BigDecimal>());
        testObj.unmodifSet = Collections.unmodifiableSet(new HashSet<TestObj>());
        testObj.noConst = new TestObjNoEmptyConst(30, "blabla\r\n\r\nggg\r\ndddd\r\n");
        testObj.testObj2 = new TestObj2();
        testObj.emptyList = Collections.emptyList();
        testObj.emptySet = Collections.emptySet();
        testObj.emptyMap = Collections.emptyMap();

        testObj.arrayList = Arrays.asList(1,2,3,4,5,6,7);

        testObj.unmodifMap = Collections.unmodifiableMap(new HashMap<String, TestObj>());
        testObj.unmodifSortedMap = Collections.unmodifiableSortedMap(new TreeMap<String, TestObj>());

        testObj.arrayListOfArrayList = new ArrayList<List<Integer>>();
        testObj.arrayListOfArrayList.add(Arrays.asList(1,2,3,4,5,6,7));
        testObj.arrayListOfArrayList.add(Arrays.asList(1,2,3));
        testObj.arrayListOfArrayList.add(Arrays.asList(1,2));

        testObj.doubleList = Collections.singletonList(1.5d);

        testObj.getTestObjList().add(BigDecimal.ZERO);
        testObj.getTestObjList().add(new BigDecimal(50000.0));
        testObj.getTestObjSet().add(new TestObj());
        testObj.getTestObjMap().put("test", new TestObj());

        testObj.setTestObj(new TestObj());
        testObj.getTestObj().setName("fdasdasdasas");
        testObj.getTestObj().setTestObj(new TestObj());
        testObj.setCalendar((GregorianCalendar) java.util.Calendar.getInstance());
        testObj.calendar2 = java.util.Calendar.getInstance();
        testObj.setDate(new java.util.Date());
        testObj.setTestEnum(TestObj.TestEnum.V1);
        testObj.setInstanceCreateData(new InstanceCreateData());
        testObj.setBigNum(new BigDecimal(Math.PI));
        testObj.setCh('Б');
        testObj.setArr(new int[10]);
        testObj.getArr()[0] = 12;
        testObj.setTestObjArr(new TestObj[10]);
        testObj.getTestObjArr()[5] = new TestObj();
        testObj.setContentLength(123);
        testObj.charArr[0] = '\\';
        testObj.charArr[1] = '[';
        testObj.charArr[2] = ']';
        testObj.charArr[3] = '$';
        testObj.charArr[4] = '%';
        return testObj;
    }

    @Test
    public void testCreateDataProviderMethod() {
        SourceGenerator sg = new SourceGenerator();

        int count;
        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (count = 0; count < 100; count++) {
            TestObj obj = createTestObj();
            long start = System.currentTimeMillis();
            sg.createDataProviderMethod(obj);
            long elapsed = (System.currentTimeMillis() - start);
            sum += elapsed;
            if(elapsed < min) min = elapsed;
            if(elapsed > max) max = elapsed;
        }

        System.out.println("Avg. generation time: " + ((double) sum)/(double)count + " ms");
        System.out.println("Min. generation time: " + min + " ms");
        System.out.println("Max. generation time: " + max + " ms");
    }

    @Test
    public void testSetByteArrayMaxLength() {
        SourceGenerator sg = new SourceGenerator();
        assertEquals(sg.getByteArrayMaxLength(), SourceGenerator.DEFAULT_BYTE_ARRAY_MAX_LENGTH);
        sg.setByteArrayMaxLength(100);
        assertEquals(sg.getByteArrayMaxLength(), 100);
    }

    @Test
    public void testSetMaxObjectDepth() {
        SourceGenerator sg = new SourceGenerator();
        assertEquals(sg.getMaxObjectDepth(), SourceGenerator.DEFAULT_MAX_DEPTH);
        sg.setMaxObjectDepth(100);
        assertEquals(sg.getMaxObjectDepth(), 100);
    }

    @Test
    public void testSetExceptionWhenMaxODepth() {
        SourceGenerator sg = new SourceGenerator();
        assertTrue(sg.isExceptionWhenMaxODepth());
        sg.setExceptionWhenMaxODepth(false);
        assertFalse(sg.isExceptionWhenMaxODepth());
    }

    @Test
    public void testGetPackageExclusions() {
        SourceGenerator sg;
        sg = new SourceGenerator();
        assertEquals(sg.getAllowedPackages().size(), 0);
        sg = new SourceGenerator("    ", new HashSet<String>(Arrays.asList("a.b","c.d")));
        assertEquals(sg.getAllowedPackages().size(), 2);
    }

    @Test
    public void testSingletonList() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(Collections.singletonList(1.5d));
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("Collections.singletonList(1.5d)"));
    }

    @Test
    public void arraysGenerationTest() {
        char[] array = new char[5];
        array[0] = '\\';
        array[1] = '\t';
        array[2] = '\n';
        array[3] = '$';
        array[4] = '%';
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(array);
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("char[] array = new char[5];"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[0] = '\\\\';"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[1] = '\\t';"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[2] = '\\n';"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[3] = '$';"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[4] = '%';"));
    }

    @Test
    public void notPublicClassTest() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(PrivateStaticClassTest.getTestClass());
        assertFalse(pr.getEndPoint().isEmpty());
        for(ProviderInfo pi : pr.getProviders()) {
            if(pi.getMethodName().startsWith("getNotPublic")) {
                assertTrue(pi.getMethodBody().contains("" +
                        "com.github.tankist88.object2source.test.AbstractPublic _notPublic = " +
                        "(com.github.tankist88.object2source.test.AbstractPublic) callConstructorReflection(" +
                        "Class.forName(\"com.github.tankist88.object2source.test.NotPublic\"));"));
            } else if(pi.getMethodName().startsWith("getExamplePackagePrivateList")) {
                assertTrue(pi.getMethodBody().contains("" +
                        "java.util.ArrayList _examplePackagePrivateList = " +
                        "(java.util.ArrayList) callConstructorReflection(" +
                        "Class.forName(\"com.github.tankist88.object2source.test.ExamplePackagePrivateList\"));"));
            } else if(pi.getMethodName().startsWith("getPrivateStaticClassTestTestClass")) {
                assertTrue(pi.getMethodBody().contains(
                        "com.github.tankist88.object2source.test.TestClassInt _privateStaticClassTestTestClass = " +
                        "(com.github.tankist88.object2source.test.TestClassInt) newInstanceHard(" +
                        "Class.forName(\"com.github.tankist88.object2source.test.PrivateStaticClassTest$TestClass\"));"));
                assertTrue(pi.getMethodBody().contains("notPublicAssignment(_privateStaticClassTestTestClass, \"id\", 1);"));
                assertTrue(pi.getMethodBody().contains("notPublicAssignment(_privateStaticClassTestTestClass, \"name\", \"ggg\");"));
            } else if(pi.getMethodName().startsWith("getArray")) {
                boolean contains =
                    pi.getMethodBody().contains("" +
                        "java.lang.Object[] array = " +
                        "(java.lang.Object[]) java.lang.reflect.Array.newInstance(" +
                        "Class.forName(\"com.github.tankist88.object2source.test.NotPublic\"), 10);")
                    ||
                    pi.getMethodBody().contains("" +
                            "java.lang.Object[] array = " +
                            "(java.lang.Object[]) java.lang.reflect.Array.newInstance(" +
                            "Class.forName(\"com.github.tankist88.object2source.test.PrivateStaticClassTest$PrivateClassNoParents\"), 5);");
                assertTrue(contains);
            } else if(pi.getMethodName().startsWith("getPrivateStaticClassTestPrivateConstructor")) {
                assertTrue(pi.getMethodBody().contains("" +
                        "com.github.tankist88.object2source.test.PrivateStaticClassTest.PrivateConstructor _privateStaticClassTestPrivateConstructor = " +
                        "(com.github.tankist88.object2source.test.PrivateStaticClassTest.PrivateConstructor) callConstructorReflection(" +
                        "com.github.tankist88.object2source.test.PrivateStaticClassTest.PrivateConstructor.class);"));
            }
        }
    }


    @Test(expectedExceptions = IllegalStateException.class)
    public void cyclicReferenceExceptionTest() {
        Cyclic1 c1 = new Cyclic1();
        Cyclic2 c2 = new Cyclic2();
        c2.setField(c1);
        c2.setId(123);
        c1.setField(c2);
        c1.setName("ggg");
        SourceGenerator sg1 = new SourceGenerator();
        sg1.createDataProviderMethod(c1);
    }

    @Test
    public void cyclicReferenceSuccessTest() {
        Cyclic1 c1 = new Cyclic1();
        Cyclic2 c2 = new Cyclic2();
        c2.setField(c1);
        c2.setId(123);
        c1.setField(c2);
        c1.setName("ggg");
        SourceGenerator sg = new SourceGenerator();
        sg.setExceptionWhenMaxODepth(false);
        ProviderResult pr = sg.createDataProviderMethod(c1);
        assertNotEquals(pr, null);
    }

    @Test
    public void finalFieldTest() {
        TestObj tObj = new TestObj();
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(tObj);
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("notPublicAssignment(_testObj, \"finalTest\", \"ggg\");"));
    }

    @Test
    public void timeZoneTest() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(TimeZone.getTimeZone(TimeZone.getAvailableIDs()[0]));
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("return (sun.util.calendar.ZoneInfo) java.util.TimeZone.getTimeZone(\"Africa/Abidjan\");"));
    }

    @Test
    public void byteTest() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod((byte) 1);
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("return (byte) 1;"));
    }

    @Test
    public void shortTest() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod((short) 1);
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("return (short) 1;"));
    }

    @Test
    public void matrixIntArrayTest() {
        int[][][] array = new int[4][3][2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 2; k++) {
                    array[i][j][k] = 1;
                }
            }
        }
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(array);
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("int[][][] array = new int[4][3][2];"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[0] = getArray_868009510();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[1] = getArray_868009510();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[2] = getArray_868009510();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[3] = getArray_868009510();"));
    }

    @Test
    public void matrixBigDecimalArrayTest() {
        BigDecimal[][][] array = new BigDecimal[4][3][2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 2; k++) {
                    array[i][j][k] = new BigDecimal(1);
                }
            }
        }
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(array);
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("java.math.BigDecimal[][][] array = new java.math.BigDecimal[4][3][2];"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[0] = getArray__1118493302();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[1] = getArray__1118493302();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[2] = getArray__1118493302();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[3] = getArray__1118493302();"));
    }

    @Test
    public void matrixNotPublicArrayTest() {
        PrivateClassForArray[][][] array = new PrivateClassForArray[4][3][2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 2; k++) {
                    array[i][j][k] = new PrivateClassForArray();
                }
            }
        }
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(array);
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("java.lang.Object[][][] array = (java.lang.Object[][][]) " +
                "java.lang.reflect.Array.newInstance(" +
                "Class.forName(\"com.github.tankist88.object2source.SourceGeneratorTest$PrivateClassForArray\"), 4, 3, 2);"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[0] = getArray_546039418();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[1] = getArray_546039418();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[2] = getArray_546039418();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[3] = getArray_546039418();"));
    }
    private static class PrivateClassForArray {
    }

    @Test(dataProvider = "allowedTypeDataProvider")
    public void allowedTypeTest(Class clazz, boolean result) {
        SourceGenerator sg = new SourceGenerator();
        sg.getAllowedPackages().add("java.lang");
        assertEquals(sg.allowedType(clazz), result);
    }

    @DataProvider
    private Object[][] allowedTypeDataProvider() {
        return new Object[][] {
                {Boolean.class, true},
                {boolean.class, true},
                {(new int[10]).getClass(), true},
                {(new double[10]).getClass(), true},
                {(new byte[10]).getClass(), true},
                {(new byte[10][10]).getClass(), true},
                {(new byte[10][10][10]).getClass(), true},
                {ArrayList.class, false},
                {(new TestObj[10]).getClass(), false},
                {(new TestObj[10][10]).getClass(), false},
                {(new TestObj[10][10][10]).getClass(), false}
        };
    }

    @Test
    public void nullCollectionTest() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(Arrays.asList(null, null));
        assertFalse(pr.getEndPoint().isEmpty());
        assertNotNull(pr);
        assertNotNull(pr.getEndPoint());
        assertTrue(pr.getProviders().size() > 1);
        assertTrue(pr.getEndPoint().getMethodBody().contains("public static java.util.List getArraysArrayList_1238037610() throws Exception"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("java.util.ArrayList _arrayList = new java.util.ArrayList();"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("_arrayList.add(null);"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("return _arrayList;"));
    }

    @Test
    public void fillMethodBaseTest() throws FillingNotSupportedException {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createFillObjectMethod(new TestObj(123L, "toboty vpered!"));
        assertFalse(pr.getEndPoint().isEmpty());
        assertTrue(pr.getEndPoint().getMethodBody().contains("public static void fillTestObj__1815918072(" +
                "com.github.tankist88.object2source.TestObj _testObj) throws Exception"));
        assertFalse(pr.getEndPoint().getMethodBody().contains("return"));
        assertEquals(pr.getEndPoint().getMethodName(), "fillTestObj__1815918072(<var_name>)");
    }

    @Test(expectedExceptions = FillingNotSupportedException.class)
    public void fillMethodArrayTest() throws FillingNotSupportedException {
        SourceGenerator sg = new SourceGenerator();
        sg.createFillObjectMethod(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
    }

    @Test
    public void fillMethodPrimitiveTest() throws FillingNotSupportedException {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createFillObjectMethod(5);
        assertFalse(pr.getEndPoint().isEmpty());
        String generated = pr.getEndPoint().getMethodBody()
                .replace(" ", "")
                .replace("\n", "")
                .replace("\r", "")
                .replace("\t", "");
        assertEquals(generated, "publicstaticvoidfillInteger_201181279(java.lang.Integer_integer)" +
                "throwsException{return;}");
    }

    @Test
    public void fillMethodUnsupportedTypeTest() throws FillingNotSupportedException {
        SourceGenerator sg = new SourceGenerator();
        sg.getAllowedPackages().add("java.lang");
        assertNull(sg.createFillObjectMethod(new TestObj()));
    }

    @Test
    public void fillMethodUnsupportedMembersTest() throws FillingNotSupportedException {
        SourceGenerator sg = new SourceGenerator();
        sg.getAllowedPackages().add("com.github.tankist88.object2source.test.Cyclic1");
        ProviderResult pr = sg.createFillObjectMethod(new Cyclic1());
        assertTrue(pr.getEndPoint().isEmpty());
        String generated = pr.getEndPoint().getMethodBody()
                .replace(" ", "")
                .replace("\n", "")
                .replace("\r", "")
                .replace("\t", "");
        assertEquals(generated, "publicstaticvoidfillCyclic1_0" +
                "(com.github.tankist88.object2source.test.Cyclic1_cyclic1)throwsException{}");
    }

    @Test
    public void generateBigDecimalTest() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(new BigDecimal("1000.00"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("return new java.math.BigDecimal(\"1000.00\");"));
    }
}
