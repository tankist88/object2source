package org.object2source.extension;

import org.object2source.SourceGenerator;

public interface EmbeddedExtension extends Extension {
    /**
     * Source generate instance for support generation of common objects and access to other extensions
     * @param sourceGenerator - source generator instance
     */
    void setSourceGenerator(SourceGenerator sourceGenerator);
}
