package com.innouni.health.entity;

/**
 * 版本信息
 * 
 * @author HuGuojun
 * @date 2014-2-10 下午3:09:25
 * @modify
 * @version 1.0.0
 */
public class VersionInfo {

	private String apkUrl; // 下载地址
	private String name; // 版本名称
	private String content; // 版本更新内容
	private int versionNum; // 版本code

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(int versionNum) {
		this.versionNum = versionNum;
	}

}
