package org.seckill.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author caoduanxi
 * @2019/11/29 15:18
 */
@Mapper
public class SeckillMapper implements SeckillDao {
    @Autowired
    private SeckillDao seckillDao;
    @Override
    public int reduceNumber(long seckillId, Date killedTime) {
        return seckillDao.reduceNumber(seckillId,killedTime);
    }

    @Override
    public Seckill queryById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public List<Seckill> queryAll(int offset, int limit) {
        return seckillDao.queryAll(offset,limit);
    }
}
