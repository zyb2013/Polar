package com.game.data.container;

import java.util.List;

import com.game.data.bean.Q_casting_add_attributeBean;
import com.game.data.dao.Q_casting_add_attributeDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_add_attributeContainer数据容器
 */
public class Q_casting_add_attributeContainer {

	private List<Q_casting_add_attributeBean> list;
	
	private Q_casting_add_attributeDao dao = new Q_casting_add_attributeDao();
	
	public void load(){
		list = dao.select();
	}

	public List<Q_casting_add_attributeBean> getList(){
		return list;
	}
	
}