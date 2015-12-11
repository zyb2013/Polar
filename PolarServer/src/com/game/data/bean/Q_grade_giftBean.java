package com.game.data.bean;



/**
 * 等级礼包bean
 * @author hongxiao.z
 * @date   2014-2-15  下午3:56:45
 */
public class Q_grade_giftBean 
{
	/**
	 * 礼包ID
	 */
	private int q_id;
	
	/**
	 *	需要等级
	 */
	private int q_need_grade;
	
	/**
	 * 最大被领取的次数
	 */
	private int q_max_gain;
	
	/**
	 * 奖励的道具列表(解析前)
	 */
	private String q_reward;
	
//	-1 铜钱
//	-2 钻石
//	-3 真气
//	-4 经验
//	-5 绑定钻石
//	-6 精魄
	
	public int getQ_id() {
		return q_id;
	}

	public void setQ_id(int q_id) {
		this.q_id = q_id;
	}

	public int getQ_need_grade() {
		return q_need_grade;
	}

	public void setQ_need_grade(int q_need_grade) {
		this.q_need_grade = q_need_grade;
	}

	public int getQ_max_gain() {
		return q_max_gain == 0 ? -1 : q_max_gain;
	}

	public void setQ_max_gain(int q_max_gain) {
		this.q_max_gain = q_max_gain;
	}

	public String getQ_reward() {
		return q_reward;
	}

	public void setQ_reward(String q_reward) {
		this.q_reward = q_reward;
	}
}
