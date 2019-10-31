package com.example.toro.json.internal.parser.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.example.toroapi.ApiException;
import com.example.toroapi.TogetherResponse;
import com.example.toro.json.internal.mapping.Converter;
import com.example.toro.json.internal.mapping.Converters;
import com.example.toro.json.internal.mapping.Reader;
import com.example.toro.json.internal.parser.json.util.ExceptionErrorListener;
import com.example.toro.json.internal.parser.json.util.JSONReader;
import com.example.toro.json.internal.parser.json.util.JSONValidatingReader;
import com.example.toro.json.internal.util.StringUtils;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

/**
 * JSON��ʽת������
 *
 * @author carver.gu
 * @since 1.0, Apr 11, 2010
 */
public class JsonConverter implements Converter {

    public <T extends TogetherResponse> T toResponse(String rsp, Class<T> clazz) throws ApiException {
        JSONReader reader = new JSONValidatingReader(new ExceptionErrorListener());
        Object rootObj = reader.read(rsp);
        if (rootObj instanceof Map<?, ?>) {
            Map<?, ?> rootJson = (Map<?, ?>) rootObj;
            //added by: rocky
            T t = fromJson(rootJson, clazz);
            ((TogetherResponse) t).setJson(rootJson);
            return t;
//            Collection<?> values = rootJson.values();
//            for (Object rspObj : values) {
//                // System.out.println("rspObj = " + rspObj);
//                if (rspObj instanceof Map<?, ?>) {
//                    // System.out.println("instanceof Map ? = 1");
//                    Map<?, ?> rspJson = (Map<?, ?>) rspObj;
//                    return fromJson(rspJson, clazz);
//                }
//            }
        }
        return null;
    }

    /**
     * ��JSON��ʽ������ת��Ϊ����
     *
     * @param <T> �����������
     * @param json JSON��ʽ������
     * @param clazz ������������
     * @return �������
     */
    public <T> T fromJson(final Map<?, ?> json, Class<T> clazz) throws ApiException {
        return Converters.convert(clazz, new Reader() {
            /**
             * added: rocky <br>
             * ���ؽ���json�õ���map����
             */
            public Map<?, ?> getRootJson() {
                return json;
            }

            public boolean hasReturnField(Object name) {
                return json.containsKey(name);
            }

            public Object getPrimitiveObject(Object name) {
                return json.get(name);
            }

            public Object getObject(Object name, Class<?> type) throws ApiException {
                Object tmp = json.get(name);
                if (tmp instanceof Map<?, ?>) {
                    Map<?, ?> map = (Map<?, ?>) tmp;
                    return fromJson(map, type);
                } else {
                    return null;
                }
            }

            public List<?> getListObjects(Object listName, Class<?> subType) throws ApiException {
                List<Object> listObjs = null;
                List<Object> listChildrenObjs = null;
                Object listTmp = json.get(listName);
                if (listTmp instanceof List<?>) {
                    listObjs = new ArrayList<Object>();
                    List<?> tmpList = (List<?>) listTmp;
                    for (Object subTmp : tmpList) {
                        if (subTmp instanceof Map<?, ?>) {// object
                            Map<?, ?> subMap = (Map<?, ?>) subTmp;
                            Object subObj = fromJson(subMap, subType);
                            if (subObj != null) {
                                listObjs.add(subObj);
                            }
                        } else if (subTmp instanceof List<?>) {// array
//                        		  listChildrenObjs = new ArrayList<Object>();
//                                  List<?> tmpChildrenList = (List<?>) subTmp;
//                                  for (Object subChildrenTmp : tmpChildrenList) {
//                                      if (subChildrenTmp instanceof Map<?, ?>) {// object
//                                          Map<?, ?> subChildrenMap = (Map<?, ?>) subChildrenTmp;
//                                          Object subObj = fromJson(subChildrenMap, subType);
//                                          if (subObj != null) {
//                                        	  listChildrenObjs.add(subObj);
//                                          }
//                                      }
//                                  }
//                                  listObjs.add(listChildrenObjs);
                        } else {// boolean, long, double, string, null
                            listObjs.add(subTmp);
                        }
                    }
                }

                return listObjs;
            }

            public List<?> getListObjects(Object listName, Object itemName, Class<?> subType) throws ApiException {
                List<Object> listObjs = null;

                Object listTmp = json.get(listName);
                if (listTmp instanceof Map<?, ?>) {
                    Map<?, ?> jsonMap = (Map<?, ?>) listTmp;
                    Object itemTmp = jsonMap.get(itemName);
                    if (itemTmp == null && listName != null) {
                        String listNameStr = listName.toString();
                        itemTmp = jsonMap.get(listNameStr.substring(0, listNameStr.length() - 1));
                    }
                    if (itemTmp instanceof List<?>) {
                        listObjs = new ArrayList<Object>();
                        List<?> tmpList = (List<?>) itemTmp;
                        for (Object subTmp : tmpList) {
                            if (subTmp instanceof Map<?, ?>) {// object
                                Map<?, ?> subMap = (Map<?, ?>) subTmp;
                                Object subObj = fromJson(subMap, subType);
                                if (subObj != null) {
                                    listObjs.add(subObj);
                                }
                            } else if (subTmp instanceof List<?>) {// array
                            } else {// boolean, long, double, string, null
                                listObjs.add(subTmp);
                            }
                        }
                    }
                }

                return listObjs;
            }
        });
    }

    /**
     * ����JSONReader�������Ķ���, ���ظ�����T���Ͷ���
     *
     * @param <T>
     * @param json
     * @param clazz
     * @return
     * @throws ApiException
     */
    public <T> T toObject(final Object json, Class<T> clazz) throws ApiException {
        if (json instanceof Map<?, ?>) {
            return fromJson((Map<?, ?>) json, clazz);
        } else {
            //û��else
            throw new ApiException("Ӧ����Map<?, ?>����, ���Ǵ���������List");
        }
    }

    /**
     * ����JSONReader�������Ķ���, ����List<T>
     *
     * @param <T>
     * @param json
     * @param clazz
     * @return
     * @throws ApiException
     */
    public <T> List<T> toList(final Object json, Class<T> clazz) throws ApiException {
        if (json instanceof List<?>) {
            List<T> list = new ArrayList<T>();
            List<?> rootList = (List<?>) json;
            for (Object o : rootList) {
                if (o instanceof Map<?, ?>) {
                    list.add(fromJson((Map<?, ?>) o, clazz));
                } else {
                    //�ݲ�֧��
                }
            }
            return list;
        }
        return null;
    }
}
