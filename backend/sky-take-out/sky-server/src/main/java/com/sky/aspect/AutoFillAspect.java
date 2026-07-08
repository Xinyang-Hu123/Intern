package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     * com.sky.mapper包下包含AutoFill注解的所有类和方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {

    }

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充");

//        获取到当前拦截的方法上的数据库操作类型
//        获取方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

//        获取方法上的注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);

//        获取数据库操作类型
        OperationType operationType = autoFill.value();

//        获取到当前被拦截的方法的参数---实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

//        转变赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

//        根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT) {
//            为4个公共字段赋值
            try {
                setValue(entity, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class, now);
                setValue(entity, AutoFillConstant.SET_CREATE_USER, Long.class, currentId);
                setValue(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
                setValue(entity, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (operationType == operationType.UPDATE) {
//            为2个公共字段赋值
            try {
                setValue(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
                setValue(entity, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }

    private void setValue(Object entity, String methodName, Class<?> paramType, Object value) throws Exception {
        try {
            Method method = entity.getClass().getDeclaredMethod(methodName, paramType);
            method.invoke(entity, value);
        } catch (NoSuchMethodException e) {
            log.warn("实体 {} 缺少方法 {}({})，已跳过自动填充", entity.getClass().getSimpleName(), methodName, paramType.getSimpleName());
        }
    }
}
