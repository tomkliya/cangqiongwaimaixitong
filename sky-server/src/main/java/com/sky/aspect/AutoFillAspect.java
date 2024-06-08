package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {

    private static final Logger log = LoggerFactory.getLogger(AutoFillAspect.class);

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){
    }
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws Throwable {
        //获取当前的数据库操作类型：update，create
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        //获取数据库操作类型
        OperationType value = annotation.value();

        //获取传入的参数
        if (joinPoint.getArgs()[0] == null) {
            log.info("employee is null");
            return;
        }
        Object entity =  joinPoint.getArgs()[0];


        //准备赋值数据

        LocalDateTime Now = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();
        if(value == OperationType.INSERT){

            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            //通过反射复制
            setCreateTime.invoke(entity, Now);
            setUpdateTime.invoke(entity, Now);
            setCreateUser.invoke(entity, id);
            setUpdateUser.invoke(entity, id);

        }else if(value == OperationType.UPDATE){

            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            //通过反射复制

            setUpdateTime.invoke(entity, Now);
            setUpdateUser.invoke(entity, id);

        }


    }

}
