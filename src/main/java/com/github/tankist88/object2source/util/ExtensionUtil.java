package com.github.tankist88.object2source.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

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
}
