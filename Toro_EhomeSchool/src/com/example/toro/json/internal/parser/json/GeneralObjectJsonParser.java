/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.toro.json.internal.parser.json;

import com.example.toroapi.ApiException;
import com.example.toro.json.internal.parser.json.util.ExceptionErrorListener;
import com.example.toro.json.internal.parser.json.util.JSONReader;
import com.example.toro.json.internal.parser.json.util.JSONValidatingReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rocky
 */
public class GeneralObjectJsonParser<T> {

    private Class<T> clazz;

    public GeneralObjectJsonParser(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T parse(String rsp) throws ApiException {
        JsonConverter converter = new JsonConverter();
        JSONReader reader = new JSONValidatingReader(new ExceptionErrorListener());
        Object rootObj = reader.read(rsp);
        if (rootObj instanceof Map<?, ?>) {
            Map<?, ?> rootJson = (Map<?, ?>) rootObj;
            return converter.fromJson(rootJson, clazz);
        }
        return null;
    }

    public T parse(Map<?, ?> json) throws ApiException {
        return new JsonConverter().fromJson(json, clazz);
    }

    public List<T> parseList(Object json) throws ApiException {
        if (json instanceof List<?>) {
            JsonConverter converter = new JsonConverter();
            List<Object> listObjs = new ArrayList<Object>();
            List<?> tmpList = (List<?>) json;
            for (Object subTmp : tmpList) {
                if (subTmp instanceof Map<?, ?>) {// object
                    Map<?, ?> subMap = (Map<?, ?>) subTmp;
                    T subObj = converter.fromJson(subMap, clazz);
                    if (subObj != null) {
                        listObjs.add(subObj);
                    }
                } else if (subTmp instanceof List<?>) {// array
                } else {// boolean, long, double, string, null
                    listObjs.add(subTmp);
                }
            }
            return (List<T>) listObjs;
        }
        return null;
    }
}
