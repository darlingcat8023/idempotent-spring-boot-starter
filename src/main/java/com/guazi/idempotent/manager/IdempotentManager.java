package com.guazi.idempotent.manager;

/**
 * 幂等管理器
 * @author liujiajun
 * @date 2020/5/25
 */
public interface IdempotentManager {

    /**
     * 执行方法
     * @param key
     * @return
     * @throws Throwable
     */
    boolean execute(String key) throws Throwable;

}
