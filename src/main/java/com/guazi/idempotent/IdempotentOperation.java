package com.guazi.idempotent;

import com.guazi.idempotent.fallback.IdempotentFallBack;

/**
 * Idempotent封装
 * @author liujiajun
 * @date 2020/5/25
 */
public interface IdempotentOperation {

    /**
     * 获取key
     * @return
     */
    String getKey();

    /**
     * 获取条件
     * @return
     */
    String getCondition();

    /**
     * 获取管理器
     * @return
     */
    String getManager();

    /**
     * 获取fallback
     * @return
     */
    Class<? extends IdempotentFallBack<?>> getFallback();

}
