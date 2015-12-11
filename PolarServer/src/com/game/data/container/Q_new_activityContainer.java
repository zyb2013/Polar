package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_newActivityBean;
import com.game.data.dao.Q_new_activityDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_activities数据容器
 */
public class Q_new_activityContainer {

	
	private HashMap<Integer, Q_newActivityBean> map = new HashMap<Integer, Q_newActivityBean>();

	private Q_new_activityDao dao = new Q_new_activityDao();
	
	public void load(){
		List<Q_newActivityBean> list = dao.select();
		Iterator<Q_newActivityBean> iter = list.iterator();
		HashMap<Integer, Q_newActivityBean> map = new HashMap<Integer, Q_newActivityBean>();
		while (iter.hasNext()) {
			Q_newActivityBean bean = (Q_newActivityBean) iter
					.next();
			map.put(bean.getQ_id(), bean);
		}
		this.map = map;
	}

	public Q_newActivityBean get(int activityId)
	{
		return this.map.get(activityId);
	}
	
	public HashMap<Integer, Q_newActivityBean> getMap(){
		return map;
	}
	
	public void update(Q_newActivityBean bean) {
		map.put(bean.getQ_id(), bean);
		dao.update(bean);
	}
}