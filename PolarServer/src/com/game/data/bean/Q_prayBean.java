package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_prayBean Bean
 */
public class Q_prayBean {

	//祈愿id
	private int q_id;
	
	//祈愿次数
	private int q_pray_times;
	
	//祈愿金币花费的钻石数量
	private int q_pray_gold_cost;
	
	//祈愿经验花费的钻石数量
	private int q_pray_exp_cost;
	

	/**
	 * get 祈愿id 
	 * @return
	 */
	public int getQ_id() {
		return q_id;
	}

	/**
	 * set 祈愿id
	 */
	public void setQ_id(int q_id) {
		this.q_id = q_id;
	}

	/**
	 * get 祈愿次数
	 * @return
	 */
	public int getQ_pray_times() {
		return q_pray_times;
	}

	/**
	 * set 祈愿次数
	 */
	public void setQ_pray_times(int q_pray_times) {
		this.q_pray_times = q_pray_times;
	}

	/**
	 * get 祈愿金币花费的钻石数量
	 */
	public int getQ_pray_gold_cost() {
		return q_pray_gold_cost;
	}

	/**
	 * set 祈愿金币花费的钻石数量
	 * @param q_pray_gold_cost
	 */
	public void setQ_pray_gold_cost(int q_pray_gold_cost) {
		this.q_pray_gold_cost = q_pray_gold_cost;
	}

	/**
	 * get 祈愿经验花费的钻石数量
	 */
	public int getQ_pray_exp_cost() {
		return q_pray_exp_cost;
	}

	/**
	 * set 祈愿经验花费的钻石数量
	 * @param q_pray_exp_cost
	 */
	public void setQ_pray_exp_cost(int q_pray_exp_cost) {
		this.q_pray_exp_cost = q_pray_exp_cost;
	}

}