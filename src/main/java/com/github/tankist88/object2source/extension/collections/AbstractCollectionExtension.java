package com.github.tankist88.object2source.extension.collections;

import com.github.tankist88.object2source.dto.InstanceCreateData;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.extension.AbstractEmbeddedExtension;
import com.github.tankist88.object2source.util.GenerationUtil;

import java.util.Collection;
import java.util.Set;

public abstract class AbstractCollectionExtension extends AbstractEmbeddedExtension {
    public void createAbstractCollectionInstance(Object obj, StringBuilder sb, Set<ProviderInfo> providers, int objectDepth) throws Exception {
        createAbstractCollectionInstance(obj, sb, providers, obj.getClass(), objectDepth);
    }

    public void createAbstractCollectionInstance(Object obj, StringBuilder sb, Set<ProviderInfo> providers, Class collectionClass, int objectDepth) throws Exception {
        sb.append(getTabSymb())
          .append(getTabSymb())
          .append(GenerationUtil.createInstStr(collectionClass, sourceGenerator.getCommonMethodsClassName()))
          .append("\n");
        for(Object o : (Collection) obj) {
            if(o == null) continue;
            InstanceCreateData data = sourceGenerator.getInstanceCreateData(o, objectDepth);
            sb.append(getTabSymb())
              .append(getTabSymb())
              .append(GenerationUtil.getInstName(collectionClass))
              .append(".")
              .append("add(")
              .append(data.getInstanceCreator())
              .append(");\n");
            providers.addAll(data.getDataProviderMethods());
        }
    }
}
