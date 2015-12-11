package com.game.db.bean;


public class GoldExpend {
	private long unuse_index;
	private long time;
	private long roleid;
	private int goldnum;
	private int reason;
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public long getRoleid() {
		return roleid;
	}
	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}
	public int getGoldnum() {
		return goldnum;
	}
	public void setGoldnum(int goldnum) {
		this.goldnum = goldnum;
	}
	public int getReason() {
		return reason;
	}
	public void setReason(int reason) {
		this.reason = reason;
	}
	public long getUnuse_index() {
		return unuse_index;
	}
	public void setUnuse_index(long unuse_index) {
		this.unuse_index = unuse_index;
	}
}
