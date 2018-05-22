package org.object2source.extension;

import org.object2source.SourceGenerator;

public abstract class AbstractEmbeddedExtension implements EmbeddedExtension {
    protected SourceGenerator sourceGenerator;

    public void setSourceGenerator(SourceGenerator sourceGenerator) {
        this.sourceGenerator = sourceGenerator;
    }

    protected String getTabSymb() {
        return sourceGenerator.getTabSymb();
    }
}
