package com.game.data.container;

import java.util.List;

import com.game.data.bean.Q_activity_monstersBean;
import com.game.data.dao.Q_activity_monstersDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_activity_monsters数据容器
 */
public class Q_activity_monstersContainer {

	private List<Q_activity_monstersBean> list;
	

	private Q_activity_monstersDao dao = new Q_activity_monstersDao();
	
	public void load(){
		list = dao.select();
	}

	public List<Q_activity_monstersBean> getList(){
		return list;
	}
	
	
}