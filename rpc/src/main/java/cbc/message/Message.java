package cbc.message;

import com.sun.org.apache.xml.internal.serialize.Serializer;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:20
 * @Description
 */
@Data
//需实现序列化接口！！！！
public abstract class Message implements Serializable {
  private final static AtomicInteger setId = new AtomicInteger(0);
  public final static String SERIALIZE_JAVA = "java";
  public final static String SERIALIZE_JSON = "json";
  public int messageType;
 Integer getIdFromMessage(){
   return setId.incrementAndGet();
 }
}
