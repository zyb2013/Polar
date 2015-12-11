package com.game.data.bean;

public class Q_hold_rewardBean {
	
	private int q_id;
	private int q_day;
	
	private String q_rewards;
	
	public String getQ_rewards() {
		return q_rewards;
	}
	
	public void setQ_rewards(String q_rewards) {
		this.q_rewards = q_rewards;
	}
	
	public int getQ_id() {
		return q_id;
	}
	public void setQ_id(int q_id) {
		this.q_id = q_id;
	}
	public int getQ_day() {
		return q_day;
	}
	public void setQ_day(int q_day) {
		this.q_day = q_day;
	}
	
	
//	CREATE TABLE `q_hold_reward` (
//			  `q_id` int(11) NOT NULL,
//			  `q_day` int(11) DEFAULT NULL,
//			  PRIMARY KEY (`q_id`)
//			) ENGINE=InnoDB DEFAULT CHARSET=utf8;
}
