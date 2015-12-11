package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.game.data.bean.Q_prayBean;
import com.game.data.dao.Q_prayDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_pray数据容器
 */
public class Q_prayContainer {

	private List<Q_prayBean> list;
	
	private HashMap<Integer, Q_prayBean> map = new HashMap<Integer, Q_prayBean>();

	private Q_prayDao dao = new Q_prayDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_prayBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_prayBean bean = (Q_prayBean) iter.next();
			map.put(bean.getQ_id(), bean);
		}
	}

	public List<Q_prayBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_prayBean> getMap(){
		return map;
	}

	public Q_prayBean getQ_prayBeanByPrayTimes(int q_pray_times) {
		List<Q_prayBean> list = getList();
		for(Q_prayBean q_prayBean : list) {
			if(q_prayBean == null) continue;
			if(q_prayBean.getQ_pray_times() == q_pray_times) {
				return q_prayBean;
			}
		}
		return null;
	}
}