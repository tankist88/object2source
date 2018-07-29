package com.github.tankist88.object2source.extension.collections;

import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.util.GenerationUtil;

import java.util.Set;

public class ArraysArrayListExtension extends AbstractCollectionExtension {
    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        createAbstractCollectionInstance(obj, bb, providers, java.util.ArrayList.class, objectDepth);
        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append(GenerationUtil.getInstName(java.util.ArrayList.class))
          .append(";\n");
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.List";
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return GenerationUtil.getClassHierarchyStr(clazz).contains("java.util.Arrays$ArrayList");
    }
}
