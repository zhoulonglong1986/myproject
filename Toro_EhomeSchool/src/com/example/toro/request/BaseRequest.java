
package com.example.toro.request;

import com.example.toroapi.ApiException;
import com.example.toroapi.Constants;
import com.example.toroapi.FileItem;
import com.example.toroapi.Md5Token;
import com.example.toroapi.TogetherRequest;
import com.example.toroapi.TogetherResponse;
import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.mapping.Converters;
import com.example.toro.json.internal.util.TogetherHashMap;
import com.example.toro.sample.Application;
import com.example.toroapi.model.Device;
import com.example.toroapi.AppConstants;
import com.example.toroapi.UserContext;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author rocky
 */
public abstract class BaseRequest<T extends TogetherResponse> implements TogetherRequest<T> {

    private Map<String, String> headerMap = new TogetherHashMap();
    protected TogetherHashMap udfParams; // add user-defined text parameters
    private Long timestamp;
    protected String pv = Constants.SDK_VERSION_FOUR;//默认协议版本号
    
    protected Device baseDevice;

    public String getPv() {
        return this.pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }
    
    
    
    
    public Device getBaseDevice() {
		return baseDevice;
	}

	public void setBaseDevice(Device baseDevice) {
		this.baseDevice = baseDevice;
	}

	public Map<String, String> getTextParams() throws ApiException {
        TogetherHashMap txtParams = new TogetherHashMap();
        try {
            String fieldName;

            List<Field> fs = Converters.getFields(this.getClass());
            for (Field field : fs) {
                ApiField jsonField = field.getAnnotation(ApiField.class);
                field.setAccessible(true);//

                //get field name
                if (jsonField != null) {//有注解的用注解
                    fieldName = jsonField.value();
                } else {//没有的注解的直接用字段名
//                    fieldName = field.getName();
                    continue;
                }

                if ("apiType".equals(fieldName) || fieldName.equals("device")) {
                    continue;
                }

                //处理文件类型
                if (File.class.isAssignableFrom(field.getType())) {
                    //如果是上传request
                    if (BaseUploadRequest.class.isAssignableFrom(this.getClass())) {
                        File f = (File) field.get(this);
                        if (f != null && f.exists()) {
                            ((BaseUploadRequest) this).getFileParams().put(fieldName, new FileItem(f));
                        }
                    } else {
                        //throw new Exception(field.getName() + "字段是File类型, 请使类" + this.getClass().getName() + "继承" + BaseUploadRequest.class.getName());
                    }
                } else {
                    txtParams.put(fieldName, field.get(this));
                }
             
                
            }
        } catch (Exception e) {
            throw new ApiException(e);
        }

        if (this.udfParams != null) {
            txtParams.putAll(this.udfParams);
        }

        //清理一下空键
        Iterator<Entry<String, String>> it = txtParams.entrySet().iterator();
        Entry<String, String> entry;
        while (it.hasNext()) {
            entry = it.next();
            if (entry.getValue() == null) {
                it.remove();
            }
        }
        setBaseParams(txtParams);
        return txtParams;
    }

    public void putOtherTextParam(String key, String value) {
        if (this.udfParams == null) {
            this.udfParams = new TogetherHashMap();
        }
        this.udfParams.put(key, value);
    }
    
    protected void setBaseParams(TogetherHashMap txtParams){
    	//Device me=UserContext.getInstans().getUser();
    	 //  if(me!=null){
               txtParams.put("lang", "zh-CN");//语言
               txtParams.put("tlb",Application.tlb);
             /*  String t=System.currentTimeMillis()+"";
               String hashCode= Md5Token.getInstance().getLongToken("zh-CN"+t+ AppConstants.Security_Key + "591847" + "538");
               txtParams.put("hash_code",hashCode.toLowerCase());
               txtParams.put("t",t);*/
         //  }
    }
}
