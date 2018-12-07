package pojo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class Handler {
    private Object object;
    private Method method;
    private Pattern pattern;
    private List<UrlParameter> params; // 自己实现
    private Class<?> returnType;
    public Handler() {
    }

    public Handler(Object object, Method method, Pattern pattern, List<UrlParameter> params, Class<?> returnType) {
        this.object = object;
        this.method = method;
        this.pattern = pattern;
        this.params = params;
        this.returnType = returnType;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<UrlParameter> getParams() {
        return params;
    }

    public void setParams(List<UrlParameter> params) {
        this.params = params;
    }
}
