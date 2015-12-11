package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_fractionBean;
import com.game.data.dao.Q_fractionDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_fractionContainer数据容器
 */
public class Q_fractionContainer {

	private List<Q_fractionBean> list;
	
	private HashMap<Integer, Q_fractionBean> map = new HashMap<Integer, Q_fractionBean>();

	private Q_fractionDao dao = new Q_fractionDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_fractionBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_fractionBean bean = (Q_fractionBean) iter.next();
			map.put(bean.getQ_dfraction_id(), bean);
		}
	}

	public List<Q_fractionBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_fractionBean> getMap(){
		return map;
	}
}