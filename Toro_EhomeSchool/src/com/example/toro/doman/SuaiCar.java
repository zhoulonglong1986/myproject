package com.example.toro.doman;

public class SuaiCar {
	
	private String cid="";
	private String photo="";
	private String time="0";
	private int state=0;//0 ��ʾû�д��������ݣ�1 ��ʾ �ϴ��������� 
	

	public SuaiCar(){};
	public SuaiCar(String cids,String photos,String times,int states){
		this.cid=cids;
		this.photo=photos;
		this.time=times;
		this.state=states;
	}
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

}
