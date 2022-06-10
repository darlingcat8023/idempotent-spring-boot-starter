package com.guazi.idempotent;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * 基于动态代理模式的Idempotent自动配置
 * @author liujiajun
 * @date 2020/5/25
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyIdempotentConfiguration extends AbstractIdempotentConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryIdempotentAdvisor idempotentAdvisor(IdempotentInterceptor idempotentInterceptor, IdempotentResource idempotentResource) {
        BeanFactoryIdempotentAdvisor advisor = new BeanFactoryIdempotentAdvisor();
        advisor.setAdvice(idempotentInterceptor);
        advisor.setIdempotentResource(idempotentResource);
        if (this.enableIdempotent != null) {
            advisor.setOrder((Integer)this.enableIdempotent.getNumber("order"));
        }
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public IdempotentResource idempotentResource() {
        return new AnnotatedIdempotentResource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public IdempotentInterceptor idempotentInterceptor(IdempotentResource idempotentResource) {
        IdempotentInterceptor interceptor = new IdempotentInterceptor();
        interceptor.setIdempotentResource(idempotentResource);
        return interceptor;
    }

}
