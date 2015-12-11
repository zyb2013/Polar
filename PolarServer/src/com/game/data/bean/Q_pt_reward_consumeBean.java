package com.game.data.bean;
/**
 * 爬塔奖励领取消耗表
 * @author hongxiao.z
 * @date   2014-2-13  下午4:33:28
 */
public class Q_pt_reward_consumeBean 
{
	/**
	 * 次数限制
	 */
	private int q_limit;
	
	/**
	 * 元宝消耗
	 */
	private int q_golds;

	public int getQ_limit() {
		return q_limit;
	}

	public void setQ_limit(int q_limit) {
		this.q_limit = q_limit;
	}

	public int getQ_golds() {
		return q_golds;
	}

	public void setQ_golds(int q_golds) {
		this.q_golds = q_golds;
	}
}
