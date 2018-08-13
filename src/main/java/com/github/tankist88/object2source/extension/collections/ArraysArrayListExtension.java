package com.github.tankist88.object2source.extension.collections;

import com.github.tankist88.object2source.dto.ProviderInfo;

import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.getClassHierarchyStr;
import static com.github.tankist88.object2source.util.GenerationUtil.getInstName;

public class ArraysArrayListExtension extends AbstractCollectionExtension {
    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception {
        createAbstractCollectionInstance(obj, bb, providers, java.util.ArrayList.class, objectDepth);
        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append(getInstName(java.util.ArrayList.class))
          .append(";\n");
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.List";
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Arrays$ArrayList");
    }
}
