package com.github.tankist88.object2source.extension.maps;

import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.util.ExtensionUtil;
import com.github.tankist88.object2source.util.GenerationUtil;

import java.util.AbstractMap;
import java.util.Set;

public class UnmodMapExtension extends AbstractMapExtension {
    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        Class realType = ExtensionUtil.getCollectionWrappedType(obj, AbstractMap.class, GenerationUtil.getClassHierarchy(obj.getClass()));

        createAbstractMapInstance(obj, bb, providers, realType, objectDepth);

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append("java.util.Collections.unmodifiableMap")
          .append("(")
          .append(GenerationUtil.getInstName(realType))
          .append(")")
          .append(";\n");
    }

    @Override
    public String getActualType(Object obj) {
        return "java.util.Map";
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return GenerationUtil.getClassHierarchyStr(clazz).contains("java.util.Collections$UnmodifiableMap");
    }
}
