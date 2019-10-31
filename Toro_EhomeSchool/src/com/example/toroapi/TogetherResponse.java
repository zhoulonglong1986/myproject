package com.example.toroapi;

import android.content.Context;

import java.io.Serializable;
import java.util.Map;

import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.StringUtils;
import com.example.toroapi.AppUtils;

/**
 * TOPAPI������Ӧ��Ϣ��
 *
 * @author fengsheng
 */
/**
 * @author rocky
 * 
 */
public abstract class TogetherResponse implements Serializable {

	public static final long serialVersionUID = 1L;
	/**
	 * ���ؽ������ [0:ʧ��,1:�ɹ�]
	 */
	@ApiField("r")
	private Integer result = -1;

	/**
	 * �������ʧ��, ������Ϣ��ʾ�ڴ�
	 */
	@ApiField("msg")
	private String msg;
	/**
	 * ���������ص���Ϣ��
	 */
	private String body;
	/**
	 * request�е��������
	 */
	private Map<String, String> params;
	/**
	 * added: rocky <br>
	 * ���ؽ���json�õ���map����
	 */
	private Map<?, ?> json;

	public abstract void check() throws ApiRuleException;

	/**
	 * ����ӳ����ɺ�˷������Զ�����, ������ҵ������, ����д�˷���
	 */
	public void afterParse() throws ApiException {
	}

	public String getMsg() {
	
		return msg;
	}

	public String getMsg(Context context) {
		/*
		 * if(StringUtils.isEmpty(msg)) return
		 * context.getString(AppUtils.rtnErrorMsg(getResult())); else return
		 * msg;
		 */
		return "";
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Map<?, ?> getJson() {
		return json;
	}

	public void setJson(Map<?, ?> json) {
		this.json = json;
	}

	public boolean isSuccess() {
		return getResult() == 1;
	}

	public Integer getResult() {
		// // System.out.println(this.getJson());
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}
}
