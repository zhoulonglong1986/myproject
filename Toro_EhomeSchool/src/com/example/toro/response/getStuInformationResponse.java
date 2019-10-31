package com.example.toro.response;

import java.util.ArrayList;
import java.util.List;


import com.example.toro.doman.Card;
import com.example.toro.doman.Member;
import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.mapping.ApiListField;
import com.example.toroapi.ApiRuleException;
import com.example.toroapi.TogetherResponse;

public class getStuInformationResponse extends TogetherResponse {
	
	
    /**
     * …Ë±∏ID
     */
    @ApiField("Rid")
    private String rid;
	
    @ApiListField("cids")
	private ArrayList<Card> cids;
	    

	@ApiListField("member")
	private ArrayList<Member> members;
	
	
	
	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public ArrayList<Card> getCids() {
		return cids;
	}

	public void setCids(ArrayList<Card> cids) {
		this.cids = cids;
	}

	public ArrayList<Member> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<Member> members) {
		this.members = members;
	}
	public void check() throws ApiRuleException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
