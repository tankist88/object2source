package org.object2source.extension.maps;

import org.object2source.dto.ProviderInfo;

import java.util.AbstractMap;
import java.util.Set;

import static org.object2source.util.ExtensionUtil.getCollectionWrappedType;
import static org.object2source.util.GenerationUtil.getClassHierarchy;
import static org.object2source.util.GenerationUtil.getClassHierarchyStr;
import static org.object2source.util.GenerationUtil.getInstName;

public class UnmodMapExtension extends AbstractMapExtension {
    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        Class realType = getCollectionWrappedType(obj, AbstractMap.class, getClassHierarchy(obj.getClass()));

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
