package org.seckill.dao.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * @author caoduanxi
 * @2019/12/1 16:13
 */
public class RedisDao {
    private JedisPool jedisPool;

    private JedisPoolConfig jedisPoolConfig;

    private String redisIp = "192.168.87.101";

    private int redisPort = 6379;

//    private String redisAuth = "";

    private int redisTimeout = 5000;

    private void initJedisConfig() {
        this.jedisPoolConfig = new JedisPoolConfig();
        this.jedisPoolConfig.setMaxTotal(200);
        this.jedisPoolConfig.setMaxIdle(50);
        this.jedisPoolConfig.setMinIdle(8);// 设置最小空闲数
        this.jedisPoolConfig.setMaxWaitMillis(10000);
        this.jedisPoolConfig.setTestOnBorrow(true);
        this.jedisPoolConfig.setTestOnReturn(true);
        // Idle时进行连接扫描
        this.jedisPoolConfig.setTestWhileIdle(true);
        // 表示idle object evitor两次扫描之间要sleep的毫秒数
        this.jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        // 表示idle object evitor每次扫描的最多的对象数
        this.jedisPoolConfig.setNumTestsPerEvictionRun(10);
        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
        // evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        this.jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);
    }

    public RedisDao() {
        initJedisConfig();
    }

    public RedisDao(String ip, int port) {
        this.redisIp = ip;
        this.redisPort = port;
        initJedisConfig();
    }

    public RedisDao(String ip, int port, int timeout) {
        this.redisIp = ip;
        this.redisPort = port;
        this.redisTimeout = timeout;
        initJedisConfig();

    }

    @PostConstruct
    private synchronized JedisPool initJedisPool() {
        this.jedisPool = new JedisPool(this.jedisPoolConfig, this.redisIp, this.redisPort, this.redisTimeout);
        return this.jedisPool;
    }

    public Jedis getJedis() {
        if (null == this.jedisPool)
            return getJedisPool().getResource();
        return this.jedisPool.getResource();
    }

    public JedisPool getJedisPool() {
        if (null == jedisPool) {
            initJedisPool();
        }
        return jedisPool;
    }

    public void close() {
        // if (jedisPool != null)
        // jedisPool.close();
    }
}
