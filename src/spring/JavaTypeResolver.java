package spring;

import pojo.ParamTypeConvertor;

import java.lang.reflect.*;

public class JavaTypeResolver {
    private ParamTypeConvertor typeConvertor = new ParamTypeConvertor();

    // 解决Type的问题
    public Object resolveParamType(Type parameterType) {
        return null;
    }

    public Object resolveParams(String urlParamValue, Type parameterType) {
        Object convertedValue = null;
        if (parameterType instanceof Class) {
            Class<?> clazzType = Class.class.cast(parameterType);
            typeConvertor.convertType(clazzType, urlParamValue);
        } else if (parameterType instanceof GenericArrayType){
            GenericArrayType genericArrayType = GenericArrayType.class.cast(parameterType);
            Type genericComponentType = genericArrayType.getGenericComponentType();
            // Array.newInstance(genericComponentType,1);
        } else if (parameterType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = ParameterizedType.class.cast(parameterType);
            // 原生类型
            Type rawType = parameterizedType.getRawType();
            // 真实类型
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        } else if (parameterType instanceof WildcardType) {
            WildcardType wildcardType = WildcardType.class.cast(parameterType);
            Type[] lowerBounds = wildcardType.getLowerBounds();
            Type[] upperBounds = wildcardType.getUpperBounds();
        } else if (parameterType instanceof TypeVariable) {
            TypeVariable typeVariable = TypeVariable.class.cast(parameterType);
            Type[] bounds = typeVariable.getBounds();
            AnnotatedType[] annotatedBounds = typeVariable.getAnnotatedBounds();
        }
        return convertedValue;
    }
}
