package com.github.tankist88.object2source.dto;

import java.io.Serializable;
import java.util.Objects;
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
        if (!(o instanceof ProviderResult)) return false;
        ProviderResult that = (ProviderResult) o;
        return Objects.equals(getEndPoint(), that.getEndPoint()) &&
                Objects.equals(getProviders(), that.getProviders());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEndPoint(), getProviders());
    }
}
