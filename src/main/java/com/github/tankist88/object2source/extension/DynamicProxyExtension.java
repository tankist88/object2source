package com.github.tankist88.object2source.extension;

import com.github.tankist88.object2source.dto.ProviderInfo;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.github.tankist88.object2source.util.ExtensionUtil.getActualClassName;
import static com.github.tankist88.object2source.util.ExtensionUtil.isDynamicProxy;
import static com.github.tankist88.object2source.util.GenerationUtil.*;

public class DynamicProxyExtension extends AbstractEmbeddedExtension {
    public static final List<String> PROXY_TYPES = Arrays.asList(
        "java.lang.reflect.Proxy", // JDK proxy
        "javassist.util.proxy.ProxyObject", // Javassist proxy
        "net.sf.cglib.proxy.Factory", // cglib proxy
        "com.ibm.ejs.container.BusinessLocalWrapper" // IBM WAS EJS Container
    );

    public static final List<String> HANDLER_TYPES = Arrays.asList(
        "java.lang.reflect.InvocationHandler",
        "javassist.util.proxy.MethodHandler",
        "net.sf.cglib.proxy.MethodInterceptor"
    );

    private static final List<String> DEC_PRIMITIVES = Arrays.asList(
            "int", "long", "char", "short", "byte"
    );

    private static final String DOUBLE_PRIMITIVE = "double";
    private static final String FLOAT_PRIMITIVE = "float";
    private static final String BOOL_PRIMITIVE = "boolean";

    @Override
    public boolean isTypeSupported(Class<?> clazz) {
        return isDynamicProxy(clazz);
    }

    @Override
    public String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception {
        addSyntheticTypeToProviders(obj.getClass(), providers);
        String typeName = createTypeName(obj.getClass());
        return getTabSymb() + getTabSymb() + "return new " + typeName + "();\n";
    }

    @Override
    public String getActualType(Object obj) {
        return createTypeName(obj.getClass());
    }
    
    private void addSyntheticTypeToProviders(Class<?> clazz, Set<ProviderInfo> providers) {
        ProviderInfo provider = new ProviderInfo();
        String typeName = createTypeName(clazz);
        provider.setMethodName(typeName + "()");
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append(getTabSymb()).append("public static class ").append(typeName).append(" implements ");
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        for (Class<?> intf : clazz.getInterfaces()) {
            if (PROXY_TYPES.contains(intf.getName())) continue;
            interfaces.add(intf);
        }
        Collections.sort(interfaces, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Iterator<Class<?>> intfIterator = interfaces.iterator();
        while (intfIterator.hasNext()) {
            bodyBuilder.append(intfIterator.next().getName());
            if (intfIterator.hasNext()) bodyBuilder.append(", ");
        }
        bodyBuilder.append(" {\n");
        for (Class intf : interfaces) {
            List<Method> methods = Arrays.asList(intf.getDeclaredMethods());
            Collections.sort(methods, new Comparator<Method>() {
                @Override
                public int compare(Method o1, Method o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            for (Method m : methods) {
                bodyBuilder.append(getTabSymb()).append(getTabSymb()).append("@Override\n")
                        .append(getTabSymb()).append(getTabSymb()).append("public").append(" ")
                        .append(getActualTypeStr(m.getReturnType())).append(" ").append(m.getName()).append("(");
                List<Type> parameters = Arrays.asList(m.getGenericParameterTypes());
                Iterator<Type> iterator = parameters.iterator();
                int argCounter = 0;
                while (iterator.hasNext()) {
                    Type p = iterator.next();
                    if (p instanceof ParameterizedType) {
                        bodyBuilder.append(p.toString()).append(" ").append("arg").append(argCounter);
                    } else if (p instanceof Class) {
                        bodyBuilder.append(getActualTypeStr((Class) p)).append(" ").append("arg").append(argCounter);
                    }
                    if (iterator.hasNext()) bodyBuilder.append(", ");
                    argCounter++;
                }
                bodyBuilder
                        .append(") {\n").append(createMethodBody(m.getReturnType()))
                        .append(getTabSymb()).append(getTabSymb()).append("}\n");
            }
        }
        bodyBuilder.append(getTabSymb()).append("}\n");
        provider.setMethodBody(bodyBuilder.toString());
        providers.add(provider);
    }
    
    private String createMethodBody(Class clazz) {
        StringBuilder bodyBuilder = new StringBuilder();
        if (!clazz.equals(Void.TYPE)) {
            if (DEC_PRIMITIVES.contains(clazz.getName())) {
                bodyBuilder.append(getTabSymb()).append(getTabSymb()).append(getTabSymb()).append("return 0;\n");
            } else if (DOUBLE_PRIMITIVE.equals(clazz.getName())) {
                bodyBuilder.append(getTabSymb()).append(getTabSymb()).append(getTabSymb()).append("return 0.0;\n");
            } else if (FLOAT_PRIMITIVE.equals(clazz.getName())) {
                bodyBuilder.append(getTabSymb()).append(getTabSymb()).append(getTabSymb()).append("return 0.0f;\n");
            } else if (BOOL_PRIMITIVE.equals(clazz.getName())) {
                bodyBuilder.append(getTabSymb()).append(getTabSymb()).append(getTabSymb()).append("return false;\n");
            } else {
                bodyBuilder.append(getTabSymb()).append(getTabSymb()).append(getTabSymb()).append("return null;\n");
            }
        }
        return bodyBuilder.toString();
    }
    
    private String getActualTypeStr(Class clazz) {
        String type;
        if (isWrapper(clazz.getName()) || clazz.getName().equals(String.class.getName())) {
            type = getClassShort(clazz.getName());
        } else if (clazz.isArray()) {
            type = getClearedClassName(getActualClassName(clazz));
        } else {
            type = clazz.getName();
        }
        return type;
    }

    private String createTypeName(Class<?> clazz) {
        StringBuilder typeNameBuilder = new StringBuilder();
        typeNameBuilder.append("Stub");
        List<String> interfaces = new ArrayList<String>();
        for (Class intf : clazz.getInterfaces()) {
            if (PROXY_TYPES.contains(intf.getName())) continue;
            interfaces.add(getClassShort(intf.getName()));
        }
        Collections.sort(interfaces, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        if (interfaces.size() == 1) {
            typeNameBuilder.append(upFirst(interfaces.get(0)));
        } else {
            for (String intf : interfaces) {
                int endIndex = intf.length() > 4 ? 4 : intf.length();
                typeNameBuilder.append(upFirst(intf.substring(0, endIndex)));
            }
        }
        return typeNameBuilder.toString();
    }
}
