package com.github.tankist88.object2source.extension;

import com.github.tankist88.object2source.dto.ProviderInfo;

import java.util.Set;

public interface Extension {
    /**
     * Check support source code generation for class with this extension
     * @param clazz - class for which need to check source code generation support
     * @return true if code generation supported, false otherwise
     */
    boolean isTypeSupported(Class<?> clazz);

    /**
     * Fill body of method which generate source code for create instance of object
     * @param bb - method body builder
     * @param providers - Set which contains support methods
     * @param objectDepth - counter for control stack size
     * @param obj - object for which to be generated source code for create instance
     * @throws Exception - if we have an error
     */
    void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception;

    /**
     * Return an actual data type of class for method return signature
     * @param obj - object for which to be generated source code for create instance
     * @return Actual data type of class for method return signature
     */
    String getActualType(Object obj);
}
