package com.example.toro.request;

import java.io.File;

import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.RequestCheckUtils;
import com.example.toro.response.LogReportResponse;
import com.example.toro.response.SubmitCardataResponse;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.Constants;

public class SubmitCardataRequest extends BaseUploadRequest<SubmitCardataResponse> {

    {
        super.setPv(Constants.SDK_VERSION_FOUR);
    }
    /**
     * 业务类型
     */
    public String Business_type ="SubmitCardata";//提交刷卡数据
    /**
     * 设备ID
     */
    @ApiField("cid")
   private String cid;
    

    @ApiField("photo")
    private File photo;

    @ApiField("tm")
    private String tm;

 

    public String getBusiness_type() {
        return Business_type;
    }

    public void setBusiness_type(String business_type) {
        Business_type = business_type;
    }

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}


	public File getPhoto() {
		return photo;
	}

	public void setPhoto(File photo) {
		this.photo = photo;
	}

	public String getTm() {
		return tm;
	}

	public void setTm(String tm) {
		this.tm = tm;
	}
	
    public Class<SubmitCardataResponse> getResponseClass() {
        return SubmitCardataResponse.class;
    }

    public void check() throws ApiRuleException {
        RequestCheckUtils.checkNotEmpty(this.cid, "cid");
    }
    
   
}