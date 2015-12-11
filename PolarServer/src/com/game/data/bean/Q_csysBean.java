package com.game.data.bean;

public class Q_csysBean {

	private int q_type;

	private String q_value;

	private int q_integral;

	private String q_parm;

	private int q_retime;

	private int q_rad_min;
	
	private int q_rad_max;

	public int getQ_rad_min() {
		return q_rad_min;
	}

	public void setQ_rad_min(int q_rad_min) {
		this.q_rad_min = q_rad_min;
	}

	public int getQ_rad_max() {
		return q_rad_max;
	}

	public void setQ_rad_max(int q_rad_max) {
		this.q_rad_max = q_rad_max;
	}

	public int getQ_type() {
		return q_type;
	}

	public void setQ_type(int q_type) {
		this.q_type = q_type;
	}

	public String getQ_value() {
		return q_value;
	}

	public void setQ_value(String q_value) {
		this.q_value = q_value;
	}

	public int getQ_integral() {
		return q_integral;
	}

	public void setQ_integral(int q_integral) {
		this.q_integral = q_integral;
	}

	public String getQ_parm() {
		return q_parm;
	}

	public void setQ_parm(String q_parm) {
		this.q_parm = q_parm;
	}

	public int getQ_retime() {
		return q_retime;
	}

	public void setQ_retime(int q_retime) {
		this.q_retime = q_retime;
	}

	// CREATE TABLE `q_csys` (
	// `q_type` int(11) DEFAULT NULL,
	// `q_value` varchar(255) DEFAULT NULL,
	// `q_integral` int(11) DEFAULT NULL,
	// `q_parm` varchar(255) DEFAULT NULL,
	// `q_retime` int(11) DEFAULT NULL
	// ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
}
