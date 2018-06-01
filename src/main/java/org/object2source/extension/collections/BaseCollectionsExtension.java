package org.object2source.extension.collections;

import org.object2source.dto.ProviderInfo;

import java.util.Set;

import static java.lang.reflect.Modifier.isPublic;
import static org.object2source.util.GenerationUtil.getClassHierarchyStr;
import static org.object2source.util.GenerationUtil.getFirstPublicType;
import static org.object2source.util.GenerationUtil.getInstName;

public class BaseCollectionsExtension extends AbstractCollectionExtension {
    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains(java.util.AbstractCollection.class.getName());
    }

    @Override
    public String getActualType(Object obj) {
        return !isPublic(obj.getClass().getModifiers()) ? getFirstPublicType(obj.getClass()).getName() : obj.getClass().getName();
    }

    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        createAbstractCollectionInstance(obj, bb, providers, objectDepth);

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append(getInstName(obj.getClass()))
          .append(";\n");
    }
}
