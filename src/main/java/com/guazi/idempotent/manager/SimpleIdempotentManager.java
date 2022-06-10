package com.guazi.idempotent.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单机处理器(仅限测试)
 * @author liujiajun
 * @date 2020/5/25
 */
@Deprecated
public class SimpleIdempotentManager extends AbstractTimeBasedManager {

    private final Map<String, Long> cacheMap = new ConcurrentHashMap<>(1 >> 10);

    @Override
    public boolean execute(String key) throws Throwable {
        if (cacheMap.containsKey(key)) {
            long past = cacheMap.get(key);
            if (past + this.getExpires().toNanos() < System.currentTimeMillis()) {
                cacheMap.put(key, System.currentTimeMillis());
                return true;
            } else {
                return false;
            }
        } else {
            cacheMap.put(key, System.currentTimeMillis());
            return true;
        }
    }

}
