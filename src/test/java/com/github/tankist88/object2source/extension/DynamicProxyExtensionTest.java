package com.github.tankist88.object2source.extension;

import com.github.tankist88.object2source.SourceGenerator;
import com.github.tankist88.object2source.TestObj;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.test.DP1;
import com.github.tankist88.object2source.test.DP2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.tankist88.object2source.TestUtil.clearWhiteSpaces;
import static com.github.tankist88.object2source.util.ExtensionUtil.isInvocationHandler;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class DynamicProxyExtensionTest {
    private static final String EXPECTED_PROVIDER = "publicstaticclassProxyForDP1DP2implementscom.github.tankist88" +
            ".object2source.test.DP1,com.github.tankist88.object2source.test.DP2{@OverridepublicbytegetByte(){retu" +
            "rn0;}@OverridepublicshortgetShort(){return0;}@OverridepublicchargetChar(){return0;}@Overridepublicint" +
            "getInt(){return0;}@OverridepubliclonggetLong(){return0;}@OverridepublicfloatgetFloat(){return0.0f;}@O" +
            "verridepublicdoublegetDouble(){return0.0;}@OverridepublicbooleanisBoolean(){returnfalse;}@Overridepub" +
            "licIntegergetInteger(){returnnull;}@OverridepublicStringgetString(java.util.Setarg0,java.util.Setarg1" +
            "){returnnull;}@OverridepublicStringgetString(java.util.List<java.lang.Integer>arg0,java.util.Listarg1" +
            "){returnnull;}@Overridepubliccom.github.tankist88.object2source.TestObjgetTestObj(java.util.List<com." +
            "github.tankist88.object2source.TestObj>arg0){returnnull;}}";

    private DynamicProxyExtension ext = new DynamicProxyExtension();

    @BeforeClass
    public void init() {
        ext.setSourceGenerator(new SourceGenerator());
    }

    @Test
    public void isTypeSupportedTest() {
        assertTrue(ext.isTypeSupported(createDynamicProxy().getClass()));
        assertFalse(ext.isTypeSupported(Integer.class));
    }

    @Test
    public void isFillingSupportedTest() {
        assertFalse(ext.isFillingSupported());
    }

    @Test
    public void getMethodBodyTest() throws Exception {
        Set<ProviderInfo> providers = new HashSet<ProviderInfo>();
        Object proxy = createDynamicProxy();
        byte b = ((DP1) proxy).getByte();
        System.out.println(b);
        String generatedBody = clearWhiteSpaces(ext.getMethodBody(providers, 10, proxy, false));
        assertEquals(generatedBody, "returnnewProxyForDP1DP2();");
        assertEquals(providers.size(), 1);
        String generatedProvider = clearWhiteSpaces(providers.iterator().next().getMethodBody());
        assertEquals(generatedProvider, EXPECTED_PROVIDER);
    }

    @Test
    public void getActualTypeTest() {
        assertEquals(ext.getActualType(createDynamicProxy()), "ProxyForDP1DP2");
    }

    @Test
    public void isInvocationHandlerTest() {
        assertTrue(isInvocationHandler((new Handler(new User())).getClass()));
        assertFalse(isInvocationHandler(Integer.class));
        assertFalse(isInvocationHandler(Object.class));
    }

    private Object createDynamicProxy() {
        Handler handler = new Handler(new User());
        return Proxy.newProxyInstance(User.class.getClassLoader(), User.class.getInterfaces(), handler);
    }

    private class Handler implements InvocationHandler {
        private Object obj;

        private Handler(Object obj) {
            this.obj = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(obj, args);
        }
    }

    private static class User implements DP1, DP2 {
        @Override
        public int getInt() {
            return 0;
        }

        @Override
        public boolean isBoolean() {
            return false;
        }

        @Override
        public char getChar() {
            return 0;
        }

        @Override
        public byte getByte() {
            return 0;
        }

        @Override
        public long getLong() {
            return 0;
        }

        @Override
        public short getShort() {
            return 0;
        }

        @Override
        public double getDouble() {
            return 0;
        }

        @Override
        public float getFloat() {
            return 0;
        }

        @Override
        public String getString(List<Integer> list, List noGenList) {
            return null;
        }

        @Override
        public TestObj getTestObj(List<TestObj> list) {
            return null;
        }

        @Override
        public Integer getInteger() {
            return null;
        }

        @Override
        public String getString(Set set1, Set set2) {
            return null;
        }
    }
}
