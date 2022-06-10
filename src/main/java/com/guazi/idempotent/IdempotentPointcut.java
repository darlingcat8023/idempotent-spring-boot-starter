package com.guazi.idempotent;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Idempotent切点
 * @author liujiajun
 * @date 2020/5/25
 */
public abstract class IdempotentPointcut extends StaticMethodMatcherPointcut implements Serializable {

    @Override
    public boolean matches(Method method, Class<?> aClass) {
        IdempotentResource resource = this.getIdempotentOperationSource();
        return resource != null && resource.getOperations(method, aClass) != null;
    }

    /**
     * 获取Resource
     * @return
     */
    @Nullable
    protected abstract IdempotentResource getIdempotentOperationSource();

}
