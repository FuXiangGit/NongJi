package com.nongji.vo;

public class MessageInfo {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	/** @Fields response : 回传的响应类型，可以配置ok|error */
	private String response;
	/** @Fields info : 需要给前端页面返回的数据 */
	private String info;
	/** @Fields object : 如果需要回传复杂参数，需要回传的内容 */
	private Object object;
	private String [] urls;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String[] getUrls() {
		return urls;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public String toString() {
		return "MessageInfo [response=" + response + ", info=" + info + ", object=" + object + "]";
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
