package org.seckill.exception;

/**
 * 秒杀结束异常
 *
 * @author caoduanxi
 * @2019/11/30 11:23
 */
public class SeckillEndException extends SeckillException {
    public SeckillEndException(String message) {
        super(message);
    }

    public SeckillEndException(String message, Throwable cause) {
        super(message, cause);
    }
}
