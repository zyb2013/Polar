package com.game.newactivity;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.game.data.bean.Q_newActivityBean;
import com.game.data.manager.DataManager;
import com.game.newactivity.message.ResAddActivityMessage;
import com.game.newactivity.message.ResRemoveActivityMessage;
import com.game.newactivity.model.SimpleActivityInfo;
import com.game.timer.SchedulerBean;
import com.game.timer.SchedulerManager;
import com.game.timer.SchedulerParser;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;


/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午4:05:50
 */
public class NewActivityContainer {
	
	private static NewActivityContainer instance;
	private final static Object obj = new Object();
	private Logger logger = Logger.getLogger(NewActivityContainer.class);
	
	
	public static NewActivityContainer getInstance() {
		if(instance == null) {
			synchronized(obj) {
				if(instance == null) {
					instance = new NewActivityContainer();
				}
			}
		}
		return instance;
	}
	
	public Q_newActivityBean get(int id) {
		return DataManager.getInstance().q_newActivityContainer.get(id);
	}
	
	public Collection<Q_newActivityBean> getAll() {
		return DataManager.getInstance().q_newActivityContainer.getMap().values();
	}

	public boolean alterBean(Q_newActivityBean newBean) {
		Q_newActivityBean oldBean = DataManager.getInstance().q_newActivityContainer.get(newBean.getQ_id());
		if(oldBean == null) {
			logger.error("no activity:"+newBean.getQ_id());
			return false;
		}
		DataManager.getInstance().q_newActivityContainer.update(newBean);
		AbstractActivity activityImpl = NewActivityManager.getInstance().getActivityImpl(newBean.getQ_id());
		activityImpl.setActivityBean(newBean);
		activityImpl.initActivity();
		NewActivityEnum activityEnum = NewActivityEnum.getByActivityId(newBean.getQ_id());
		if(oldBean.isQ_onof() && !newBean.isQ_onof()) {
			try {
				SchedulerManager.getInstance().shutTask(activityEnum.name());
				ResRemoveActivityMessage msg = new ResRemoveActivityMessage();
				msg.setActivityId(newBean.getQ_id());
				MessageUtil.tell_world_message(msg);
			} catch (SchedulerException e) {
				logger.error("wrong with activity:"+newBean.getQ_id(),e);
				return false;
			}
			return true;
		}else if(!oldBean.isQ_onof() && newBean.isQ_onof()) {
			//注册
			try {
				SchedulerManager.getInstance().register(activityEnum.name(), activityImpl);
				//添加调度任务
				LinkedList<SchedulerBean> parseDBString = SchedulerParser.parseDBString(newBean.getQ_stime());
				if(CollectionUtil.isNotBlank(parseDBString)) {
					SchedulerManager.getInstance().addSchedulerBean(parseDBString.get(0));
				}else {
					logger.error("no q_stime in activity:"+newBean.getQ_id());
				}
				ResAddActivityMessage msg = new ResAddActivityMessage();
				msg.setInfo(new SimpleActivityInfo(newBean.getQ_id(),0));
				MessageUtil.tell_world_message(msg);
			} catch (Exception e) {
				logger.error("wrong with activity:"+newBean.getQ_id(),e);
				return false;
			}
			return true;
		}else if(oldBean.isQ_onof() && newBean.isQ_onof() && !oldBean.getQ_stime().equals(newBean.getQ_stime())) {
			try {
				SchedulerManager.getInstance().shutTask(activityEnum.name());
				SchedulerManager.getInstance().register(activityEnum.name(), activityImpl);
				//添加调度任务
				LinkedList<SchedulerBean> parseDBString = SchedulerParser.parseDBString(newBean.getQ_stime());
				if(CollectionUtil.isNotBlank(parseDBString)) {
					SchedulerManager.getInstance().addSchedulerBean(parseDBString.get(0));
				}else {
					logger.error("no q_stime in activity:"+newBean.getQ_id());
				}
			} catch (Exception e) {
				logger.error("wrong with activity:"+newBean.getQ_id(),e);
				return false;
			}
			return true;
		}
		return true;
	}
	
}
