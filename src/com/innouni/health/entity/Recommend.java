package com.innouni.health.entity;

/**
 * 我的推荐实体类(包括我的菜谱)
 * 
 * @author HuGuojun
 * @date 2013-12-26 下午5:17:54
 * @modify
 * @version 1.0.0
 */
public class Recommend {

	private Object id;
	private Object title;
	private Object content;
	private Object addTime;
	private Object calory;

	public Object getCalory() {
		return calory;
	}

	public void setCalory(Object calory) {
		this.calory = calory;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public Object getTitle() {
		return title;
	}

	public void setTitle(Object title) {
		this.title = title;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Object getAddTime() {
		return addTime;
	}

	public void setAddTime(Object addTime) {
		this.addTime = addTime;
	}

}
