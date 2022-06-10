package com.guazi.idempotent.annotation;

import com.guazi.idempotent.fallback.DefaultIdempotentFallBack;
import com.guazi.idempotent.fallback.IdempotentFallBack;

import java.lang.annotation.*;

/**
 * @author liujiajun
 * @date 2020/5/22
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 约束
     * @return
     */
    String key();

    /**
     * 触发条件
     * @return
     */
    String condition() default "";

    /**
     * 指定使用哪个幂等管理器
     * @return
     */
    String idempotentManager() default "idempotentManager";

    /**
     * 自定义异常处理器
     * @return
     */
    Class<? extends IdempotentFallBack<?>> fallback() default DefaultIdempotentFallBack.class;

}
