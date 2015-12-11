package com.game.data.container;

import java.util.List;

import com.game.data.bean.Q_casting_rewardBean;
import com.game.data.dao.Q_casting_rewardDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_rewardContainer数据容器
 */
public class Q_casting_rewardContainer {

	private List<Q_casting_rewardBean> list;
	
	private Q_casting_rewardDao dao = new Q_casting_rewardDao();
	
	public void load(){
		list = dao.select();
	}

	public List<Q_casting_rewardBean> getList(){
		return list;
	}
	
}