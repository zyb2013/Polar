package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_fraction Bean
 */
public class Q_fractionBean {

	//表中物品编号
	private int q_dfraction_id;
	
	//兑换该奖品所需积分:10代表需要10积分;500代表需要500积分;以此类推
	private int q_dfraction_type;
	
	//物品id
	private int q_dfraction_item_id;
	
	//物品名
	private String q_dfraction_item_name;
	
	//该奖品的数量
	private int q_dfraction_item_amount;
	
	//强化等级_追加等级_卓越属性
	private String q_dfraction_item_addition;

	/**
	 * get 表中物品编号
	 * @return
	 */
	public int getQ_dfraction_id() {
		return q_dfraction_id;
	}

	/**
	 * set 表中物品编号
	 */
	public void setQ_dfraction_id(int q_dfraction_id) {
		this.q_dfraction_id = q_dfraction_id;
	}

	/**
	 * get 兑换该奖品所需积分:10代表需要10积分;500代表需要500积分;以此类推
	 * @return
	 */
	public int getQ_dfraction_type() {
		return q_dfraction_type;
	}

	/**
	 * set 兑换该奖品所需积分:10代表需要10积分;500代表需要500积分;以此类推
	 */
	public void setQ_dfraction_type(int q_dfraction_type) {
		this.q_dfraction_type = q_dfraction_type;
	}

	/**
	 * get 物品id
	 * @return
	 */
	public int getQ_dfraction_item_id() {
		return q_dfraction_item_id;
	}

	/**
	 * set 物品id
	 */
	public void setQ_dfraction_item_id(int q_dfraction_item_id) {
		this.q_dfraction_item_id = q_dfraction_item_id;
	}

	/**
	 * get 物品名
	 * @return
	 */
	public String getQ_dfraction_item_name() {
		return q_dfraction_item_name;
	}

	/**
	 * set 物品名
	 */
	public void setQ_dfraction_item_name(String q_dfraction_item_name) {
		this.q_dfraction_item_name = q_dfraction_item_name;
	}

	/**
	 * get 该奖品的数量
	 * @return
	 */
	public int getQ_dfraction_item_amount() {
		return q_dfraction_item_amount;
	}

	/**
	 * set 该奖品的数量
	 */
	public void setQ_dfraction_item_amount(int q_dfraction_item_amount) {
		this.q_dfraction_item_amount = q_dfraction_item_amount;
	}

	/**
	 * get 强化等级_追加等级_卓越属性
	 * @return
	 */
	public String getQ_dfraction_item_addition() {
		return q_dfraction_item_addition;
	}

	/**
	 * set 强化等级_追加等级_卓越属性
	 */
	public void setQ_dfraction_item_addition(String q_dfraction_item_addition) {
		this.q_dfraction_item_addition = q_dfraction_item_addition;
	}
	
}