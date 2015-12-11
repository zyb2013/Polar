package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_lotteryBean;
import com.game.data.dao.Q_lotteryDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_lotteryContainer数据容器
 */
public class Q_lotteryContainer {

	private List<Q_lotteryBean> list;
	
	private HashMap<Integer, Q_lotteryBean> map = new HashMap<Integer, Q_lotteryBean>();

	private Q_lotteryDao dao = new Q_lotteryDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_lotteryBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_lotteryBean bean = (Q_lotteryBean) iter.next();
			map.put(bean.getQ_dlottery_id(), bean);
		}
	}

	public List<Q_lotteryBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_lotteryBean> getMap(){
		return map;
	}
}