package com.innouni.health.entity;

/**
 * 留言实体
 * 
 * @author HuGuojun
 * @date 2014-2-13 下午2:43:28
 * @modify
 * @version 1.0.0
 */
public class MessageInfo {

	private Object id;
	private Object type;
	private Object time;
	private Object content;
	
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	public Object getType() {
		return type;
	}
	public void setType(Object type) {
		this.type = type;
	}
	public Object getTime() {
		return time;
	}
	public void setTime(Object time) {
		this.time = time;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
	
}
