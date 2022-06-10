package com.guazi.idempotent;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * Spel表达式解析器
 * @author liujiajun
 * @date 2020/5/25
 */
public class IdempotentExpressionEvaluator {

    private final SpelExpressionParser parser;

    private final ParameterNameDiscoverer parameterNameDiscoverer;

    protected IdempotentExpressionEvaluator() {
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.parser = new SpelExpressionParser();
    }

    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    /**
     * 构建表达式上下文
     * @param method
     * @param args
     * @param target
     * @param targetClass
     * @param targetMethod
     * @param result
     * @param beanFactory
     * @return
     */
    public EvaluationContext createEvaluationContext(Method method, Object[] args, Object target, Class<?> targetClass, Method targetMethod, @Nullable Object result, @Nullable BeanFactory beanFactory) {
        ExpressionRootObject rootObject = new ExpressionRootObject(method, args, target, targetClass);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(rootObject, targetMethod, args, this.getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    protected ExpressionParser getParser() {
        return this.parser;
    }

    protected Expression getExpression(String expression) {
        return this.getParser().parseExpression(expression);
    }

    public String key(String keyExpression, EvaluationContext evalContext) {
        return String.valueOf(this.getExpression(keyExpression).getValue(evalContext));
    }

    public boolean condition(String conditionExpression, EvaluationContext evalContext) {
        return Boolean.TRUE.equals(this.getExpression(conditionExpression).getValue(evalContext, Boolean.class));
    }

    /**
     * Spel上下文Root
     */
    static class ExpressionRootObject {

        private final Method method;
        private final Object[] args;
        private final Object target;
        private final Class<?> targetClass;

        public ExpressionRootObject(Method method, Object[] args, Object target, Class<?> targetClass) {
            this.method = method;
            this.target = target;
            this.targetClass = targetClass;
            this.args = args;
        }

        public Method getMethod() {
            return this.method;
        }

        public String getMethodName() {
            return this.method.getName();
        }

        public Object[] getArgs() {
            return this.args;
        }

        public Object getTarget() {
            return this.target;
        }

        public Class<?> getTargetClass() {
            return this.targetClass;
        }
    }

}
