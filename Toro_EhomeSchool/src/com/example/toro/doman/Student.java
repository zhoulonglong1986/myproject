package com.example.toro.doman;

import java.util.ArrayList;

import com.example.toro.json.internal.mapping.ApiField;

public class Student {
	
	private String id="";
	
	private ArrayList<Card> cids=new ArrayList<Card>();
	private ArrayList<Member> members=new ArrayList<Member>();
	
	

	
	public ArrayList<Card> getCids() {
		return cids;
	}

	public void setCids(ArrayList<Card> cids) {
		if(null==cids){
			this.cids=new ArrayList<Card>();
		}else{
			this.cids = cids;
		}
		
	}

	public ArrayList<Member> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<Member> arrayList) {
		if(null==arrayList){
			this.members=new ArrayList<Member>();
		}else{
			this.members = arrayList;
		}
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
