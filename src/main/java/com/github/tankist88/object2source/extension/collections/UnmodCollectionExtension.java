package com.github.tankist88.object2source.extension.collections;

import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.util.ExtensionUtil;
import com.github.tankist88.object2source.util.GenerationUtil;

import java.util.AbstractCollection;
import java.util.Set;

public class UnmodCollectionExtension extends AbstractCollectionExtension {
    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        Class realType = ExtensionUtil.getCollectionWrappedType(obj, AbstractCollection.class, GenerationUtil.getClassHierarchy(obj.getClass()));

        String methodName = "unmodifiableCollection";
        if(GenerationUtil.getInterfacesHierarchyStr(realType).contains("java.util.List")) {
            methodName = "unmodifiableList";
        } else if(GenerationUtil.getInterfacesHierarchyStr(realType).contains("java.util.Set")) {
            methodName = "unmodifiableSet";
        }

        createAbstractCollectionInstance(obj, bb, providers, realType, objectDepth);

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append("java.util.Collections.")
          .append(methodName)
          .append("(")
          .append(GenerationUtil.getInstName(realType))
          .append(")")
          .append(";\n");
    }

    @Override
    public String getActualType(Object obj) {
        try {
            Class unmodifiableCollectionType = ExtensionUtil.getCollectionWrappedType(obj, AbstractCollection.class, GenerationUtil.getClassHierarchy(obj.getClass()));
            if (GenerationUtil.getInterfacesHierarchyStr(unmodifiableCollectionType).contains("java.util.List")) {
                return "java.util.List";
            } else if (GenerationUtil.getInterfacesHierarchyStr(unmodifiableCollectionType).contains("java.util.Set")) {
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
        return GenerationUtil.getClassHierarchyStr(clazz).contains("java.util.Collections$UnmodifiableCollection");
    }
}
