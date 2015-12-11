package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_liveness_eventBean;
import com.game.data.dao.Q_liveness_eventDao;

/**
 * 活跃度宝箱原型库
 * @author hongxiao.z
 * @date   2013-12-26  下午6:01:21
 */
public class Q_liveness_eventContainer {

	private List<Q_liveness_eventBean> list;
	
	private HashMap<Short, Q_liveness_eventBean> map = new HashMap<Short, Q_liveness_eventBean>();

	private Q_liveness_eventDao dao = new Q_liveness_eventDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_liveness_eventBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_liveness_eventBean bean = (Q_liveness_eventBean) iter.next();
			map.put(bean.getQ_type_id(), bean);
		}
	}

	public List<Q_liveness_eventBean> getList(){
		return list;
	}
	
	public HashMap<Short, Q_liveness_eventBean> getMap(){
		return map;
	}
}