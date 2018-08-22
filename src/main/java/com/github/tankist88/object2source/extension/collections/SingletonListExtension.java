package com.github.tankist88.object2source.extension.collections;

import com.github.tankist88.object2source.dto.InstanceCreateData;
import com.github.tankist88.object2source.dto.ProviderInfo;

import java.util.List;
import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.getClassHierarchyStr;

public class SingletonListExtension extends AbstractCollectionExtension {
    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$SingletonList");
    }

    @Override
    public String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception {
        Object element = ((List) obj).get(0);
        InstanceCreateData instData = sourceGenerator.getInstanceCreateData(element, objectDepth);
        providers.addAll(instData.getDataProviderMethods());
        return getTabSymb() + getTabSymb() + "return java.util.Collections.singletonList(" +
                instData.getInstanceCreator() + ");\n";
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.List";
    }
}
