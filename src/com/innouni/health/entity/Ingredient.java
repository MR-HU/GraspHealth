package com.innouni.health.entity;

/**
 * 食材信息实体类
 * 
 * @author HuGuojun
 * @date 2014-1-10 下午4:46:37
 * @modify
 * @version 1.0.0
 */
public class Ingredient {

	private String id;
	private String name;
	private String unit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
