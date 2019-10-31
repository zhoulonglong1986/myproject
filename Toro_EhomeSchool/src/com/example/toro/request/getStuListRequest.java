package com.example.toro.request;

import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.RequestCheckUtils;
import com.example.toro.response.getSchNoticeResponse;
import com.example.toro.response.getStuListResponse;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.Constants;

public class getStuListRequest extends BaseRequest<getStuListResponse> {

    {
        super.setPv(Constants.SDK_VERSION_FOUR);
    }
    
    
    /**
     * 业务类型
     *
     */
    public String Business_type ="getStuList";//提交刷卡数据
    
    
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

    public Class<getStuListResponse> getResponseClass() {
        return getStuListResponse.class;
    }

    public void check() throws ApiRuleException {
        RequestCheckUtils.checkNotEmpty(this.mid, "mid");
    }



    
}
