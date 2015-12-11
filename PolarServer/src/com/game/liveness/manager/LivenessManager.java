package com.game.liveness.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.count.structs.CountTypes;
import com.game.data.bean.Q_liveness_boxBean;
import com.game.data.bean.Q_liveness_eventBean;
import com.game.data.manager.DataManager;
import com.game.db.bean.LivenessBean;
import com.game.dblog.LogService;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.liveness.log.LivenessLog;
import com.game.liveness.message.ResEventChanageToClientMessage;
import com.game.liveness.message.ResGainBoxToClientMessage;
import com.game.liveness.message.ResGainStateToClientMessage;
import com.game.liveness.message.ResLivenessEventsToClientMessage;
import com.game.liveness.message.ResLivenessToClientMessage;
import com.game.liveness.structs.EventInfo;
import com.game.liveness.structs.LivenessInfo;
import com.game.liveness.structs.StateInfo;
import com.game.manager.ManagerPool;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;
import com.game.utils.VersionUpdateUtil;
import com.game.vip.manager.VipManager;

/**
 * 活跃度管理类
 * @author hongxiao.z
 * @date   2013-12-26  下午3:09:14
 */
public class LivenessManager
{
	protected static Logger logger = Logger.getLogger(LivenessManager.class);

	private static Object obj = new Object();
	
//	/**
//	 * key   角色ID
//	 * value 角色活跃度模块信息
//	 */
//	private static Map<Long, LivenessInfo> allLiveness = new HashMap<Long, LivenessInfo>();
	
	/**
	 * 获取角色活跃度数据
	 * @param playerid	角色ID
	 * @return
	 * @create	hongxiao.z      2013-12-27 下午8:18:08
	 */
//	public static LivenessInfo gainLivenessInfo(long playerid)
//	{
//		return allLiveness.get(playerid);
//	}
	
	/**
	 * 装载一个活跃度模块信息到内存
	 * @param playerid		角色ID
	 * @param info			活跃度模块信息
	 * @create	hongxiao.z      2013-12-27 下午8:19:19
	 */
//	public static void loadLiveness(long playerid, LivenessInfo info)
//	{
//		allLiveness.put(playerid, info);
//	}
	
	//玩家管理类实例
	private static LivenessManager manager;
	public static LivenessManager getInstance(){
		synchronized (obj) 
		{
			if(manager == null)
			{
				manager = new LivenessManager();
			}
		}
		return manager;
	}
	
	private LivenessManager(){}
	
	/**
	 * 获取指定角色当前的活跃度
	 * @param player			玩家角色
	 * @create	hongxiao.z      2013-12-26 下午3:07:18
	 */
	public void gainLiveness(Player player)
	{
//		ResLivenessToClientMessage msg = new ResLivenessToClientMessage((short)gainLivenessInfo(player.getId()).getLiveness()); 
		ResLivenessToClientMessage msg = new ResLivenessToClientMessage((short)player.getLiveness()); 
		MessageUtil.tell_player_message(player, msg);	//返回信息
	}
	
	/**
	 * 获取活跃事件列表信息
	 * @param player
	 * @create	hongxiao.z      2013-12-26 下午10:17:41
	 */
	public void gainEvents(Player player)
	{
		ResLivenessEventsToClientMessage msg = new ResLivenessEventsToClientMessage(player.getEvents());
		MessageUtil.tell_player_message(player, msg);	//返回信息
	}
	
	/**
	 * 获取宝箱领取状态信息
	 * @param player
	 * @create	hongxiao.z      2013-12-26 下午10:17:41
	 */
	public void gainBoxState(Player player)
	{
		ResGainStateToClientMessage msg = new ResGainStateToClientMessage(player.getGainStates());
		MessageUtil.tell_player_message(player, msg);	//返回信息
	}
	
