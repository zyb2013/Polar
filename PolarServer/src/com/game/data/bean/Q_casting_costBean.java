package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_cost Bean
 */
public class Q_casting_costBean {

	//最小等级
	private int q_min_level;
	
	//最大等级
	private int q_max_level;
	
	//花费金币
	private int q_cost_coin;

	/**
	 * get 最小等级
	 * @return
	 */
	public int getQ_min_level() {
		return q_min_level;
	}

	/**
	 * set 最小等级
	 */
	public void setQ_min_level(int q_min_level) {
		this.q_min_level = q_min_level;
	}

	/**
	 * get 最大等级
	 * @return
	 */
	public int getQ_max_level() {
		return q_max_level;
	}

	/**
	 * set 最大等级
	 */
	public void setQ_max_level(int q_max_level) {
		this.q_max_level = q_max_level;
	}

	/**
	 * get 花费金币
	 * @return
	 */
	public int getQ_cost_coin() {
		return q_cost_coin;
	}

	/**
	 * set 花费金币
	 */
	public void setQ_cost_coin(int q_cost_coin) {
		this.q_cost_coin = q_cost_coin;
	}
	
}