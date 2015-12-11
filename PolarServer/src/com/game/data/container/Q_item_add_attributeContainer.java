package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.game.data.bean.Q_item_add_attributeBean;
import com.game.data.dao.Q_item_add_attributeDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_item_strength数据容器
 */
public class Q_item_add_attributeContainer {

	private List<Q_item_add_attributeBean> list;
	
	private HashMap<String, Q_item_add_attributeBean> map = new HashMap<String, Q_item_add_attributeBean>();

	private Q_item_add_attributeDao dao = new Q_item_add_attributeDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_item_add_attributeBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_item_add_attributeBean bean = (Q_item_add_attributeBean) iter.next();
			map.put(bean.getQ_id(), bean);
		}
	}

	public List<Q_item_add_attributeBean> getList(){
		return list;
	}
	
	public HashMap<String, Q_item_add_attributeBean> getMap(){
		return map;
	}
}