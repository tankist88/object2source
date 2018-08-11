package com.github.tankist88.object2source.dto;

import java.io.Serializable;
import java.util.Objects;

public class ProviderInfo implements Serializable {
    private String methodName;
    private String methodBody;

    public ProviderInfo() {
    }

    public ProviderInfo(String methodName, String methodBody) {
        this.methodName = methodName;
        this.methodBody = methodBody;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProviderInfo)) return false;
        ProviderInfo that = (ProviderInfo) o;
        return Objects.equals(getMethodName(), that.getMethodName()) &&
                Objects.equals(getMethodBody(), that.getMethodBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethodName(), getMethodBody());
    }
}
