package com.game.data.container;

import java.util.List;

import com.game.data.bean.Q_casting_costBean;
import com.game.data.dao.Q_casting_costDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_costContainer数据容器
 */
public class Q_casting_costContainer {

	private List<Q_casting_costBean> list;
	
	private Q_casting_costDao dao = new Q_casting_costDao();
	
	public void load(){
		list = dao.select();
	}

	public List<Q_casting_costBean> getList(){
		return list;
	}
	
}