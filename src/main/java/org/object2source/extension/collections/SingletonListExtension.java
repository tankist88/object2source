package org.object2source.extension.collections;

import org.object2source.dto.InstanceCreateData;
import org.object2source.dto.ProviderInfo;

import java.util.List;
import java.util.Set;

import static org.object2source.util.GenerationUtil.getClassHierarchyStr;

public class SingletonListExtension extends AbstractCollectionExtension {
    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$SingletonList");
    }

    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        Object element = ((List) obj).get(0);

        InstanceCreateData instData = sourceGenerator.getInstanceCreateData(element, objectDepth);

        providers.addAll(instData.getDataProviderMethods());

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append("java.util.Collections.singletonList")
          .append("(")
          .append(instData.getInstanceCreator())
          .append(")")
          .append(";\n");
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.List";
    }
}
