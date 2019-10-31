/*
 * 基础抽象Uploadrequest, 所有有上传需求的request都继承自此类
 */
package com.example.toro.request;

import com.example.toroapi.FileItem;
import com.example.toroapi.TogetherResponse;
import com.example.toroapi.TogetherUploadRequest;
import com.example.toro.json.internal.util.TogetherHashMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rocky
 */
public abstract class BaseUploadRequest<T extends TogetherResponse> extends BaseRequest<T> implements TogetherUploadRequest<T> {

    private Map<String, FileItem> fileParams = new HashMap();

    public Map<String, FileItem> getFileParams() {
        return fileParams;
    }

    public TogetherHashMap getUdfParams() {
        return udfParams;
    }

    public void setUdfParams(TogetherHashMap udfParams) {
        this.udfParams = udfParams;
    }
}
