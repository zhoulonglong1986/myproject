package com.example.toroapi;

import android.content.Context;

import java.io.Serializable;
import java.util.Map;

import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.StringUtils;
import com.example.toroapi.AppUtils;

/**
 * TOPAPI基础响应信息。
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
	 * 返回结果类型 [0:失败,1:成功]
	 */
	@ApiField("r")
	private Integer result = -1;

	/**
	 * 如果返回失败, 错误消息显示在此
	 */
	@ApiField("msg")
	private String msg;
	/**
	 * 服务器返回的消息体
	 */
	private String body;
	/**
	 * request中的请求参数
	 */
	private Map<String, String> params;
	/**
	 * added: rocky <br>
	 * 返回解析json得到的map对象
	 */
	private Map<?, ?> json;

	public abstract void check() throws ApiRuleException;

	/**
	 * 会在映射完成后此方法被自动调用, 有其他业务需求, 请重写此方法
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
