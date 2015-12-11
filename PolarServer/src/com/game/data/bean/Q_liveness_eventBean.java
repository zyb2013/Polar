package com.game.data.bean;
/**
 * 活跃事件原型Bean
 * @author hongxiao.z
 * @date   2013-12-26  下午5:49:22
 */
public class Q_liveness_eventBean 
{
	/*目标类型id（用于区分是什么类型的目标实现）*/
	private short q_type_id;
	
	/*目标执行的次数*/
	private short q_count;
	
	/*目标达成时获得的活跃度*/
	private short q_value;
	
	/*类型说明*/
	private String q_type_info;

	/**
	 * 目标类型id（用于区分是什么类型的目标实现）
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午6:42:08
	 */
	public short getQ_type_id() {
		return q_type_id;
	}

	/**
	 * 目标类型id（用于区分是什么类型的目标实现）
	 * @param q_type_id
	 * @create	hongxiao.z      2013-12-26 下午6:42:11
	 */
	public void setQ_type_id(short q_type_id) {
		this.q_type_id = q_type_id;
	}

	/**
	 * 目标执行的次数
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午6:42:16
	 */
	public int getQ_count() {
		return q_count;
	}

	/**
	 * 目标执行的次数
	 * @param q_count
	 * @create	hongxiao.z      2013-12-26 下午6:42:20
	 */
	public void setQ_count(short q_count) {
		this.q_count = q_count;
	}

	public short getQ_value() {
		return q_value;
	}

	/**
	 * 目标达成时获得的活跃度
	 * @param q_value
	 * @create	hongxiao.z      2013-12-26 下午6:42:28
	 */
	public void setQ_value(short q_value) {
		this.q_value = q_value;
	}

	/**
	 * 目标达成时获得的活跃度
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午6:42:31
	 */
	public String getQ_type_info() {
		return q_type_info;
	}

	/**
	 * 类型说明
	 * @param q_type_info
	 * @create	hongxiao.z      2013-12-26 下午6:42:37
	 */
	public void setQ_type_info(String q_type_info) {
		this.q_type_info = q_type_info;
	}
	
}
