package com.guazi.idempotent;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * PointCutAdvisor
 * @author liujiajun
 * @date 2020/5/25
 */
public class BeanFactoryIdempotentAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private IdempotentResource idempotentResource;

    private final IdempotentPointcut pointcut = new IdempotentPointcut() {
        @Override
        protected IdempotentResource getIdempotentOperationSource() {
            return BeanFactoryIdempotentAdvisor.this.idempotentResource;
        }
    };

    public void setIdempotentResource(IdempotentResource idempotentResource) {
        this.idempotentResource = idempotentResource;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

}
