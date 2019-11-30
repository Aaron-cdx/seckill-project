package org.seckill.exception;

/**
 * 秒杀异常
 *
 * @author caoduanxi
 * @2019/11/30 11:23
 */
public class SeckillException extends Exception {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
