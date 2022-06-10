package com.guazi.idempotent.aspect;

import com.guazi.idempotent.AbstractIdempotentConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * 基于静态代理Aspectj模式的Idempotent自动配置
 * @author liujiajun
 * @date 2020/5/27
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AspectjIdempotentConfiguration extends AbstractIdempotentConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AnnotationIdempotentAspect idempotentAspect() {
        return AnnotationIdempotentAspect.aspectOf();
    }

}
