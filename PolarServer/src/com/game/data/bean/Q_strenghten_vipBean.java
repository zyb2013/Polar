package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_prayBean Bean
 */
public class Q_strenghten_vipBean {

	//强化的等级索引
	private String q_strenghten_level;
	
	//最低vip等级限制
	private int q_vip_level;
	
	//特权强化消耗的钻石
	private int q_diamond;

	public String getQ_strenghten_level() {
		return q_strenghten_level;
	}

	public void setQ_strenghten_level(String q_strenghten_level) {
		this.q_strenghten_level = q_strenghten_level;
	}

	public int getQ_vip_level() {
		return q_vip_level;
	}

	public void setQ_vip_level(int q_vip_level) {
		this.q_vip_level = q_vip_level;
	}

	public int getQ_diamond() {
		return q_diamond;
	}

	public void setQ_diamond(int q_diamond) {
		this.q_diamond = q_diamond;
	}
}