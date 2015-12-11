package com.game.newactivity.model;

/**
 * @author luminghua
 *
 * @date   2014年2月25日 下午8:26:07
 */
public class SimpleActivityInfo {
	
	private int activityId;
	private byte count;
	
	public SimpleActivityInfo() {
		
	}
	/**
	 * @param q_id
	 * @param i
	 */
	public SimpleActivityInfo(int q_id, int i) {
		this.activityId = q_id;
		this.count = (byte) i;
	}
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public byte getCount() {
		return count;
	}
	public void setCount(byte count) {
		this.count = count;
	}
}
