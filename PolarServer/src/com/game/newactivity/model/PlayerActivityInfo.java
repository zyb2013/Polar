package com.game.newactivity.model;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;

/**
 * @author luminghua
 *
 * @date   2014年2月25日 下午7:40:51
 */
public class PlayerActivityInfo {

	private int id;
	private long playerId;
	private int job;
	private int activityId;
	private String info;
	private String awardInfo;//[order1,order2...]可以领第几项，从0开始
	private int canAward;//awardInfo的数组长度，方便从数据库中索引
	private long time;//生成的时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getAwardInfo() {
		return awardInfo;
	}
	public void setAwardInfo(String awardInfo) {
		this.awardInfo = awardInfo;
	}
	public int getCanAward() {
		return canAward;
	}
	
	public int getJob() {
		return job;
	}
	public void setJob(int job) {
		this.job = job;
	}
	
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * 不要直接调用，通过addAward和reduceAward会自动计数
	 * @param canAward
	 */
	@Deprecated
	public void setCanAward(int canAward) {
		this.canAward = canAward;
	}
	
	public JSONArray getAward2JSONArray() {
		if(!StringUtils.isBlank(awardInfo)) {
			return JSONArray.fromObject(awardInfo);
		}
		return null;
	}
	
	public void addAward(int order) {
		if(StringUtils.isBlank(awardInfo)) {
			awardInfo = "[]";
		}
		JSONArray array = JSONArray.fromObject(awardInfo);
		boolean add = array.add(String.valueOf(order));
		if(add) {
			awardInfo = array.toString();
			canAward ++;
		}
	}
	
	public void reduceAward(int order) {
		if(StringUtils.isBlank(awardInfo)) {
			return;
		}
		JSONArray array = JSONArray.fromObject(awardInfo);
		boolean remove = array.remove(String.valueOf(order));
		if(remove) {
			awardInfo = array.toString();
			canAward --;
		}
	}
	
	public String toString() {
		return "id:["+id+"] playerId:["+playerId+"] job:["+job+"] aid:["+activityId+"] info:["+info+"] award:["+awardInfo+"] can:"+canAward;
	}
	
}
