package com.game.data.container;

import java.util.List;

import com.game.data.bean.Q_casting_strengthBean;
import com.game.data.dao.Q_casting_strengthDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_strengthContainer数据容器
 */
public class Q_casting_strengthContainer {

	private List<Q_casting_strengthBean> list;
	
	private Q_casting_strengthDao dao = new Q_casting_strengthDao();
	
	public void load(){
		list = dao.select();
	}

	public List<Q_casting_strengthBean> getList(){
		return list;
	}
	
}