	/**
	 * 角色上线时加载活跃度数据
	 * @param player			角色实体
	 * @create	hongxiao.z      2013-12-26 下午8:45:39
	 */
	public void onlineLoad(Player player)
	{
//		//从DB加载记录数据
//		LivenessBean record = dao.select(player.getId());
//		//角色绑定的数据
//		LivenessInfo info = new LivenessInfo();
//		
//		//DB无记录则创建初始化
//		if(record == null)
//		{
//			//新建数据条目插入DB
//			dao.insert(gainRecord(player.getId(), info));
//		}
//		else	//解析转换绑定数据
//		{
//			transform(record, info);
//		}
		
//		if(record != null)
//		{
		
		//查看是否还有领取次数
		int count = (int)ManagerPool.countManager.getCount(player, CountTypes.LIVENESS_RESET, null);
		
		if(count == 0)	//重置计数不等于0
		{
			reset(player, false);
		}
//			Calendar calendar = Calendar.getInstance();
//			
//			//求出最近上线时间
//			calendar.setTimeInMillis(player.getLoginTime());
//			int year1 = calendar.get(Calendar.YEAR);
//			int day1 = calendar.get(Calendar.DAY_OF_YEAR);
//
//			//求出最后下线时间
//			calendar.setTimeInMillis(player.getLogoutTime());
//			int year2 = calendar.get(Calendar.YEAR);
//			int day2 = calendar.get(Calendar.DAY_OF_YEAR);
//
//			//如果不是同一天，重置数据
//			if (!(year1 == year2 && day1 == day2)) 
//			{
//				reset(player, false);
//			}
//		}
		
//		allLiveness.put(player.getId(), info);
//		player.setLivenessInfo(info);	//绑定数据到角色身上
//		logger.error(" >>>>>> 加载活跃度数据！！！");
//		System.out.println(" >>>>>> 加载活跃度数据！！！");
	}
	
	/**
	 * 领取宝箱奖励
	 * @param player	角色实体
	 * @param boxid		目标宝箱ID
	 * @create	hongxiao.z      2013-12-26 下午10:31:55
	 */
	public void gainBox(Player player, short boxid) 
	{
		//获取这个宝箱的领取记录状态
		StateInfo state = gainStateInfo(player, boxid);
		//填补数据
		if(state == null)
		{
			state = new StateInfo();
			state.setBoxid(boxid);
			state.setGain((short)0);
			player.getGainStates().add(state);
		}
		
		//已经领取过这个宝箱奖励
		if(state.getGain() != 0)
		{
			MessageUtil.tell_player_message(player, new ResGainBoxToClientMessage(boxid, (byte)1));	//返回操作异常
			return;
		}
		
		//获取宝箱数据原型
		Q_liveness_boxBean model = DataManager.getInstance().q_liveness_boxContainer.getMap().get(boxid);
		
		if(model == null)	//数据原型缺失
		{
			return;
		}
		
		//活跃度不满足最小值领取条件
		if(player.getLiveness() < model.getQ_value_min())
		{
			MessageUtil.tell_player_message(player, new ResGainBoxToClientMessage(boxid, (byte)1));	//返回操作异常
			return;
		}
		
		//奖励的道具ID列表
		String itemsInfo = model.getQ_items();
		//解析所有道具数据
		String[] itemInfo = itemsInfo.split("_");
		
		//声明道具容器装载道具
		List<Item> items = new ArrayList<Item>();
		
		//循环创建每个道具
		for (String iInfo : itemInfo) 
		{
			//解析获得道具原型ID和数量 下标0为道具id，1为数量
			String[] itemAndCount = iInfo.split(":");
			List<Item> temps = Item.createItems(Integer.parseInt(itemAndCount[0]), Integer.parseInt(itemAndCount[1]), true, 0);
			items.addAll(temps);
		}
		
		//获取入包所需容量空间
		int emptynum = BackpackManager.getInstance().getEmptyGridNum(player);
		//背包已满
		if(items.size() > emptynum)
		{
			MessageUtil.notify_player(player, 
					Notifys.ERROR,ResManager.getInstance().getString("很抱歉，包裹空间已满，还需要{1}个空格子才可以进行此操作"), String.valueOf(items.size() - emptynum));
			MessageUtil.tell_player_message(player, new ResGainBoxToClientMessage(boxid, (byte)1));	//返回操作异常
			return;
		}
		
		//标记该宝箱已领取
		state.setGain((short)(state.getGain() + 1));
		
		//道具入包
		BackpackManager.getInstance().addItems(player, items, Reasons.LIVENESSS_REWARD, 0);
		ResGainBoxToClientMessage msg = new ResGainBoxToClientMessage(boxid, (byte)0);
		MessageUtil.tell_player_message(player, msg);	//返回操作成功
		
		PlayerManager.getInstance().updatePlayer(player);
		
		//日志记录
		try {
			LivenessLog log = new LivenessLog(player);
			log.setContent(itemsInfo);
			log.setBoxid(boxid);
			LogService.getInstance().execute(log);
		} catch (Exception e) {
			logger.error(e, e);
		}
		
		logger.error(new StringBuilder("[领取活跃度宝箱奖励][玩家ID:").append(player.getId())
				.append("]-[宝箱ID:").append(boxid).append("]-[奖励信息:").append(itemsInfo).append("]").toString());
	}
	
