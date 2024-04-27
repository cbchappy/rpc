package cbc.message;

import lombok.Data;

import java.time.Period;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:36
 * @Description
 */
@Data
public class RequestMessage extends Message{
    //消息的id, 用来区分返回消息以及获取promise
    private Integer id;
    //请求的server接口(接口的全限定名)
    private String interfaceName;

    //请求的方法
    private String methodName;

    //请求的方法参数类型
    private Class[] parameterTypes;

    //返回结果的类型
    private Class<?> resultType;

    //请求的方法参数
    private Object[] parameterValues;

    //序列化方式
    private String serializeType;

    public RequestMessage() {
        id = getIdFromMessage();
        messageType = 0;
    }
}
