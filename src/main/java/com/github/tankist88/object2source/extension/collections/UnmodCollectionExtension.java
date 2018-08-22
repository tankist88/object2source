package com.github.tankist88.object2source.extension.collections;

import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.util.ExtensionUtil;

import java.util.AbstractCollection;
import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.*;

public class UnmodCollectionExtension extends AbstractCollectionExtension {
    @Override
    public String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception {
        Class realType = ExtensionUtil.getCollectionWrappedType(obj, AbstractCollection.class, getClassHierarchy(obj.getClass()));

        String methodName = "unmodifiableCollection";
        if(getInterfacesHierarchyStr(realType).contains("java.util.List")) {
            methodName = "unmodifiableList";
        } else if(getInterfacesHierarchyStr(realType).contains("java.util.Set")) {
            methodName = "unmodifiableSet";
        }

        StringBuilder bb = new StringBuilder();
        createAbstractCollectionInstance(obj, bb, providers, realType, objectDepth);
        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append("java.util.Collections.")
          .append(methodName)
          .append("(")
          .append(getInstName(realType))
          .append(")")
          .append(";\n");
        return bb.toString();
    }

    @Override
    public String getActualType(Object obj) {
        try {
            Class unmodifiableCollectionType = ExtensionUtil.getCollectionWrappedType(obj, AbstractCollection.class, getClassHierarchy(obj.getClass()));
            if (getInterfacesHierarchyStr(unmodifiableCollectionType).contains("java.util.List")) {
                return "java.util.List";
            } else if (getInterfacesHierarchyStr(unmodifiableCollectionType).contains("java.util.Set")) {
                return "java.util.Set";
            } else {
                return "java.util.Collection";
            }
        } catch (IllegalAccessException iae) {
            return "";
        }
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$UnmodifiableCollection");
    }
}