	/**
	 * 通过角色记录的活跃度实体数据获取DBbean实体
	 * @param roleid		角色id
	 * @param info			角色记录的活跃度实体数据
	 * @return				DBbean实体
	 * @create	hongxiao.z      2013-12-26 下午9:58:36
	 */
	LivenessBean gainRecord(long roleid, LivenessInfo info)
	{
		LivenessBean record = new LivenessBean();
		record.setRoleid(roleid);
		record.setLiveness(info.getLiveness());
		
		String stateJson = JSONserializable.toString(info.getGainStates());
		record.setGain_state(VersionUpdateUtil.dateSave(stateJson)); 
		
		String eventJson = JSONserializable.toString(info.getEvents());
		record.setEvents(VersionUpdateUtil.dateSave(eventJson)); 
		return record;
	}
	
	/**
	 * 将DBbean实体转换为角色记录的活跃度实体数据
	 * @param record			数据源
	 * @param info				目标数据
	 * @create	hongxiao.z      2013-12-26 下午10:04:22
	 */
	@SuppressWarnings("unchecked")
	void transform(LivenessBean record, LivenessInfo info)
	{
		try
		{
			info.setLiveness(record.getLiveness());
			String stateJson =  VersionUpdateUtil.dateLoad(record.getGain_state());
			info.setGainStates((ArrayList<StateInfo>)JSONserializable.toList(stateJson, StateInfo.class));
			
			String eventJson =  VersionUpdateUtil.dateLoad(record.getEvents());
			info.setEvents((ArrayList<EventInfo>)JSONserializable.toList(eventJson, EventInfo.class));
		}
		catch (Exception e) 
		{
			logger.error(e, e);
		}
	}
	
	/**
	 * 触发事件
	 * @param eventid
	 * @create	hongxiao.z      2013-12-27 上午10:27:03
	 */
	private void triggerEvent(Player player, short eventid, int num)
	{
		Q_liveness_eventBean model = DataManager.getInstance().q_liveness_eventContainer.getMap().get(eventid);
		
		//事件原型获取不到
		if(model == null)
		{
			logger.error("[活跃度事件触发][事件:" + eventid + "]的事件原型不存在, 事件触发执行被终止！");
			return;
		}
		
		//获取事件信息记录
		EventInfo event = gainEventInfo(player, eventid);
		
		//记录填充
		if(event == null)	
		{
			event = new EventInfo();
			event.setEventid(eventid);
			player.getEvents().add(event);
		}
		
		//已经触发过
		if(event.isTrigger()) return;
		
		//事件目标达成
//		if(model.getQ_count() <= event.getCount())
//		{
//			return;
//		}
		
		int endCount = num <= 0 ? 1 : num;
		
		//事件记录更新
		event.setCount((short)(event.getCount() + endCount));
		
		//推送数据到前端
		ResEventChanageToClientMessage msg = new ResEventChanageToClientMessage();
		msg.setEventId(eventid);
		msg.setCount(event.getCount());
		MessageUtil.tell_player_message(player, msg);	
		
		if(event.getCount() > model.getQ_count())
		{
			event.setCount((short)model.getQ_count());
		}
		
		//目标达成则奖励活跃度
		if(event.getCount() == model.getQ_count())	//暂时注释，方便调试
		{
			event.setTrigger(true);
			
			//更新活跃度
			int addliveness = model.getQ_value();
			
			if(EventType.getType(eventid) == EventType.CWVIP)	//VIP事件特殊处理，引用VIP提供的
			{
				addliveness = VipManager.getInstance().getActiveAddition(player);
			}
			
			updateLiveness(player, addliveness);
		}
	}
	
