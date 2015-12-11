package com.game.data.bean;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author 周江华
 * 
 * @version 1.0.0
 * 
 * 装备合成 Bean
 */
public class Q_equip_composeBean {
	private int item_type;//大类
	private String item_name;//道具名
	private int compose_id;//合成编号
	private String use_item;//材料
	private String use_not_sign_item;//非指定ID材料
	private int success_percent;//成功率
	private int money_need;//需要金额
	private int diamond_need;//需要钻石
	private String  other_result_items;//合成结果1
	private String use_not_sign_item_id;// 指定需要消耗的物品模板
	private String Q_Nonfinite_item;	// 一些可用额外材料加成概率的限制规则
	private int Q_Nonfinite_num;		//额外材料的数量限制
	private int success_percent_max;	//最高的合成成功率
	private String q_addition_prob;		//生成追加等级的概率
	
	private List<Integer> addProbs = new ArrayList<>();
	private int maxAddProb;			//最大概率
	
	public void analysis()
	{
		if(q_addition_prob == null || q_addition_prob.isEmpty()) return;
		
		String[] probs = q_addition_prob.split(";");
		for (String str : probs) 
		{
			String[] temp = str.split("\\|");
//			int lv = Integer.parseInt(temp[0]);
			int prob = Integer.parseInt(temp[1]);
			maxAddProb += prob;	//叠加概率区间
			addProbs.add(maxAddProb);
		}
	}
	
	/**
	 * 根据概率获取追加等级
	 * @param prob
	 * @return
	 * @create	hongxiao.z      2014-3-12 下午2:34:42
	 */
	public int getAddProbs(int prob)
	{
		for(int i = 0; i < addProbs.size(); i++)
		{
			Integer probEle = addProbs.get(i);
			
			if(probEle == null) continue;
					
			if(prob <= probEle) return i;
		}
		
		return  0;
	}
	
	public List<Integer> getAddProbs() {
		return addProbs;
	}

	public int getMaxAddProb() {
		return maxAddProb;
	}

	/**
	 * @return the use_not_sign_item_id
	 */
	public String getUse_not_sign_item_id() {
		return use_not_sign_item_id;
	}

	/**
	 * @param use_not_sign_item_id
	 *            the use_not_sign_item_id to set
	 */
	public void setUse_not_sign_item_id(String use_not_sign_item_id) {
		this.use_not_sign_item_id = use_not_sign_item_id;
	}
	public String getUse_not_sign_item() {
		return use_not_sign_item;
	}
	public void setUse_not_sign_item(String use_not_sign_item) {
		this.use_not_sign_item = use_not_sign_item;
	}
	public int getItem_type() {
		return item_type;
	}
	public void setItem_type(int item_type) {
		this.item_type = item_type;
	}
	
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	
	public int getCompose_id() {
		return compose_id;
	}
	public void setCompose_id(int compose_id) {
		this.compose_id = compose_id;
	}
	public String getUse_item() {
		return use_item;
	}
	public void setUse_item(String use_item) {
		this.use_item = use_item;
	}
	public int getSuccess_percent() {
		return success_percent;
	}
	public void setSuccess_percent(int success_percent) {
		this.success_percent = success_percent;
	}
	public int getMoney_need() {
		return money_need;
	}
	public void setMoney_need(int money_need) {
		this.money_need = money_need;
	}
	public int getDiamond_need() {
		return diamond_need;
	}
	public void setDiamond_need(int diamond_need) {
		this.diamond_need = diamond_need;
	}
	public String getOther_result_items() {
		return other_result_items;
	}
	public void setOther_result_items(String other_result_items) {
		this.other_result_items = other_result_items;
	}

	public String getQ_Nonfinite_item() {
		return Q_Nonfinite_item;
	}

	public void setQ_Nonfinite_item(String q_Nonfinite_item) {
		Q_Nonfinite_item = q_Nonfinite_item;
	}

	public int getQ_Nonfinite_num() {
		return Q_Nonfinite_num;
	}

	public void setQ_Nonfinite_num(int q_Nonfinite_num) {
		Q_Nonfinite_num = q_Nonfinite_num;
	}

	public int getSuccess_percent_max() {
		return success_percent_max;
	}

	public void setSuccess_percent_max(int success_percent_max) {
		this.success_percent_max = success_percent_max;
	}

	public String getQ_addition_prob() {
		return q_addition_prob;
	}

	public void setQ_addition_prob(String q_addition_prob) {
		this.q_addition_prob = q_addition_prob;
	}
}