package cbc.serverCenter.find;

import cbc.exception.ServerException;
import cbc.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;

import static cbc.constants.RedisConstants.SERVER_REGISTER_NAME;

/**
 * @Author Cbc
 * @DateTime 2024/4/23 21:47
 * @Description
 */
public class FindServerOnRedis {

    public static void findServer(String serverName){
        //检查是否存在服务
        serverName = SERVER_REGISTER_NAME + serverName;
        Jedis jedis = JedisConnectionFactory.getJedis();
        if (!jedis.exists(serverName)) {
            throw new ServerException("服务未找到!");
        }

        //TODO 对服务进行连接  使用netty创建客户端进行连接
        Map<String, String> map = jedis.hgetAll(serverName);

    }
}
