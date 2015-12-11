package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_grade_giftBean;
import com.game.data.dao.Q_gradeGiftDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_activities_drop数据容器
 */
public class Q_gradegiftContainer {

	private List<Q_grade_giftBean> list;
	
	private HashMap<Integer, Q_grade_giftBean> map = new HashMap<Integer, Q_grade_giftBean>();

	private Q_gradeGiftDao dao = new Q_gradeGiftDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_grade_giftBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_grade_giftBean bean = (Q_grade_giftBean) iter.next();
			map.put(bean.getQ_id(), bean);
		}
	}

	public List<Q_grade_giftBean> getList(){
		return list;
	}
	
	public Q_grade_giftBean get(int giftId)
	{
		return map.get(giftId);
	}
	
	public HashMap<Integer, Q_grade_giftBean> getMap(){
		return map;
	}
}