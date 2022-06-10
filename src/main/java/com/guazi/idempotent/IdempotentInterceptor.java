package com.guazi.idempotent;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.Serializable;

/**
 * 切点拦截器
 * @author liujiajun
 * @date 2020/5/25
 */
public class IdempotentInterceptor extends AbstractIdempotentInterceptor implements MethodInterceptor, Serializable {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        IdempotentOperationInvoker invoker = () -> {
            try {
                return methodInvocation.proceed();
            } catch (Throwable e) {
                throw new IdempotentOperationInvoker.ThrowableWrapper(e);
            }
        };
        try {
            return this.execute(invoker, methodInvocation.getMethod(), methodInvocation.getMethod(), methodInvocation.getArguments());
        } catch (IdempotentOperationInvoker.ThrowableWrapper e) {
            throw e.getOriginal();
        }
    }

}
