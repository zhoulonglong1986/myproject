package com.example.toro.response;

import java.util.List;

import com.example.toro.doman.SchNotice;
import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.mapping.ApiListField;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.TogetherResponse;

public class getStuListResponse extends TogetherResponse {
	
	

    /**
     * 学生列表
     */
	@ApiField("stu")
    private String stu="";
	


	public String getStu() {
		return stu;
	}



	public void setStu(String stu) {
		this.stu = stu;
	}



	public void check() throws ApiRuleException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	




}

