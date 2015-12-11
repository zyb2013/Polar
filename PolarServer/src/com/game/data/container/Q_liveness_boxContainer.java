package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_liveness_boxBean;
import com.game.data.dao.Q_liveness_boxDao;

/**
 * 活跃度系统原型库
 * @author hongxiao.z
 * @date   2013-12-26  下午6:01:21
 */
public class Q_liveness_boxContainer {

	private List<Q_liveness_boxBean> list;
	
	private HashMap<Short, Q_liveness_boxBean> map = new HashMap<Short, Q_liveness_boxBean>();

	private Q_liveness_boxDao dao = new Q_liveness_boxDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_liveness_boxBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_liveness_boxBean bean = (Q_liveness_boxBean) iter
					.next();
			map.put(bean.getQ_box_id(), bean);
		}
	}

	public List<Q_liveness_boxBean> getList(){
		return list;
	}
	
	public HashMap<Short, Q_liveness_boxBean> getMap(){
		return map;
	}
}