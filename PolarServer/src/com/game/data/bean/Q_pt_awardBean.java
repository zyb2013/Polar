package com.game.data.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 爬塔奖励领取消耗表
 * @author hongxiao.z
 * @date   2014-2-13  下午4:33:28
 */
public class Q_pt_awardBean 
{
	/**
	 * 层标识
	 */
	private int q_level;
	
	/**
	 * 奖励
	 */
	private String q_award;
	
	/**
	 * 层名称
	 */
	private String q_name;
	
//	-1 铜钱
//	-2 钻石
//	-3 真气
//	-4 经验
//	-5 绑定钻石
//	-6 精魄
	
	/**
	 * 道具列表
	 * key   道具ID
	 * value 数量
	 */
	private Map<Integer, Integer> rewards = new HashMap<Integer, Integer>();

	/**
	 * 解析道具到map
	 * @create	hongxiao.z      2014-2-13 下午5:18:01
	 */
	public void analysisItem()
	{
		String[] items = q_award.split(";");
		for (String itemInfo : items) 
		{
			if(itemInfo == null || itemInfo.isEmpty())
			{
				continue;	
			}
			
			String[] infos = itemInfo.split("_");
			
			Integer num = rewards.get(infos[0]);
			
			int itemId = Integer.parseInt(infos[0]);
			int number = Integer.parseInt(infos[1]);
			rewards.put(itemId, ((num == null ? 0 : num) + number));
		}
	}
	
	/**
	 * 获取道具列表
	 * key 道具ID
	 * value 道具数量
	 * @return
	 * @create	hongxiao.z      2014-2-13 下午5:23:57
	 */
	public Set<Entry<Integer, Integer>> gainItems()
	{
		return rewards.entrySet();
	}
	
	public int getQ_level() {
		return q_level;
	}

	public void setQ_level(int q_level) {
		this.q_level = q_level;
	}

	public String getQ_award() {
		return q_award;
	}

	public void setQ_award(String q_award) {
		this.q_award = q_award;
	}

	public String getQ_name() {
		return q_name;
	}

	public void setQ_name(String q_name) {
		this.q_name = q_name;
	}
}
