package com.youlan.common.core.helper;

import lombok.Getter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

public class AspectHelper {
    @Getter
    public static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 获取切面方法签名
     */
    public static MethodSignature getMethodSignature(JoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }

    /**
     * 获取切面方法
     */
    public static Method getTargetMethod(JoinPoint joinPoint) {
        return getMethodSignature(joinPoint).getMethod();
    }

    /**
     * 获取切面类
     */
    public static Class<?> getTargetClass(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass();
    }

    /**
     * 获取切面对象
     */
    public static Object getTargetObject(JoinPoint joinPoint) {
        return joinPoint.getTarget();
    }

    /**
     * 获取切面参数
     */
    public static Object[] getTargetMethodArgs(JoinPoint joinPoint) {
        return joinPoint.getArgs();
    }

    /**
     * 获取切换参数名称
     */
    public static String[] getTargetMethodNames(JoinPoint joinPoint) {
        return parameterNameDiscoverer.getParameterNames(getTargetMethod(joinPoint));
    }
}
