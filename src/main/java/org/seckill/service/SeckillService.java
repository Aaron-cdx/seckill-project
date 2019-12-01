package org.seckill.service;

import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 接口的设计需要站在使用者的角度来设计
 *
 * @author caoduanxi
 * @2019/11/30 9:58
 */
public interface SeckillService {
    List<Seckill> getSeckillList();

    Seckill getById(long seckillId);

    /**
     * 暴露接口地址
     * 秒杀开始，暴露秒杀接口的地址，输出接口地址
     * 秒杀失败，则返回系统当前时间和秒杀时间
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀
     * 秒杀成功获取秒杀状态以及秒杀状态信息
     * 秒杀失败的话获取秒杀状态以及秒杀信息
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    ExecutionSeckill executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillEndException, SeckillException, RepeatException;

    ExecutionSeckill executionProcedure(long seckillId, long userPhone, String md5)
            throws SeckillEndException, SeckillException, RepeatException;;
}
