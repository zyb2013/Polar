package com.game.data.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.game.json.JSONserializable;
import com.game.utils.StringUtil;

/**
 * 遗落技能bean
 * @author hongxiao.z
 * @date   2014-2-18  下午5:45:34
 */
public class Q_lost_skillBean {

	//遗落技能唯一ID
	private int q_skill_id;
	
	//遗落技能类型
	private int q_skill_type;
	
	//遗落技能等级
	private int q_skill_level;
	
	//遗落技能学习前置等级
	private int q_need_level;
	
	//遗落技能学习前置信息 道具ID:数量,道具ID:数量,道具ID:数量    如果是货币，按照原有的-1 -2 -3的配法ID
	private String q_need_info;
	
	//加成的属性信息
	//[生命上限:值,魔法上限:值]
	private String q_add_attr;
	
	//! 技能名
	private String q_skill_name;
	
	//需求道具
	private List<int[]> needItems = new ArrayList<int[]>();
	
	//加成的属性
	private HashMap<String, Integer> attrs;
	
	public List<int[]> getNeedItems() {
		return needItems;
	}
	
	//需求道具格式解析
	@SuppressWarnings("unchecked")
	public void analysis()
	{
		String[] temps = this.q_need_info.split(";");
		for (String string : temps) 
		{
			int[] element = new int[2];
			String[] elements = string.split("_");
			element[0] = Integer.parseInt(elements[0]);
			element[1] = Integer.parseInt(elements[1]);
			needItems.add(element);
		}
		
		attrs = (HashMap<String, Integer>) JSONserializable.toObject(StringUtil.formatToJson(this.q_add_attr), HashMap.class);
	}

	public int getQ_skill_id() {
		return q_skill_id;
	}

	public void setQ_skill_id(int q_skill_id) {
		this.q_skill_id = q_skill_id;
	}

	public int getQ_skill_type() {
		return q_skill_type;
	}

	public void setQ_skill_type(int q_skill_type) {
		this.q_skill_type = q_skill_type;
	}

	public int getQ_skill_level() {
		return q_skill_level;
	}

	public void setQ_skill_level(int q_skill_level) {
		this.q_skill_level = q_skill_level;
	}

	public int getQ_need_level() {
		return q_need_level;
	}

	public void setQ_need_level(int q_need_level) {
		this.q_need_level = q_need_level;
	}

	public String getQ_need_info() {
		return q_need_info;
	}

	public void setQ_need_info(String q_need_info) {
		this.q_need_info = q_need_info;
	}

	public String getQ_add_attr() {
		return q_add_attr;
	}

	public void setQ_add_attr(String q_add_attr) {
		this.q_add_attr = q_add_attr;
	}
	
	public String getQ_skill_name(){
		return this.q_skill_name;
	}
	
	public void setQ_skill_name(String q_skill_name){
		this.q_skill_name = q_skill_name;
	}

	public Set<Entry<String, Integer>> getAttrs() {
		return attrs.entrySet();
	}
}