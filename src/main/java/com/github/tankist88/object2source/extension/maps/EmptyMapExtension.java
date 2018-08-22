package com.github.tankist88.object2source.extension.maps;

import com.github.tankist88.object2source.dto.ProviderInfo;

import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.getClassHierarchyStr;

public class EmptyMapExtension extends AbstractMapExtension {
    @Override
    public String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) {
        return getTabSymb() + getTabSymb() + "return java.util.Collections.emptyMap();\n";
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.Map";
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$EmptyMap");
    }
}
