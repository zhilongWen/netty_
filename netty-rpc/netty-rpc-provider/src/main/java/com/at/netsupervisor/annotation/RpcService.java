package com.at.netsupervisor.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @create 2023-06-11
 */
@Target({ElementType.TYPE}) // 能修饰类、接口或枚举类型
@Retention(RetentionPolicy.RUNTIME) //注解信息在执行时出现
@Component
public @interface RpcService {
}
