package org.object2source.extension.collections;

import org.object2source.dto.ProviderInfo;

import java.util.AbstractCollection;
import java.util.Set;

import static org.object2source.util.ExtensionUtil.getCollectionWrappedType;
import static org.object2source.util.GenerationUtil.*;

public class UnmodCollectionExtension extends AbstractCollectionExtension {
    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        Class realType = getCollectionWrappedType(obj, AbstractCollection.class, getClassHierarchy(obj.getClass()));

        String methodName = "unmodifiableCollection";
        if(getInterfacesHierarchyStr(realType).contains("java.util.List")) {
            methodName = "unmodifiableList";
        } else if(getInterfacesHierarchyStr(realType).contains("java.util.Set")) {
            methodName = "unmodifiableSet";
        }

        createAbstractCollectionInstance(obj, bb, providers, realType, objectDepth);

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append("java.util.Collections.")
          .append(methodName)
          .append("(")
          .append(getInstName(realType))
          .append(")")
          .append(";\n");
    }

    @Override
    public String getActualType(Object obj) {
        try {
            Class unmodifiableCollectionType = getCollectionWrappedType(obj, AbstractCollection.class, getClassHierarchy(obj.getClass()));
            if (getInterfacesHierarchyStr(unmodifiableCollectionType).contains("java.util.List")) {
                return "java.util.List";
            } else if (getInterfacesHierarchyStr(unmodifiableCollectionType).contains("java.util.Set")) {
                return "java.util.Set";
            } else {
                return "java.util.Collection";
            }
        } catch (IllegalAccessException iae) {
            return "";
        }
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return getClassHierarchyStr(clazz).contains("java.util.Collections$UnmodifiableCollection");
    }
}
