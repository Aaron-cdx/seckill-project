package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author caoduanxi
 * @2019/12/1 14:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
    @Autowired
    private SeckillRedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;

    long seckillId = 1001;

    @Test
    public void getSeckill() {
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if(seckill != null){
                String s = redisDao.putSeckill(seckill);
                System.out.println(s);
                seckill = redisDao.getSeckill(seckillId);
                System.out.println(seckill);
            }
        }
    }

    @Test
    public void putSeckill() {
    }
}