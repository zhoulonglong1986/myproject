/*
 * ��־�ϱ�
 */
package com.example.toro.request;

import com.example.toroapi.ApiRuleException;
import com.example.toroapi.Constants;
import com.example.toroapi.LogReport;
import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.RequestCheckUtils;
import com.example.toro.response.LogReportResponse;

/**
 * ��Ҫ��־, ���󱨸��ϱ�
 *
 * @author rocky
 */
public class LogReportRequest extends BaseRequest<LogReportResponse> {

    {
        super.setPv(Constants.SDK_VERSION_FOUR);
    }
    /**
     * ҵ������
     */
    public String Business_type ="errorLog";//�쳣������־
    /**
     * �豸ID
     */
    @ApiField("did")
    private Integer deviceId;
    /**
     * �������
     */
    @ApiField("code")
    private String errCode;
    /**
     * ������Ϣ
     */
    @ApiField("msg")
    private String errMsg;

    /**
     * ҵ������
     *
     * @return
     */

    public String getBusiness_type() {
        return Business_type;
    }

    public void setBusiness_type(String business_type) {
        Business_type = business_type;
    }

    public Class<LogReportResponse> getResponseClass() {
        return LogReportResponse.class;
    }

    public void check() throws ApiRuleException {
        RequestCheckUtils.checkNotEmpty(this.deviceId, "deviceId");
    }

    public void bug() {
        String s = null;
        try {
            s.length();
        } catch (Exception e) {
            
            //for (StackTraceElement ste : e.fillInStackTrace().getStackTrace()) {
                //// System.out.println("|---------->" + ste);
            //}
            //// System.out.println("" + e.fillInStackTrace());
        }
    }

    /*
     * ���涼���Զ����ɵĴ���
     */
    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
