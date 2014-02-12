package com.innouni.health.entity;

/**
 * 我的收藏实体类
 * 
 * @author HuGuojun
 * @date 2013-12-26 下午5:17:54
 * @modify
 * @version 1.0.0
 */
public class Collectiom {

	private Object id; // 收藏ID
	private Object foodId; // 菜品ID
	private Object foodName;
	private Object addTime;
	private Object activeId; // 运动ID
	private Object activeName;
	private Object activeASlope;
	private Object activeBInterept;
	private Object activeSubType;
	private Object activeSubTypeName;
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

	public Object getFoodId() {
		return foodId;
	}

	public void setFoodId(Object foodId) {
		this.foodId = foodId;
	}

	public Object getFoodName() {
		return foodName;
	}

	public void setFoodName(Object foodName) {
		this.foodName = foodName;
	}

	public Object getAddTime() {
		return addTime;
	}

	public void setAddTime(Object addTime) {
		this.addTime = addTime;
	}

	public Object getActiveId() {
		return activeId;
	}

	public void setActiveId(Object activeId) {
		this.activeId = activeId;
	}

	public Object getActiveName() {
		return activeName;
	}

	public void setActiveName(Object activeName) {
		this.activeName = activeName;
	}

	public Object getActiveASlope() {
		return activeASlope;
	}

	public void setActiveASlope(Object activeASlope) {
		this.activeASlope = activeASlope;
	}

	public Object getActiveBInterept() {
		return activeBInterept;
	}

	public void setActiveBInterept(Object activeBInterept) {
		this.activeBInterept = activeBInterept;
	}

	public Object getActiveSubType() {
		return activeSubType;
	}

	public void setActiveSubType(Object activeSubType) {
		this.activeSubType = activeSubType;
	}

	public Object getActiveSubTypeName() {
		return activeSubTypeName;
	}

	public void setActiveSubTypeName(Object activeSubTypeName) {
		this.activeSubTypeName = activeSubTypeName;
	}

}
