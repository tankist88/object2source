package com.github.tankist88.object2source.extension.maps;

import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.util.ExtensionUtil;

import java.util.AbstractMap;
import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.*;

public class UnmodMapExtension extends AbstractMapExtension {
    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception {
        Class realType = ExtensionUtil.getCollectionWrappedType(obj, AbstractMap.class, getClassHierarchy(obj.getClass()));

        createAbstractMapInstance(obj, bb, providers, realType, objectDepth);

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append("java.util.Collections.unmodifiableMap")
          .append("(")
          .append(getInstName(realType))
          .append(")")
          .append(";\n");
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.Map";
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$UnmodifiableMap");
    }
}
