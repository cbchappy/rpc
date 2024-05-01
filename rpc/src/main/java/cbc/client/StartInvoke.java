package cbc.client;

import cbc.message.RequestMessage;
import cbc.serverCenter.find.FindServerOnRedis;
import io.netty.util.concurrent.DefaultPromise;
import org.redisson.MapWriterTask;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Cbc
 * @DateTime 2024/4/27 15:30
 * @Description
 */
public class StartInvoke {
    //存储已经开启的StartClient
    private static Map<String, StartClient> map = new HashMap<>();

    private static Map<Class, Object> proxyMap = new HashMap<>();
    /**
     * 返回实例以供client调用
     */
    public static <T> T getProxy(String serverName, Class<T> clazz){

        if(proxyMap.containsKey(clazz)){
            return (T) proxyMap.get(clazz);
        }
        //寻找服务
        StartClient client = map.get(serverName);

        if(client == null){
            client = FindServerOnRedis.findServer(serverName);
        }

        StartClient finalClient = client;
        Object o = Proxy.newProxyInstance(FindServerOnRedis.class.getClassLoader(), new Class[]{clazz}, (p, m, args) -> {
            //创建并完善request
            RequestMessage request = new RequestMessage();
            request.setParameterValues(args);
            request.setParameterTypes(m.getParameterTypes());
            request.setMethodName(m.getName());
            request.setInterfaceName(clazz.getSimpleName());
            request.setClazz(m.getReturnType().getName());
            return finalClient.send(request);
        });
        proxyMap.put(clazz, o);
        return (T) o;
    }

    /**
     * 移除client
     * @param serverName
     */
    public static void removeClient(String serverName){
        map.remove(serverName);
    }
}
