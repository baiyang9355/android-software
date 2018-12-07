package com;

import pojo.ParamTypeConvertor;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParameterizedTypeTest {

    List<String> a;
    Set<Integer> b;
    Map<String,Float> c;

    private List<List<String>> d;
    private Set<Set<String>> e;
    private Map<String,Map<String,String>> f;

    private List<ParameterizedClassTest<String>> g;
    private Set<ParameterizedClassTest<String>> h;
    private Map<ParameterizedClassTest<String>,Map<String,String>> i;

    public List<String> getA() {
        return a;
    }

    public void setA(List<String> a) {
        this.a = a;
    }

    public Set<Integer> getB() {
        return b;
    }

    public void setB(Set<Integer> b) {
        this.b = b;
    }

    public Map<String, Float> getC() {
        return c;
    }

    public void setC(Map<String, Float> c) {
        this.c = c;
    }

    public List<List<String>> getD() {
        return d;
    }

    public void setD(List<List<String>> d) {
        this.d = d;
    }

    public Set<Set<String>> getE() {
        return e;
    }

    public void setE(Set<Set<String>> e) {
        this.e = e;
    }

    public Map<String, Map<String, String>> getF() {
        return f;
    }

    public void setF(Map<String, Map<String, String>> f) {
        this.f = f;
    }

    public List<ParameterizedClassTest<String>> getG() {
        return g;
    }

    public void setG(List<ParameterizedClassTest<String>> g) {
        this.g = g;
    }

    public Set<ParameterizedClassTest<String>> getH() {
        return h;
    }

    public void setH(Set<ParameterizedClassTest<String>> h) {
        this.h = h;
    }

    public Map<ParameterizedClassTest<String>, Map<String, String>> getI() {
        return i;
    }

    public void setI(Map<ParameterizedClassTest<String>, Map<String, String>> i) {
        this.i = i;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        // 获取Field的泛型参数
        Field a = ParameterizedTypeTest.class.getDeclaredField("a");
        Field b = ParameterizedTypeTest.class.getDeclaredField("b");
        Field c = ParameterizedTypeTest.class.getDeclaredField("c");
        ParameterizedType aGenericType = (ParameterizedType) a.getGenericType();
        ParameterizedType bGenericType = (ParameterizedType) b.getGenericType();
        ParameterizedType cGenericType = (ParameterizedType) c.getGenericType();
        System.out.println(Arrays.toString(aGenericType.getActualTypeArguments()));
        System.out.println(Arrays.toString(bGenericType.getActualTypeArguments()));
        System.out.println(Arrays.toString(cGenericType.getActualTypeArguments()));

        // 嵌套参数也没问题
        Field d = ParameterizedTypeTest.class.getDeclaredField("d");
        Field e = ParameterizedTypeTest.class.getDeclaredField("e");
        Field f = ParameterizedTypeTest.class.getDeclaredField("f");
        ParameterizedType dGenericType = (ParameterizedType) d.getGenericType();
        ParameterizedType eGenericType = (ParameterizedType) e.getGenericType();
        ParameterizedType fGenericType = (ParameterizedType) f.getGenericType();
        System.out.println(Arrays.toString(dGenericType.getActualTypeArguments()));
        System.out.println(Arrays.toString(eGenericType.getActualTypeArguments()));
        System.out.println(Arrays.toString(fGenericType.getActualTypeArguments()));
        // 嵌套参数也没问题
        Field g = ParameterizedTypeTest.class.getDeclaredField("g");
        Field h = ParameterizedTypeTest.class.getDeclaredField("h");
        Field i = ParameterizedTypeTest.class.getDeclaredField("i");
        ParameterizedType gGenericType = (ParameterizedType) g.getGenericType();
        ParameterizedType hGenericType = (ParameterizedType) h.getGenericType();
        ParameterizedType iGenericType = (ParameterizedType) i.getGenericType();
        System.out.println(Arrays.toString(gGenericType.getActualTypeArguments()));
        System.out.println(Arrays.toString(hGenericType.getActualTypeArguments()));
        System.out.println(Arrays.toString(iGenericType.getActualTypeArguments()));
        System.out.println("----------------------");
        Method[] declaredMethods = ParameterizedTypeTest.class.getDeclaredMethods();
        for (Method method: declaredMethods) {
            if ("test".equals(method.getName())) {
                Parameter[] parameters = method.getParameters();
                for (int idx = 0; idx < parameters.length; idx++) {
                    Parameter parameter = parameters[idx];
                    ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
                    System.out.println(parameter.getName() + " --> " + parameterizedType + " --> " + Arrays.toString(parameterizedType.getActualTypeArguments()));
                }
            }
            System.out.println("------------------------");
            if ("test1".equals(method.getName())) {
                Parameter[] parameters = method.getParameters();
                for (int idx = 0; idx < parameters.length; idx++) {
                    Parameter parameter = parameters[idx];
                    ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
                    System.out.println(parameter.getName() + " --> " + parameterizedType + " --> " + Arrays.toString(parameterizedType.getActualTypeArguments()));
                }
            }
        }

        System.out.println("-----------------------");
        ParamTypeConvertor paramTypeConvertor = new ParamTypeConvertor();
        Method[] methods = paramTypeConvertor.getClass().getDeclaredMethods();
        for (Method method: methods) {
            if ("transform".equals(method.getName())) {
                try {
                    method.setAccessible(true); // 设置可访问
                    System.out.println(method.invoke(paramTypeConvertor,"haha"));
                } catch (InvocationTargetException|IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    Object test(List<String> a,Set<Integer> b, Map<String,Float> c) {
        System.out.println("---------list-----------");
        for (String str: a) {
            System.out.println(str);
        }

        System.out.println("---------set------------");
        for (Integer integer: b) {
            System.out.println(integer);
        }

        System.out.println("---------map------------");
        for (Map.Entry<String,Float> entry: c.entrySet()) {
            System.out.println(entry.getKey() + "-->" + entry.getValue());
        }
        return null;
    }

    Object test1(int d) {
        return null;
    }
}

class ParameterizedClassTest<T> {
}
