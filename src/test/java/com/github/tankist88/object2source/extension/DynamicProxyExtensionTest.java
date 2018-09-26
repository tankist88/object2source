package com.github.tankist88.object2source.extension;

import com.github.tankist88.object2source.SourceGenerator;
import com.github.tankist88.object2source.TestObj;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.test.DP1;
import com.github.tankist88.object2source.test.DP2;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
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
    private static final String EXPECTED_PROVIDER = "publicstaticclassStubDP1DP2implementscom.github.tankist88.obj" +
            "ect2source.test.DP1,com.github.tankist88.object2source.test.DP2{@OverridepublicbooleanisBoolean(){ret" +
            "urnfalse;}@OverridepublicbytegetByte(){return0;}@Overridepublicbyte[]getByteArr(byte[]arg0){returnnul" +
            "l;}@OverridepublicchargetChar(){return0;}@OverridepublicdoublegetDouble(){return0.0;}@Overridepublicf" +
            "loatgetFloat(){return0.0f;}@OverridepublicintgetInt(){return0;}@OverridepubliclonggetLong(){return0;}" +
            "@OverridepublicshortgetShort(){return0;}@Overridepubliccom.github.tankist88.object2source.TestObjgetT" +
            "estObj(java.util.List<com.github.tankist88.object2source.TestObj>arg0){returnnull;}@OverridepublicInt" +
            "egergetInteger(){returnnull;}@OverridepublicStringgetString(java.util.List<java.lang.Integer>arg0,jav" +
            "a.util.Listarg1){returnnull;}@OverridepublicStringgetString(java.util.Setarg0,java.util.Setarg1){retu" +
            "rnnull;}}";

    private DynamicProxyExtension ext = new DynamicProxyExtension();

    @BeforeClass
    public void init() {
        ext.setSourceGenerator(new SourceGenerator());
    }

    @Test(dataProvider = "proxyProvider")
    public void isTypeSupportedTest(Object proxy) {
        assertTrue(ext.isTypeSupported(proxy.getClass()));
        assertFalse(ext.isTypeSupported(Integer.class));
    }

    @Test
    public void isFillingSupportedTest() {
        assertFalse(ext.isFillingSupported());
    }

    @Test(dataProvider = "proxyProvider")
    public void getMethodBodyTest(Object proxy) throws Exception {
        Set<ProviderInfo> providers = new HashSet<ProviderInfo>();
        byte b = ((DP1) proxy).getByte();
        assertEquals(b, 0);
        String generatedBody = clearWhiteSpaces(ext.getMethodBody(providers, 10, proxy, false));
        assertEquals(generatedBody, "returnnewStubDP1DP2();");
        assertEquals(providers.size(), 1);
        String generatedProvider = clearWhiteSpaces(providers.iterator().next().getMethodBody());
        assertEquals(generatedProvider, EXPECTED_PROVIDER);
    }

    @Test(dataProvider = "proxyProvider")
    public void getActualTypeTest(Object proxy) {
        assertEquals(ext.getActualType(proxy), "StubDP1DP2");
    }

    @Test(dataProvider = "handlerProvider")
    public void isInvocationHandlerTest(Object handler) {
        assertTrue(isInvocationHandler(handler.getClass()));
        assertFalse(isInvocationHandler(Integer.class));
        assertFalse(isInvocationHandler(Object.class));
    }

    @DataProvider
    public Object[][] proxyProvider() {
        return new Object[][] {
                { createDynamicProxyJDK() },
                { createDynamicProxyJavassist() },
                { createDynamicProxyCgLib() }
        };
    }

    @DataProvider
    public Object[][] handlerProvider() {
        return new Object[][] {
                { new HandlerJDK(new User()) },
                { new HandlerJavassist(new User()) },
                { new HandlerCgLib(new User()) }
        };
    }

    private Object createDynamicProxyJDK() {
        HandlerJDK handler = new HandlerJDK(new User());
        return Proxy.newProxyInstance(User.class.getClassLoader(), User.class.getInterfaces(), handler);
    }

    private Object createDynamicProxyJavassist() {
        try {
            HandlerJavassist handler = new HandlerJavassist(new User());
            ProxyFactory factory = new ProxyFactory();
            factory.setInterfaces(User.class.getInterfaces());
            Object instance = factory.createClass().newInstance();
            ((ProxyObject) instance).setHandler(handler);
            return instance;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Object createDynamicProxyCgLib() {
        HandlerCgLib handler = new HandlerCgLib(new User());
        return Enhancer.create(null, User.class.getInterfaces(), handler);
    }

    private class HandlerJDK implements InvocationHandler {
        private Object obj;

        private HandlerJDK(Object obj) {
            this.obj = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(obj, args);
        }
    }

    private class HandlerJavassist implements MethodHandler {
        private Object obj;

        private HandlerJavassist(Object obj) {
            this.obj = obj;
        }

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            return thisMethod.invoke(obj, args);
        }
    }

    private class HandlerCgLib implements MethodInterceptor {
        private Object obj;

        private HandlerCgLib(Object obj) {
            this.obj = obj;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return methodProxy.invoke(obj, objects);
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
        public byte[] getByteArr(byte[] arr) {
            return new byte[0];
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
