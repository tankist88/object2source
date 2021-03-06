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
     * Return filling support status
     * @return true - extension supports filling objects
     */
    boolean isFillingSupported();

    /**
     * Fill body of method which generate source code for create instance of object
     * @param providers - Set which contains support methods
     * @param objectDepth - counter for control stack size
     * @param obj - object for which to be generated source code for create instance
     * @param fillObj - true - object will be filled, new instance not to be created, false - create new instance
     * @return string contains method body
     * @throws Exception - if we have an error
     */
    String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception;

    /**
     * Return an actual data type of class for method return signature
     * @param obj - object for which to be generated source code for create instance
     * @return Actual data type of class for method return signature
     */
    String getActualType(Object obj);
}
