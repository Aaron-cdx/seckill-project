package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

import java.util.Date;

/**
 * @author caoduanxi
 * @2019/11/29 14:09
 */
public interface SuccessKilledDao {

    /**
     * 执行插入成功明细的操作
     * @param seckillId
     * @param phoneNumber
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("phoneNumber") long phoneNumber);

    /**
     * 查询成功明细，通过查询的id以及电话号码
     * @param seckillId
     * @return
     */
    SuccessKilled queryWithSeckill(@Param("seckillId")long seckillId,@Param("phoneNumber") long phoneNumber);
}
