package com.example.toro.doman;

import com.example.toro.json.internal.mapping.ApiField;

public class Softupdate {
	
	@ApiField("app_url")
	private String appUrl="";// �����°汾apk��url
	
	@ApiField("release_log")
	private String releaseLog="";// ������־
	
	public String getAppUrl() {
		return appUrl;
	}
	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}
	public String getReleaseLog() {
		return releaseLog;
	}
	public void setReleaseLog(String releaseLog) {
		this.releaseLog = releaseLog;
	}

}
