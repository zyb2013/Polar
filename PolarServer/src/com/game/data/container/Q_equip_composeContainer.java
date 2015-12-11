package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_equip_composeBean;
import com.game.data.dao.Q_equip_composeDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_item_strength数据容器
 */
public class Q_equip_composeContainer {

	private List<Q_equip_composeBean> list;
	
	private HashMap<Integer, Q_equip_composeBean> map = new HashMap<Integer, Q_equip_composeBean>();

	private Q_equip_composeDao dao = new Q_equip_composeDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_equip_composeBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_equip_composeBean bean = (Q_equip_composeBean) iter.next();
			bean.analysis();
			map.put(bean.getCompose_id(), bean);
		}
	}

	public List<Q_equip_composeBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_equip_composeBean> getMap(){
		return map;
	}
}