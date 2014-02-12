package com.innouni.health.entity;

/**
 * 专家实体
 * 
 * @author HuGuojun
 * @date 2014-2-11 下午4:46:54
 * @modify
 * @version 1.0.0
 */
public class ExpertInfo {

	private Object id;
	private Object imageUrl;
	private Object name;
	private Object description;

	public ExpertInfo() {
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public Object getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(Object imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Object getName() {
		return name;
	}

	public void setName(Object name) {
		this.name = name;
	}

	public Object getDescription() {
		return description;
	}

	public void setDescription(Object description) {
		this.description = description;
	}

}
