package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_bank_rateBean;
import com.game.data.dao.Q_bank_rateDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_bank_rate数据容器
 */
public class Q_bank_rateContainer {

	private List<Q_bank_rateBean> list;
	
	private HashMap<Integer, Q_bank_rateBean> map = new HashMap<Integer, Q_bank_rateBean>();

	private Q_bank_rateDao dao = new Q_bank_rateDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_bank_rateBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_bank_rateBean bean = (Q_bank_rateBean) iter
					.next();
			map.put(bean.getQ_level(), bean);
		}
	}

	public List<Q_bank_rateBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_bank_rateBean> getMap(){
		return map;
	}
}