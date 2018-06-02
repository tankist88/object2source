package org.object2source;

import org.object2source.dto.InstanceCreateData;
import org.object2source.dto.ProviderInfo;
import org.object2source.dto.ProviderResult;
import org.object2source.test.Cyclic1;
import org.object2source.test.Cyclic2;
import org.object2source.test.PrivateStaticClassTest;
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

        testObj.arrayListOfArrayList = new ArrayList<>();
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

        ProviderResult pr = null;
        int count;
        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (count = 0; count < 100; count++) {
            TestObj obj = createTestObj();
            long start = System.currentTimeMillis();
            pr = sg.createDataProviderMethod(obj);
            long elapsed = (System.currentTimeMillis() - start);
            sum += elapsed;
            if(elapsed < min) min = elapsed;
            if(elapsed > max) max = elapsed;
        }

        for(ProviderInfo info : pr.getProviders()) {
            System.out.println(info.getMethodBody());
        }

        System.out.println("Avg. generation time: " + ((double) sum)/(double)count + " ms");
        System.out.println("Min. generation time: " + min + " ms");
        System.out.println("Max. generation time: " + max + " ms");
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
        assertEquals(sg.isExceptionWhenMaxODepth(), true);
        sg.setExceptionWhenMaxODepth(false);
        assertEquals(sg.isExceptionWhenMaxODepth(), false);
    }

    @Test
    public void testGetPackageExclusions() {
        SourceGenerator sg;
        sg = new SourceGenerator();
        assertEquals(sg.getPackageExclusions().size(), 0);
        sg = new SourceGenerator("    ", new HashSet<>(Arrays.asList("a.b","c.d")));
        assertEquals(sg.getPackageExclusions().size(), 2);
    }

    @Test
    public void testSingletonList() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(Collections.singletonList(1.5d));
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
        assertTrue(pr.getEndPoint().getMethodBody().contains("char[] array = new char[5];"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[0] = '\\\\';"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[1] = '\\t';"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[2] = '\\n';"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[3] = '$';"));
        assertTrue(pr.getEndPoint().getMethodBody().contains("array[4] = '%';"));
    }

    @Test
    public void privateStaticClassTest() {
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(PrivateStaticClassTest.getTestClass());
        for(ProviderInfo pi : pr.getProviders()) {
            if(pi.getMethodName().startsWith("getNotPublic")) {
                assertTrue(pi.getMethodBody().contains("" +
                        "org.object2source.test.AbstractPublic _notPublic = " +
                        "(org.object2source.test.AbstractPublic) callConstructorReflection(" +
                        "Class.forName(\"org.object2source.test.NotPublic\"));"));
            } else if(pi.getMethodName().startsWith("getExamplePackagePrivateList")) {
                assertTrue(pi.getMethodBody().contains("" +
                        "java.util.ArrayList _examplePackagePrivateList = " +
                        "(java.util.ArrayList) callConstructorReflection(" +
                        "Class.forName(\"org.object2source.test.ExamplePackagePrivateList\"));"));
            } else if(pi.getMethodName().startsWith("getPrivateStaticClassTestTestClass")) {
                assertTrue(pi.getMethodBody().contains(
                        "org.object2source.test.TestClassInt _privateStaticClassTestTestClass = " +
                        "(org.object2source.test.TestClassInt) newInstanceHard(" +
                        "Class.forName(\"org.object2source.test.PrivateStaticClassTest$TestClass\"));"));
                assertTrue(pi.getMethodBody().contains("notPublicAssignment(_privateStaticClassTestTestClass, \"id\", 1);"));
                assertTrue(pi.getMethodBody().contains("notPublicAssignment(_privateStaticClassTestTestClass, \"name\", \"ggg\");"));
            } else if(pi.getMethodName().startsWith("getArray")) {
                assertTrue(pi.getMethodBody().contains("" +
                        "java.lang.Object[] array = " +
                        "(java.lang.Object[]) java.lang.reflect.Array.newInstance(" +
                        "Class.forName(\"org.object2source.test.NotPublic\"), 10);"));
            } else if(pi.getMethodName().startsWith("getPrivateStaticClassTestPrivateConstructor")) {
                assertTrue(pi.getMethodBody().contains("" +
                        "org.object2source.test.PrivateStaticClassTest.PrivateConstructor _privateStaticClassTestPrivateConstructor = " +
                        "(org.object2source.test.PrivateStaticClassTest.PrivateConstructor) callConstructorReflection(" +
                        "org.object2source.test.PrivateStaticClassTest.PrivateConstructor.class);"));
            }
        }
    }

    @Test
    public void cyclicReferenceTest() {
        Cyclic1 c1 = new Cyclic1();
        Cyclic2 c2 = new Cyclic2();
        c2.setField(c1);
        c2.setId(123);
        c1.setField(c2);
        c1.setName("ggg");

        SourceGenerator sg1 = new SourceGenerator();
        ProviderResult pr1 = sg1.createDataProviderMethod(c1);
        assertEquals(pr1, null);

        SourceGenerator sg2 = new SourceGenerator();
        sg2.setExceptionWhenMaxODepth(false);
        ProviderResult pr2 = sg2.createDataProviderMethod(c1);
        assertNotEquals(pr2, null);
    }

    @Test
    public void finalFieldTest() {
        TestObj tObj = new TestObj();
        SourceGenerator sg = new SourceGenerator();
        ProviderResult pr = sg.createDataProviderMethod(tObj);
        assertTrue(pr.getEndPoint().getMethodBody().contains("notPublicAssignment(_testObj, \"finalTest\", \"ggg\");"));
    }
}
