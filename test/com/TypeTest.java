package com;

import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

public class TypeTest{

    public static void main(String[] args) {
        Class test2 = Test2.class;
        Method[] declaredMethods = test2.getDeclaredMethods();
        for (Method method: declaredMethods) {
            if ("test021".equals(method.getName())) {
                Type[] genericParameterTypes = method.getGenericParameterTypes();
                // since 1.8
//              Parameter[] parameters = method.getParameters();
//              for (Parameter parameter: parameters) {
//                  printTypeInfo(parameter.getParameterizedType());
//              }
                for (Type genericParameterType: genericParameterTypes) {
                    printTypeInfo(genericParameterType);
                }
            }
        }

        Class test3 = Test3.class;
        Method[] declaredMethods1 = test3.getDeclaredMethods();
        for (Method method: declaredMethods1) {
            if ("test4".equals(method.getName())) {
            //  Type[] genericParameterTypes = method.getGenericParameterTypes();
            //  for (Type genericParameterType: genericParameterTypes) {
            //      printTypeInfo(genericParameterType);
            //  }

                Parameter[] parameters = method.getParameters();
                for (Parameter parameter: parameters) {
                    printTypeInfo(parameter.getParameterizedType());
                }
            }
        }
    }

    public static void printTypeInfo(Type type) {
        if (type.getClass() == sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl.class) { // 泛型对象
            ParameterizedTypeImpl genericType = (ParameterizedTypeImpl) type;
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            for (Type actualTypeArg: actualTypeArguments) {
                printTypeInfo(actualTypeArg);
            }
            println(type.getClass(),"typeName : " + type.getTypeName(),"actualTypeArguments : "+ Arrays.toString(actualTypeArguments));
        } else if (type.getClass() == sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl.class) { // 泛型数组
            GenericArrayTypeImpl genericType = (GenericArrayTypeImpl) type;
            Type genericComponentType = genericType.getGenericComponentType();
            printTypeInfo(genericComponentType);
            println(type.getClass(), "typeName : " + type.getTypeName(),"genericComponentType : "+ genericComponentType);
        }  else if (type.getClass() == sun.reflect.generics.reflectiveObjects.TypeVariableImpl.class) {
            TypeVariableImpl genericType = (TypeVariableImpl) type;
            Type[] bounds = genericType.getBounds();
            // TODO 待研究
            GenericDeclaration genericDeclaration = genericType.getGenericDeclaration();
            TypeVariable<?>[] typeParameters = genericDeclaration.getTypeParameters();
            println(genericType.getClass(),"typeName : " + type.getTypeName(),"bounds : " + Arrays.toString(bounds),"genericDeclaration : " + genericDeclaration, "typeParameters : " + Arrays.toString(typeParameters));
        } else if (type.getClass() == sun.reflect.generics.reflectiveObjects.WildcardTypeImpl.class){
            WildcardTypeImpl genericType = (WildcardTypeImpl) type;
            Type[] lowerBounds = genericType.getLowerBounds();
            Type[] upperBounds = genericType.getUpperBounds();
            println(type.getClass(),"typeName : " + type.getTypeName(), "lowerBounds : " + Arrays.toString(lowerBounds),"upperBounds : " + Arrays.toString(upperBounds));
        } else {
            println(type.getTypeName());
        }
    }

    private static void println(Object ... objects) {
        StringBuilder builder = new StringBuilder();
        for (Object obj: objects) {
            builder.append("|").append(obj.toString());
        }
        String result = builder.toString();
        if (result.length() > 0) {
            result = result.substring(1);
        }
        System.out.println(result);
    }
}

class Test2 {
    // int test1(int info) { // int
    //     return info + 1;
    // }
    // <T extends Number> T test1(T info) { // sun.reflect.generics.reflectiveObjects.TypeVariableImpl
    //     return info;
    // }
    // 为什么要通配符
    // 在java中，数组是可以协变的，比如dog extends Animal，那么Animal[] 与dog[]是兼容的。
    // 而集合是不能协变的，也就是说List<Animal>不是List<dog>的父类，这时候就可以用到通配符了。
    void test00(List<? extends Number> infos) {
         // 最外层： class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : java.util.List<? extends java.lang.Number>|actualTypeArguments : [? extends java.lang.Number]
        // 内层类型：class sun.reflect.generics.reflectiveObjects.WildcardTypeImpl|typeName : ? extends java.lang.Number|lowerBounds : []|upperBounds : [class java.lang.Number]
    }

