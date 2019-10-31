package com.example.toro.doman;

import com.example.toro.json.internal.mapping.ApiField;

public class Card {
	
    @ApiField("cid")
	private String cid="";
    @ApiField("rid")
	private String rid="";
	
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	
}
