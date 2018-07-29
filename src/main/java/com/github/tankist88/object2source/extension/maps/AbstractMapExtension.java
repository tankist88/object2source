package com.github.tankist88.object2source.extension.maps;

import com.github.tankist88.object2source.dto.InstanceCreateData;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.extension.AbstractEmbeddedExtension;
import com.github.tankist88.object2source.util.GenerationUtil;

import java.util.Map;
import java.util.Set;

public abstract class AbstractMapExtension extends AbstractEmbeddedExtension {
    public void createAbstractMapInstance(Object obj, StringBuilder sb, Set<ProviderInfo> providers, int objectDepth) throws Exception {
        createAbstractMapInstance(obj, sb, providers, obj.getClass(), objectDepth);
    }

    public void createAbstractMapInstance(Object obj, StringBuilder sb, Set<ProviderInfo> providers, Class collectionClass, int objectDepth) throws Exception {
        sb.append(getTabSymb())
          .append(getTabSymb())
          .append(GenerationUtil.createInstStr(collectionClass, sourceGenerator.getCommonMethodsClassName()))
          .append("\n");

        for (Object o : ((Map) obj).entrySet()) {
            Map.Entry entry = (Map.Entry) o;

            InstanceCreateData dataKey = sourceGenerator.getInstanceCreateData(entry.getKey(), objectDepth);
            InstanceCreateData dataValue = sourceGenerator.getInstanceCreateData(entry.getValue(), objectDepth);

            sb.append(getTabSymb())
              .append(getTabSymb())
              .append(GenerationUtil.getInstName(collectionClass))
              .append(".")
              .append("put(")
              .append(dataKey.getInstanceCreator())
              .append(", ")
              .append(dataValue.getInstanceCreator())
              .append(");\n");

            providers.addAll(dataKey.getDataProviderMethods());
            providers.addAll(dataValue.getDataProviderMethods());
        }
    }
}
