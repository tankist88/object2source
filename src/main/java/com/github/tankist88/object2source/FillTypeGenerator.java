package com.github.tankist88.object2source;

import com.github.tankist88.object2source.dto.ProviderResult;

public interface FillTypeGenerator extends TypeGenerator {
    /**
     * Generate method source code for fill instance of object
     * @param obj - object for source code generation
     * @return ProviderResult which store generated source code for fill instance of object
     * and Set of needed support methods
     */
    ProviderResult createFillObjectMethod(Object obj);
}
