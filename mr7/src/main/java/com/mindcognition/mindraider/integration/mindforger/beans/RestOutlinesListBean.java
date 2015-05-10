package com.mindcognition.mindraider.integration.mindforger.beans;

public class RestOutlinesListBean {

	private RestOutlineDescriptorBean[] outlines;

	public RestOutlinesListBean() {
	}
	
	public RestOutlinesListBean(RestOutlineDescriptorBean[] outlines) {
		super();
		this.outlines = outlines;
	}

	public RestOutlineDescriptorBean[] getOutlines() {
		return outlines;
	}

	public void setOutlines(RestOutlineDescriptorBean[] outlines) {
		this.outlines = outlines;
	}
}
