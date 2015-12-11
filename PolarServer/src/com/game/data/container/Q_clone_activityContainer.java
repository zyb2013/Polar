package com.game.data.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_clone_activityBean;
import com.game.data.dao.Q_clone_activityDao;
import com.game.zones.manager.ZonesConstantConfig;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_clone_activity数据容器
 */
public class Q_clone_activityContainer {

	private List<Q_clone_activityBean> list;
	
	private HashMap<Integer, Q_clone_activityBean> map = new HashMap<Integer, Q_clone_activityBean>();
	
	//爬塔副本处理 hongxiao.z 2014.2.12
	private List<Q_clone_activityBean> towers = new ArrayList<Q_clone_activityBean>();
	
	private Q_clone_activityDao dao = new Q_clone_activityDao();
	
	public void load()
	{
		list = dao.select();
		
		towers.clear();
		
		Iterator<Q_clone_activityBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_clone_activityBean bean = (Q_clone_activityBean) iter.next();
			
			if(bean == null)
			{
				continue;
			}
			
			//爬塔副本的特殊处理
			if(bean.getQ_zone_type() == ZonesConstantConfig.PT_TYPE)
			{
				towers.add(bean);
			}
			
			map.put(bean.getQ_id(), bean);
		}
		
		//按照层排序爬塔
		Collections.sort(towers,
			new Comparator<Q_clone_activityBean>() 
			{
				@Override
				public int compare(Q_clone_activityBean o1,	Q_clone_activityBean o2) 
				{
					int result = o1.getQ_dungeon_stage() - o2.getQ_dungeon_stage();
					return result != 0 ? result : o1.getQ_dungeon_level() - o2.getQ_dungeon_level();
				}
			}
		);
	}

	public List<Q_clone_activityBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_clone_activityBean> getMap(){
		return map;
	}
	
	public Q_clone_activityBean get(int zoneId)
	{
		return map.get(zoneId);
	}
	
	/**
	 * 根据层和关卡获取爬塔的bean数据
	 * @param level				层
	 * @param stage				关卡
	 * @return					数据实体
	 * @create	hongxiao.z      2014-2-12 下午9:05:36
	 */
	public Q_clone_activityBean getTowerBean(int stage, int level)
	{
		for (Q_clone_activityBean bean : towers) 
		{
			if(bean.getQ_dungeon_level() == level && bean.getQ_dungeon_stage() == stage)
			{
				return bean;
			}
		}
		
		return null;
	}
	
	/**
	 * 根据层和关卡获取爬塔的副本ID
	 * @param level				层
	 * @param stage				关卡
	 * @return					数据实体
	 * @create	hongxiao.z      2014-2-12 下午9:05:36
	 */
	public int getTowerId(int level, int stage)
	{
		Q_clone_activityBean bean = getTowerBean(level, stage);
		
		return bean != null ? bean.getQ_id() : -1;
	}
	
	/**
	 * 获取第一层的爬塔数据
	 * @return
	 * @create	hongxiao.z      2014-2-12 下午9:09:03
	 */
	public Q_clone_activityBean getTowerMin()
	{
		return towers.get(0);
	}
}