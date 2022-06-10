package com.guazi.idempotent;

import com.guazi.idempotent.annotation.Idempotent;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注解和Bean映射关系
 * @author liujiajun
 * @date 2020/5/25
 */
public class AnnotatedIdempotentResource implements IdempotentResource {

    /**
     * 用来保存bean method和Idempotent的映射关系
     */
    private final Map<Object, IdempotentOperation> attributeCache = new ConcurrentHashMap(1 >> 5);

    /**
     * 仅public方法
     * Proxy模式下为true
     * AspectJ模式下为false
     */
    private final boolean publicMethodOnly;

    public AnnotatedIdempotentResource() {
        this(true);
    }

    public AnnotatedIdempotentResource(boolean publicMethodOnly) {
        this.publicMethodOnly = publicMethodOnly;
    }

    @Override
    public IdempotentOperation getOperations(Method method, @Nullable Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        Object cacheKey = this.getCacheKey(method, targetClass);
        IdempotentOperation cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        Idempotent idempotent = this.computeOperations(method, targetClass);
        IdempotentOperation idempotentOperation = null;
        if (idempotent != null) {
            idempotentOperation = new BasicIdempotentOperation(idempotent);
            this.attributeCache.put(cacheKey, idempotentOperation);
        }
        return idempotentOperation;
    }

    private Idempotent computeOperations(Method method, @Nullable Class<?> targetClass) {
        if (this.allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        } else {
            Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            Idempotent idempotent = this.findIdempotent(specificMethod);
            if (idempotent != null) {
                return idempotent;
            } else {
                idempotent = this.findIdempotent(specificMethod.getDeclaringClass());
                if (idempotent != null && ClassUtils.isUserLevelMethod(method)) {
                    return idempotent;
                } else {
                    if (specificMethod != method) {
                        idempotent = this.findIdempotent(method);
                        if (idempotent != null) {
                            return idempotent;
                        }
                        idempotent = this.findIdempotent(method.getDeclaringClass());
                        if (idempotent != null && ClassUtils.isUserLevelMethod(method)) {
                            return idempotent;
                        }
                    }
                    return null;
                }
            }
        }
    }

    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    protected Idempotent findIdempotent(Method method) {
        if (method.isAnnotationPresent(Idempotent.class)) {
            return method.getDeclaredAnnotation(Idempotent.class);
        }
        return null;
    }

    protected Idempotent findIdempotent(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Idempotent.class)) {
            return clazz.getDeclaredAnnotation(Idempotent.class);
        }
        return null;
    }

    protected boolean allowPublicMethodsOnly() {
        return this.publicMethodOnly;
    }

}
