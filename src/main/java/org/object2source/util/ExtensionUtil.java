package org.object2source.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class ExtensionUtil {
    public static Class getCollectionWrappedType(Object obj, Class collectionParent, List<Class> classHierarchy) throws IllegalAccessException {
        if(obj == null) return null;
        for(Field f : GenerationUtil.getAllFieldsOfClass(classHierarchy)) {
            boolean deniedModifier =
                    Modifier.isStatic(f.getModifiers()) ||
                    Modifier.isNative(f.getModifiers());
            Object value = !deniedModifier ? GenerationUtil.getFieldValue(f, obj) : null;
            if(value != null && (GenerationUtil.getClassHierarchy(value.getClass()).contains(collectionParent))) {
                return value.getClass();
            }
        }
        return null;
    }
}
