package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_strenghten_vipBean;
import com.game.data.dao.Q_strenghten_vipDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_exchangeContainer数据容器
 */
public class Q_strenghten_vipContainer {

	private List<Q_strenghten_vipBean> list;
	
	private HashMap<String, Q_strenghten_vipBean> map = new HashMap<String, Q_strenghten_vipBean>();
	
	private Q_strenghten_vipDao dao = new Q_strenghten_vipDao();
	
	public void load()
	{
		list = dao.select();
		Iterator<Q_strenghten_vipBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_strenghten_vipBean bean = (Q_strenghten_vipBean) iter.next();
			map.put(bean.getQ_strenghten_level(), bean);
		}
	}

	public Q_strenghten_vipBean get(String key)
	{
		return map.get(key);
	}
	
	public List<Q_strenghten_vipBean> getList(){
		return list;
	}
	
	public HashMap<String, Q_strenghten_vipBean> getMap(){
		return map;
	}
	
}