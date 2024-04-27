package cbc.message;

import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:37
 * @Description
 */
@Data
public class ResponseMessage extends Message{
    //消息id
    private Integer id;

    //序列化方式
    private String serializeType;

    //返回的结果
    private Object result;

    //返回的错误
    private Exception exception;

    //返回结果的类型
    private Class<?> resultType;

    public ResponseMessage() {
        messageType = 1;
    }
}
