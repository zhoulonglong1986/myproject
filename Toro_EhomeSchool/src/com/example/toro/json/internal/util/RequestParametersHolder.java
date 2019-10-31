package com.example.toro.json.internal.util;

import java.util.HashMap;
import java.util.Map;

public class RequestParametersHolder {

	private TogetherHashMap protocalMustParams;
	private TogetherHashMap protocalOptParams;
	private TogetherHashMap applicationParams;

	public TogetherHashMap getProtocalMustParams() {
		return protocalMustParams;
	}

	public void setProtocalMustParams(TogetherHashMap protocalMustParams) {
		this.protocalMustParams = protocalMustParams;
	}

	public TogetherHashMap getProtocalOptParams() {
		return protocalOptParams;
	}

	public void setProtocalOptParams(TogetherHashMap protocalOptParams) {
		this.protocalOptParams = protocalOptParams;
	}

	public TogetherHashMap getApplicationParams() {
		return applicationParams;
	}

	public void setApplicationParams(TogetherHashMap applicationParams) {
		this.applicationParams = applicationParams;
	}

	public Map<String, String> getAllParams() {
		Map<String, String> params = new HashMap<String, String>();
		if (protocalMustParams != null && !protocalMustParams.isEmpty()) {
			params.putAll(protocalMustParams);
		}
		if (protocalOptParams != null && !protocalOptParams.isEmpty()) {
			params.putAll(protocalOptParams);
		}
		if (applicationParams != null && !applicationParams.isEmpty()) {
			params.putAll(applicationParams);
		}
		return params;
	}

}
