package com.github.tankist88.object2source;

import com.github.tankist88.object2source.dto.ProviderResult;

public interface CreateTypeGenerator extends TypeGenerator {
    /**
     * Generate method source code for create instance of object
     * @param obj - object for source code generation
     * @return ProviderResult which store generated source code for create instance of object
     * and Set of needed support methods
     */
    ProviderResult createDataProviderMethod(Object obj);
}
