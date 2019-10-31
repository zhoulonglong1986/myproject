package com.example.toroapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.example.toro.json.internal.parser.json.ObjectJsonParser;
import com.example.toro.json.internal.util.TogetherHashMap;
import com.example.toro.json.internal.util.RequestParametersHolder;
import com.example.toro.json.internal.util.TogetherUtils;
import com.example.toro.json.internal.util.WebUtils;
import com.example.toroapi.AppConstants;
import com.example.toroapi.Logger;

public class DefaultTogetherClient implements TogetherHttpClient {

	// public static final String SERVER_URL_TWO =
	// "http://t2.e5ex.com/android";// 协议2
	// public static final String SERVER_URL_THREE =
	// "http://t3.e5ex.com/android";// 协议3
	// public static final String SERVER_URL_THREE =
	// AppConstants.isServerTestVerson ? "http://testt3.e5ex.cn:9004/android" :
	// "http://t3.e5ex.com/android";// 协议3
	
	// public static final String SERVER_URL_FOUR="http://api.e5ex.cn:9002";
	// public static final String
	// SERVER_URL_FOUR="http://api.e5ex.com";//https://172.10.1.102:9003
	//public static final String SERVER_URL_FOUR = "http://wxu.e5ex.com";// https://172.10.1.102:9003
	public static final String SERVER_URL_FOUR = "http://zhxy.hlbra.net";// https://172.10.1.102:9003

	private static final String APP_KEY = "origin";
	private static final String TYPE = "type";
	private static final String VERSION = "pv";
	private static final String TIMESTAMP = "timestamp";
	private String serverUrl;
	private String appKey = "android";
	private String appSecret = "0";
	private int connectTimeout = 20 * 1000;// 20秒
	private int readTimeout = 20 * 1000;// 20秒
	private boolean needCheckRequest = true; // 是否在客户端校验请求
	private boolean needEnableParser = true; // 是否对响应结果进行解释
	private boolean useSimplifyJson = false; // 是否采用精简化的JSON返回
	
	private WebUtils webutil;

	public static <T extends TogetherResponse> T exec(TogetherRequest<T> request)
			throws ApiException {
		DefaultTogetherClient client = new DefaultTogetherClient();

		return client.execute(request);
	}

	public DefaultTogetherClient() {
		this.serverUrl = SERVER_URL_FOUR;
	}

