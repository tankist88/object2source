package org.object2source.extension.collections;

import org.object2source.dto.InstanceCreateData;
import org.object2source.dto.ProviderInfo;
import org.object2source.extension.AbstractEmbeddedExtension;

import java.util.Collection;
import java.util.Set;

import static org.object2source.util.GenerationUtil.createInstStr;
import static org.object2source.util.GenerationUtil.getInstName;

abstract class AbstractCollectionExtension extends AbstractEmbeddedExtension {
    void createAbstractCollectionInstance(Object obj, StringBuilder sb, Set<ProviderInfo> providers, int objectDepth) throws Exception {
        createAbstractCollectionInstance(obj, sb, providers, obj.getClass(), objectDepth);
    }

    void createAbstractCollectionInstance(Object obj, StringBuilder sb, Set<ProviderInfo> providers, Class collectionClass, int objectDepth) throws Exception {
        sb.append(getTabSymb())
          .append(getTabSymb())
          .append(createInstStr(collectionClass, sourceGenerator.getCommonMethodsClassName()))
          .append("\n");
        for(Object o : (Collection) obj) {
            if(o == null) continue;
            InstanceCreateData data = sourceGenerator.getInstanceCreateData(o, objectDepth);
            sb.append(getTabSymb())
              .append(getTabSymb())
              .append(getInstName(collectionClass))
              .append(".")
              .append("add(")
              .append(data.getInstanceCreator())
              .append(");\n");
            providers.addAll(data.getDataProviderMethods());
        }
    }
}
