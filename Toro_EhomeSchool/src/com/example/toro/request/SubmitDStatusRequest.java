package com.example.toro.request;

import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.RequestCheckUtils;
import com.example.toro.response.SubmitDStatusResponse;
import com.example.toro.response.getStuListResponse;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.Constants;

public class SubmitDStatusRequest extends BaseRequest<SubmitDStatusResponse> {

    {
        super.setPv(Constants.SDK_VERSION_FOUR);
    }
    
    
    /**
     * 业务类型
     *
     */
    public String Business_type ="SubmitDeviceStatus";//提交考勤机状态报告
    
  

	/**
     * 状态数据
     */
     @ApiField("data")
     private String data;
    


	public String getBusiness_type() {
        return Business_type;
    }

    public void setBusiness_type(String business_type) {
        Business_type = business_type;
    }
    

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
  
    public Class<SubmitDStatusResponse> getResponseClass() {
        return SubmitDStatusResponse.class;
    }

    public void check() throws ApiRuleException {
        RequestCheckUtils.checkNotEmpty(this.data, "data");
    }



    
}