package com.github.tankist88.object2source.dto;

import java.io.Serializable;
import java.util.Set;

public class ProviderResult implements Serializable {
    private ProviderInfo endPoint;
    private Set<ProviderInfo> providers;

    public ProviderInfo getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(ProviderInfo endPoint) {
        this.endPoint = endPoint;
    }

    public Set<ProviderInfo> getProviders() {
        return providers;
    }

    public void setProviders(Set<ProviderInfo> providers) {
        this.providers = providers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderResult that = (ProviderResult) o;
        if (endPoint != null ? !endPoint.equals(that.endPoint) : that.endPoint != null) return false;
        return providers != null ? providers.equals(that.providers) : that.providers == null;
    }

    @Override
    public int hashCode() {
        int result = endPoint != null ? endPoint.hashCode() : 0;
        result = 31 * result + (providers != null ? providers.hashCode() : 0);
        return result;
    }
}
