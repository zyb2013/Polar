package com.game.data.bean;
/**
 * 活跃度宝箱原型配置Bean
 * @author hongxiao.z
 * @date   2013-12-26  下午5:49:22
 */
public class Q_liveness_boxBean 
{
	/*宝箱的ID(根据宝箱ID领取对应奖励)*/
	private short q_box_id;
	
	/*宝箱名称*/
	private String q_box_name;
	
	/*领取这个宝箱的最少活跃值限制*/
	private short q_value_min;
	
	/*奖励的道具ID列表，格式（道具ID:数量），支持多个，用_隔开每个道具ID*/
	private String q_items;

	/**
	 * 宝箱的ID(根据宝箱ID领取对应奖励)
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午5:53:13
	 */
	public short getQ_box_id() {
		return q_box_id;
	}

	/**
	 * 宝箱的ID(根据宝箱ID领取对应奖励)
	 * @param q_box_id
	 * @create	hongxiao.z      2013-12-26 下午5:53:31
	 */
	public void setQ_box_id(short q_box_id) {
		this.q_box_id = q_box_id;
	}

	/**
	 * 宝箱名称
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午5:53:54
	 */
	public String getQ_box_name() {
		return q_box_name;
	}

	/**
	 * 宝箱名称
	 * @param q_box_name
	 * @create	hongxiao.z      2013-12-26 下午5:53:58
	 */
	public void setQ_box_name(String q_box_name) {
		this.q_box_name = q_box_name;
	}

	/**
	 * 领取这个宝箱的最少活跃值限制
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午5:54:05
	 */
	public int getQ_value_min() {
		return q_value_min;
	}

	/**
	 * 领取这个宝箱的最少活跃值限制
	 * @param q_value_min
	 * @create	hongxiao.z      2013-12-26 下午5:54:08
	 */
	public void setQ_value_min(short q_value_min) {
		this.q_value_min = q_value_min;
	}

	/**
	 * 奖励的道具ID列表，格式（道具ID:数量），支持多个，用_隔开每个道具ID
	 * @return
	 * @create	hongxiao.z      2013-12-26 下午5:54:16
	 */
	public String getQ_items() {
		return q_items;
	}

	/**
	 * 奖励的道具ID列表，格式（道具ID:数量），支持多个，用_隔开每个道具ID
	 * @param q_items
	 * @create	hongxiao.z      2013-12-26 下午5:54:19
	 */
	public void setQ_items(String q_items) {
		this.q_items = q_items;
	} 
}
