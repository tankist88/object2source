package com.github.tankist88.object2source.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class InstanceCreateData implements Serializable {
    private String instanceCreator;
    private Set<ProviderInfo> dataProviderMethods;

    public InstanceCreateData() {
    }

    public InstanceCreateData(String instanceCreator) {
        this.instanceCreator = instanceCreator;
    }

    public String getInstanceCreator() {
        return instanceCreator;
    }

    public void setInstanceCreator(String instanceCreator) {
        this.instanceCreator = instanceCreator;
    }

    public Set<ProviderInfo> getDataProviderMethods() {
        if(dataProviderMethods == null) {
            dataProviderMethods = new HashSet<ProviderInfo>();
        }
        return dataProviderMethods;
    }
}
