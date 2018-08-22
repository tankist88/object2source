package com.github.tankist88.object2source.extension.maps;

import com.github.tankist88.object2source.dto.ProviderInfo;

import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.*;
import static java.lang.reflect.Modifier.isPublic;

public class BaseMapsExtension extends AbstractMapExtension {
    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains(java.util.AbstractMap.class.getName());
    }

    @Override
    public String getActualType(Object obj) {
        return !isPublic(obj.getClass().getModifiers()) ? getFirstPublicType(obj.getClass()).getName() : obj.getClass().getName();
    }

    @Override
    public String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception {
        StringBuilder bb = new StringBuilder();
        createAbstractMapInstance(obj, bb, providers, objectDepth);
        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append(getInstName(obj.getClass()))
          .append(";\n");
        return bb.toString();
    }
}
