package cbc.message;

import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:20
 * @Description
 */
@Data
public abstract class Message {
  public final static String SERIALIZE_JAVA = "java";
  public final static String SERIALIZE_JSON = "json";
  public int messageType;

}
