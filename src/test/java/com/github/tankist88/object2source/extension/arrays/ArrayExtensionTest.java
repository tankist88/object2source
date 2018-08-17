package com.github.tankist88.object2source.extension.arrays;

import com.github.tankist88.object2source.SourceGenerator;
import com.github.tankist88.object2source.TestObj;
import com.github.tankist88.object2source.dto.ProviderInfo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.UUID;

import static org.testng.Assert.*;

public class ArrayExtensionTest {
    private static final String[] RESULTS = new String[] {
            "boolean[]array=newboolean[1];array[0]=false;returnarray;",
            "byte[]array=newbyte[1];array[0]=(byte)0;returnarray;",
            "char[]array=newchar[1];array[0]='g';returnarray;",
            "short[]array=newshort[1];array[0]=(short)0;returnarray;",
            "int[]array=newint[1];array[0]=0;returnarray;",
            "long[]array=newlong[1];array[0]=0L;returnarray;",
            "float[]array=newfloat[1];array[0]=0.0f;returnarray;",
            "double[]array=newdouble[1];array[0]=0.0d;returnarray;",
            "java.lang.String[]array=newjava.lang.String[1];array[0]=null;returnarray;",
            "java.math.BigDecimal[]array=newjava.math.BigDecimal[1];array[0]=null;returnarray;",
            "java.math.BigInteger[]array=newjava.math.BigInteger[1];array[0]=null;returnarray;",
            "java.sql.Timestamp[]array=newjava.sql.Timestamp[1];array[0]=null;returnarray;",
            "java.sql.Time[]array=newjava.sql.Time[1];array[0]=null;returnarray;",
            "java.util.Date[]array=newjava.util.Date[1];array[0]=null;returnarray;",
            "java.sql.Date[]array=newjava.sql.Date[1];array[0]=null;returnarray;",
            "java.util.UUID[]array=newjava.util.UUID[1];array[0]=null;returnarray;",
            "java.util.Calendar[]array=newjava.util.Calendar[1];array[0]=null;returnarray;",
            "com.github.tankist88.object2source.TestObj[]array=" +
                    "newcom.github.tankist88.object2source.TestObj[1];array[0]=null;returnarray;",
            "java.lang.Object[]array=(java.lang.Object[])java.lang.reflect.Array.newInstance(Class.forName(" +
                    "\"com.github.tankist88.object2source.extension.arrays.ArrayExtensionTest$PrivateClassForArray\"" +
                    "),1);array[0]=null;returnarray;"
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
    public void fillMethodBodyTest(Object array, String result) throws Exception {
        StringBuilder sb = new StringBuilder();
        ae.fillMethodBody(sb, new HashSet<ProviderInfo>(), 20, array);
        String generated = sb.toString()
                .replace("\n","")
                .replace("\r","")
                .replace(" ", "");
//        System.out.println("\"" + generated + "\",");
        assertEquals(generated, result);
    }

    @DataProvider
    public Object[][] arraysDataProvider() {
        return new Object[][] {
                {Array.newInstance(boolean.class, 1), RESULTS[0]},
                {Array.newInstance(byte.class, 1), RESULTS[1]},
                {new char[] {'g'}, RESULTS[2]},
                {Array.newInstance(short.class, 1), RESULTS[3]},
                {Array.newInstance(int.class, 1), RESULTS[4]},
                {Array.newInstance(long.class, 1), RESULTS[5]},
                {Array.newInstance(float.class, 1), RESULTS[6]},
                {Array.newInstance(double.class, 1), RESULTS[7]},
                {Array.newInstance(String.class, 1), RESULTS[8]},
                {Array.newInstance(BigDecimal.class, 1), RESULTS[9]},
                {Array.newInstance(BigInteger.class, 1), RESULTS[10]},
                {Array.newInstance(Timestamp.class, 1), RESULTS[11]},
                {Array.newInstance(Time.class, 1), RESULTS[12]},
                {Array.newInstance(java.util.Date.class, 1), RESULTS[13]},
                {Array.newInstance(java.sql.Date.class, 1), RESULTS[14]},
                {Array.newInstance(UUID.class, 1), RESULTS[15]},
                {Array.newInstance(Calendar.class, 1), RESULTS[16]},
                {Array.newInstance(TestObj.class, 1), RESULTS[17]},
                {Array.newInstance(PrivateClassForArray.class, 1), RESULTS[18]}
        };
    }

    private static class PrivateClassForArray {
    }
}
