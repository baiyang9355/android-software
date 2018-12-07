package pojo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ParamTypeConvertor {

    private Object convertNumber(Class<?> parameterType, String paramValue){
        if (parameterType== Float.class) {
            return Float.parseFloat(paramValue);
        } else if(parameterType== BigDecimal.class) {
            return new BigDecimal(paramValue);
        } else if(parameterType== AtomicLong.class) {
            return new AtomicLong(Long.parseLong(paramValue));
        } else if(parameterType== Long.class) {
            return Long.parseLong(paramValue);
        } else if(parameterType== Double.class) {
            return Double.parseDouble(paramValue);
        } else if (parameterType== AtomicInteger.class) {
            return new AtomicInteger(Integer.parseInt(paramValue));
        } else if (parameterType== Short.class) {
            return Short.parseShort(paramValue);
        } else if (parameterType== BigInteger.class) {
            return new BigInteger(paramValue);
        } else if (parameterType== Byte.class) {
            return Byte.parseByte(paramValue);
        } else if (parameterType== Integer.class) {
            return Integer.parseInt(paramValue);
        }
        throw new RuntimeException("unsupported number parameter type !!!");
    }
    public Object convertType(Class<?> parameterType, String paramValue) {
        Class superClass = parameterType.getSuperclass();
        Class<?>[] interfaces = parameterType.getInterfaces();
        List<Class<?>> superInterfaces = new ArrayList<>();
        for (Class<?> clazz: interfaces) {
            superInterfaces.add(clazz);
        }
        if(superClass == Number.class) {
            return convertNumber(parameterType,paramValue);
        } else if (parameterType == String.class) {
            return paramValue;
        } else if (parameterType == List.class || superInterfaces.contains(List.class)) { // list
            // 怎么获取 List,Map,Set的泛型类型
            return null;
        } else if (parameterType == Set.class || superInterfaces.contains(Set.class)){ // set
            return null;
        } else if (parameterType == Map.class || superInterfaces.contains(Map.class)){ // Map
            return null;
        } else {
            try {
                return parameterType.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    String transform(String type) {
        return type;
    }


}
