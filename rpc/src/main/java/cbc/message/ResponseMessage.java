package cbc.message;

import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:37
 * @Description
 */
@Data
public class ResponseMessage extends Message{


    //返回的结果
    private Object result;

    //返回的错误
    private Exception exception;

    public ResponseMessage() {
        messageType = 1;
    }
}
