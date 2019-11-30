package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author caoduanxi
 * @2019/11/30 16:47
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-*.xml"})
public class SeckillServiceImplTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("seckillList={}", seckillList);
    }

    @Test
    public void getById() {
        Seckill seckill = seckillService.getById(1000);
        logger.info("seckill={}", seckill);
    }


    @Test
    public void seckillExecutionLogic() {
        long seckillId = 1000;
        long phoneNumber = 15779236476L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        // 如果开启秒杀了
        if (exposer.isExposed()) {
            logger.info("exposer={}", exposer);
            try {
                ExecutionSeckill executionSeckill = seckillService.executeSeckill(seckillId, phoneNumber, exposer.getMd5());
                logger.info("executionSeckill={}", executionSeckill);
            } catch (RepeatException e) {
                logger.warn(e.getMessage());
            } catch (SeckillEndException e) {
                logger.warn(e.getMessage());
            } catch (SeckillException e) {
                logger.warn(e.getMessage());
            }
        } else {
            logger.info("exposer={}", exposer);
        }
    }

    @Test
    public void exportSeckillUrl() {
        Exposer exposer = seckillService.exportSeckillUrl(1000);
        logger.info("exposer={}", exposer);
        /*
        exposer=Exposer{exposed=true, seckillId=1000, md5='729083467b69cf80310607f34a273cbc', now=0, startTime=0, endTime=0}
         */
    }

    @Test
    public void executeSeckill() {
        String md5 = "729083467b69cf80310607f34a273cbc";
        long seckillId = 1000;
        long phoneNumber = 15779236476L;
        try {
            ExecutionSeckill executionSeckill = seckillService.executeSeckill(seckillId, phoneNumber, md5);
            logger.info("executionSeckill={}", executionSeckill);
        } catch (RepeatException e) {
            logger.warn(e.getMessage());
        } catch (SeckillEndException e) {
            logger.warn(e.getMessage());
        } catch (SeckillException e) {
            e.printStackTrace();
        }
    }
}