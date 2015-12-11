package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_PandoraBean;
import com.game.data.dao.Q_pandoraDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_gift数据容器
 */
public class Q_pandoraContainer {

	private List<Q_PandoraBean> list;
	
	private HashMap<Integer, Q_PandoraBean> map = new HashMap<Integer, Q_PandoraBean>();

	private Q_pandoraDao dao = new Q_pandoraDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_PandoraBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_PandoraBean bean = (Q_PandoraBean) iter
					.next();
			map.put(bean.getQ_id(), bean);
		}
	}

	public List<Q_PandoraBean> getList() {
		return list;
	}
	
	public HashMap<Integer, Q_PandoraBean> getMap() {
		return map;
	}
}