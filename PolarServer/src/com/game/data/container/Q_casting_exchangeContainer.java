package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_casting_exchangeBean;
import com.game.data.dao.Q_casting_exchangeDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_exchangeContainer数据容器
 */
public class Q_casting_exchangeContainer {

	private List<Q_casting_exchangeBean> list;
	
	private HashMap<Integer, Q_casting_exchangeBean> map = new HashMap<Integer, Q_casting_exchangeBean>();
	
	private Q_casting_exchangeDao dao = new Q_casting_exchangeDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_casting_exchangeBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_casting_exchangeBean bean = (Q_casting_exchangeBean) iter.next();
			map.put(bean.getQ_id(), bean);
		}
	}

	public List<Q_casting_exchangeBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_casting_exchangeBean> getMap(){
		return map;
	}
	
}