package cbc.jedis;

import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author Cbc
 * @DateTime 2024/4/23 20:41
 * @Description
 */
public class JedisConnectionFactory {
    private static final JedisPool JEDIS_POOL;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大连接数
        jedisPoolConfig.setMaxTotal(8);
        //最大空闲连接数
        jedisPoolConfig.setMaxIdle(4);
        //最小空闲连接数
        jedisPoolConfig.setMinIdle(0);
        //设置最长等待时间
        jedisPoolConfig.setMaxWait(BaseObjectPoolConfig.DEFAULT_MAX_WAIT);
        //初始化线程池JEDIS_POOL
        JEDIS_POOL = new JedisPool(jedisPoolConfig, "192.168.200.128", 6379, 1000);
    }

    /**
     * 返回一个Jedis
     * @return
     */
    public static Jedis getJedis(){
        return JEDIS_POOL.getResource();
    }
}
