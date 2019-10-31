package com.example.toro.request;

import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.RequestCheckUtils;
import com.example.toro.response.SubmitCardataResponse;
import com.example.toro.response.getSchNoticeResponse;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.Constants;

public class getSchNoticeRequest extends BaseRequest<getSchNoticeResponse> {

    {
        super.setPv(Constants.SDK_VERSION_FOUR);
    }
    
    
    /**
     * 业务类型
     *
     */
    public String Business_type ="getSchNotice";//获取学校公告列表
    
    
    /**
     * 设备ID
     */
     @ApiField("mid")
     private String mid;
    

    public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getBusiness_type() {
        return Business_type;
    }

    public void setBusiness_type(String business_type) {
        Business_type = business_type;
    }

    public Class<getSchNoticeResponse> getResponseClass() {
        return getSchNoticeResponse.class;
    }

    public void check() throws ApiRuleException {
        RequestCheckUtils.checkNotEmpty(this.mid, "mid");
    }



    
}
