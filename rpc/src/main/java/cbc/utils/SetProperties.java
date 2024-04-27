package cbc.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Cbc
 * @DateTime 2024/4/27 19:38
 * @Description
 */
public class SetProperties {
    private final static Map<String, String> map = new HashMap<>();

    public static void addProperty(String key, String value){
        map.put(key, value);
    }

    public static void removeProperty(String key){
        map.remove(key);
    }

    public static String getProperty(String key){
        return map.get(key);
    }
}
