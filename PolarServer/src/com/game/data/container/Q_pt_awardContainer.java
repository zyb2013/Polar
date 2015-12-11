package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_pt_awardBean;
import com.game.data.dao.Q_ptAwardDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_activities_drop数据容器
 */
public class Q_pt_awardContainer {

	private List<Q_pt_awardBean> list;
	
	private HashMap<Integer, Q_pt_awardBean> map = new HashMap<Integer, Q_pt_awardBean>();

	private Q_ptAwardDao dao = new Q_ptAwardDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_pt_awardBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_pt_awardBean bean = (Q_pt_awardBean) iter.next();
			bean.analysisItem();
			map.put(bean.getQ_level(), bean);
		}
	}

	public List<Q_pt_awardBean> getList(){
		return list;
	}
	
	public Q_pt_awardBean get(int level)
	{
		return map.get(level);
	}
	
	public HashMap<Integer, Q_pt_awardBean> getMap(){
		return map;
	}
}