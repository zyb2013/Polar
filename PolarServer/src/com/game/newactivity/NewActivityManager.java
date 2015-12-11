package com.game.newactivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.game.backpack.structs.Item;
import com.game.data.bean.Q_newActivityBean;
import com.game.dblog.LogService;
import com.game.newactivity.AbstractActivity.GetAwardResult;
import com.game.newactivity.log.NewActivityGetAwardLog;
import com.game.newactivity.message.ResGetNewActivityAward;
import com.game.newactivity.message.ResNewActivityList;
import com.game.newactivity.model.PlayerActivityInfo;
import com.game.newactivity.model.SimpleActivityInfo;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.timer.SchedulerBean;
import com.game.timer.SchedulerManager;
import com.game.timer.SchedulerParser;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;

/**
 * @author luminghua
 *
 * @date   2014年2月21日 下午9:03:43
 */
public class NewActivityManager {

	private static NewActivityManager instance;
	private final static Object obj = new Object();
	private Logger logger = Logger.getLogger(NewActivityManager.class);
	
	private Map<Integer,AbstractActivity> activityInstancesMap = new HashMap<Integer,AbstractActivity>();
	
	
	
	public static NewActivityManager getInstance() {
		if(instance == null) {
			synchronized(obj) {
				if(instance == null) {
					instance = new NewActivityManager();
				}
			}
		}
		return instance;
	}
	
	public void init() throws Exception {
		NewActivityEnum[] values = NewActivityEnum.values();
		for(NewActivityEnum a : values) {
			Q_newActivityBean activityBean = NewActivityContainer.getInstance().get(a.getId());
			if(activityBean != null && activityBean.isQ_onof()) {
				Class<? extends AbstractActivity> get_class = a.get_class();
				AbstractActivity newInstance = get_class.newInstance();
				newInstance.setActivityBean(activityBean);
				//保存实例
				activityInstancesMap.put(a.getId(), newInstance);
				//注册
				SchedulerManager.getInstance().register(a.name(), newInstance);
				//添加调度任务
				LinkedList<SchedulerBean> parseDBString = SchedulerParser.parseDBString(activityBean.getQ_stime());
				if(CollectionUtil.isNotBlank(parseDBString)) {
					SchedulerManager.getInstance().addSchedulerBean(parseDBString.get(0));
				}else {
					logger.error("no q_stime in activity:"+activityBean.getQ_id());
				}
				newInstance.initActivity();
			}else {
				logger.info("活动没配置或者关闭："+a.name());
			}
		}
	}
	
	public void login(Player player) {
		Map<Long,Map<Integer,PlayerActivityInfo>> playerInfoMap = PlayerActivityInfoManager.getInstance().getPlayerInfoMap();
		Map<Integer, PlayerActivityInfo> map = playerInfoMap.get(player.getId());
		if(map == null) {
			map = new ConcurrentHashMap<Integer,PlayerActivityInfo>();
			playerInfoMap.put(player.getId(), map);
			//加载活动信息
			List<PlayerActivityInfo> selectByPlayerId = PlayerActivityInfoManager.getInstance().selectByPlayerId(player.getId());
			if(CollectionUtil.isNotBlank(selectByPlayerId)) {
				for(PlayerActivityInfo info : selectByPlayerId) {
					map.put(info.getActivityId(), info);
				}
			}
		}
		//发送活动列表
		if(CollectionUtil.isNotBlank(activityInstancesMap)) {
			List<SimpleActivityInfo> activities = new LinkedList<SimpleActivityInfo>();
			Iterator<AbstractActivity> iterator = activityInstancesMap.values().iterator();
			while(iterator.hasNext()) {
				AbstractActivity next = iterator.next();
				if(next.isCanVisible(player) && next.getActivityBean().isQ_onof()) {
					SimpleActivityInfo simpleInfo = new SimpleActivityInfo();
					int activityId = next.getActivityBean().getQ_id();
					//活动id
					simpleInfo.setActivityId(activityId);
					PlayerActivityInfo playerActivityInfo = map.get(activityId);
					if(playerActivityInfo != null) {
						//该活动可领奖数字
						simpleInfo.setCount((byte) playerActivityInfo.getCanAward());
					}
					activities.add(simpleInfo);
				}
			}
			ResNewActivityList msg = new ResNewActivityList();
			msg.setActivities(activities);
			MessageUtil.tell_player_message(player, msg);
		}
	}
	
