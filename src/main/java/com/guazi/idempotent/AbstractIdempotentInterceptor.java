package com.guazi.idempotent;

import com.guazi.idempotent.fallback.IdempotentFallBack;
import com.guazi.idempotent.manager.IdempotentManager;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 抽象切点拦截器
 * @author liujiajun
 * @date 2020/5/25
 */
public abstract class AbstractIdempotentInterceptor implements BeanFactoryAware, InitializingBean, SmartInitializingSingleton {

    private BeanFactory beanFactory;

    private IdempotentResource idempotentResource;

    private final IdempotentExpressionEvaluator expressionEvaluator = new IdempotentExpressionEvaluator();

    private volatile boolean initialized = false;

    public IdempotentResource getIdempotentResource() {
        return this.idempotentResource;
    }

    public void setIdempotentResource(IdempotentResource idempotentResource) {
        this.idempotentResource = idempotentResource;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception { }

    @Override
    public void afterSingletonsInstantiated() {
        this.initialized = true;
    }

    /**
     * 从beanFactory获取bean
     * @param beanName
     * @param expectedType
     * @param <T>
     * @return
     */
    protected <T> T getBean(String beanName, Class<T> expectedType) {
        if (this.beanFactory == null) {
            throw new IllegalStateException("BeanFactory must be set on aspect for " + expectedType.getSimpleName() + " retrieval");
        } else {
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, expectedType, beanName);
        }
    }

    protected Object execute(IdempotentOperationInvoker invoker, Object target, Method method, Object[] args) throws Throwable {
        if (this.initialized) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);;
            IdempotentResource resource = this.getIdempotentResource();
            if (resource != null) {
                IdempotentOperation operation = resource.getOperations(method, targetClass);
                if (operation != null) {
                    return this.execute(invoker, method, this.buildContext(operation, method, args, target, targetClass));
                }
            }
        }
        return invoker.invoke();
    }

    private Object execute(IdempotentOperationInvoker invoker, Method method, IdempotentOperationContext context) throws Throwable {
        EvaluationContext evaluationContext = context.createEvaluationContext(null);
        if (this.conditionPass(context, evaluationContext)) {
            String key = this.generateKey(context, evaluationContext);
            IdempotentManager idempotentManager = context.metadata.idempotentManager;
            IdempotentFallBack<?> fallBack = context.metadata.fallBack;
            try {
                if (!idempotentManager.execute(key)) {
                    return fallBack.onFailure(key, context.args);
                }
            } catch (Throwable e) {
                return fallBack.onError(key, context.args, e);
            }
        }
        return invoker.invoke();
    }

    private String generateKey(IdempotentOperationContext context, EvaluationContext evaluationContext) {
        String key = context.metadata.operation.getKey();
        return this.expressionEvaluator.key(key, evaluationContext);
    }

    private boolean conditionPass(IdempotentOperationContext context, EvaluationContext evaluationContext) {
        String condition = context.metadata.operation.getCondition();
        if (StringUtils.hasText(condition)) {
            return this.expressionEvaluator.condition(condition, evaluationContext);
        }
        return true;
    }

    private Object register(Class<?> clazz) {
        return ((AutowireCapableBeanFactory)this.beanFactory).createBean(clazz);
    }

    private IdempotentOperationContext buildContext(IdempotentOperation operations, Method method, Object[] args, Object target, Class<?> targetClass) throws Throwable {
        String manager = operations.getManager();
        IdempotentManager idempotentManager = this.getBean(manager, IdempotentManager.class);
        Class<?> fallback = operations.getFallback();
        IdempotentFallBack<?> instance = (IdempotentFallBack<?>) this.register(fallback);
        IdempotentOperationMetadata metadata = new IdempotentOperationMetadata(operations, method, targetClass, idempotentManager, instance);
        return new IdempotentOperationContext(metadata, args, target);
    }

    /**
     * Idempotent实例数据封装
     */
    protected static class IdempotentOperationMetadata {
        private final IdempotentOperation operation;
        private final Method method;
        private final Class<?> targetClass;
        private final Method targetMethod;
        private final IdempotentManager idempotentManager;
        private final IdempotentFallBack<?> fallBack;

        public IdempotentOperationMetadata(IdempotentOperation operation, Method method, Class<?> targetClass, IdempotentManager manager, IdempotentFallBack<?> fallBack) {
            this.operation = operation;
            this.method = BridgeMethodResolver.findBridgedMethod(method);
            this.targetClass = targetClass;
            this.targetMethod = !Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(method, targetClass) : this.method;
            this.idempotentManager = manager;
            this.fallBack = fallBack;
        }
    }

    protected class IdempotentOperationContext {
        private final IdempotentOperationMetadata metadata;
        private final Object[] args;
        private final Object target;

        public IdempotentOperationContext(IdempotentOperationMetadata metadata, Object[] args, Object target) {
            this.metadata = metadata;
            this.args = this.extractArgs(metadata.method, args);
            this.target = target;
        }

        private Object[] extractArgs(Method method, Object[] args) {
            if (!method.isVarArgs()) {
                return args;
            } else {
                // 用来处理方法德不定参数
                Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
                Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
                System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
                System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
                return combinedArgs;
            }
        }

        private EvaluationContext createEvaluationContext(@Nullable Object result) {
            return AbstractIdempotentInterceptor.this.expressionEvaluator.createEvaluationContext(this.metadata.method, this.args, this.target, this.metadata.targetClass, this.metadata.targetMethod, result, AbstractIdempotentInterceptor.this.beanFactory);
        }

    }

}
