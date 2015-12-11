package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_lotteryBean Bean
 */
public class Q_lotteryBean {

	//表中物品编号
	private int q_dlottery_id;
	
	//物品id
	private int q_dlottery_item_id;
	
	//抽1次能获得的该奖品的数量
	private int q_dlottery_item_amount;
	
	//物品的珍贵程度:0为普通;1为珍贵;2为极其珍贵
	private int q_dlottery_item_value;
	
	//强化等级_追加等级_卓越属性
	private String q_dlottery_item_addition;
	
	//抽中这件奖品的概率，万分比表示
	private int q_dlottery_item_probability;
	
	//当玩家第一次抽奖时，依照此字段抽奖。珍贵度为0的物品在此字段中的数值显著下降，珍贵度为1的物品在此字段中的数值显著提高，珍贵度为2的物品在此字段中的数值略微提高。万分比表示。
	private int q_dlottery_item_special_probability;

	//玩家的职业,范围:1-9
	private int q_dlottery_job;
	
	/**
	 * get 表中物品编号
	 * @return
	 */
	public int getQ_dlottery_id() {
		return q_dlottery_id;
	}

	/**
	 * set 表中物品编号
	 */
	public void setQ_dlottery_id(int q_dlottery_id) {
		this.q_dlottery_id = q_dlottery_id;
	}

	/**
	 * get 物品id
	 * @return
	 */
	public int getQ_dlottery_item_id() {
		return q_dlottery_item_id;
	}

	/**
	 * set 物品id
	 */
	public void setQ_dlottery_item_id(int q_dlottery_item_id) {
		this.q_dlottery_item_id = q_dlottery_item_id;
	}

	/**
	 * get 抽1次能获得的该奖品的数量
	 * @return
	 */
	public int getQ_dlottery_item_amount() {
		return q_dlottery_item_amount;
	}

	/**
	 * set抽1次能获得的该奖品的数量
	 */
	public void setQ_dlottery_item_amount(int q_dlottery_item_amount) {
		this.q_dlottery_item_amount = q_dlottery_item_amount;
	}

	/**
	 * get 物品的珍贵程度:0为普通;1为珍贵;2为极其珍贵
	 * @return
	 */
	public int getQ_dlottery_item_value() {
		return q_dlottery_item_value;
	}

	/**
	 * set 物品的珍贵程度:0为普通;1为珍贵;2为极其珍贵
	 */
	public void setQ_dlottery_item_value(int q_dlottery_item_value) {
		this.q_dlottery_item_value = q_dlottery_item_value;
	}

	/**
	 * get 强化等级_追加等级_卓越属性
	 * @return
	 */
	public String getQ_dlottery_item_addition() {
		return q_dlottery_item_addition;
	}

	/**
	 * set 强化等级_追加等级_卓越属性
	 */
	public void setQ_dlottery_item_addition(String q_dlottery_item_addition) {
		this.q_dlottery_item_addition = q_dlottery_item_addition;
	}

	/**
	 * get 抽中这件奖品的概率，万分比表示
	 * @return
	 */
	public int getQ_dlottery_item_probability() {
		return q_dlottery_item_probability;
	}

	/**
	 * set 抽中这件奖品的概率，万分比表示
	 */
	public void setQ_dlottery_item_probability(int q_dlottery_item_probability) {
		this.q_dlottery_item_probability = q_dlottery_item_probability;
	}

	/**
	 * get 当玩家第一次抽奖时，依照此字段抽奖。珍贵度为0的物品在此字段中的数值显著下降，珍贵度为1的物品在此字段中的数值显著提高，珍贵度为2的物品在此字段中的数值略微提高。万分比表示。
	 * @return
	 */
	public int getQ_dlottery_item_special_probability() {
		return q_dlottery_item_special_probability;
	}

	/**
	 * set 当玩家第一次抽奖时，依照此字段抽奖。珍贵度为0的物品在此字段中的数值显著下降，珍贵度为1的物品在此字段中的数值显著提高，珍贵度为2的物品在此字段中的数值略微提高。万分比表示。 
	 */
	public void setQ_dlottery_item_special_probability(
			int q_dlottery_item_special_probability) {
		this.q_dlottery_item_special_probability = q_dlottery_item_special_probability;
	}

	/**
	 * get 玩家的职业,范围:1-9
	 * @return
	 */
	public int getQ_dlottery_job() {
		return q_dlottery_job;
	}

	/**
	 * set 玩家的职业,范围:1-9
	 */
	public void setQ_dlottery_job(int q_dlottery_job) {
		this.q_dlottery_job = q_dlottery_job;
	}

}