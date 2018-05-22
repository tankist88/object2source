package org.object2source;

import org.object2source.dto.ProviderResult;

public interface BaseTypeGenerator {
    /**
     * Generate method source code for create instance of object
     * @param obj - object for source code generation
     * @return org.object2source.dto.ProviderResult which store generated source code for create instance of object
     * and Set of needed support methods
     */
    ProviderResult createDataProviderMethod(Object obj);
}
