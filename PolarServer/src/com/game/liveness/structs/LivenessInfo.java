package com.game.liveness.structs;

import java.util.ArrayList;
import java.util.List;

import com.game.object.GameObject;

/**
 * 角色活跃度模块数据Bean
 * @author hongxiao.z
 * @date   2013-12-26  下午7:47:52
 */
public class LivenessInfo extends GameObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6159484955837208567L;

	/*当前活跃度*/
	private int liveness;
	
	/*宝箱领取状态列表*/
	private  ArrayList<StateInfo> gainStates = new ArrayList<StateInfo>();
	
	/*事件进度列表*/
	private  ArrayList<EventInfo> events= new ArrayList<EventInfo>();

	
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
	public List<StateInfo> getGainStates() 
	{
		return gainStates;
	}

	/**
	 * 事件进度列表
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午7:51:55
	 */
	public List<EventInfo> getEvents() 
	{
		return events;
	}

	public void setGainStates(ArrayList<StateInfo> gainStates) {
		this.gainStates = gainStates;
	}

	public void setEvents(ArrayList<EventInfo> events) {
		this.events = events;
	}
}
