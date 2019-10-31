package com.example.toro.doman;

import com.example.toro.json.internal.mapping.ApiField;

public class SchNotice {
	 @ApiField("id")
	private String id="";
	 
	 @ApiField("time")
	private long time=0;
	 
	 @ApiField("type")
	private int type=0;
	 
	 @ApiField("title")
	private String title="";
	 
	 @ApiField("txt")
	private String txt="";
	 
	 @ApiField("photo")
	private String photo="";

	 @ApiField("video")
	private String video="";
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}

	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}

}
