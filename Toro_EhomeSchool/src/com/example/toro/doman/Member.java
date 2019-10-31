package com.example.toro.doman;

import com.example.toro.json.internal.mapping.ApiField;

public class Member {
	
	@ApiField("rid")
	private String rid="";
	 @ApiField("name")
	private String name="";
	 @ApiField("relatship")
	private String relatship="";
	 @ApiField("clazz")
	private String clazz="";
	 @ApiField("age")
	private int age=0;
	 @ApiField("photo")
	private String photo="";
	
	
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelatship() {
		return relatship;
	}
	public void setRelatship(String relatship) {
		this.relatship = relatship;
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

}
