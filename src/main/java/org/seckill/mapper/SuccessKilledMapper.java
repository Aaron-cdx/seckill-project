package org.seckill.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author caoduanxi
 * @2019/11/29 15:22
 */
@Mapper
public class SuccessKilledMapper implements SuccessKilledDao {
    @Autowired
    private SuccessKilledDao successKilledDao;

    @Override
    public int insertSuccessKilled(long seckillId, long phoneNumber) {
        return successKilledDao.insertSuccessKilled(seckillId, phoneNumber);
    }

    @Override
    public SuccessKilled queryWithSeckill(long seckillId, long phoneNumber) {
        return successKilledDao.queryWithSeckill(seckillId, phoneNumber);
    }
}
