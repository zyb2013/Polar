package com.game.data.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_csysBean;
import com.game.data.dao.Q_csysDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_csys数据容器
 */
public class Q_csysContainer {

	private List<Q_csysBean> list;
	
	private HashMap<String, Q_csysBean> map = new HashMap<String, Q_csysBean>();
	HashMap<Integer, Q_csysBean> killlist = new HashMap<Integer, Q_csysBean>();
	
	
	private int max=0;

	private Q_csysDao dao = new Q_csysDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_csysBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_csysBean bean = (Q_csysBean) iter
					.next();
			map.put(bean.getQ_value(), bean);
			if(bean.getQ_type()==4){
				killlist.put(Integer.parseInt(bean.getQ_value()), bean);
				if(Integer.parseInt(bean.getQ_value())>max){
					max = Integer.parseInt(bean.getQ_value());
				}
			}
		}
	}

	public List<Q_csysBean> getList(){
		return list;
	}
	
	public HashMap<String, Q_csysBean> getMap(){
		return map;
	}
	
	public int getIntegralByPlayer(int kill) {
		Q_csysBean bean = killlist.get(kill);
		
		if(bean!=null){
			return bean.getQ_integral();
		}
		return  killlist.get(max).getQ_integral();
	}
}