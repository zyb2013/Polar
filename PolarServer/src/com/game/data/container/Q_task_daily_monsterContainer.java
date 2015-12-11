package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.game.data.bean.Q_task_daily_monsterBean;
import com.game.data.dao.Q_task_daily_monsterDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 */
public class Q_task_daily_monsterContainer {

	private List<Q_task_daily_monsterBean> list;
	
	private HashMap<Integer, List<Q_task_daily_monsterBean>> levelMap = new HashMap<Integer, List<Q_task_daily_monsterBean>>();
	private HashMap<Integer,Q_task_daily_monsterBean> idMap = new HashMap<Integer,Q_task_daily_monsterBean>();
	private Q_task_daily_monsterDao dao = new Q_task_daily_monsterDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_task_daily_monsterBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_task_daily_monsterBean bean = (Q_task_daily_monsterBean) iter.next();
			List<Q_task_daily_monsterBean> list2 = levelMap.get(bean.getQ_level());
			if(list2 == null) {
				list2 = new LinkedList<Q_task_daily_monsterBean>();
				levelMap.put(bean.getQ_level(), list2);
			}
			list2.add(bean);
			idMap.put(bean.getQ_id(), bean);
		}
	}

	public List<Q_task_daily_monsterBean> getList(){
		return list;
	}
	
	public List<Q_task_daily_monsterBean> getBeanByLevel(int level){
		return levelMap.get(level);
	}
	
	public Q_task_daily_monsterBean getBeanById(int id) {
		return idMap.get(id);
	}
}