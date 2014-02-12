package com.innouni.health.entity;

/**
 * 指导信息
 * 
 * @author HuGuojun
 * @date 2014-2-12 上午9:47:03
 * @modify
 * @version 1.0.0
 */
public class Opinion {

	private String id;
	private String title;
	private String name;
	private String url;
	private String addTime;

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
