package com.game.data.bean;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午12:22:22
 */
public class Q_newActivityBean {

	private int q_id;
	private String q_desc;
	private String q_notice;//公告
	private String q_startAndEnd;//开始和结束时间
	private String q_stime;//时间调度
	private String q_logic;//逻辑，主要是 领取条件+奖励
	private String q_condDesc;//领取条件描述
	private String q_expend;//扩展用
	private boolean q_onof;//开关
	private String q_group;//活动分组，可用于前端显示多个图标，每个图标包含当组活动列表
	
	public int getQ_id() {
		return q_id;
	}
	public void setQ_id(int q_id) {
		this.q_id = q_id;
	}
	
	public String getQ_desc() {
		return q_desc;
	}
	public void setQ_desc(String q_desc) {
		this.q_desc = q_desc;
	}
	
	public String getQ_notice() {
		return q_notice;
	}
	public void setQ_notice(String q_notice) {
		this.q_notice = q_notice;
	}
	public String getQ_startAndEnd() {
		return q_startAndEnd;
	}
	public void setQ_startAndEnd(String q_startAndEnd) {
		this.q_startAndEnd = q_startAndEnd;
	}
	public String getQ_condDesc() {
		return q_condDesc;
	}
	public void setQ_condDesc(String q_condDesc) {
		this.q_condDesc = q_condDesc;
	}
	public String getQ_stime() {
		return q_stime;
	}
	public void setQ_stime(String q_stime) {
		this.q_stime = q_stime;
	}
	public String getQ_logic() {
		return q_logic;
	}
	public void setQ_logic(String q_logic) {
		this.q_logic = q_logic;
	}
	public String getQ_expend() {
		return q_expend;
	}
	public void setQ_expend(String q_expend) {
		this.q_expend = q_expend;
	}
	public boolean isQ_onof() {
		return q_onof;
	}
	public void setQ_onof(boolean q_onof) {
		this.q_onof = q_onof;
	}
	public String getQ_group() {
		return q_group;
	}
	public void setQ_group(String q_group) {
		this.q_group = q_group;
	}
	
}
