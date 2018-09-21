package com.github.tankist88.object2source.dto;

import java.io.Serializable;

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
        if (o == null || getClass() != o.getClass()) return false;
        ProviderInfo that = (ProviderInfo) o;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        return methodBody != null ? methodBody.equals(that.methodBody) : that.methodBody == null;
    }

    @Override
    public int hashCode() {
        int result = methodName != null ? methodName.hashCode() : 0;
        result = 31 * result + (methodBody != null ? methodBody.hashCode() : 0);
        return result;
    }
}
