package com.game.db.bean;
/**
 * 角色活跃度模块数据Bean
 * @author hongxiao.z
 * @date   2013-12-26  下午7:47:52
 */
public class LivenessBean 
{
	/*角色id*/
	private long roleid;
	
	/*当前活跃度*/
	private int liveness;
	
	/*宝箱领取状态列表*/
	private String gain_state;
	
	/*事件进度列表*/
	private String events;

	/**
	 * 角色id
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午7:51:24
	 */
	public long getRoleid() {
		return roleid;
	}

	/**
	 * 角色id
	 * @param roleid
	 * @create	hongxiao.z      2013-12-26 下午7:51:27
	 */
	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}

	/**
	 * 当前活跃度
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午7:51:34
	 */
	public int getLiveness() {
		return liveness;
	}

	/**
	 * 当前活跃度
	 * @param liveness
	 * @create	hongxiao.z      2013-12-26 下午7:51:37
	 */
	public void setLiveness(int liveness) {
		this.liveness = liveness;
	}

	/**
	 * 宝箱领取状态列表
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午7:51:44
	 */
	public String getGain_state() {
		return gain_state;
	}

	/**
	 * 宝箱领取状态列表
	 * @param gain_state
	 * @create	hongxiao.z      2013-12-26 下午7:51:47
	 */
	public void setGain_state(String gain_state) {
		this.gain_state = gain_state;
	}

	/**
	 * 事件进度列表
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午7:51:55
	 */
	public String getEvents() {
		return events;
	}

	/**
	 * 事件进度列表
	 * @param events
	 * @create	hongxiao.z      2013-12-26 下午7:51:57
	 */
	public void setEvents(String events) {
		this.events = events;
	}
	
	
}
