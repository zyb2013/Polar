package com.game.data.container;

import java.util.Iterator;
import java.util.List;

import com.game.data.bean.Q_equip_compose_appendBean;
import com.game.data.dao.Q_equip_compose_appendDao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_strengthContainer数据容器
 */
public class Q_equip_compose_appendContainer {

	private List<Q_equip_compose_appendBean> list;
	
	private Q_equip_compose_appendDao dao = new Q_equip_compose_appendDao();
	
	public void load()
	{
		list = dao.select();
		
		Iterator<Q_equip_compose_appendBean> iter = list.iterator();
		
		while(iter.hasNext())
		{
			Q_equip_compose_appendBean bean = iter.next();
			if(bean.getCompose_id() == 0)
			{
				iter.remove();
				continue;
			}
			
			bean.analysis();
		}
	}

	public Q_equip_compose_appendBean get(int composeId)
	{
		for (Q_equip_compose_appendBean bean : list) 
		{
			if(composeId == bean.getCompose_id()) return bean;
		}
		
		return null;
	}
	
}