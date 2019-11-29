package org.seckill.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


/**
 * @author caoduanxi
 * @2019/11/29 17:12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledMapperTest {
    @Resource
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() {
        int i = successKilledDao.insertSuccessKilled(1000, 15779236476L);
        System.out.println("i= "+i);
    }

    @Test
    public void queryWithSeckill() {
        SuccessKilled successKilled = successKilledDao.queryWithSeckill(1000, 15779236476L);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}