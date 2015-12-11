package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_exchange Bean
 */
public class Q_casting_exchangeBean {

	//表中物品编号
	private int q_id;
	
	//兑换物品ID
	private int q_item_id;
	
	//是否引用品质特效（服务器不需要；0或不填：不用，1：用）
	private int q_item_effect;
	
	//需求工艺度
	private int q_technology_point;
	
	//需求角色等级
	private int q_character_level;

	/**
	 * get 表中物品编号
	 * @return
	 */
	public int getQ_id() {
		return q_id;
	}

	/**
	 * set 表中物品编号
	 */
	public void setQ_id(int q_id) {
		this.q_id = q_id;
	}

	/**
	 * get 兑换物品ID
	 * @return
	 */
	public int getQ_item_id() {
		return q_item_id;
	}

	/**
	 * set 兑换物品ID
	 */
	public void setQ_item_id(int q_item_id) {
		this.q_item_id = q_item_id;
	}

	/**
	 * get 是否引用品质特效（服务器不需要；0或不填：不用，1：用）
	 * @return
	 */
	public int getQ_item_effect() {
		return q_item_effect;
	}

	/**
	 * set 是否引用品质特效（服务器不需要；0或不填：不用，1：用）
	 */
	public void setQ_item_effect(int q_item_effect) {
		this.q_item_effect = q_item_effect;
	}

	/**
	 * get 需求工艺度
	 * @return
	 */
	public int getQ_technology_point() {
		return q_technology_point;
	}

	/**
	 * set 需求工艺度
	 */
	public void setQ_technology_point(int q_technology_point) {
		this.q_technology_point = q_technology_point;
	}

	/**
	 * get 需求角色等级
	 * @return
	 */
	public int getQ_character_level() {
		return q_character_level;
	}

	/**
	 * set 需求角色等级
	 */
	public void setQ_character_level(int q_character_level) {
		this.q_character_level = q_character_level;
	}

}