package com.game.zones.structs;

/**
 * 爬塔记录实体
 * @author hongxiao.z
 * @date   2014-2-13  上午10:28:09
 */
public class PtRecord 
{
	//角色ID
	private long bestId;
	//最佳通关者(角色全名)
	private String bestName;
	//最佳时间(秒)
	private int bestTime;
	//最近通关者
	private String latelyName;
	//最近通关者职业
	private byte latelyJob;
	//最近通关者战斗力
	private int latelyFightPower;
	
	public long getBestId() {
		return bestId;
	}
	public void setBestId(long bestId) {
		this.bestId = bestId;
	}
	public String getBestName() {
		return bestName;
	}
	public void setBestName(String bestName) {
		this.bestName = bestName;
	}
	public int getBestTime() {
		return bestTime;
	}
	public void setBestTime(int bestTime) {
		this.bestTime = bestTime;
	}
	public String getLatelyName() {
		return latelyName;
	}
	public void setLatelyName(String latelyName) {
		this.latelyName = latelyName;
	}
	public byte getLatelyJob() {
		return latelyJob;
	}
	public void setLatelyJob(byte latelyJob) {
		this.latelyJob = latelyJob;
	}
	public int getLatelyFightPower() {
		return latelyFightPower;
	}
	public void setLatelyFightPower(int latelyFightPower) {
		this.latelyFightPower = latelyFightPower;
	}
	
	public PtRecord(long bestId, String bestName, int bestTime,
			String latelyName, byte latelyJob, int latelyFightPower) {
		super();
		this.bestId = bestId;
		this.bestName = bestName;
		this.bestTime = bestTime;
		this.latelyName = latelyName;
		this.latelyJob = latelyJob;
		this.latelyFightPower = latelyFightPower;
	}
	
	public PtRecord() {
		super();
	}
}
