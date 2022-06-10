package com.guazi.idempotent.manager;

import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * 基于时间序列的Manager
 * @author liujiajun
 * @date 2020/5/27
 */
public abstract class AbstractTimeBasedManager implements IdempotentManager {

    private Duration expires;

    public void setExpires(Duration expires) {
        this.expires = expires;
    }

    public Duration getExpires() {
        return this.expires;
    }

    /**
     * 判断是否有超时时间
     * @param expires
     * @return
     */
    public boolean shouldExpireWithin(@Nullable Duration expires) {
        return expires != null && !expires.isZero() && !expires.isNegative();
    }
}
