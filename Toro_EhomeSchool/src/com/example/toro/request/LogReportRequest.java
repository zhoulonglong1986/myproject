/*
 * 日志上报
 */
package com.example.toro.request;

import com.example.toroapi.ApiRuleException;
import com.example.toroapi.Constants;
import com.example.toroapi.LogReport;
import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.RequestCheckUtils;
import com.example.toro.response.LogReportResponse;

/**
 * 主要日志, 错误报告上报
 *
 * @author rocky
 */
public class LogReportRequest extends BaseRequest<LogReportResponse> {

    {
        super.setPv(Constants.SDK_VERSION_FOUR);
    }
    /**
     * 业务类型
     */
    public String Business_type ="errorLog";//异常报错日志
    /**
     * 设备ID
     */
    @ApiField("did")
    private Integer deviceId;
    /**
     * 错误编码
     */
    @ApiField("code")
    private String errCode;
    /**
     * 错误消息
     */
    @ApiField("msg")
    private String errMsg;

    /**
     * 业务类型
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
     * 下面都是自动生成的代码
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
