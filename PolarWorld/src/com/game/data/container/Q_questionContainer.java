package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_questionBean;
import com.game.data.dao.Q_questionDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_question数据容器
 */
public class Q_questionContainer {

	private List<Q_questionBean> list;
	
	private HashMap<Integer, Q_questionBean> map = new HashMap<Integer, Q_questionBean>();

	private Q_questionDao dao = new Q_questionDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_questionBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_questionBean bean = (Q_questionBean) iter
					.next();
			map.put(bean.getQ_id(), bean);
		}
	}

	public List<Q_questionBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_questionBean> getMap(){
		return map;
	}
}