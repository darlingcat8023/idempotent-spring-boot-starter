package com.guazi.idempotent.fallback;

import com.guazi.idempotent.excetption.IdempotentFailException;

/**
 * 默认异常处理
 * @author liujiajun
 * @date 2020/5/25
 */
public class DefaultIdempotentFallBack implements IdempotentFallBack<Void> {

    @Override
    public Void onFailure(String key, Object[] args) throws Throwable {
        throw new IdempotentFailException(key);
    }

    @Override
    public Void onError(String key, Object[] args, Throwable throwable) throws Throwable {
        throw throwable;
    }
}
