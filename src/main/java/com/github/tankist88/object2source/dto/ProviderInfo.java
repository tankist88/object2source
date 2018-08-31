package com.github.tankist88.object2source.dto;

import java.io.Serializable;
import java.util.Objects;

public class ProviderInfo implements Serializable {
    private String methodName;
    private String methodBody;
    private boolean empty;

    public ProviderInfo() {
    }

    public ProviderInfo(String methodName, String methodBody, boolean empty) {
        this.methodName = methodName;
        this.methodBody = methodBody;
        this.empty = empty;
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

    public boolean isEmpty() {
        return empty;
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
