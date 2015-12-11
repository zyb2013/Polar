package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.game.data.bean.Q_activation_attributeBean;
import com.game.data.dao.Q_activation_attributeDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_activities_drop数据容器
 */
public class Q_activation_attributeContainer {

	private List<Q_activation_attributeBean> list;
	
	private HashMap<Integer, Q_activation_attributeBean> id2map = new HashMap<Integer, Q_activation_attributeBean>();
	
	private HashMap<Integer, List<Q_activation_attributeBean>> type2map = new HashMap<Integer, List<Q_activation_attributeBean>>();

	private Q_activation_attributeDao dao = new Q_activation_attributeDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_activation_attributeBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_activation_attributeBean bean = (Q_activation_attributeBean) iter
					.next();
			id2map.put(bean.getQ_id(), bean);
			List<Q_activation_attributeBean> list2 = type2map.get(bean.getQ_type());
			if(list2 == null) {
				list2 = new LinkedList<Q_activation_attributeBean>();
				type2map.put(bean.getQ_type(), list2);
			}
			list2.add(bean);
		}
	}

	public List<Q_activation_attributeBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_activation_attributeBean> getId2Map(){
		return id2map;
	}
	
	public List<Q_activation_attributeBean> getListByType(int type){
		return type2map.get(type);
	}
}