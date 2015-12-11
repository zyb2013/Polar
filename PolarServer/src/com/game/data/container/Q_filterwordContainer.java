package com.game.data.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_filterwordBean;
import com.game.data.dao.Q_filterwordDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_filterword数据容器
 */
public class Q_filterwordContainer {

	private List<Q_filterwordBean> list;
	
	private HashMap<String, List<Q_filterwordBean>> map = new HashMap<String, List<Q_filterwordBean>>();

	private Q_filterwordDao dao = new Q_filterwordDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_filterwordBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_filterwordBean bean = (Q_filterwordBean) iter
					.next();
			List<Q_filterwordBean> q_filterwordBeansList = map.get(bean.getQ_country());
			if(q_filterwordBeansList == null) {
				q_filterwordBeansList = new ArrayList<Q_filterwordBean>();
				map.put(bean.getQ_country(), q_filterwordBeansList);
			}
			q_filterwordBeansList.add(bean);
		}
	}

	public List<Q_filterwordBean> getList(){
		return list;
	}
	
	public HashMap<String, List<Q_filterwordBean>> getMap(){
		return map;
	}
}