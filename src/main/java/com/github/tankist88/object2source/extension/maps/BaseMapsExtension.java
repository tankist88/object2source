package com.github.tankist88.object2source.extension.maps;

import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.util.GenerationUtil;

import java.util.Set;

import static java.lang.reflect.Modifier.isPublic;

public class BaseMapsExtension extends AbstractMapExtension {
    @Override
    public boolean isTypeSupported(Class clazz) {
        return GenerationUtil.getClassHierarchyStr(clazz).contains(java.util.AbstractMap.class.getName());
    }

    @Override
    public String getActualType(Object obj) {
        return !isPublic(obj.getClass().getModifiers()) ? GenerationUtil.getFirstPublicType(obj.getClass()).getName() : obj.getClass().getName();
    }

    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        createAbstractMapInstance(obj, bb, providers, objectDepth);

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append(GenerationUtil.getInstName(obj.getClass()))
          .append(";\n");
    }
}
