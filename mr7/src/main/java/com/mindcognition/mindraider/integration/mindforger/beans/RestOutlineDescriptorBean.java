package com.mindcognition.mindraider.integration.mindforger.beans;

public class RestOutlineDescriptorBean {

	private String key;
	private String globalId;
	private String title;
	
	public RestOutlineDescriptorBean() {
	}

	public RestOutlineDescriptorBean(String key, String globalId, String title) {
		this.key = key;
		this.globalId = globalId;
		this.title = title;
	}

	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
