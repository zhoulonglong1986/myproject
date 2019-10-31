package com.example.toro.request;

import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.RequestCheckUtils;
import com.example.toro.response.getSchNoticeResponse;
import com.example.toro.response.getStuInformationResponse;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.Constants;

public class getStuInformationRequest extends BaseRequest<getStuInformationResponse> {

    {
        super.setPv(Constants.SDK_VERSION_FOUR);
    }
    
    
    /**
     * 业务类型
     *
     */
    public String Business_type ="getStuInformation";//提交刷卡数据
    
    
    /**
     * 设备ID
     */
     @ApiField("mid")
     private String mid;
    

    @ApiField("id")
    private String id;
    
    @ApiField("sign")
    private String sign;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}
	
    public String getMid() {
		return mid;
	}
    

	public String getBusiness_type() {
        return Business_type;
    }

    public void setBusiness_type(String business_type) {
        Business_type = business_type;
    }

    public Class<getStuInformationResponse> getResponseClass() {
        return getStuInformationResponse.class;
    }

    public void check() throws ApiRuleException {
        RequestCheckUtils.checkNotEmpty(this.mid, "mid");
    }



    
}

