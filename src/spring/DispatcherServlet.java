package spring;

import annotation.*;
import pojo.Handler;
import pojo.UrlParameter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

public class DispatcherServlet {
    private static final String CONF = "application.properties";
    private Properties properties;
    private List<String> classNames = new ArrayList<String>();
    private Map<String,Object> ioc = new HashMap<String,Object>();
    private List<Handler> handlerMapping = new ArrayList<>();

    public List<Handler> getHandlerMapping() {
        return handlerMapping;
    }

    public void init() {
        // 1. 加载Properties 文件
        loadProperties();
        // 2. 扫描所有类
        scanClasses();
        // 3. 初始化所有类并放入 ioc 框架
        initIoc();
        // 4. AutoWired 注入
        doAutoWired();
        // 5. handlerMapping 处理 映射关系
        doHandlerMapping();
    }

    // 处理 handler mapping 映射关系
    private void doHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Object object: ioc.values()) {
            // 只处理包含 Controller 的数据
            Class<?> clazz = object.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            // 处理 Controller 注解的类
            // 1. 类上面是否有RequestMapping 注解
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                String url = requestMapping.value();
                if (!"".equals(url)) {
                    if (url.endsWith("/")) {
                        url = url.substring(0,url.length() - 1);
                    }
                    baseUrl = url;
                }
            }
            // 处理所有的方法
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method: declaredMethods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) { // 只处理 被 RequestMapping 修饰的方法
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String url = requestMapping.value();
                url =  baseUrl + url;
                Pattern pattern = Pattern.compile(url);
                List<UrlParameter> params = new ArrayList<>();
                Parameter[] parameters = method.getParameters();
                for (Parameter parameter: parameters) {
                    String parameterName = parameter.getName();
                    String urlName = null;
                    if (parameter.isAnnotationPresent(ParameterMapping.class)) {
                        ParameterMapping annotation = parameter.getAnnotation(ParameterMapping.class);
                        urlName = annotation.value();
                    } else if (parameter.isAnnotationPresent(ModelAttribute.class)) {
                        ModelAttribute annotation = parameter.getAnnotation(ModelAttribute.class);
                        urlName = annotation.value();
                    }
                    Type parameterType = parameter.getParameterizedType();
                    params.add(new UrlParameter(parameterName, parameterType,urlName));
                    Class<?> returnType = method.getReturnType();
                    handlerMapping.add(new Handler(object,method,pattern,params,returnType));
                }
            }
        }
    }

    private void doAutoWired() {
        if (ioc.isEmpty()) {
            return;
        }
        Collection<Object> values = ioc.values();
        for (Object obj: values) {
            // 获取所有的方法
            Class<?> clazz = obj.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field: declaredFields) {
                field.setAccessible(true); // 允许访问私有方法
                if(! field.isAnnotationPresent(AutoWired.class)) { // 如果不是 被 AutoWired 方法修饰，则不处理
                    continue;
                }
                AutoWired autoWired = field.getAnnotation(AutoWired.class);
                String value = autoWired.value();
                if ("".equals(value)) {
                    value = firstLowerStr(field.getType().getName());
                }
                try {
                    field.set(obj,ioc.get(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initIoc() {
        if (classNames.isEmpty()) {
            return;
        }
        for (String className: classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                // 只初始化被Controller跟Service注解的类
                if(clazz.isAnnotationPresent(Controller.class)) {
                    Controller controller = clazz.getAnnotation(Controller.class);
                    String iocKey = controller.value();
                    if ("".equals(iocKey)) { // 如果 是 空字符串
                        iocKey = firstLowerStr(clazz.getName()); // 类名首字母小写
                    }
                    ioc.put(iocKey,clazz.newInstance());
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    String iocKey = service.value();
                    if ("".equals(iocKey)){ // 如果 是 空字符串
                        iocKey = firstLowerStr(clazz.getName()); // 类名首字母小写
                    }
                    ioc.put(iocKey,clazz.newInstance());
                    // 接口 <--> 实例对应关系
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> inter: interfaces) {
                        ioc.put(firstLowerStr(inter.getName()), clazz.newInstance());
                    }
                }
            }  catch (ClassNotFoundException| InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private String firstLowerStr(String name) {
        char[] chars = name.toCharArray();
        if (chars[0] < 91) {
            chars[0] += 32;
        }
        return new String(chars);
    }


    private void scanClasses() {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        String basePackageKey = "basePackage";
        String basePackage = properties.getProperty(basePackageKey);
        try {
            String classpath = getClass().getResource("/").getPath();
            // 解决路径中，中文乱码问题
            classpath = java.net.URLDecoder.decode(classpath, "utf-8");
            String path = basePackage.replace('.', File.separatorChar);
            File baseFile = new File(classpath + path);
            if (baseFile.isDirectory()) {
                File[] listFiles = baseFile.listFiles();
                if (null != listFiles) {
                    for (File f : listFiles) {
                        loadClasses(basePackage, f);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClasses(String basePackage, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    loadClasses(basePackage + "." + file.getName(), f);
                }
            }
        } else if(file.isFile()){
            String className = basePackage + "." + file.getName().replace(".class", "");
            classNames.add(className);
        }
    }

    private void loadProperties() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONF);
        try {
            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
