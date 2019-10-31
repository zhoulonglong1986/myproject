package com.example.toro.json.internal.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import com.example.toroapi.ApiException;
import com.example.toroapi.Constants;
import com.example.toroapi.TogetherResponse;
import com.example.toroapi.model.BaseModel;
import com.example.toro.json.internal.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/**
 * 转换工具类。
 *
 * @author carver.gu
 * @since 1.0, Apr 11, 2010
 */
public class Converters {

    /**
     * 是否对JSON返回的数据类型进行校验，默认不校验。给内部测试JSON返回时用的开关。
     * 规则：返回的"基本"类型只有String,Long,Boolean,Date,采取严格校验方式，如果类型不匹配，报错
     */
    public static boolean isCheckJsonType = false;
    private static final Set<String> baseFields = new HashSet<String>();
    private static final Map<String, List<Field>> fieldCache = new ConcurrentHashMap<String, List<Field>>();
    private static final Map<String, Field> fieldCache2 = new ConcurrentHashMap<String, Field>();

    static {
        baseFields.add("json");
        baseFields.add("msg");
        baseFields.add("result");
        baseFields.add("body");
        baseFields.add("params");
        //baseFields.add("success");
    }

    private Converters() {
    }

    /**
     * 使用指定 的读取器去转换字符串为对象。
     *
     * @param <T> 领域泛型
     * @param clazz 领域类型
     * @param reader 读取器
     * @return 领域对象
     * @throws ApiException
     */
    public static <T> T convert(Class<T> clazz, Reader reader) throws ApiException {
        T rsp = null;

        try {
            rsp = clazz.newInstance();

            String key = clazz.getName();
            List<Field> flist = fieldCache.get(key);//from cache

            if (flist == null) {
                flist = getFields(clazz);
                fieldCache.put(key, flist);//cache
            }

            for (Field field : flist) {
                String itemName = field.getName();//支持"不加注解也可以解析"
                String listName = null;

                ApiField jsonField = field.getAnnotation(ApiField.class);
                if (jsonField != null) {
                    itemName = jsonField.value();
                }

                ApiListField jsonListField = field.getAnnotation(ApiListField.class);
                if (jsonListField != null) {
                    listName = jsonListField.value();
                }

                if (!reader.hasReturnField(itemName)) {
                    if (listName == null || !reader.hasReturnField(listName)) {
                        continue; // ignore non-return field
                    }
                }

                Class<?> typeClass = field.getType();

                //
                field.setAccessible(true);

                // 目前
                if (String.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof String) {
                        field.set(rsp, value.toString());
                    } else {
                        if (isCheckJsonType && value != null) {
                            throw new ApiException(itemName + " is not a String");
                        }
                        if (value != null) {
                            field.set(rsp, value.toString());
                        } else {
                            field.set(rsp, "");
                        }
                    }
                } else if (Long.class.isAssignableFrom(typeClass) || long.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Long) {
                        field.set(rsp, (Long) value);
                    } else {
                        if (isCheckJsonType && value != null) {
                            throw new ApiException(itemName + " is not a Number(Long)");
                        }
                        if (StringUtils.isNumeric(value)) {
                            field.set(rsp, Long.valueOf(value.toString()));
                        }
                    }
                } else if (Integer.class.isAssignableFrom(typeClass) || int.class.isAssignableFrom(typeClass)) {
                    //// System.out.println("\t=======>> Double field=" + field.getName());
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Integer) {
                        field.set(rsp, (Integer) value);
                    } else {
                        if (isCheckJsonType && value != null) {
                            throw new ApiException(itemName + " is not a Number(Integer)");
                        }
                        if (StringUtils.isNumeric(value)) {
                            field.set(rsp, Integer.valueOf(value.toString()));
                        }
                    }
                } else if (Boolean.class.isAssignableFrom(typeClass) || boolean.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Boolean) {
                        field.set(rsp, (Boolean) value);
                    } else {
                        if (isCheckJsonType && value != null) {
                            throw new ApiException(itemName + " is not a Boolean");
                        }
                        if (value != null) {
                            field.set(rsp, Boolean.valueOf(value.toString()));
                        }
                    }
                } else if (Double.class.isAssignableFrom(typeClass) || double.class.isAssignableFrom(typeClass)) {
                    //// System.out.println("\t=======>> Double field=" + value.toString());
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Long) {
                        long v = ((Long) value).longValue();
                        field.set(rsp, (double) v);
                    } else if (value instanceof Double) {
                        field.set(rsp, (Double) value);
                    } else {
                        if (isCheckJsonType && value != null) {
                            throw new ApiException(itemName + " is not a Double");
                        }
                        if (StringUtils.isDouble(value)) {
                            field.set(rsp, Double.valueOf(value.toString()));
                        }
                    }
                } else if (Float.class.isAssignableFrom(typeClass) || float.class.isAssignableFrom(typeClass)) {
                    //// System.out.println("\t=======>> Double field=" + value.toString());
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Long) {
                        long v = ((Long) value).longValue();
                        field.set(rsp, (float) v);
                    } else if (value instanceof Double) {
                        field.set(rsp, (Float) value);
                    } else {
                        if (isCheckJsonType && value != null) {
                            throw new ApiException(itemName + " is not a Float");
                        }
                        if (StringUtils.isDouble(value)) {
                            field.set(rsp, Float.valueOf(value.toString()));
                        }
                    }
                } else if (Number.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Number) {
                        field.set(rsp, (Number) value);
                    } else {
                        if (isCheckJsonType && value != null) {
                            throw new ApiException(itemName + " is not a Number");
                        }
                    }
                } else if (Date.class.isAssignableFrom(typeClass)) {
                    DateFormat format = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                    format.setTimeZone(TimeZone.getTimeZone(Constants.DATE_TIMEZONE));
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof String) {
                        field.set(rsp, format.parse(value.toString()));
                    }
                } else if (List.class.isAssignableFrom(typeClass)) {
                    Type fieldType = field.getGenericType();
                    if (fieldType instanceof ParameterizedType) {
                        ParameterizedType paramType = (ParameterizedType) fieldType;
                        Type[] genericTypes = paramType.getActualTypeArguments();
                        if (genericTypes != null && genericTypes.length > 0) {
                            if (genericTypes[0] instanceof Class<?>) {
                                Class<?> subType = (Class<?>) genericTypes[0];
                                List<?> listObjs = reader.getListObjects(listName, subType);
                                if (listObjs != null) {
                                    field.set(rsp, listObjs);
                                }
                            }
                        }
                    }
                } else {
                    Object obj = reader.getObject(itemName, typeClass);
                    if (obj != null) {
                        field.set(rsp, obj);
                    }
                }
            }

        } catch (Exception e) {
            throw new ApiException(e);
        }

        return rsp;
    }

    /**
     * 取得一个类的所有字段, 包括父类(如果是BaseModel的子类)
     *
     * @param clazz
     * @return
     */
    public static List<Field> getFields(Class<?> clazz) {

        List<Field> all = new ArrayList<Field>();
        //本类
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            all.addAll(Arrays.asList(fields));
        }

        //父类继续查找
        clazz = clazz.getSuperclass();
        if (BaseModel.class.isAssignableFrom(clazz) || TogetherResponse.class.isAssignableFrom(clazz)) {
            //fields = getFields(clazz);
            all.addAll(getFields(clazz));
        }

        return all;
    }

    /**
     * 指定clazz中的字段名称数组, 以及对应的值数组, 返回一个解析出的对象实例
     *
     * @param <T>
     * @param values
     * @param fields
     * @param clazz
     * @return
     * @throws ApiException
     */
    public static <T> T toObject(String[] fields, Object[] values, Class<T> clazz) throws ApiException {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < fields.length; i++) {
            map.put(fields[i], values[i]);
        }
        return Converters.convert(map, clazz);
    }

    /**
     * 指定clazz中的字段名称(逗号分隔字符串), 以及对应的值(逗号分隔字符串), 返回一个解析出的对象实例
     *
     * @param <T>
     * @param fields
     * @param values
     * @param clazz
     * @return
     * @throws ApiException
     */
    public static <T> T toObject(String fields, String values, Class<T> clazz) throws ApiException {
        return toObject(fields.split(","), values.split(","), clazz);
    }

    /**
     * 自定义解析
     *
     * @param <T>
     * @param map
     * @param clazz
     * @return
     * @throws ApiException
     */
    public static <T> T convert(Map<String, Object> map, Class<T> clazz) throws ApiException {
        try {
            T rsp = clazz.newInstance();
            for (String fieldString : map.keySet()) {
                String key = clazz.getName() + ".field:" + fieldString;
                Field field = fieldCache2.get(key);
                if (field == null) {
                    field = clazz.getDeclaredField(fieldString);
                    fieldCache2.put(key, field);
                }

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Class typeClass = field.getType();
                Object value = map.get(fieldString);
                //// System.out.println("\t=======>> field=" + fieldString + " value=" + field.getName());
                if (String.class.isAssignableFrom(typeClass)) {
                    if (value instanceof String) {
                        field.set(rsp, value.toString());
                    } else {
                        if (value != null) {
                            field.set(rsp, value.toString());
                        } else {
                            field.set(rsp, "");
                        }
                    }
                } else if (Long.class.isAssignableFrom(typeClass) || long.class.isAssignableFrom(typeClass)) {
                    if (value instanceof Long) {
                        field.set(rsp, (Long) value);
                    } else {
                        if (StringUtils.isNumeric(value)) {
                            field.set(rsp, Long.valueOf(value.toString()));
                        }
                    }
                } else if (Integer.class.isAssignableFrom(typeClass) || int.class.isAssignableFrom(typeClass)) {
                    //// System.out.println("\t=======>> Integer field=" + field.getName());
                    if (value instanceof Integer) {
                        field.set(rsp, (Integer) value);
                    } else {
                        if (StringUtils.isNumeric(value)) {
                            field.set(rsp, Integer.valueOf(value.toString()));
                        }
                    }
                } else if (Boolean.class.isAssignableFrom(typeClass) || boolean.class.isAssignableFrom(typeClass)) {
                    if (value instanceof Boolean) {
                        field.set(rsp, (Boolean) value);
                    } else {
                        if (value != null) {
                            field.set(rsp, Boolean.valueOf(value.toString()));
                        }
                    }
                } else if (Double.class.isAssignableFrom(typeClass) || double.class.isAssignableFrom(typeClass)) {
                    // // System.out.println("\t=======>> Double field=" + field.getName());
                    if (value instanceof Long) {
                        long v = ((Long) value).longValue();
                        field.set(rsp, (double) v);
                    } else if (value instanceof Double) {
                        field.set(rsp, (Double) value);
                    }
                } else if (double.class.isAssignableFrom(typeClass)) {
                    field.set(rsp, (Double) value);
                } else if (Number.class.isAssignableFrom(typeClass)) {
                    if (value instanceof Number) {
                        field.set(rsp, (Number) value);
                    }
                }

            }
            return rsp;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }

    }
}
