package pojo;

import java.lang.reflect.Type;

public class UrlParameter {
    private String paramName; // 方法入参名称
    private Type parameterType; // 方法入参类型
    private String urlParameterName; // 对应的url的字段名称
    public UrlParameter(String paramName, Type parameterType, String urlParameterName) {
        this.paramName = paramName;
        this.parameterType = parameterType;
        this.urlParameterName = urlParameterName;
    }

    public UrlParameter() {
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Type getParameterType() {
        return parameterType;
    }

    public void setParameterType(Type parameterType) {
        this.parameterType = parameterType;
    }

    public String getUrlParameterName() {
        return urlParameterName;
    }

    public void setUrlParameterName(String urlParameterName) {
        this.urlParameterName = urlParameterName;
    }
}
