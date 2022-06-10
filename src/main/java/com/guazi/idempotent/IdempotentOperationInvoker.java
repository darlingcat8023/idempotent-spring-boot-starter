package com.guazi.idempotent;

/**
 * 切点方法包装类
 * @author liujiajun
 * @date 2020/5/25
 */
@FunctionalInterface
public interface IdempotentOperationInvoker {

    /**
     * 执行切点方法，包装异常类
     * @return
     * @throws IdempotentOperationInvoker.ThrowableWrapper
     */
    Object invoke() throws IdempotentOperationInvoker.ThrowableWrapper;

    /**
     * 包装异常
     */
    class ThrowableWrapper extends RuntimeException {

        private final Throwable original;

        public ThrowableWrapper(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }

}
