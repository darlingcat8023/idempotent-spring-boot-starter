package com.guazi.idempotent;

import com.guazi.idempotent.annotation.Idempotent;
import com.guazi.idempotent.fallback.IdempotentFallBack;

/**
 * Idempotent注解包装
 * @author liujiajun
 * @date 2020/5/25
 */
public class BasicIdempotentOperation implements IdempotentOperation {

    private final String key;

    private final String condition;

    private final String idempotentManager;

    private final Class<? extends IdempotentFallBack<?>> fallback;

    public BasicIdempotentOperation(Idempotent idempotent) {
        this.key = idempotent.key();
        this.condition = idempotent.condition();
        this.idempotentManager = idempotent.idempotentManager();
        this.fallback = idempotent.fallback();
    }

    @Override
    public String getManager() {
        return this.idempotentManager;
    }

    @Override
    public String getCondition() {
        return this.condition;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Class<? extends IdempotentFallBack<?>> getFallback() {
        return this.fallback;
    }

}
