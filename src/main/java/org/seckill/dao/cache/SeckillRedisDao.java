package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.seckill.infrastructure.RedisComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

/**
 * @author caoduanxi
 * @2019/12/1 16:14
 */
@RedisComponent
public class SeckillRedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RuntimeSchema<Seckill> seckillSchema = RuntimeSchema.createFrom(Seckill.class);

    @Autowired
    private RedisDao redisDao;

    public Seckill getSeckill(long seckillId) {
        try {
            Jedis jedis = redisDao.getJedis();

            try {
                String key = "seckill:" + seckillId;
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    Seckill seckill = seckillSchema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, seckillSchema);
                    return seckill;
                }
            } finally {
                redisDao.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill) {
        try {
            Jedis jedis = redisDao.getJedisPool().getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, seckillSchema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                int timeout = 60 * 60;
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}