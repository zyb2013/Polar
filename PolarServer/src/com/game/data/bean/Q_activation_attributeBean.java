package com.game.data.bean;

/**
 * @author luminghua.ko@gmail.com
 *
 * @date   2014年3月18日 上午11:30:40
 */
public class Q_activation_attributeBean {

	private int q_id;
	private int q_type;//激活类型 1=卓越条目激活
	private String q_param;//激活参数 卓越条目激活对应参数:卓越条目数
	private String q_attribute;//激活属性
	
	
	public int getQ_id() {
		return q_id;
	}
	public void setQ_id(int q_id) {
		this.q_id = q_id;
	}
	public int getQ_type() {
		return q_type;
	}
	public void setQ_type(int q_type) {
		this.q_type = q_type;
	}
	public String getQ_param() {
		return q_param;
	}
	public void setQ_param(String q_param) {
		this.q_param = q_param;
	}
	public String getQ_attribute() {
		return q_attribute;
	}
	public void setQ_attribute(String q_attribute) {
		this.q_attribute = q_attribute;
	}
	
}
