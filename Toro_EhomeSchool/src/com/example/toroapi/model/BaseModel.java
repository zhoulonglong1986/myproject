/*
 * 所有model类的基类
 */
package com.example.toroapi.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author rocky
 */
public abstract class BaseModel implements Serializable {

	@Override
	public String toString() {
		List<Field> fieldList = getFields(this.getClass());
		Field[] fields = this.getClass().getDeclaredFields();
		StringBuilder sb = new StringBuilder(
				"\r\n--------------------------\r\n");
		sb.append("{\r\n");
		Object o;
		for (Field f : fieldList) {
			if (Modifier.isFinal(f.getModifiers())) {
				continue;
			}
			f.setAccessible(true);
			try {
				o = f.get(this);
			} catch (Exception e) {
				o = e.toString();
			}
			sb.append("    ").append(f.getName()).append(" : ").append(o)
					.append("\r\n");
		}
		sb.append("}\r\n");
		return sb.toString();
	}

	/**
	 * 获取所有class的所有field
	 * 
	 * @param clazz
	 * @return
	 */
	public List<Field> getFields(Class<?> clazz) {
		List<Field> fieldList = new ArrayList();
		Field[] fields = clazz.getDeclaredFields();
		fieldList.addAll(Arrays.asList(fields));
		clazz = clazz.getSuperclass();
		if (BaseModel.class.getName().equals(clazz.getName())
				|| BaseModel.class.isAssignableFrom(clazz)) {
			fieldList.addAll(getFields(clazz));
		}
		return fieldList;
	}
}
