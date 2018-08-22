package com.github.tankist88.object2source.extension.collections;

import com.github.tankist88.object2source.dto.ProviderInfo;

import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.getClassHierarchyStr;

public class EmptyListExtension extends AbstractCollectionExtension {
    @Override
    public String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) {
        return  getTabSymb() + getTabSymb() + "return java.util.Collections.emptyList();\n";
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.List";
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$EmptyList");
    }
}
