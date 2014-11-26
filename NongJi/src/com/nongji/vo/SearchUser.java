package com.nongji.vo;

public class SearchUser {

	private String Phone;
	private Double la;
	private Double lo;
	private String userID;
	private String carID;
	private String userName;
	private int state;
	private int searchUserID;
	private String MSG;
	private String TypeMsg;
	
	
	public String getTypeMsg() {
		return TypeMsg;
	}
	public void setTypeMsg(String typeMsg) {
		TypeMsg = typeMsg;
	}
	public String getMSG() {
		return MSG;
	}
	public void setMSG(String mSG) {
		MSG = mSG;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getSearchUserID() {
		return searchUserID;
	}
	public void setSearchUserID(int searchUserID) {
		this.searchUserID = searchUserID;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public Double getLa() {
		return la;
	}
	public void setLa(Double la) {
		this.la = la;
	}
	public Double getLo() {
		return lo;
	}
	public void setLo(Double lo) {
		this.lo = lo;
	}
	public String getCarID() {
		return carID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public void setCarID(String carID) {
		this.carID = carID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	
	
	
	
	
}
