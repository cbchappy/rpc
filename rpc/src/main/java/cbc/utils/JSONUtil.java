package cbc.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author Cbc
 * @DateTime 2024/4/26 20:45
 * @Description
 */
public class JSONUtil {

    /**
     * 将json属性复制到指定class,并返回实例
     */
    public static <T> T getEntityFromJSON(Class<T> clazz, String json) throws Exception{
        //获取类的所有属性
        T t = clazz.newInstance();

        JSONObject jsonObject = JSON.parseObject(json);
        Map<String, Object> innerMap = jsonObject.getInnerMap();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            Object o = innerMap.get(declaredField.getName());
            declaredField.set(t, o);
        }

        return t;
    }
}
