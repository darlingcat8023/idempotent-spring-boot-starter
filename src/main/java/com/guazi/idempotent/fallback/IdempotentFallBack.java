package com.guazi.idempotent.fallback;

/**
 * 熔断处理器
 * 只要实现了该接口的子类，无论是否有@Component等注解标注，都会被Spring实例化为一个Bean
 * 实例化后的Bean并不会被注册到BeanFactory缓存中，所以不同的子类之间互不影响
 * @author liujiajun
 * @date 2020/5/25
 */
public interface IdempotentFallBack<T> {

    /**
     * 幂等失败时的处理逻辑
     * @param key 根据SpEL表达式计算出的key
     * @param args 方法参数数组
     * @return 方法返回值
     * @throws Throwable
     */
    T onFailure(String key, Object[] args) throws Throwable;

    /**
     * 幂等出错时的处理逻辑
     * @param key 根据SpEL表达式计算出的key
     * @param args 方法参数数组
     * @param throwable 执行过程中抛出的异常
     * @return 方法返回值
     * @throws Throwable
     */
    T onError(String key, Object[] args, Throwable throwable) throws Throwable;

}
