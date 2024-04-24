package cbc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 21:04
 * @Description
 */
public class ReadProperties {

    private final static Properties PROPERTIES = new Properties();

    /**
     * 读取配置文件
     * @param key
     * @return
     */
    public static String readProperties(String key){
        try {
            InputStream stream = ReadProperties.class.getResourceAsStream("/application.properties");
            PROPERTIES.load(stream);
            stream.close();
            return PROPERTIES.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
