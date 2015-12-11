package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_lost_skillBean;
import com.game.data.dao.Q_lostSkillDao;

/**
 * 遗落技能数据容器
 * @author hongxiao.z
 * @date   2014-2-18  下午5:53:16
 */
public class Q_lost_skillContainer {

	private List<Q_lost_skillBean> list;
	
	private HashMap<Integer, Q_lost_skillBean> map = new HashMap<Integer, Q_lost_skillBean>();

	private Q_lostSkillDao dao = new Q_lostSkillDao();
	
	public void load(){
		list = dao.select();
		Iterator<Q_lost_skillBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_lost_skillBean bean = (Q_lost_skillBean) iter.next();
			bean.analysis();
			map.put(bean.getQ_skill_id(), bean);
		}
	}

	public Q_lost_skillBean get(int skillId)
	{
		return map.get(skillId);
	}
	
	/**
	 * 根据技能等级和类型获取bean
	 * @param skillType
	 * @param level
	 * @return
	 * @create	hongxiao.z      2014-2-18 下午5:55:42
	 */
	public Q_lost_skillBean getByTypeLv(int skillType, int level)
	{
		for (Q_lost_skillBean bean : list) 
		{
			if(skillType == bean.getQ_skill_type() && level == bean.getQ_skill_level())
			{
				return bean;
			}
		}
		
		return null;
	}
	
	public List<Q_lost_skillBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_lost_skillBean> getMap(){
		return map;
	}
}