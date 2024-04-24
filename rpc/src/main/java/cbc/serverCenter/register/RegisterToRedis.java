package cbc.serverCenter.register;


import cbc.exception.ServerException;
import cbc.jedis.JedisConnectionFactory;
import cbc.server.StartServer;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

import static cbc.constants.RedisConstants.*;


/**
 * @Author Cbc
 * @DateTime 2024/4/23 21:07
 * @Description 将服务注册到redis
 */

public class RegisterToRedis {

    private static StartServer startServer;

    /**
     * 开始注册
     */
    public static void register(String serverName, String host, Integer port){

        //1.检查是否已被注册
        //注意对名字进行转换
        serverName = SERVER_REGISTER_NAME + serverName;

        Jedis jedis = JedisConnectionFactory.getJedis();
        if (jedis.exists(serverName)) {
            throw new ServerException("该服务名称已存在!");
        }

        //开启服务
        startServer = new StartServer(port);

        //TODO 使用netty开启服务端
        Map<String, String> map = new HashMap<>(2);
        map.put(SERVER__REGISTER_PORT, port.toString());
        map.put(SERVER__REGISTER_HOST, host);
        jedis.hset(serverName, map);

    }

    /**
     * 移除服务
     */
    public static void removeServer(String serverName){
        //对名字进行转换
        serverName = SERVER_REGISTER_NAME + serverName;

        Jedis jedis = JedisConnectionFactory.getJedis();
        if (!jedis.exists(serverName)) {
            return;
        }

        //在redis中删除服务
        jedis.del(serverName);

        //关闭服务端
        startServer.closeServer();
        startServer = null;
    }
}
