import pojo.*;
import spring.DispatcherServlet;
import spring.JavaTypeResolver;

import java.io.*;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

public class UrlVisitor {
    private JavaTypeResolver typeResolver = new JavaTypeResolver();
    public static void main(String[] args) {
        // 初始化参数
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.init();
        UrlVisitor urlVisitor = new UrlVisitor();
        // urlVisitor.call("https://www.baidu.com/test/sayHello?name=zhangsan");
        // urlVisitor.call("https://xxx/test/add?a=2&b=2");
         urlVisitor.call(URLEncoder.encode("https://xxx/test2/showInfo?person.name=张三&person.gender=男&person.age=23&person.job.salary=703456.7&person.job.department=信息工程部&person.job.address=北京市金融街228号&name=张三"),dispatcherServlet.getHandlerMapping());
        // urlVisitor.call(URLEncoder.encode("https://xxx/test2/showInfo2"));
    }

    private void call(String url,List<Handler> handlerMapping) {
        try {
            Request request = parseRequest(url);
            // 处理请求方法
            for (Handler handler: handlerMapping) {
                if(!handler.getPattern().matcher(request.getUrl()).matches()) {
                    continue;
                }
                // url参数转换成方法的入参
                Object[] params = convertUrlParam2MethodParam(request, handler);
                try {
                    // 调用映射到的方法
                    Object returnObj = handler.getMethod().invoke(handler.getObject(),params);
                    System.out.println(returnObj);
                } catch (InvocationTargetException|IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Object[] convertUrlParam2MethodParam(Request request, Handler handler) {
        // 1. 先将 handler的参数以及参数类型单独放到一个Map中
        Map<String, Type> rootParamTypeMap = getMethodParamsNameAndTypeMap(handler);
        // 2. 解析url请求的参数，转换成对应的类型的值
        Map<String,Object> objTreeMap = new HashMap<>();
        for (Map.Entry<String,String> paramsEntry: request.getUrlParamsMap().entrySet()) {
            String urlParamKey = paramsEntry.getKey();
            String urlParamValue = paramsEntry.getValue().trim();

            if (urlParamKey.contains(".")) {
                // 对象图模式解析
                mappingComplexPojo(rootParamTypeMap, objTreeMap, urlParamKey, urlParamValue);
            } else { // 简单对象解析
                mappingSimpleObject(rootParamTypeMap, objTreeMap, urlParamKey, urlParamValue);
            }
        }

        // 按照方法需要的参数顺序放入参数数组并返回
        Object[] params = new Object[handler.getParams().size()];
        for (int i = 0; i < handler.getParams().size(); i++) {
            UrlParameter param = handler.getParams().get(i);
            Object paramValue = objTreeMap.get(param.getUrlParameterName() == null ? param.getParamName() : param.getUrlParameterName());
            params[i] = paramValue;
        }
        return params;
    }

    private void mappingSimpleObject(Map<String, Type> rootParamTypeMap, Map<String, Object> objTreeMap, String urlParamKey, String urlParamValue) {
        Type parameterType = rootParamTypeMap.get(urlParamKey);
        if (null == parameterType) {
            return;
        }
        Object convertedValue = typeResolver.resolveParams(urlParamValue, parameterType);
        objTreeMap.put(urlParamKey,convertedValue);
    }

    /**
     *  复杂对象解析
     * @param rootParamTypeMap 从方法中获取的参数跟其类型对应关系
     * @param objTreeMap  url参数解析之后放入到该Map中
     * @param urlParamKey 一对url参数中的key
     * @param urlParamValue 一对url参数中的value
     */
    private void mappingComplexPojo(Map<String, Type> rootParamTypeMap, Map<String, Object> objTreeMap, String urlParamKey, String urlParamValue) {
        String[] fieldTree = urlParamKey.split(Pattern.quote("."));
        StringBuilder parentPath = null;
        StringBuilder currentPath = null;
        for (int idx = 0; idx <  fieldTree.length; idx++) {
            String field = fieldTree[idx];
            if (null == parentPath) {
                parentPath = new StringBuilder();
                currentPath = new StringBuilder(field);
            } else {
                currentPath.append(".").append(field);
            }

            Object self = null;
            // 先获取它的父节点
            if (!"".equals(parentPath.toString())) {
                self = objTreeMap.get(currentPath.toString());
                if (null== self) { // 如果缓存列表里没有，才创建
                    Object parent = objTreeMap.get(parentPath.toString());
                    try {
                        Field declaredField = parent.getClass().getDeclaredField(field);
                        declaredField.setAccessible(true);
                        if (idx == fieldTree.length - 1) {
                            self = typeResolver.resolveParams(urlParamValue,declaredField.getGenericType());
                            declaredField.set(parent,self);
                        } else {
                            self = declaredField.getType().newInstance();
                            declaredField.set(parent,self);
                        }
                    } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }

            } else { // 根节点
                self = objTreeMap.get(field);
                if (null== self) { // 如果缓存列表里没有，才创建
                    try {
                        Class selfType = rootParamTypeMap.get(field);
                        if (null == selfType) {
                            break;
                        }
                        if (idx == fieldTree.length - 1) {
                            self = typeConvertor.convertType(selfType, urlParamValue);
                        } else {
                            self = selfType.newInstance();
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (null != self) {
                objTreeMap.put(currentPath.toString(),self);
            }
            parentPath = new StringBuilder(currentPath.toString());
        }
    }

    private Map<String, Type> getMethodParamsNameAndTypeMap(Handler handler) {
        Map<String,Type> rootParamTypeMap = new HashMap<>();
        for (UrlParameter urlParameter: handler.getParams()) {
            rootParamTypeMap.put(urlParameter.getUrlParameterName() == null ? urlParameter.getParamName() : urlParameter.getUrlParameterName(),urlParameter.getParameterType());
        }
        return rootParamTypeMap;
    }

    private Request parseRequest(String url) throws UnsupportedEncodingException {
        url = URLDecoder.decode(url, "utf-8");
        Integer questionMarkIndex = url.indexOf('?');
        String requestUrl = null;
        String requestParams = null;
        if (-1 != questionMarkIndex) {
            requestUrl = url.substring(0,questionMarkIndex);
            requestParams = url.substring(questionMarkIndex + 1);
        } else {
            requestUrl = url;
        }
        // 去掉ip端口等主机连接信息，获取到访问的url路径
        String urlPath = extractUrlPath(requestUrl);
        // RequestParams解析成键值对放入到Map中
        Map<String, String> urlParamsMap = parseUrlParams(requestParams);
        return new Request(url,urlPath,urlParamsMap);
    }

    private Map<String, String> parseUrlParams(String requestParams) {
        Map<String,String> urlParamsMap = new HashMap<>();
        if (null != requestParams) { // 如果有参数
            String[] paramMappings = requestParams.split("&");
            // List<String> urlParamsList = new ArrayList<>();
            for (String paramMap : paramMappings) {
                String[] kv = paramMap.split("=");
                String key = kv[0];
                String value = kv[1];
                urlParamsMap.put(key, value);
                // urlParamsList.add(key);
            }
        }
        return urlParamsMap;
    }

    private String extractUrlPath(String requestUrl) {
        requestUrl = requestUrl.replaceAll("/+","/");
        String[] segs = requestUrl.split("/");
        int startIndex = segs[0].length() + segs[1].length() + 1;
        requestUrl = requestUrl.substring(startIndex);
        return requestUrl;
    }


}