	/**
	 * 活跃度更新
	 * @param player
	 * @param addLiveness		增加的活跃度
	 * @create	hongxiao.z      2013-12-27 上午10:47:28
	 */
	public void updateLiveness(Player player, int addLiveness)
	{
		player.setLiveness(player.getLiveness() + addLiveness);
		//推送活跃度
		ResLivenessToClientMessage mssg = new ResLivenessToClientMessage((short)player.getLiveness());
		MessageUtil.tell_player_message(player, mssg);
		logger.error(new StringBuilder("[活跃度更新][玩家ID:").append(player.getId())
				.append("]-[增加的活跃度:").append(addLiveness).append("]-[当前活跃度:").append(mssg.getLiveness()).append("]").toString());
	}
	
	/**
	 * 执行事件一次
	 * @param player
	 * @param eventid			事件ID
	 * @create	hongxiao.z      2013-12-28 下午5:02:55
	 */
	public void exec(Player player, short eventid)
	{
		EventType type = EventType.getType(eventid);
		if(type == null)
		{
			return;
		}
		
//		type.exec(player);
	}
	
	/**
	 * 重置调用
	 * @param player
	 * @param bool		true为在线重置， false为上线重置
	 * @create	hongxiao.z      2013-12-27 上午11:09:27
	 */
	public void reset(Player player, boolean bool)
	{
		//VIP加成点数
		player.setLiveness(0);
		player.getGainStates().clear();
		player.getEvents().clear();
		
		//调用VIP事件
		if(VipManager.getInstance().isVIP(player))
		{
			changeVip(player);
		}
		
		//在线重置通知
		if(bool)
		{
			gainBoxState(player);
			gainEvents(player);
			gainLiveness(player);
		}
		
		ManagerPool.countManager.addCount(player, CountTypes.LIVENESS_RESET, null, 1, 1, 1);
//		dao.updateAll(gainRecord(player.getId(), info));
	}
	
	////////////////////////////////////////////////
	//-----------------外部调用接口-------------------//
	////////////////////////////////////////////////
	/**
	 * 击杀怪物调用
	 * @param player		角色实体
	 * @create	hongxiao.z      2013-12-27 上午9:52:01
	 */
	public void killMonster(Player player)
	{
		triggerEvent(player, EventType.JSGW.getCode(), 0);
	}
	
	/**
	 * 微端调用
	 * @param player
	 * @create	hongxiao.z      2014-3-10 下午3:20:45
	 */
	public void microPort(Player player)
	{
		triggerEvent(player, EventType.WDDL.getCode(), 0);
	}
	
	/**
	 * 击杀精英怪物调用
	 * @param player		角色实体
	 * @create	hongxiao.z      2013-12-27 上午9:52:01
	 */
	public void killEliteMonster(Player player)
	{
		triggerEvent(player, EventType.JSJYGW.getCode(), 0);
	}
	
