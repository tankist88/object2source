package org.object2source.extension.common;

import org.object2source.dto.InstanceCreateData;
import org.object2source.dto.ProviderInfo;
import org.object2source.extension.AbstractEmbeddedExtension;
import org.object2source.util.GenerationUtil;

import java.lang.reflect.Modifier;
import java.util.Set;

public class PrivateClassExtension extends AbstractEmbeddedExtension {
    @Override
    public boolean isTypeSupported(Class<?> clazz) {
        return Modifier.isPrivate(clazz.getModifiers());
    }

    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        bb.append(GenerationUtil.createInstStr(
                getFirstPublicType(obj),
                obj.getClass(),
                sourceGenerator.getCommonMethodsClassName())
        ).append("\n");

        InstanceCreateData instCrDt = sourceGenerator.generateObjInstance(obj, GenerationUtil.getClassHierarchy(obj.getClass()), objectDepth, false);
        bb.append(instCrDt.getInstanceCreator());

        providers.addAll(instCrDt.getDataProviderMethods());
    }

    @Override
    public String getActualType(Object obj) {
        return getFirstPublicType(obj).getName();
    }

    private Class getFirstPublicType(Object obj) {
        for(Class c : GenerationUtil.getClassHierarchy(obj.getClass())) {
            if(Modifier.isPublic(c.getModifiers()) && !Modifier.isAbstract(c.getModifiers())) {
                return c;
            }
        }
        return GenerationUtil.getInterfacesHierarchy(obj.getClass()).get(0);
    }
}
