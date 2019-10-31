package com.example.toro.response;

import com.example.toro.json.internal.mapping.ApiField;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.TogetherResponse;

public class SubmitDStatusResponse extends TogetherResponse { 
	
	/**
     * 推送类型
     */
    @ApiField("t")
    private String t;
    
    /**
     * 开机时间
     */
    @ApiField("uptime")
    private String uptime;
    
    public String getT() {
		return t;
	}



	public void setT(String t) {
		this.t = t;
	}



	public String getUptime() {
		return uptime;
	}



	public void setUptime(String uptime) {
		this.uptime = uptime;
	}



	public String getDowntime() {
		return downtime;
	}



	public void setDowntime(String downtime) {
		this.downtime = downtime;
	}



	/**
     * 关机时间
     */
    @ApiField("downtime")
    private String downtime;
    
    

	public void check() throws ApiRuleException {
        throw new UnsupportedOperationException("Not supported yet.");
    }}

