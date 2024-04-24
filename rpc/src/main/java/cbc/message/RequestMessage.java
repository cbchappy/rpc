package cbc.message;

import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:36
 * @Description
 */
@Data
public class RequestMessage extends Message{
    //请求的server接口(接口的全限定名)
    private String interfaceName;

    //请求的方法
    private String methodName;

    //请求的方法参数类型
    private Class[] parameterTypes;

    //请求的方法参数
    private Object[] parameterValues;

    //序列化方式
    private String serializeType;

    public RequestMessage() {
        messageType = 0;
    }
}
