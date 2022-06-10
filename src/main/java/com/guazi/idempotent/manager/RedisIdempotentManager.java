package com.guazi.idempotent.manager;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.types.Expiration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.data.redis.connection.RedisStringCommands.SetOption.SET_IF_ABSENT;

/**
 * Redis管理器
 * @author liujiajun
 * @date 2020/5/27
 */
public final class RedisIdempotentManager extends AbstractTimeBasedManager {

    private static final byte[] BYTE_IDEMPOTENT_VALUE = "IDEMPOTENT_VALUE".getBytes();

    private RedisConnectionFactory redisConnectionFactory;

    public RedisIdempotentManager(RedisConnectionFactory redisConnectionFactory) {
        super();
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public boolean execute(String key) throws Throwable {
        RedisConnection connection = this.redisConnectionFactory.getConnection();
        Boolean flag = Boolean.FALSE;
        byte[] keyBytes = key.getBytes();
        try {
            if (shouldExpireWithin(this.getExpires())) {
                flag = connection.set(keyBytes, BYTE_IDEMPOTENT_VALUE, Expiration.from(this.getExpires().toMillis(), MILLISECONDS), SET_IF_ABSENT);
            } else {
                flag = connection.set(keyBytes, BYTE_IDEMPOTENT_VALUE);
            }
        } finally {
            connection.close();
        }
        return flag;
    }

}
