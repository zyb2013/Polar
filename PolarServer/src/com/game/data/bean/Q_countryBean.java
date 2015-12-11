package com.game.data.bean;

public class Q_countryBean {
	
	private int q_lv;
	private String q_rewards;
	

	public int getQ_lv() {
		return q_lv;
	}
	public void setQ_lv(int q_lv) {
		this.q_lv = q_lv;
	}
	public String getQ_rewards() {
		return q_rewards;
	}
	public void setQ_rewards(String q_rewards) {
		this.q_rewards = q_rewards;
	}
	
	
	
//	CREATE TABLE `q_hold_reward` (
//			  `q_id` int(11) NOT NULL,
//			  `q_day` int(11) DEFAULT NULL,
//			  PRIMARY KEY (`q_id`)
//			) ENGINE=InnoDB DEFAULT CHARSET=utf8;
}
