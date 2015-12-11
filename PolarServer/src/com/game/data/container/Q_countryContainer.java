package com.game.data.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_countryBean;
import com.game.data.dao.Q_countryDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_country数据容器
 */
public class Q_countryContainer {

	private List<Q_countryBean> list;
	
	private HashMap<Integer, Q_countryBean> map = new HashMap<Integer, Q_countryBean>();
	
	
	private Q_countryDao dao = new Q_countryDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_countryBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_countryBean bean = (Q_countryBean) iter
					.next();
			map.put(bean.getQ_lv(), bean);
		}
	}

	public List<Q_countryBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_countryBean> getMap(){
		return map;
	}
}