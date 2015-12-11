package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_pt_reward_consumeBean;
import com.game.data.dao.Q_ptRewardConsumeDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_activities_drop数据容器
 */
public class Q_pt_reward_consumeContainer {

	private List<Q_pt_reward_consumeBean> list;
	
	private HashMap<Integer, Q_pt_reward_consumeBean> map = new HashMap<Integer, Q_pt_reward_consumeBean>();

	private Q_ptRewardConsumeDao dao = new Q_ptRewardConsumeDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_pt_reward_consumeBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_pt_reward_consumeBean bean = (Q_pt_reward_consumeBean) iter.next();
			
			if(bean == null)
			{
				continue;
			}
			
			map.put(bean.getQ_limit(), bean);
		}
	}

	public List<Q_pt_reward_consumeBean> getList(){
		return list;
	}
	
	public Q_pt_reward_consumeBean get(int limit)
	{
		return map.get(limit);
	}
	
	public HashMap<Integer, Q_pt_reward_consumeBean> getMap(){
		return map;
	}
}