	public DefaultTogetherClient(String serverUrl, String appKey,
			String appSecret) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.serverUrl = serverUrl;
	}

	public <T extends TogetherResponse> T execute(TogetherRequest<T> request)
			throws ApiException {
		if (AppConstants.isTest) {
			// System.out.println("===Request =====> " +
			// request.getTextParams().toString());
		}

		try {
			T res = execute(request, null);
			if (AppConstants.isTest) {
				// System.out.println("==Response.Body =====> " +
				// res.getBody());
			}

			if (AppConstants.isTest && !res.isSuccess()) {
				// System.out.println("==Response.Msg =====> " + res.getMsg());
			}

			return res;
		} catch (ApiException e) {
			if (!request.getBusiness_type().equals("errorLog")
					&& request.getPv() != Constants.SDK_VERSION_FOUR) {
				LogReport.log(0, e, request);
			}

			T localResponse = null;
			try {
				localResponse = request.getResponseClass().newInstance();
			} catch (Exception xe) {
				throw new ApiException(xe);
			}
			localResponse.setResult(0);
			localResponse.setBody(e.getRspBody());
			localResponse.setMsg(e.getErrMsg());
			return localResponse;
		} catch (Exception e) {
			if (!request.getBusiness_type().equals("errorLog")
					&& request.getPv() != Constants.SDK_VERSION_FOUR) {
				LogReport.log(0, e, request);
			}

			T localResponse = null;
			try {
				localResponse = request.getResponseClass().newInstance();
			} catch (Exception xe) {
				throw new ApiException(xe);
			}
			localResponse.setResult(0);
			localResponse.setMsg(e.getMessage());
			return localResponse;
		}
	}

	public <T extends TogetherResponse> T execute(TogetherRequest<T> request,String session) throws ApiException {
		
		TogetherParser<T> parser = new ObjectJsonParser<T>(request.getResponseClass(), this.useSimplifyJson);
		
		return _execute(request, parser, null);
	}

	private <T extends TogetherResponse> T _execute(TogetherRequest<T> request,TogetherParser<T> parser, String session) throws ApiException {
		
		if (this.needCheckRequest) {
			try {
				request.check();
			} catch (ApiRuleException e) {
				T localResponse = null;
				try {
					localResponse = request.getResponseClass().newInstance();
				} catch (Exception xe) {
					throw new ApiException(xe);
				}
				localResponse.setResult(0);
				localResponse.setMsg(e.getErrMsg());
				return localResponse;
			}
		}

		Map<String, Object> rt = doPost(request, session);
		if (rt == null) {
			return null;
		}

		T tRsp = null;
		if (this.needEnableParser) {
			try {
				tRsp = parser.parse((String) rt.get("rsp"));
				tRsp.setBody((String) rt.get("rsp"));
				tRsp.setParams((TogetherHashMap) rt.get("textParams"));
				tRsp.afterParse();
			} catch (ApiException e) {
				e.setRspBody((String) rt.get("rsp"));
				throw e;
			}
		} else {
			try {
				tRsp = request.getResponseClass().newInstance();
				tRsp.setBody((String) rt.get("rsp"));
				tRsp.setParams((TogetherHashMap) rt.get("textParams"));
			} catch (Exception e) {
				// abort e
				e.printStackTrace();
			}
		}
		return tRsp;
	}

	/**
	 * for test
	 * 
	 * @param <T>
	 * @param jsonStr
	 * @param responseClass
	 * @return
	 */
	public <T extends TogetherResponse> T parse(String jsonStr,Class<T> responseClass) {
		T tRsp = null;
		TogetherParser<T> parser = new ObjectJsonParser<T>(responseClass,
				this.useSimplifyJson);
		try {
			tRsp = parser.parse(jsonStr);
			tRsp.setBody((String) jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tRsp;
	}

	public <T extends TogetherResponse> Map<String, Object> doPost(
			TogetherRequest<T> request, String session) throws ApiException {
		Map<String, Object> result = new HashMap<String, Object>();
		RequestParametersHolder requestHolder = new RequestParametersHolder();
		TogetherHashMap appParams = new TogetherHashMap(request.getTextParams());
		requestHolder.setApplicationParams(appParams);

		// 添加协议级请求参数
		// TogetherHashMap protocalMustParams = new TogetherHashMap();
		// protocalMustParams.put(TYPE, request.getApiType());
		// protocalMustParams.put(VERSION, request.getPv());
		// protocalMustParams.put(APP_KEY, appKey);
		// Long timestamp = request.getTimestamp();// 允许用户设置时间戳
		// if (timestamp == null) {
		// timestamp = System.currentTimeMillis();
		// }
		// protocalMustParams.put(TIMESTAMP, timestamp);
		// requestHolder.setProtocalMustParams(protocalMustParams);
		StringBuilder urlSb = new StringBuilder(SERVER_URL_FOUR);

		// 设置协议版本
		urlSb.append("/").append(request.getPv());

		// 设置业务类型
		urlSb.append("/").append(request.getBusiness_type());

		// 设备ID - DID
		urlSb.append("/").append(AppConstants.Mid);

		// System.out.println("url = " + urlSb);
		//
		// urlSb.append("/server");

		// try {
		// String sysMustQuery =
		// WebUtils.buildQuery(requestHolder.getProtocalMustParams(),
		// Constants.CHARSET_UTF8);
		// String sysOptQuery =
		// WebUtils.buildQuery(requestHolder.getProtocalOptParams(),
		// Constants.CHARSET_UTF8);
		//
		// urlSb.append("?");
		// urlSb.append(sysMustQuery);
		// if (sysOptQuery != null && sysOptQuery.length() > 0) {
		// urlSb.append("&");
		// urlSb.append(sysOptQuery);
		// }
		//
		// // System.out.println("---request_url = " + urlSb.toString());
		// } catch (IOException e) {
		// e.printStackTrace();
		// throw new ApiException(e);
		// }

		String rsp = null;
		try {

			System.out.println("==================***" + urlSb.toString());
			Iterator iter = appParams.keySet().iterator();

			/*
			 * while(iter.hasNext()){ Map.Entry entry=(Map.Entry) iter.next();
			 * Object key=entry.getKey(); Object value=entry.getValue();
			 * System.out.println("============key======***"+key);
			 * System.out.println("============value======***"+value);
			 * 
			 * }
			 */
			webutil=new WebUtils();
			// 是否需要上传文件
			if (request instanceof TogetherUploadRequest) {
				TogetherUploadRequest<T> uRequest = (TogetherUploadRequest<T>) request;
				Map<String, FileItem> fileParams = TogetherUtils.cleanupMap(uRequest.getFileParams());
				rsp = webutil.doPost(urlSb.toString(), appParams, fileParams,
						Constants.CHARSET_UTF8, connectTimeout, readTimeout,
						request.getHeaderMap());
			} else {
				rsp = webutil.doPost(urlSb.toString(), appParams,
						Constants.CHARSET_UTF8, connectTimeout, readTimeout,
						request.getHeaderMap());
			}
		} catch (Exception e) {
			System.out.println("====================e");
			throw new ApiException(e);

		}

		System.out.println("====================over");
		result.put("rsp", rsp);
		result.put("textParams", appParams);
		// result.put("protocalMustParams", protocalMustParams);
		result.put("url", urlSb.toString());
		return result;
	}
}
