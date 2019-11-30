package org.seckill.exception;

/**
 * 重复秒杀异常
 *
 * @author caoduanxi
 * @2019/11/30 11:22
 */
public class RepeatException extends SeckillException {
    public RepeatException(String message) {
        super(message);
    }

    public RepeatException(String message, Throwable cause) {
        super(message, cause);
    }
}