	/**
	 * 组队通关恶魔广场调用
	 * @param player		角色实体
	 * @create	hongxiao.z      2013-12-27 上午9:52:01
	 */
	public void completeEmgc(Player player)
	{
		triggerEvent(player, EventType.ZDEMGC.getCode(), 0);
	}
	
	/**
	 * 通关爬塔调用
	 * @param player		角色实体
	 * @create	hongxiao.z      2013-12-27 上午9:52:01
	 */
	public void completePT(Player player)
	{
		triggerEvent(player, EventType.PT.getCode(), 0);
	}
	
	/**
	 * 组队通关血色城堡调用
	 * @param player		角色实体
	 * @create	hongxiao.z      2013-12-27 上午9:52:01
	 */
	public void completeXscb(Player player)
	{
		triggerEvent(player, EventType.ZDTGXSCB.getCode(), 0);
	}
	
	/**
	 * 完成日常任务调用
	 * @param player		角色实体
	 * @create	hongxiao.z      2013-12-27 上午9:52:01
	 */
	public void completeRc(Player player)
	{
		triggerEvent(player, EventType.WCRCRW.getCode(), 0);
	}
	
	/**
	 * 铸造工厂调用
	 * @param player
	 * @create	hongxiao.z      2013-12-27 上午10:53:41
	 */
	public void expenseZZGC(Player player)
	{
		triggerEvent(player, EventType.ZZGC.getCode(), 0);
	}
	
	/**
	 * 强化装备调用
	 * @param player
	 * @create	hongxiao.z      2013-12-27 上午10:53:41
	 */
	public void expenseQH(Player player)
	{
		triggerEvent(player, EventType.QH.getCode(), 0);
	}
	
	/**
	 * 赤色要塞
	 * @param player
	 * @create	hongxiao.z      2014-3-10 下午3:37:13
	 */
	public void expenseCSYX(Player player)
	{
		triggerEvent(player, EventType.CSYX.getCode(), 0);
	}
	
	/**
	 * 消费绑钻调用(2014.3.10更改为每天累计)
	 * @param player
	 * @create	hongxiao.z      2013-12-27 上午10:53:41
	 */
	public void expenseBindGold(Player player, int num)
	{
		triggerEvent(player, EventType.XFBZ.getCode(), Math.abs(num));
	}
	
	/**
	 * 钻石寻宝调用
	 * @param player
	 * @create	hongxiao.z      2013-12-27 上午10:53:41
	 */
	public void huntTreasure(Player player)
	{
		triggerEvent(player, EventType.ZSXB.getCode(), 0);
	}
	
	/**
	 * 金币祈愿调用
	 * @param player
	 * @create	hongxiao.z      2013-12-27 上午10:53:41
	 */
	public void raffle(Player player)
	{
		triggerEvent(player, EventType.JBCJ.getCode(), 0);
	}
	
	/**
	 * 成为VIP调用
	 * @param player
	 * @create	hongxiao.z      2013-12-27 上午10:53:41
	 */
	public void changeVip(Player player)
	{
		//VIP加成点数
		triggerEvent(player, EventType.CWVIP.getCode(), 0);
	}
	
	/**
	 * 获取角色的宝箱领取状态数据
	 * @param info				角色活跃度数据
	 * @param boxid				宝箱id
	 * @return
	 * @create	hongxiao.z      2013-12-27 下午7:42:19
	 */
	private StateInfo gainStateInfo(Player player, short boxid)
	{
		
		
		for (StateInfo state : player.getGainStates()) 
		{
			if(state.getBoxid() == boxid)
			{
				return state;
			}
		}
		
		return null;
	}
	
	/**
	 * 获取角色的事件记录数据
	 * @param info				角色活跃度数据
	 * @param eventid				宝箱id
	 * @return
	 * @create	hongxiao.z      2013-12-27 下午7:42:19
	 */
	public EventInfo gainEventInfo(Player player, short eventid)
	{
		for (EventInfo event : player.getEvents()) 
		{
			if(event.getEventid() == eventid)
			{
				return event;
			}
		}
		
		return null;
	}
}
