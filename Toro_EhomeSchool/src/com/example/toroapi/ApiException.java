package com.example.toroapi;

/**
 * 客户端异常。
 * 
 * @author carver.gu
 * @since 1.0, Apr 11, 2010
 */
public class ApiException extends Exception {

	private static final long serialVersionUID = -238091758285157331L;
	private String errCode;
	private String errMsg;
	private String rspBody;// http请求内容

	public ApiException() {
		super();
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(Throwable cause) {
		super(cause);
	}

	public ApiException(String errCode, String errMsg) {
		super(errCode + ":" + errMsg);
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	public String getErrCode() {
		return this.errCode;
	}

	public String getErrMsg() {
		return this.errMsg;
	}

	public String getRspBody() {
		return rspBody;
	}

	public void setRspBody(String rspBody) {
		this.rspBody = rspBody;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
