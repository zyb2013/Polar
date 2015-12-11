package com.game.data.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_hold_rewardBean;
import com.game.data.dao.Q_hold_rewardDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_hold_reward数据容器
 */
public class Q_hold_rewardContainer {

	private List<Q_hold_rewardBean> list;
	
	private HashMap<Integer, Q_hold_rewardBean> map = new HashMap<Integer, Q_hold_rewardBean>();
	
	
	private Q_hold_rewardDao dao = new Q_hold_rewardDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_hold_rewardBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_hold_rewardBean bean = (Q_hold_rewardBean) iter
					.next();
			map.put(bean.getQ_day(), bean);
		}
	}

	public List<Q_hold_rewardBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_hold_rewardBean> getMap(){
		return map;
	}
}