package com.innouni.health.entity;

/**
 * 饮食实体类
 * 
 * @author HuGuojun
 * @date 2014-1-13 下午4:33:28
 * @modify
 * @version 1.0.0
 */
public class Diet {

	private Object id;
	private Object foodId;
	private Object foodName;
	private Object foodAmount;
	private Object foodUnitType;
	private Object calorieIn;
	private Object addTime;

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

	public Object getFoodAmount() {
		return foodAmount;
	}

	public void setFoodAmount(Object foodAmount) {
		this.foodAmount = foodAmount;
	}

	public Object getFoodUnitType() {
		return foodUnitType;
	}

	public void setFoodUnitType(Object foodUnitType) {
		this.foodUnitType = foodUnitType;
	}

	public Object getCalorieIn() {
		return calorieIn;
	}

	public void setCalorieIn(Object calorieIn) {
		this.calorieIn = calorieIn;
	}

	public Object getAddTime() {
		return addTime;
	}

	public void setAddTime(Object addTime) {
		this.addTime = addTime;
	}

}
