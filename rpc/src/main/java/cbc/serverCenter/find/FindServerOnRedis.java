package cbc.serverCenter.find;

import cbc.client.StartClient;
import cbc.constants.RedisConstants;
import cbc.exception.ServerException;
import cbc.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;

import static cbc.constants.RedisConstants.*;

/**
 * @Author Cbc
 * @DateTime 2024/4/23 21:47
 * @Description
 */
public class FindServerOnRedis {

    public static StartClient findServer(String serverName){
        String originalName = serverName;
        //检查是否存在服务
        serverName = SERVER_REGISTER_NAME + serverName;
        Jedis jedis = JedisConnectionFactory.getJedis();// client入口 --->server的注册名 要调用的server模块 调用方法
                                                         //   --->返回动态代理的实例  ----> client直接调用实例
        if (!jedis.exists(serverName)) {                //----> 接口名相同 方法名相同 参数类型相同
            throw new ServerException("服务未找到!");     //server入口
        }                                               //

        // 对服务进行连接  使用netty创建客户端进行连接
        Map<String, String> map = jedis.hgetAll(serverName);
        String host = map.get(SERVER__REGISTER_HOST);
        int port = Integer.parseInt(map.get(SERVER__REGISTER_PORT));

       //创建客户端
        return new StartClient(host, port, originalName);

    }


}
