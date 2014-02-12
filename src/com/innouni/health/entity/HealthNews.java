package com.innouni.health.entity;

/**
 * 健康资讯实体类
 * 
 * @author HuGuojun
 * @date 2014-2-11 下午2:46:19
 * @modify
 * @version 1.0.0
 */
public class HealthNews {

	private String id;
	private String title;
	private String addTime;

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

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
}
