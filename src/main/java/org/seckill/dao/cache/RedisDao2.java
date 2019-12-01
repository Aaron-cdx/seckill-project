package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 单纯使用这个代码会报错
 * @author caoduanxi
 * @2019/12/1 14:00
 */
public class RedisDao2 {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JedisPool jedisPool;

    public RedisDao2(String ip, int port) {
//        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxTotal(100);
//        //设置最大连接数（100个足够用了，没必要设置太大）
//        config.setMaxTotal(100);
//        //最大空闲连接数
//        config.setMaxIdle(10);
//        //获取Jedis连接的最大等待时间（50秒）
//        config.setMaxWaitMillis(50 * 1000);
//        //在获取Jedis连接时，自动检验连接是否可用
//        config.setTestOnBorrow(true);
//        //在将连接放回池中前，自动检验连接是否有效
//        config.setTestOnReturn(true);
//        //自动测试池中的空闲连接是否都是可用连接
//        config.setTestWhileIdle(true);

//        jedisPool = new JedisPool(config,ip, port);
        jedisPool = new JedisPool(ip, port);
        System.out.println(jedisPool);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(long seckillId) {
        // redis 操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;

                // 由于redis内部并没有自动实现序列化，所以需要自己手动实现序列化
                byte[] bytes = jedis.get(key.getBytes());
                // 从缓存中获取
                // 如果含有这个redis的键
                if (bytes != null) {
                    // 构造一个空的seckill对象
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    // seckill被反序列化
                    return seckill;
                }
            } finally {
                jedisPool.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    // 序列化对象
    public String putSeckill(Seckill seckill) {
        // Object(Seckill) -> 序列化 -> bytes[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                // 默认使用一个缓冲区
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 超时缓存
                int timeout = 60 * 60;
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedisPool.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
