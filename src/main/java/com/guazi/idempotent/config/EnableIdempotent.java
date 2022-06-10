package com.guazi.idempotent.config;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动入口
 * @author liujiajun
 * @date 2020/5/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({IdempotentConfigurationSelector.class})
public @interface EnableIdempotent {

    /**
     * 仅支持Proxy模式
     * @return
     */
    AdviceMode mode() default AdviceMode.PROXY;

    /**
     * 优先级
     * @return
     */
    int order() default 2147483646;

}
