package org.seckill.service.impl;

import java.util.HashMap;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.SeckillRedisDao;
import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @author caoduanxi
 * @2019/11/30 11:34
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private SeckillRedisDao redisDao;
    // 盐值，用于混淆md5值
    private final String slat = "fahs8ofujoaw*&(793749r8JIOjdofawjoi";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        // 获取秒杀对象，首先从redis中看看有没有，没有再从mysql数据库中查找
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            // 获取当前要秒杀的对象
            seckill = seckillDao.queryById(seckillId);
            // 对象为空的话，直接false
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                String result = redisDao.putSeckill(seckill);
                logger.info("存入redis的结果result={}", result);
            }
        }
        // 对象不为空的话
        // 判断是否能够秒杀
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date now = new Date();
        // 如果说时间上失败的话，代表秒杀失败
        if (now.getTime() < startTime.getTime() || now.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 如果说能够实现秒杀的话，此时需要暴露接口地址
        // 这里的MD5值是为了防止外界获取到秒杀的商品id从而利用脚本来秒杀，造成不必要的安全问题
        String md5 = getMD5(seckillId);
        return new Exposer(true, seckillId, md5);
    }

    // 这里的权限需要限定，防止外界访问获取到MD5值
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    public ExecutionSeckill executeSeckill(long seckillId, long userPhone, String md5) throws SeckillEndException, SeckillException, RepeatException {
        Date now = new Date();
        try {
            // 如果md5值为空，且不等于当前系统所获得的，表示输出被篡改
            if (md5 == null || !md5.equals(getMD5(seckillId))) {
                throw new SeckillException("数据被篡改！");
            }
            // 这里为了减少网络延迟和GC的影响，可以将购买明细的插入提前，交换顺序
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                throw new RepeatException("重复秒杀！");
            } else {
                // 这里来减库存,热点商品竞争发生在这里，先插入明细有利于当重复秒杀的时候不用重复的判断减库存
                // 这样可以减少一次GC所耗费的时间
                int reduceNumber = seckillDao.reduceNumber(seckillId, now);
                // 如果秒杀未结束且减库存失败，表明重复秒杀行为
                if (reduceNumber <= 0) {
                    // 表明秒杀结束
                    throw new SeckillEndException("秒杀结束！");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryWithSeckill(seckillId, userPhone);
                    return new ExecutionSeckill(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
            /*// 减库存
            int reduceNumber = seckillDao.reduceNumber(seckillId, now);
            // 如果秒杀未结束且减库存失败，表明重复秒杀行为
            if (reduceNumber <= 0) {
                // 表明秒杀结束
                throw new SeckillEndException("秒杀结束！");
            } else {
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    throw new RepeatException("重复秒杀！");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryWithSeckill(seckillId, userPhone);
                    return new ExecutionSeckill(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }*/
        } catch (SeckillEndException e1) {
            throw e1;
        } catch (RepeatException e2) {
            throw e2;
        } catch (Exception e) {
            // 将编译期异常转变为运行期异常
            logger.error(e.getMessage());
            throw new SeckillException("系统错误" + e.getMessage());
        }
    }

    // 使用存储过程实现秒杀
    public ExecutionSeckill executionProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new ExecutionSeckill(seckillId, SeckillStateEnum.DATA_REWRITE);
        }

        Date killTime = new Date();
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("killTime", killTime);
        map.put("phoneNumber", userPhone);
        map.put("result", null);
        // 执行存储过程
        try {
            seckillDao.killByProcedure(map);
            // 获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled successKilled = successKilledDao.queryWithSeckill(seckillId, userPhone);
                return new ExecutionSeckill(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new ExecutionSeckill(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ExecutionSeckill(seckillId, SeckillStateEnum.INNER_ERROR);
        }

    }

}
