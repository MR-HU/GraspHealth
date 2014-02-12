package com.innouni.health.entity;

public class Sport {

	private Object sportType; //类型(上班 下班 普通运动)
	private Object logId;
	private Object id;
	private Object name;
	private Object type;
	private Object typeName;
	private Object time;
	private Object unit; 
	private Object calory;

	public Object getLogId() {
		return logId;
	}

	public void setLogId(Object logId) {
		this.logId = logId;
	}

	public Object getSportType() {
		return sportType;
	}

	public void setSportType(Object sportType) {
		this.sportType = sportType;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public Object getName() {
		return name;
	}

	public void setName(Object name) {
		this.name = name;
	}

	public Object getType() {
		return type;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public Object getTypeName() {
		return typeName;
	}

	public void setTypeName(Object typeName) {
		this.typeName = typeName;
	}

	public Object getTime() {
		return time;
	}

	public void setTime(Object time) {
		this.time = time;
	}

	public Object getUnit() {
		return unit;
	}

	public void setUnit(Object unit) {
		this.unit = unit;
	}

	public Object getCalory() {
		return calory;
	}

	public void setCalory(Object calory) {
		this.calory = calory;
	}

}
