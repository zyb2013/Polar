package com.game.data.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.game.data.bean.Q_task_mainBean;
import com.game.data.dao.Q_task_changejobDao;
import com.game.data.dao.Q_task_mainDao;
import com.game.utils.CollectionUtil;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_task_main数据容器
 */
public class Q_task_mainContainer {

	private List<Q_task_mainBean> list;
	
	private HashMap<Integer, Q_task_mainBean> map = new HashMap<Integer, Q_task_mainBean>();
	//等级触发的任务
	private Map<Integer, List<Q_task_mainBean>> levelMap = new HashMap<Integer, List<Q_task_mainBean>>();

	private Q_task_mainDao dao = new Q_task_mainDao();
	private Q_task_changejobDao changejobDao = new Q_task_changejobDao();
	
	public void load(){
		list = dao.select();
		List<Q_task_mainBean> changeJobList = changejobDao.select();
		if(CollectionUtil.isNotBlank(changeJobList)) {
			list.addAll(changeJobList);
		}
		Iterator<Q_task_mainBean> iter = list.iterator();
		while (iter.hasNext()) {
			Q_task_mainBean bean = (Q_task_mainBean) iter
					.next();
			map.put(bean.getQ_taskid(), bean);
			if(bean.getQ_accept_level() > 0) {
				List<Q_task_mainBean> levelList = levelMap.get(bean.getQ_accept_level());
				if(levelList == null) {
					levelList = new LinkedList<Q_task_mainBean>();
					levelMap.put(bean.getQ_accept_level(), levelList);
				}
				levelList.add(bean);
			}
		}
	}

	public List<Q_task_mainBean> getList(){
		return list;
	}
	
	public HashMap<Integer, Q_task_mainBean> getMap(){
		return map;
	}
	
	public LinkedList<Q_task_mainBean> getLevelTaskByLevel(int level){
		return (LinkedList<Q_task_mainBean>) levelMap.get(level);
	}
}