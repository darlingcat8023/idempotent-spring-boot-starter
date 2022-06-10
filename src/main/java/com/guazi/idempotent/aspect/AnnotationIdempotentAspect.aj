package com.guazi.idempotent.aspect;

import com.guazi.idempotent.AbstractIdempotentInterceptor;
import com.guazi.idempotent.AnnotatedIdempotentResource;
import com.guazi.idempotent.IdempotentOperationInvoker;
import com.guazi.idempotent.annotation.Idempotent;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 基于注解的AspectJ静态切面
 * @author liujiajun
 * @date 2020/5/27
 */
public aspect AnnotationIdempotentAspect extends AbstractIdempotentInterceptor {

    /**
     * AspectJ构造函数
     */
    public AnnotationIdempotentAspect() {
        this.setIdempotentResource(new AnnotatedIdempotentResource(false));
    }

    /**
     * AspectJ组合静态切点表达式
     */
    protected pointcut idempotentMethodExecution(Idempotent idempotent) :
            @annotation(idempotent);

    /**
     * 使用环绕通知切入目标方法
     */
    @SuppressAjWarnings("adviceDidNotMatch")
    Object around(final Idempotent idempotent) : idempotentMethodExecution(idempotent) {
        MethodSignature methodSignature = (MethodSignature) thisJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        IdempotentOperationInvoker aspectJInvoker = () -> {
            try {
                return proceed(idempotent);
            }
            catch (Throwable ex) {
                throw new IdempotentOperationInvoker.ThrowableWrapper(ex);
            }
        };
        try {
            return super.execute(aspectJInvoker, thisJoinPoint.getTarget(), method, thisJoinPoint.getArgs());
        }
        catch (Throwable th) {
            return null;
        }
    }

}
