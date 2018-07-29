package com.github.tankist88.object2source;

import com.github.tankist88.object2source.extension.Extension;

public interface TypeGenerator extends BaseTypeGenerator {
    /**
     * Register extension for generation source code of specific object
     * @param extension - extension object to be registered
     */
    void registerExtension(Extension extension);

    /**
     * Find most suitable extension in early registered extensions for generate source code for object with class
     * @param clazz - class for witch need to find code generation extension
     * @return Extension for code generation or null if extension not found
     */
    Extension findExtension(Class clazz);
}
