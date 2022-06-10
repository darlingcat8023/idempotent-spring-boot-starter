package com.guazi.idempotent;

import com.guazi.idempotent.IdempotentOperation;

import java.lang.reflect.Method;

/**
 * @author liujiajun
 * @date 2020/5/25
 */
public interface IdempotentResource {

    /**
     * 获取IdempotentOperation
     * @param method
     * @param targetClass
     * @return
     */
    IdempotentOperation getOperations(Method method, Class<?> targetClass);

}
