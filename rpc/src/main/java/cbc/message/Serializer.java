package cbc.message;

import cbc.exception.ServerException;
import com.alibaba.fastjson.JSON;

import java.io.*;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:51
 * @Description 序列化
 */
public interface Serializer {
    public Object serialize(Object content);

    public <T> T deSerialize(Class<T> clazz, Object content);


    /**
     * 序列化方式
     */
    enum Algorithm implements Serializer {
        java {

            public byte[] serialize(Object content) {
                try {
                    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                    ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
                    objectOut.writeObject(content);
                    //关闭
                    byteOut.close();
                    return byteOut.toByteArray();
                } catch (IOException e) {
                    throw new ServerException("对象序列化成字节失败!");
                }

            }

            public <T> T deSerialize(Class<T> clazz, Object content) {

                try {

                    ByteArrayInputStream byteIn = new ByteArrayInputStream((byte[]) content);
                    ObjectInputStream objectIn = new ObjectInputStream(byteIn);
                    byteIn.close();
                    return (T)objectIn.readObject();
                } catch (Exception e) {
                    throw new ServerException("对象反序列化失败!" + e.getMessage());
                }
            }


        },

        json {

            public String serialize(Object content) {

                return JSON.toJSONString(content);
            }


            public <T> T deSerialize(Class<T> clazz, Object content) {
                return  JSON.parseObject((String) content, clazz);
            }

        }
    }

}
