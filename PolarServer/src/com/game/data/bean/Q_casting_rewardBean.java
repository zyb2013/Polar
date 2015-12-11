package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_reward Bean
 */
public class Q_casting_rewardBean {

	//最小等级
	private int q_min_level;
	
	//最大等级
	private int q_max_level;
	
	//道具Id（不填为没有）
	private int q_item_id;
	
	//获得职业（0为无限制，有多个职业时用“ǀ”隔开）
	private String q_job;
	
	//获得概率权重值
	private int q_weight;

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
	 * get 道具Id（不填为没有）
	 * @return
	 */
	public int getQ_item_id() {
		return q_item_id;
	}

	/**
	 * set 道具Id（不填为没有）
	 */
	public void setQ_item_id(int q_item_id) {
		this.q_item_id = q_item_id;
	}

	/**
	 * get 获得职业（0为无限制，有多个职业时用“ǀ”隔开）
	 * @return
	 */
	public String getQ_job() {
		return q_job;
	}

	/**
	 * set 获得职业（0为无限制，有多个职业时用“ǀ”隔开）
	 */
	public void setQ_job(String q_job) {
		this.q_job = q_job;
	}

	/**
	 * get 获得概率权重值
	 * @return
	 */
	public int getQ_weight() {
		return q_weight;
	}

	/**
	 * set 获得概率权重值
	 */
	public void setQ_weight(int q_weight) {
		this.q_weight = q_weight;
	}

}