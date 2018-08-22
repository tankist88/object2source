package com.github.tankist88.object2source.extension.maps;

import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.util.ExtensionUtil;

import java.util.AbstractMap;
import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.*;

public class UnmodSortedMapExtension extends AbstractMapExtension {
    @Override
    public String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception {
        Class realType = ExtensionUtil.getCollectionWrappedType(obj, AbstractMap.class, getClassHierarchy(obj.getClass()));
        StringBuilder bb = new StringBuilder();
        createAbstractMapInstance(obj, bb, providers, realType, objectDepth);
        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append("java.util.Collections.unmodifiableSortedMap")
          .append("(")
          .append(getInstName(realType))
          .append(")")
          .append(";\n");
        return bb.toString();
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.SortedMap";
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$UnmodifiableSortedMap");
    }
}
