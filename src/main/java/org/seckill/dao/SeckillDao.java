package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author caoduanxi
 * @2019/11/29 14:07
 */

public interface SeckillDao {
    /**
     * 执行减库存操作
     * @param seckillId
     * @param killedTime
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killedTime") Date killedTime);

    /**
     * 通过秒杀id查询物品
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 查询所有的物品，为分页准备
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