	/**
	 * 活动触发
	 * 
	 * @param player
	 * @param activity
	 * @param objects
	 * 		具体附加数据
	 */
	public void trigger(Player player,NewActivityEnum activity,Object...objects) {
		try {
			AbstractActivity activityImpl = activityInstancesMap.get(activity.getId());
			if(activityImpl != null) {
				if(!activityImpl.isBetweenStartAndEnd() && activityImpl.getActivityBean().isQ_onof()) {
					logger.info(activity.name()+" 活动过期咯");
					return;
				}
				activityImpl.trigger(player, objects);
			}else {
				logger.error("没有对应活动的实例："+activity.getId());
			}
		}catch(Exception e) {
			logger.error("触发活动报错："+player.getId()+","+activity.name(), e);
		}
	}
	
	/**
	 * 获取活动信息
	 * 
	 * @param player
	 * @param id
	 */
	public void sendDetailActivityInfo(Player player,int id) {
		AbstractActivity activityImpl = activityInstancesMap.get(id);
		if(activityImpl != null) {
			activityImpl.sendDetailActivityInfo(player);
		}else {
			logger.error("没有对应活动的实例："+id);
		}
	}
	
	/**
	 * 领奖
	 * @param player
	 * @param id
	 * @param order
	 */
	public void getAward(Player player,int id,int order) {
		AbstractActivity activityImpl = activityInstancesMap.get(id);
		if(activityImpl != null && activityImpl.getActivityBean().isQ_onof()) {
			GetAwardResult result = activityImpl.getAward(player, order);
			ResGetNewActivityAward msg = new ResGetNewActivityAward();
			if(result.result == GetAwardResult.ResultType.succeed) {
				try {
					//记录日志
					NewActivityGetAwardLog log = new NewActivityGetAwardLog();
					log.setRoleId(player.getId());
					log.setActivityId(id);
					Item[] items = result.items;
					StringBuilder itemStr = new StringBuilder("");
					if(CollectionUtil.isNotBlank(items)) {
						for(Item item:items) {
							itemStr.append(item.getItemModelId()+"_"+item.getNum());
							itemStr.append(",");
						}
					}
					log.setAwards(itemStr.toString());
					LogService.getInstance().execute(log);
					//更新界面
					sendDetailActivityInfo(player,id);
				}catch(Exception e) {
					logger.error("报错："+player.getId()+","+id+","+order,e);
				}
			}
			msg.setResult((byte) result.result.ordinal());
			msg.setActivityId(id);
			if(result.items != null) {
				List<Integer> list = new LinkedList<Integer>();
				for(Item item :result.items) {
					list.add(item.getItemModelId());
				}
				msg.setItems(list);
			}
			MessageUtil.tell_player_message(player, msg);
			MessageUtil.notify_player(player, Notifys.NORMAL, result.result.desc);
		}else {
			logger.error("没有对应活动的实例或活动已过期："+id);
		}
	}
	
	public AbstractActivity getActivityImpl(int activityId) {
		return activityInstancesMap.get(activityId);
	}
	
	
//	public void sendActivityDetails(Player player,int id,int order) {
//		AbstractActivityImpl activityImpl = activityInstancesMap.get(id);
//		if(activityImpl != null) {
//			activityImpl.sendActivityDetails(player, order);
//		}else {
//			logger.error("没有对应活动的实例："+id);
//		}
//	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
}
