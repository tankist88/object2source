package org.object2source.extension.maps;

import org.object2source.dto.InstanceCreateData;
import org.object2source.dto.ProviderInfo;
import org.object2source.extension.AbstractEmbeddedExtension;

import java.util.Map;
import java.util.Set;

import static org.object2source.util.GenerationUtil.createInstStr;
import static org.object2source.util.GenerationUtil.getInstName;

public abstract class AbstractMapExtension extends AbstractEmbeddedExtension {
    public void createAbstractMapInstance(Object obj, StringBuilder sb, Set<ProviderInfo> providers, int objectDepth) throws Exception {
        createAbstractMapInstance(obj, sb, providers, obj.getClass(), objectDepth);
    }

    public void createAbstractMapInstance(Object obj, StringBuilder sb, Set<ProviderInfo> providers, Class collectionClass, int objectDepth) throws Exception {
        sb.append(getTabSymb())
          .append(getTabSymb())
          .append(createInstStr(collectionClass, sourceGenerator.getCommonMethodsClassName()))
          .append("\n");

        for (Object o : ((Map) obj).entrySet()) {
            Map.Entry entry = (Map.Entry) o;

            InstanceCreateData dataKey = sourceGenerator.getInstanceCreateData(entry.getKey(), objectDepth);
            InstanceCreateData dataValue = sourceGenerator.getInstanceCreateData(entry.getValue(), objectDepth);

            sb.append(getTabSymb())
              .append(getTabSymb())
              .append(getInstName(collectionClass))
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
