package com.github.tankist88.object2source.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.github.tankist88.object2source.extension.DynamicProxyExtension.HANDLER_TYPES;
import static com.github.tankist88.object2source.extension.DynamicProxyExtension.PROXY_TYPES;
import static com.github.tankist88.object2source.util.GenerationUtil.*;

public class ExtensionUtil {
    public static Class getCollectionWrappedType(Object obj, Class collectionParent, List<Class> classHierarchy) throws IllegalAccessException {
        if(obj == null) return null;
        for(Field f : getAllFieldsOfClass(classHierarchy)) {
            boolean deniedModifier =
                    Modifier.isStatic(f.getModifiers()) ||
                    Modifier.isNative(f.getModifiers());
            Object value = !deniedModifier ? getFieldValue(f, obj) : null;
            if(value != null && (getClassHierarchy(value.getClass()).contains(collectionParent))) {
                return value.getClass();
            }
        }
        return null;
    }
    
    public static boolean isDynamicProxy(Class clazz) {
        boolean noInterfaces = clazz.getInterfaces() == null || clazz.getInterfaces().length == 0;
        boolean noSuperClasses = clazz.getSuperclass() == null || clazz.getSuperclass().getName().equals(Object.class.getName());
        if (noInterfaces && noSuperClasses) return false;
        List<String> parents = new ArrayList<String>();
        if (clazz.getInterfaces() != null) {
            for (Class intf : clazz.getInterfaces()) {
                parents.add(intf.getName());
            }
        }
        if (clazz.getSuperclass() != null) {
            parents.add(clazz.getSuperclass().getName());
        }
        for (String parent : parents) {
            if (PROXY_TYPES.contains(parent)) return true;
        }
        return false;
    }

    public static boolean isInvocationHandler(Class clazz) {
        boolean noInterfaces = clazz.getInterfaces() == null || clazz.getInterfaces().length == 0;
        if (noInterfaces) return false;
        List<String> parents = new ArrayList<String>();
        for (Class intf : clazz.getInterfaces()) {
            parents.add(intf.getName());
        }
        for (String parent : parents) {
            if (HANDLER_TYPES.contains(parent)) return true;
        }
        return false;
    }
}