    void test01(List<? super Integer> infos) {
        // 最外层：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : java.util.List<? super java.lang.Integer>|actualTypeArguments : [? super java.lang.Integer]
        // 内层类型：class sun.reflect.generics.reflectiveObjects.WildcardTypeImpl|typeName : ? super java.lang.Integer|lowerBounds : [class java.lang.Integer]|upperBounds : [class java.lang.Object]
    }

    void test02(List<?> infos) {
        // 最外层：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : java.util.List<?>|actualTypeArguments : [?]
        // class sun.reflect.generics.reflectiveObjects.WildcardTypeImpl|typeName : ?|lowerBounds : []|upperBounds : [class java.lang.Object]
    }

    <T extends Number> T test1(List<T> infos) {
        // List<T> infos 外类型：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : java.util.List<T>|actualTypeArguments : [T]
        // T 的类型：class sun.reflect.generics.reflectiveObjects.TypeVariableImpl|typeName : T|bounds : [class java.lang.Number]|genericDeclaration : java.lang.Number com.Test2.test1(java.util.List)|typeParameters : [T]
        return (infos == null || infos.size() == 0) ? null :infos.get(0);
    }

    String test2(List<String> infos) {
        // List<String> info 类型：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : java.util.List<java.lang.String>|actualTypeArguments : [class java.lang.String]
        // List<String> 参数的类型： java.lang.String
        return (infos == null || infos.size() == 0) ? null:infos.get(0);
    }

    Test3<Test3<Test3<Test3<String>>>> test3(Test3<Test3<Test3<Test3<String>>>> infos) {
        // 从外到内，层数关系依次是
        // 最外层（第一层）：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : com.Test3<com.Test3<com.Test3<com.Test3<java.lang.String>>>>|actualTypeArguments : [com.Test3<com.Test3<com.Test3<java.lang.String>>>]
        // 第二层：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : com.Test3<com.Test3<com.Test3<java.lang.String>>>|actualTypeArguments : [com.Test3<com.Test3<java.lang.String>>]
        // 第三层：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : com.Test3<com.Test3<java.lang.String>>|actualTypeArguments : [com.Test3<java.lang.String>]
        // 第四层：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : com.Test3<java.lang.String>|actualTypeArguments : [class java.lang.String]
        // 最内层（第五层）：java.lang.String
        return infos;
    }
}

class Test3<T> {
    void test1(T[] infos) {
        // 最外层类型：class sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl|typeName : T[]|genericComponentType : T
        // 内层类型：class sun.reflect.generics.reflectiveObjects.TypeVariableImpl|typeName : T|bounds : [class java.lang.Object]|genericDeclaration : class com.Test3|typeParameters : [T]
    }

    <T extends Number> T test2(T[] infos) {
        // 最外层类型：class sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl|typeName : T[]|genericComponentType : T
        // 内层类型：class sun.reflect.generics.reflectiveObjects.TypeVariableImpl|typeName : T|bounds : [class java.lang.Number]|genericDeclaration : java.lang.Number com.Test3.test2(java.lang.Number[])|typeParameters : [T]
        return (infos == null || infos.length == 0) ? null:infos[0];
    }

    void test3(List<?>[] infos) {
        // class sun.reflect.generics.reflectiveObjects.WildcardTypeImpl|typeName : ?|lowerBounds : []|upperBounds : [class java.lang.Object]
        // class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : java.util.List<?>|actualTypeArguments : [?]
        // class sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl|typeName : java.util.List<?>[]|genericComponentType : java.util.List<?>
    }

    void test4(List[] infos) {
        // java.util.List[]
    }

    <R> void test5(Test3<R>[] infos) {
        // 最外层类型：class sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl|typeName : com.Test3<R>[]|genericComponentType : com.Test3<R>
        // 内层类型：class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl|typeName : com.Test3<R>|actualTypeArguments : [R]
        // 最内层类型：class sun.reflect.generics.reflectiveObjects.TypeVariableImpl|typeName : R|bounds : [class java.lang.Object]|genericDeclaration : void com.Test3.test5(com.Test3[])|typeParameters : [R]
    }
}