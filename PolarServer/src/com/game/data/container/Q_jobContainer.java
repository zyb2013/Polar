package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_jobBean;
import com.game.data.dao.Q_jobDao;

/**
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 *          Q_item数据容器
 */
public class Q_jobContainer {

	private List<Q_jobBean> list;

	private HashMap<Integer, Q_jobBean> map = new HashMap<Integer, Q_jobBean>();

	private Q_jobDao dao = new Q_jobDao();

	public void load() {
		list = dao.select();
		Iterator<Q_jobBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_jobBean bean = (Q_jobBean) iter.next();
			map.put(bean.getId(), bean);
		}
	}

	public List<Q_jobBean> getList() {
		return list;
	}

	public HashMap<Integer, Q_jobBean> getMap() {
		return map;
	}
}