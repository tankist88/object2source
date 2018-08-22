package com.github.tankist88.object2source.extension.arrays;

import com.github.tankist88.object2source.SourceGenerator;
import com.github.tankist88.object2source.TestObj;
import com.github.tankist88.object2source.dto.ProviderInfo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static org.testng.Assert.*;

public class ArrayExtensionTest {
    private static final String[] RESULTS = new String[] {
            "boolean[]array=newboolean[1];array[0]=true;returnarray;",
            "byte[]array=newbyte[1];array[0]=(byte)1;returnarray;",
            "char[]array=newchar[1];array[0]='g';returnarray;",
            "short[]array=newshort[1];array[0]=(short)1;returnarray;",
            "int[]array=newint[1];array[0]=1;returnarray;",
            "long[]array=newlong[1];array[0]=1L;returnarray;",
            "float[]array=newfloat[1];array[0]=1.0f;returnarray;",
            "double[]array=newdouble[1];array[0]=1.0d;returnarray;",
            "boolean[]array=newboolean[1];returnarray;",
            "byte[]array=newbyte[1];returnarray;",
            "char[]array=newchar[1];returnarray;",
            "short[]array=newshort[1];returnarray;",
            "int[]array=newint[1];returnarray;",
            "long[]array=newlong[1];returnarray;",
            "float[]array=newfloat[1];returnarray;",
            "double[]array=newdouble[1];returnarray;",
            "java.lang.String[]array=newjava.lang.String[1];array[0]=\"ggg\";returnarray;",
            "java.math.BigDecimal[]array=newjava.math.BigDecimal[1];" +
                    "array[0]=newjava.math.BigDecimal(1d);returnarray;",
            "java.math.BigInteger[]array=newjava.math.BigInteger[1];" +
                    "array[0]=newjava.math.BigInteger(\"1\");returnarray;",
            "java.sql.Timestamp[]array=newjava.sql.Timestamp[1];returnarray;",
            "java.sql.Time[]array=newjava.sql.Time[1];returnarray;",
            "java.util.Date[]array=newjava.util.Date[1];array[0]=newjava.util.Date(1534600034803L);returnarray;",
            "java.sql.Date[]array=newjava.sql.Date[1];returnarray;",
            "java.util.UUID[]array=newjava.util.UUID[1];returnarray;",
            "java.util.Calendar[]array=newjava.util.Calendar[1];" +
                    "array[0]=getCalendarInstance(\"Europe/Moscow\",1534600034819L);returnarray;",
            "com.github.tankist88.object2source.TestObj[]" +
                    "array=newcom.github.tankist88.object2source.TestObj[1];returnarray;",
            "java.lang.Object[]array=(java.lang.Object[])java.lang.reflect.Array.newInstance(Class.forName(" +
                    "\"com.github.tankist88.object2source.extension.arrays.ArrayExtensionTest$PrivateClassForArray\"" +
                    "),1);returnarray;",
            "int[][][]array=newint[2][2][2];array[0]=getArray__711776323();array[1]=getArray__549247319();returnarray;"
    };

    private ArraysExtension ae = new ArraysExtension();

    @BeforeClass
    public void init() {
        ae.setSourceGenerator(new SourceGenerator());
    }

    @Test
    public void isTypeSupportedTest() {
        assertTrue(ae.isTypeSupported((new int[0]).getClass()));
        assertFalse(ae.isTypeSupported(Integer.class));
    }

    @Test
    public void getActualTypeTest() {
        assertEquals(ae.getActualType(new int[0]), "int[]");
    }

    @Test(dataProvider = "arraysDataProvider")
    public void getMethodBodyTest(Object array, String result) throws Exception {
        String generated = ae.getMethodBody(new HashSet<ProviderInfo>(), 20, array, false)
                .replace("\n","")
                .replace("\r","")
                .replace(" ", "");
        assertEquals(generated, result);
    }

    @DataProvider
    public Object[][] arraysDataProvider() {
        return new Object[][] {
                {new boolean[] {true}, RESULTS[0]},
                {new byte[] {1}, RESULTS[1]},
                {new char[] {'g'}, RESULTS[2]},
                {new short[] {1}, RESULTS[3]},
                {new int[] {1}, RESULTS[4]},
                {new long[] {1L}, RESULTS[5]},
                {new float[] {1.0f}, RESULTS[6]},
                {new double[] {1.0d}, RESULTS[7]},
                {new boolean[1], RESULTS[8]},
                {new byte[1], RESULTS[9]},
                {new char[1], RESULTS[10]},
                {new short[1], RESULTS[11]},
                {new int[1], RESULTS[12]},
                {new long[1], RESULTS[13]},
                {new float[1], RESULTS[14]},
                {new double[1], RESULTS[15]},
                {new String[] {"ggg"}, RESULTS[16]},
                {new BigDecimal[] {BigDecimal.ONE}, RESULTS[17]},
                {new BigInteger[] {BigInteger.ONE}, RESULTS[18]},
                {new Timestamp[1], RESULTS[19]},
                {new Time[1], RESULTS[20]},
                {new java.util.Date[] {new java.util.Date(1534600034803L)}, RESULTS[21]},
                {new java.sql.Date[1], RESULTS[22]},
                {new UUID[1], RESULTS[23]},
                {new Calendar[] {getCalendarInstance("Europe/Moscow",1534600034819L)}, RESULTS[24]},
                {new TestObj[1], RESULTS[25]},
                {new PrivateClassForArray[1], RESULTS[26]},
                {new int[][][] {{{1,2},{3,4}},{{5,6},{7,8}}}, RESULTS[27]}
        };
    }

    private static class PrivateClassForArray {
    }

    private static GregorianCalendar getCalendarInstance(String tzId, long mTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(tzId));
        cal.setTimeInMillis(mTime);
        return (GregorianCalendar) cal;
    }
}
