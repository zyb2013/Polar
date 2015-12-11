package com.game.data.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.game.utils.StringUtil;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_version Bean
 */
public class Q_equip_compose_appendBean {

	private int compose_id;	//合成ID
	private String q_equip_steplv_append;	//装备品阶影响的概率 
	//key 品阶    value 概率 1,2000|2,2000
	private List<DataValue> stagePres = new ArrayList<DataValue>();
	
	private String q_equip_strenglv;		//装备强化影响的概率
	private List<DataValue> strengLvPres = new ArrayList<DataValue>();
	
	private String q_equip_attributelv;		//装备追加影响的概率
	private List<DataValue> addLvPres = new ArrayList<DataValue>();
		
	private String q_equip_remarkablenum;	//卓越条目影响的概率
	//key 卓越条目    value 概率 25,26,27,28,29,30,31,32,33,34,35,37,37,47:1,1000|2,5000
	private List<DataValue> remarkablePres = new ArrayList<DataValue>();
	//卓越属性的类型编号列表 
	List<Integer> remarkables = new ArrayList<Integer>();
	
	private String q_equip_isluck;	//会心一击影响的概率 49,1000
	//会心一击影响的概率值 
	private int luckPre;
	//会心一击属性的类型编号
	private int luckType;
	
	private String q_equip_IGNORE_ATTACKPERCENT;	//无视一击影响的概率  44,1000
	//会心一击影响的概率值
	private int ignorePre;
	//会心一击属性的类型编号
	private int ignoreType;
	
	
	/**
	 * 数据解析 
	 * @create	hongxiao.z      2014-2-25 上午11:53:54
	 */
	public void analysis()
	{
		//品阶影响的概率解析
		String[] tempInfo = q_equip_steplv_append.split("\\|");
		for (String str : tempInfo) 
		{
			String[] temp = str.split(",");
			stagePres.add(new DataValue(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
		}
		
		sort(stagePres);
		
		//强化等级概率解析
		tempInfo = q_equip_strenglv.split("\\|");
		for (String str : tempInfo) 
		{
			String[] temp = str.split(",");
			strengLvPres.add(new DataValue(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
		}
		
		sort(strengLvPres);
		
		//追加等级概率解析
		tempInfo = q_equip_attributelv.split("\\|");
		for (String str : tempInfo) 
		{
			String[] temp = str.split(",");
			addLvPres.add(new DataValue(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
		}
		
		//从高到低
		sort(addLvPres);
		
		//解析卓越属性概率相关
		tempInfo = q_equip_remarkablenum.split(":");
		//卓越属性类型解析
		if (tempInfo.length >0 && !StringUtil.isBlank(tempInfo[0])){
			String[] types = tempInfo[0].split(",");
			for (String str : types) 
			{
				remarkables.add(Integer.parseInt(str));
			}
			
			//卓越条目概率信息解析
			if (tempInfo.length >1 && !StringUtil.isBlank(tempInfo[1])){
				String[] infos = tempInfo[1].split("\\|");
				for (String str : infos) 
				{
					String[] temp = str.split(",");
					remarkablePres.add(new DataValue(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
				}
			}
		}
		//从高到低
		sort(remarkablePres);
		
		//会心一击信息解析
		tempInfo = q_equip_isluck.split(",");
		luckType = Integer.parseInt(tempInfo[0]);
		luckPre  = Integer.parseInt(tempInfo[1]);
		
		//无视一击信息解析
		tempInfo = q_equip_isluck.split(",");
		ignoreType = Integer.parseInt(tempInfo[0]);
		ignorePre  = Integer.parseInt(tempInfo[1]);
	}
	
	/**
	 * 排序
	 * @param list
	 * @create	hongxiao.z      2014-2-25 下午2:54:31
	 */
	private void sort(List<DataValue> list)
	{
		//从高到低
		Collections.sort(list, new Comparator<DataValue>() 
		{
			@Override
			public int compare(DataValue o1, DataValue o2) 
			{
				return o2.key - o1.key;
			}
		});
	}
	
	public int getCompose_id() {
		return compose_id;
	}
	public void setCompose_id(int compose_id) {
		this.compose_id = compose_id;
	}
	public String getQ_equip_steplv_append() {
		return q_equip_steplv_append;
	}
	public void setQ_equip_steplv_append(String q_equip_steplv_append) {
		this.q_equip_steplv_append = q_equip_steplv_append;
	}
	public String getQ_equip_strenglv() {
		return q_equip_strenglv;
	}
	public void setQ_equip_strenglv(String q_equip_strenglv) {
		this.q_equip_strenglv = q_equip_strenglv;
	}
	public String getQ_equip_attributelv() {
		return q_equip_attributelv;
	}
	public void setQ_equip_attributelv(String q_equip_attributelv) {
		this.q_equip_attributelv = q_equip_attributelv;
	}
	public String getQ_equip_remarkablenum() {
		return q_equip_remarkablenum;
	}
	public void setQ_equip_remarkablenum(String q_equip_remarkablenum) {
		this.q_equip_remarkablenum = q_equip_remarkablenum;
	}
	public String getQ_equip_isluck() {
		return q_equip_isluck;
	}
	public void setQ_equip_isluck(String q_equip_isluck) {
		this.q_equip_isluck = q_equip_isluck;
	}
	public String getQ_equip_IGNORE_ATTACKPERCENT() {
		return q_equip_IGNORE_ATTACKPERCENT;
	}
	public void setQ_equip_IGNORE_ATTACKPERCENT(String q_equip_IGNORE_ATTACKPERCENT) {
		this.q_equip_IGNORE_ATTACKPERCENT = q_equip_IGNORE_ATTACKPERCENT;
	}

	/**
	 * 根据品阶等级获取加成的概率
	 * @param remarkableSize
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:16:19
	 */
	public int getStageLvPres(int stageLv)
	{
		for (DataValue value : stagePres) 
		{
			if(stageLv == value.key) return value.value;
		}

		return 0;
	}

	/**
	 * 根据强化等级获取加成的概率
	 * @param remarkableSize
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:16:19
	 */
	public int getStrengLvPres(int strengLv)
	{
		for (DataValue value : strengLvPres) 
		{
			if(strengLv == value.key) return value.value;
		}

		return 0;
	}

	/**
	 * 根据追加等级获取加成的概率
	 * @param remarkableSize
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:16:19
	 */
	public int getAddLvPres(int addLv)
	{
		for (DataValue value : addLvPres) 
		{
			if(addLv == value.key) return value.value;
		}

		return 0;
	}

	/**
	 * 根据卓越条目数获取加成的概率
	 * @param remarkableSize
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:16:19
	 */
	public int getRemarkablePres(int remarkableSize)
	{
		for (DataValue value : remarkablePres) 
		{
			if(remarkableSize == value.key) return value.value;
		}

		return 0;
	}

	/**
	 * 提供获取卓越类型的属性包含的属性类型编号
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:15:05
	 */
	public List<Integer> getRemarkables() {
		return remarkables;
	}

	/**
	 * 提供获取会心一击属性加成的概率
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:14:01
	 */
	public int getLuckPre() {
		return luckPre;
	}

	/**
	 * 提供获取会心一击属性的属性编号
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:14:01
	 */
	public int getLuckType() {
		return luckType;
	}

	/**
	 * 提供获取无视一击属性加成的概率
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:14:01
	 */
	public int getIgnorePre() {
		return ignorePre;
	}

	/**
	 * 提供获取无视一击属性的属性类型编号
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午12:14:01
	 */
	public int getIgnoreType() {
		return ignoreType;
	}
	
	public class DataValue
	{
		int key;
		int value;
		
		public DataValue(int key, int value) 
		{
			this.key = key;
			this.value = value;
		}

		public int getKey() {
			return key;
		}

		public int getValue() {
			return value;
		}
	}
}