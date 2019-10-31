package com.example.toro.response;

import java.util.ArrayList;
import java.util.List;

import com.example.toro.doman.SchNotice;
import com.example.toro.doman.Softupdate;
import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.mapping.ApiListField;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.TogetherResponse;

/**
 * 获取学校公告列表
 * 
 * @author Administrator
 * 
 */
public class getSchNoticeResponse extends TogetherResponse {

	/**
	 * 学校名称
	 */
	@ApiField("schname")
	private String schname;

	/**
	 * 当前机器所在城市
	 */
	@ApiField("city")
	private String city;
	
	/**
	 * 学校logo url
	 */
	@ApiField("logourl")
	private String logourl;



	/**
	 * 公告列表
	 */
	@ApiListField("data")
	private ArrayList<SchNotice> SchNotices;

	/**
	 * softupdate
	 */
	@ApiListField("softupdate")
	private Softupdate softupdate;

	public String getSchname() {
		return schname;
	}

	public void setSchname(String schname) {
		this.schname = schname;
	}

	public ArrayList<SchNotice> getSchNotices() {

		return SchNotices;
	}

	public void setSchNotices(ArrayList<SchNotice> schNotices) {
		SchNotices = schNotices;
	}

	public Softupdate getSoftupdate() {
		return softupdate;
	}

	public void setSoftupdate(Softupdate softupdate) {
		this.softupdate = softupdate;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void check() throws ApiRuleException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public String getLogourl() {
		return logourl;
	}

	public void setLogourl(String logourl) {
		this.logourl = logourl;
	}

}
