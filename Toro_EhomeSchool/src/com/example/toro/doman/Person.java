package com.example.toro.doman;

import com.example.toro.json.internal.mapping.ApiField;

public class Person {
	 @ApiField("id")
	private Integer id=0;
	 @ApiField("name")
	private String name="";
	 @ApiField("phone")
	private String phone="";

	public Person(){}
	
	public Person(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", phone=" + phone + "]";
	}
}
