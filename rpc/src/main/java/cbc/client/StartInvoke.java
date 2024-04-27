package cbc.client;

import cbc.message.RequestMessage;
import cbc.serverCenter.find.FindServerOnRedis;

import java.lang.reflect.Proxy;

/**
 * @Author Cbc
 * @DateTime 2024/4/27 15:30
 * @Description
 */
public class StartInvoke {
    /**
     * 返回实例以供client调用
     */
    public static <T> T getProxy(String serverName, Class<T> clazz){
        //寻找服务
        StartClient client = FindServerOnRedis.findServer(serverName);

        //创建proxy
        Object o = Proxy.newProxyInstance(FindServerOnRedis.class.getClassLoader(), new Class[]{clazz}, (p, m, args) -> {
            //创建并完善request
            RequestMessage request = new RequestMessage();
            request.setParameterValues(args);
            request.setParameterTypes(m.getParameterTypes());
            request.setMethodName(m.getName());
            request.setInterfaceName(clazz.getSimpleName());
            request.setResultType(m.getReturnType());
            return client.send(request);
        });

        return (T) o;
    }
}
