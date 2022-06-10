package com.guazi.idempotent.excetption;

/**
 * 异常类
 * @author liujiajun
 * @date 2020/5/25
 */
public class IdempotentFailException extends RuntimeException {

    public IdempotentFailException(String key) {
        super(key + " has already exists");
    }

}
