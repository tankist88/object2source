package org.object2source.extension.collections;

import org.object2source.dto.ProviderInfo;

import java.util.Set;

import static org.object2source.util.GenerationUtil.getClassHierarchyStr;

public class EmptySetExtension extends AbstractCollectionExtension {
    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append("java.util.Collections.emptySet()")
          .append(";\n");
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.Set";
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$EmptySet");
    }
}
