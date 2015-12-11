package com.game.zones.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.biwudao.manager.BiWuDaoManager;
import com.game.config.Config;
import com.game.cooldown.structs.CooldownTypes;
import com.game.count.structs.CountTypes;
import com.game.csys.manager.CsysManger;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.bean.Q_monsterBean;
import com.game.data.bean.Q_pt_awardBean;
import com.game.data.bean.Q_pt_reward_consumeBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.map.structs.Map;
import com.game.monster.structs.Monster;
import com.game.player.manager.PlayerManager;
import com.game.player.message.ReqSyncPlayerPataMessage;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.script.structs.ScriptEnum;
import com.game.server.config.MapConfig;
import com.game.server.impl.MServer;
import com.game.server.impl.WServer;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.structs.Reasons;
import com.game.task.struts.Task;
import com.game.task.struts.TaskEnum;
import com.game.team.bean.TeamInfo;
import com.game.toplist.manager.TopListManager;
import com.game.toplist.structs.DiTuZoneTop;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.ServerParamUtil;
import com.game.utils.VersionUpdateUtil;
import com.game.vip.manager.VipManager;
import com.game.zones.bean.RaidZoneInfo;
import com.game.zones.bean.ZoneMonstrInfo;
import com.game.zones.log.ZoneLog;
import com.game.zones.message.ReqInventedZoneMessage;
import com.game.zones.message.ReqZoneContinuousRaidsMessage;
import com.game.zones.message.ReqZoneContinuousRaidsStopMessage;
import com.game.zones.message.ReqZoneContinuousRaidsYBMessage;
import com.game.zones.message.ReqZoneImmediatelyMessage;
import com.game.zones.message.ReqZoneIntoMessage;
import com.game.zones.message.ReqZoneOpenPanelMessage;
import com.game.zones.message.ReqZoneOutMessage;
import com.game.zones.message.ResAllRaidZoneInfoMessage;
import com.game.zones.message.ResAutoRaidInfoMessage;
import com.game.zones.message.ResTowerIndexMessage;
import com.game.zones.message.ResTowerNextLvMessage;
import com.game.zones.message.ResTowerRewardMessage;
import com.game.zones.message.ResTowerRewardSurplusMessage;
import com.game.zones.message.ResZoneBossAppearMessage;
import com.game.zones.message.ResZoneContinuousRaidsEndMessage;
import com.game.zones.message.ResZoneContinuousRaidsMessage;
import com.game.zones.message.ResZoneKillProgressRateMessage;
import com.game.zones.message.ResZonePanelSelectMessage;
import com.game.zones.message.ResZonePassShowMessage;
import com.game.zones.message.ResZoneTimerMessage;
import com.game.zones.message.ResfastestClearanceToGameMessage;
import com.game.zones.script.ICreateZoneScript;
import com.game.zones.script.IZoneDeleteScript;
import com.game.zones.structs.ContinuousRaidsInfo;
import com.game.zones.structs.PtRecord;
import com.game.zones.structs.Raid;
import com.game.zones.structs.ZoneContext;


public class ZonesManager {
	
	//副本线程
	private ConcurrentHashMap<Long, MServer> mServers = new ConcurrentHashMap<Long, MServer>();
	//副本上下文
	private ConcurrentHashMap<Long, ZoneContext> contexts = new ConcurrentHashMap<Long, ZoneContext>();
	
	//爬塔副本数据记录
	//key   副本ID
	//value 记录
	private ConcurrentHashMap<Integer, PtRecord> towerRecord = new ConcurrentHashMap<Integer, PtRecord>();
	
	private static Object obj = new Object();
	protected Logger log = Logger.getLogger(ZonesManager.class);
	//玩家管理类实例
	private static ZonesManager manager;
	
	private ZonesManager(){}
	
	public static ZonesManager getInstance(){
		synchronized (obj) {
			if(manager == null){
				manager = new ZonesManager();
			}
		}
		return manager;
	}
	
	private HashMap<Integer, DiTuZoneTop> topmap = new HashMap<Integer, DiTuZoneTop>();
	
	private int ZONEMAX = 900;
	
	public static String ManualDeathnum = "ManualDeathnum";
	public static String killmonsternum = "killmonsternum";
	public static String Manualendtime = "Manualendtime";
	public static String Raidautoendtime = "Raidautoendtime";
	public static String Entrances = "Entrances";	//通关次数
	
	/**获得当前副本最快通关记录
	 * 
	 * @param player
	 * @param zoneid
	 * @return	-1表示没有记录
	 */
	public int getZoneTime(Player player ,int zoneModelid) 
	{
		//新添加的副本通关时间 hongxiao.z
		if(isPt(zoneModelid))
		{
			//处理历史最快时间
			Integer fastest = player.getZoneinfo().get(ZonesConstantConfig.PT_FASTEST + zoneModelid);
			return fastest == null ? -1 : fastest;
		}
		
		HashMap<String, Integer> zonetimes = player.getRaidinfo().getZoneraidtimes();
		if (zonetimes.containsKey(zoneModelid+"")) {
			return zonetimes.get(zoneModelid+"");
		}
		
		
		return -1;
	}
	
	public int getZoneTopTime(int zoneModelid) {
		if(topmap.get(zoneModelid)!=null){
			return (int)topmap.get(zoneModelid).getComptime()/1000;
		}
		return -1;
	}
	
	
	//将玩家的通关时间 发送到世界服务器
	public void sendNewtimeToWorld(Player player,long newtime,int zoneModelId) {
		DiTuZoneTop dituzonetop = new DiTuZoneTop(player.getId(), player.getName(), newtime,zoneModelId);
		TopListManager.getInstance().completeZone(1, player, JSON.toJSONString(dituzonetop,SerializerFeature.WriteClassName));
	}
	
	
	public Q_clone_activityBean getContainer(int zoneid){
		Q_clone_activityBean activityBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneid);
		return activityBean;
	}
	
	
	//进入虚拟副本协议   玩家消失
	public void stReqInventedZoneInMessage(Player player,ReqInventedZoneMessage msg) {

	
		MapManager.getInstance().quitMapForInventedZone(player);
//		int areaId =  MapManager.getInstance().getAreaId(player.getPosition());
//		Area area = map.getAreas().get(areaId);
//		// 通知周围玩家
//				ResRoundPlayerDisappearMessage other_msg = new ResRoundPlayerDisappearMessage();
//				other_msg.getPlayerIds().add(player.getId());
//
//				List<Area> rounds =  MapManager.getInstance().getRoundAreas(map, areaId);
//				for (int i = 0; i < rounds.size(); i++) {
//					area = rounds.get(i);
//					while (iter.hasNext()) {
//						Player other = (Player) iter.next();
//						if (other.getId() == player.getId()) {
//							continue;
//						}
//						MessageUtil.tell_player_message(other, other_msg);
//					}
//				}
//		player.setShow(false);
	}
	
	//领取奖励  并  离开虚拟副本协议
	public void stReqInventedZoneOutMessage(Player player,ReqInventedZoneMessage msg) {

		//任务奖励的代码请写在这里
		player.setShow(true);
		
//		ManagerPool.backpackManager.changeMoney(player, 380, Reasons.RAID_MONEY, Config.getId());



		ManagerPool.mapManager.changeMap(player, player.getMap(),player.getMapModelId(), 1, player.getPosition(), this.getClass().getName() + ".stResReqZoneIntoMessage");	
		// 任务奖励的代码请写在这里
		ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.COMPLETECLASSICBATTLE, msg.getZoneid());
	}

	
	/**打开挑战面板后返回消息
	 * 
	 * @param parameter
	 * @param msg
	 */
	public void stReqZoneOpenPanelMessage(Player player,ReqZoneOpenPanelMessage msg) {
		ResAllRaidZoneInfoMessage smsg = new ResAllRaidZoneInfoMessage(); 
		List<Q_clone_activityBean> actlist = ManagerPool.dataManager.q_clone_activityContainer.getList();
		Raid raid = player.getRaidinfo();
		for (Q_clone_activityBean q_clone_activityBean : actlist) {
			if (q_clone_activityBean.getQ_zone_type() == 1 || q_clone_activityBean.getQ_zone_type() ==4) {
				RaidZoneInfo info=new RaidZoneInfo();
				int zoneModelid = q_clone_activityBean.getQ_id();
				long manual = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+zoneModelid);
				long status = ManagerPool.countManager.getCount(player, CountTypes.ZONE_TEAM_ST, ""+zoneModelid);
				//long auto =ManagerPool.countManager.getCount(player, CountTypes.ZONE_AUTO, ""+zoneModelid);
				info.setAutomun((byte) manual);
				info.setManualmun((byte) manual);
				info.setRanking(0); //TODO 当前副本排名	
				info.setZoneid(zoneModelid);
				info.setClearance((byte) status);
				info.setThroughtime(getZoneTime(player,zoneModelid));	//本人最快通关时间
				
				if (getTopmap().containsKey(zoneModelid)) {
					DiTuZoneTop diTuZoneTop = getTopmap().get(zoneModelid);
					info.setFulltime((int) diTuZoneTop.getComptime());
					info.setFullname(diTuZoneTop.getName());
				}else {
					info.setFulltime(0);
					info.setFullname("");
				}
				
				if (raid.getZonestar().containsKey(zoneModelid+"")) {
					int num = raid.getZonestar().get(zoneModelid+"");
					info.setStarnum((byte) num);
				}
				if (q_clone_activityBean.getQ_zone_type() ==4) {	//战魂次数
					long count = ManagerPool.countManager.getCount(player, CountTypes.FIGHTSPIRIT_NUM, zoneModelid + "_" + player.getId());
					info.setZhanhunnum((int) count);
				}
				smsg.getRaidzoneinfolist().add(info);
			}
		}
		MessageUtil.tell_player_message(player,smsg );
	}
	
	
	/**
	 * 解析重置时间设定
	 * @param str
	 * @return
	 */
	public HashMap<String, Integer> getJsDeserialization(String str) {
		@SuppressWarnings("unchecked")
		HashMap<String,Integer> timedata = (HashMap<String,Integer>) JSONserializable.toObject("{"+str+"}",HashMap.class);
		if (timedata == null) {
			timedata = new HashMap<String,Integer>();
			timedata.put("type", 1);
			timedata.put("time", 1);
		}
		return timedata;
	}

	/**选中副本，返回消息
	 * 
	 * @param parameter
	 * @param msg
	 */
	
	public void stReqZonePanelSelectMessage(Player player, int zoneModelid) 
	{
		stReturnZoneInfo(player, zoneModelid);
	}

	/**选中副本，返回消息
	 * 
	 * @param parameter
	 * @param msg
	 */
	public void stReturnZoneInfo(Player player,int zoneModelid) 
	{
		ResZonePanelSelectMessage smsg = new ResZonePanelSelectMessage();
		RaidZoneInfo raidzoneinfo=new RaidZoneInfo();
	//	RaidFlop raidflop = player.getRaidflop();
		long manual = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+zoneModelid);
		long auto =ManagerPool.countManager.getCount(player, CountTypes.ZONE_AUTO, ""+zoneModelid);
		Q_clone_activityBean zoneBean = getContainer(zoneModelid);
		if (zoneBean != null) {
			HashMap<String, Integer> timedata = getJsDeserialization(zoneBean.getQ_reset_time());
			if (manual== 0) {//现在改成手动和自动共用一个变量
				ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, ""+zoneModelid,timedata.get("type"), 0, timedata.get("time"));
			}
			if (auto== 0) {
				ManagerPool.countManager.addCount(player, CountTypes.ZONE_AUTO, ""+zoneModelid,timedata.get("type"), 0, timedata.get("time"));
			}
			
			//临时设置
			if(isPt(zoneBean))
			{
				Integer firstTime = player.getZoneinfo().get(zoneModelid + "_firstTime");
				raidzoneinfo.setThroughtime(firstTime == null ? 0 : firstTime);
			}
			else
			{
				raidzoneinfo.setThroughtime(getZoneTime(player,zoneModelid));	//最快通关时间
			}
			
			raidzoneinfo.setAutomun((byte) manual);//现在改成手动和自动共用一个变量
			raidzoneinfo.setManualmun((byte) manual);
			//raidzoneinfo.setRanking(0); //TODO 当前副本排名	
			raidzoneinfo.setZoneid(zoneModelid);
			
			if (getTopmap().containsKey(zoneModelid)) {
				DiTuZoneTop diTuZoneTop = getTopmap().get(zoneModelid);
				raidzoneinfo.setFulltime((int) diTuZoneTop.getComptime());
				raidzoneinfo.setFullname(diTuZoneTop.getName());
			}else {
				raidzoneinfo.setFulltime(0);
				raidzoneinfo.setFullname("");
			}
			
			smsg.setRaidzoneinfolist(raidzoneinfo);
			Raid raid = player.getRaidinfo();
			if (raid.getZonestar().containsKey(zoneModelid + "")) 
			{
				int num = raid.getZonestar().get(zoneModelid + "");
				raidzoneinfo.setStarnum((byte) num);
			}
			
//			Integer firstTime = player.getZoneinfo().get(zoneModelid + "_firstTime");
//			smsg.getRaidzoneinfolist().setThroughtime(firstTime == null ? 0 : firstTime);
			
			//设置全F最快通关时间
//			smsg.getRaidzoneinfolist().setFullname(topmap.get(zoneModelid).getName());
//			smsg.getRaidzoneinfolist().setFulltime((int)topmap.get(zoneModelid).getComptime());
			//设置已挑战的次数
//			long count = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, "" + zoneModelid);
//			smsg.getRaidzoneinfolist().setManualmun((byte)count);
			
//			if (raidflop != null) {
//				if(raidflop.getManualrewardidx().size() == 0 && raidflop.getManualrewardlist().size() > 0){
//					smsg.setManualstatus(1);
//				}
//				if(raidflop.getAutorewardidx().size() == 0 && raidflop.getAutorewardlist().size() > 0){
//					smsg.setAutostatus(1);
//				}
//			}

			//如果是爬塔
//			if(isPt(zoneModelid))
//			{
//				setPtSurplusSum(player, smsg);
//			}
			MessageUtil.tell_player_message(player,smsg );	
		}

	}
	


	
	
	/**进入副本消息
	 * 
	 * @param player
	 * @param msg
	 */
	
	public void stResReqZoneIntoMessage(Player player, ReqZoneIntoMessage msg) 
	{
		if (!player.isShow()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("玩家状态异常 无法进入副本"));
			return;
		}
		
		//是否爬塔
		if(challengePt(player, msg.getIsauto())) return; 
		
		int zoneModelid= msg.getZoneid();	//副本数据模版ID
		
		Q_clone_activityBean zoneBean = getContainer(zoneModelid);
 		if (zoneBean != null) {
			//检查自身传送是否受限
			if ( !ManagerPool.mapManager.ischangMap(player)) {
				return;
			}
			//判断 是否有  进入副本的 道具要求
			if(zoneBean.getQ_need_item() > 0){
				int  ItemCount =ManagerPool.backpackManager.getItemNum(player, zoneBean.getQ_need_item());
				
				if(ItemCount<1){
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("进入副本所需道具不足"));	
					return;
				}
			}
			
			
			if (zoneBean.getQ_isscript() ==1) {	//活动副本脚本
				if (mServers.size() >= ZONEMAX) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("副本系统繁忙，请稍后再试"));
					return;
				}
	 			ICreateZoneScript script = (ICreateZoneScript) ManagerPool.scriptManager.getScript(ScriptEnum.ZONE_CREATE);
	 			if (script != null) {
	 				try {
	 					script.onCreate(player, zoneModelid);
	 				} catch (Exception e) {
	 					log.error(e, e);
	 				}
	 			} else {
	 				log.error("进入副本脚本不存在！");
	 			}
	 			return;
			}

			byte type = msg.getIsauto();	//是否自动扫荡
			long manual = 0;
			
			if(zoneBean.getQ_wait_time()!=0){
				
				long lastTime = ManagerPool.countManager.getLastTime(player, CountTypes.ZONE_MANUAL, ""+zoneModelid);
				
				if(zoneBean.getQ_wait_time()- (System.currentTimeMillis()-lastTime)>0){
					
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("副本在冷却中。"));	
					//return;
				}
			
			}
			
			long auto =ManagerPool.countManager.getCount(player, CountTypes.ZONE_AUTO, ""+zoneModelid);		
			//初始化计数器
			HashMap<String, Integer> timedata = getJsDeserialization(zoneBean.getQ_reset_time());
			if (manual== 0) {//现在改成手动和自动共用一个变量
				ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, ""+zoneModelid,timedata.get("type"), 0, timedata.get("time"));
			}
			if (auto== 0) {
				ManagerPool.countManager.addCount(player, CountTypes.ZONE_AUTO, ""+zoneModelid,timedata.get("type"), 0, timedata.get("time"));
			}
			
 			Map map=ManagerPool.mapManager.getMap(player);
 			Raid raidinfo = player.getRaidinfo();
 			if (map.getZoneId() > 0) {
 				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已经在副本中。"));	
				return;
			}
 			
// 			if ( !TimeUtil.isNowSatisfiedBy(zoneBean.getQ_open_time())) {
// 				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该副本只在指定时间开放，请届时参与。"));	
//				return;
//			}
 			
 			int num = 0;
 			if (zoneBean.getQ_zone_type() == 1) {	//只有剧情副本才算VIP次数
			// int vipid = VipManager.getInstance().getPlayerVipId(player);
			// if(vipid>0){
			// Q_vipBean vipdb = DataManager.getInstance().q_vipContainer.getMap().get(vipid);
			// if (vipdb != null) {
			// num = vipdb.getQ_zone_time();
			// }
			// }
			}

 			
 			if (type == 1) {	//自动扫荡 (只能单人)
 				if(zoneBean.getQ_type() == 2){	//检测当前副本允许进入模式
 					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("此副本不能进行自动扫荡。"));	
 					return;
 				}

 				//爬塔的额外处理最大次数
 				long maxCount = 0;
 				
 				maxCount = zoneBean.getQ_raids_num() + num;
 				
				if (manual >= maxCount) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("此副本的今日扫荡次数已达到上限。"));	
					return;
				}
 				
				int time = getZoneTime(player,msg.getZoneid());
 				if (time > 0) 
 				{
 					if (zoneBean.getQ_zone_type() == 1)
 					{
 		 				if (raidinfo.getRaidzonemodelid() > 0  && raidinfo.getRaidautoendtime() >0 ) {
 		 					/* xuliang
 		 					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("请先完成当前的战役副本自动扫荡。"));	
 		 					*/
 							return;
 						}
 	 					ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, zoneModelid+"", 1);	//现在改成手动和自动共用一个变量
 	 					/* xuliang
 	 					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("战役副本自动扫荡开始了。"));	
 	 					*/
 	 					raidinfo.setRaidzonemodelid(zoneModelid);
 	 					raidinfo.setRaidautoendtime(time + (int)(System.currentTimeMillis()/1000));
 	 					stReturnZoneInfo(player ,zoneModelid);
 	 					ResAutoRaidInfoMessage automsg = new ResAutoRaidInfoMessage();
 	 					automsg.setSurplustime(time);
 	 					automsg.setZoneid(zoneModelid);
 	 					MessageUtil.tell_player_message(player, automsg);
 	 					stResZoneSurplusSum(player);
 						ZoneLog zlog = new ZoneLog();
 						zlog.setPlayerid(player.getId());
 						zlog.setZonemodelid(zoneModelid);
 						zlog.setType(-2);
 						zlog.setSid(player.getCreateServerId());
 						LogService.getInstance().execute(zlog);
 					}
				}
 				else 
				{
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您没有通关，无法进行扫荡。"));	
				}
				
			}
 			else 
			{
				if (mServers.size() >= ZONEMAX) 
				{
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("副本系统繁忙，请稍后再试"));
					return;
				}
				
				else if(zoneBean.getQ_type() == 2)
				{	//检测当前副本允许进入模式
					boolean isteam = false;
					if (player.getTeamid() > 0) 
					{
						TeamInfo teaminfo = ManagerPool.teamManager.getTeam(player.getTeamid());
						if (teaminfo != null) {
							if (teaminfo.getMemberinfo().size() > 1) {
								isteam = true;
							}
						}
					}
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("此副本需要组队才可以进入。"));	
						if (isteam == false) {
						return;
					}
				}
				else
				{
					if (manual >= zoneBean.getQ_manual_num() + num) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("此副本的今日进入次数已达到上限。"));	
						return;
					}
				}
				
				if (player.getLevel() < zoneBean.getQ_min_lv()) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的等级不够，此副本进入最小等级是{1}级。"),String.valueOf(zoneBean.getQ_min_lv()));	
					return;
				}
				
				if (player.getLevel() > zoneBean.getQ_max_lv()) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的等级过高，此副本进入最大等级是{1}级。"),String.valueOf(zoneBean.getQ_max_lv()));	
					return;
				}
				
				//初始化本次副本个人记录
				player.getZoneinfo().put(ManualDeathnum+"_"+zoneModelid, 0);
				player.getZoneinfo().put(killmonsternum+"_"+zoneModelid, 0);
				player.getZoneinfo().put(Manualendtime+"_"+zoneModelid, 0);
				
				//七曜战将 副本脚本跳转 hongxiao.z 
				/*if (zoneBean.getQ_isscript() == 4) {
	 				//防止跳过前面的副本
	 				HashMap<String, Integer> raidmap = raidinfo.getZoneraidtimes();
	 				if (zoneModelid > 4001) {
 		 				if (!raidmap.containsKey(""+(zoneModelid-1))) {
 		 					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("你敢不敢一个个通关呢？使用外挂已记录"));	
 		 					return;
						}
					}
					
					if (mServers.size() >= ZONEMAX) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("副本系统繁忙，请稍后再试"));
						return;
					}
		 			ICreateZoneScript script = (ICreateZoneScript) ManagerPool.scriptManager.getScript(ScriptEnum.ZONE_CREATE);
		 			if (script != null) {
		 				try {
		 					script.onCreate(player, zoneModelid);
		 				} catch (Exception e) {
		 					log.error(e, e);
		 				}
		 			} else {
		 				log.error("进入副本脚本不存在！");
		 			}
		 			return;
				}*/
				
				
				HashMap<String, Object> others = new HashMap<String, Object>();
				others.put("time", (int)(System.currentTimeMillis()/1000));	//开始时间
				others.put("zonetype",zoneBean.getQ_zone_type());			//副本类型 1扫荡
				others.put("teamtype",0);		//队伍类型，0，单人，1组队
				others.put("boss",1);		//是否已经出BOSS 0 已经出，1未出
				

				List<Integer> mapidlist = JSON.parseArray(zoneBean.getQ_mapid(),Integer.class);
				
				if(zoneBean.getQ_need_item()!=0)
				{
					if(!ManagerPool.backpackManager.removeItem(player, zoneBean.getQ_need_item(),1, Reasons.BUYITEM, Config.getId()))
					{
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("道具扣除失败"));
						return;
					}
				}
			
				ZoneContext zone = setZone("扫荡副本",others,mapidlist,zoneModelid);
				
//				zone = setZone("扫荡副本",others,mapidlist,zoneModelid);	//创建副本，返回副本消息
				
				if (zone != null) 
				{
					MapConfig config = zone.getConfigs().get(0);
					Map zoneMap = ManagerPool.mapManager.getMap(config.getServerId(), config.getLineId(), config.getMapId());
					zoneMap.setRound(Math.max(zoneMap.getWidth(), zoneMap.getHeight()) * 2 + 1);
					Position position = new Position();
					position.setX((short) (zoneBean.getQ_x()*MapUtils.GRID_BORDER));
					position.setY((short) (zoneBean.getQ_y()*MapUtils.GRID_BORDER));
					//拉人进副本
					raidinfo.setRaidmanualzonemodelid(zoneModelid);
					raidinfo.setManualendtime(0);
					raidinfo.setKillmonsternum(0);
					raidinfo.setDeathnum(0);
					
					long status = ManagerPool.countManager.getCount(player, CountTypes.ZONE_TEAM_ST, ""+zoneModelid);
					if (status == 0) {
						ManagerPool.countManager.addCount(player, CountTypes.ZONE_TEAM_ST, ""+zoneModelid,1, 0, 0);
					}

				
					ManagerPool.mapManager.changeMap(player, zone.getConfigs().get(0).getMapId(), zone.getConfigs().get(0).getMapModelId(), 1, position, this.getClass().getName() + ".stResReqZoneIntoMessage");	
//		hongxiao.z	ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, zoneModelid+"", 1);
					Map cmap = ManagerPool.mapManager.getMap(player);
//		hongxiao.z	stResZoneSurplusSum(player);
					if (cmap != null) {
						ResZoneTimerMessage cmsg = new ResZoneTimerMessage();
						cmsg.setZoneid(zoneModelid);
						cmsg.setSurplustime(zoneBean.getQ_exist_time());// 发剩余时间
						cmsg.setMonstrssum(cmap.getMonsters().size());
						cmsg.setMonstrsnum(cmap.getMonsters().size());
						cmsg.setStatus((byte) 1);
						HashMap<Integer, Integer> monnuMap = getMapMonsterSum(cmap.getMonsters());
						cmsg.setZoenmonstrinfolist(getMapMonsterInfo(monnuMap));
						MessageUtil.tell_player_message(player, cmsg);
						zone.getOthers().put("monstrsum", monnuMap);
						ZoneLog zlog = new ZoneLog();
						zlog.setPlayerid(player.getId());
						zlog.setZonemodelid(zoneModelid);
						zlog.setType(-1);
						LogService.getInstance().execute(zlog);
					}
				}
			}
		}
	}
	
	/**
	 * 挑战爬塔副本
	 * @param player
	 * @create	hongxiao.z      2014-2-13 下午9:17:28
	 */
	private boolean challengePt(Player player, int type)
	{
		//是否进入爬塔
		if(type != -ZonesConstantConfig.PT_TYPE) return false;
		
		//组队不能进入副本
		TeamInfo teaminfo = ManagerPool.teamManager.getTeam(player.getTeamid());
		if (teaminfo != null) 
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("请先退出组队伍再进行此副本挑战。"));	
			return true;
		}
		
		//检查自身传送是否受限
		if ( !ManagerPool.mapManager.ischangMap(player)) 
		{
			return true;
		}
		
		//获取当前副本ID
		Integer zoneId = player.getZoneinfo().get(ZonesConstantConfig.PT_CHALLENGE);
		
		//找不到副本数据，或者副本本层已经通关
		if(zoneId == null || zoneId <= 0) return true;
		
		Q_clone_activityBean zoneBean = DataManager.getInstance().q_clone_activityContainer.get(zoneId);
		
		if (player.getLevel() < zoneBean.getQ_min_lv()) 
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().
					getString("您的等级不够，此副本进入最小等级是{1}级。"), String.valueOf(zoneBean.getQ_min_lv()));	
			return true;
		}
		
		ZoneContext zone = null;
		
		//精英爬塔
		ICreateZoneScript script = (ICreateZoneScript) ManagerPool.scriptManager.getScript(ScriptEnum.ZONE_CREATE);
		if (script != null) 
		{
			try {
				zone = script.onCreate(player, zoneId);
			} catch (Exception e) {
				log.error(e, e);
			}
		} else {
			log.error("进入副本脚本不存在！");
		}
		
		if (zone != null) 
		{
			MapConfig config = zone.getConfigs().get(0);
			Map zoneMap = ManagerPool.mapManager.getMap(config.getServerId(), config.getLineId(), config.getMapId());
			zoneMap.setRound(Math.max(zoneMap.getWidth(), zoneMap.getHeight()) * 2 + 1);
			Position position = new Position();
			position.setX((short) (zoneBean.getQ_x() * MapUtils.GRID_BORDER));
			position.setY((short) (zoneBean.getQ_y() * MapUtils.GRID_BORDER));
			Map cmap = ManagerPool.mapManager.getMap(player);
			if (cmap != null) {
				ResZoneTimerMessage cmsg = new ResZoneTimerMessage();
				cmsg.setZoneid(zoneBean.getQ_id());
				cmsg.setSurplustime(zoneBean.getQ_exist_time());// 发剩余时间
				cmsg.setMonstrssum(cmap.getMonsters().size());
				cmsg.setMonstrsnum(cmap.getMonsters().size());
				cmsg.setStatus((byte) 1);
				HashMap<Integer, Integer> monnuMap = getMapMonsterSum(cmap.getMonsters());
				cmsg.setZoenmonstrinfolist(getMapMonsterInfo(monnuMap));
				MessageUtil.tell_player_message(player, cmsg);
				zone.getOthers().put("monstrsum", monnuMap);
				ZoneLog zlog = new ZoneLog();
				zlog.setPlayerid(player.getId());
				zlog.setZonemodelid(zoneBean.getQ_id());
				zlog.setType(-1);
				LogService.getInstance().execute(zlog);
			}
			
			ManagerPool.mapManager.changeMap(player, zone.getConfigs().get(0).getMapId(), zone.getConfigs().get(0).getMapModelId(), 1, position, this.getClass().getName() + ".stResReqZoneIntoMessage");	
		}
		
		
		return true;
	}

	/**
	 * 爬塔的扫荡实现
	 * @param player			
	 * @param zoneBean
	 * @create	hongxiao.z      2014-1-13 上午11:40:40
	 */
	@Deprecated
	void pt_sweep(Player player, Q_clone_activityBean zoneBean)
	{
//			ManagerPool.countManager.addCount(player, CountTypes.PT_MANUAL, null, 1, 1, 0);
//			ManagerPool.countManager.addCount(player, CountTypes.PT_MANUAL, null, 1);	//挑战次数计时器，加1
//			stResZoneSurplusSum(player);
			
			//获取历史最快时间
			Integer fastest = player.getZoneinfo().get(ZonesConstantConfig.PT_FASTEST + zoneBean.getQ_id());
			
			//程序异常才会判断成立，正常情况一定会拿的到数据
			if(fastest == null)
			{
				return;
			}
			
			//计算星级
			int sTime = zoneBean.getQ_exist_time() - fastest;	//剩余时间
			int star = ZonesFlopManager.getInstance().getStart(sTime, zoneBean.getQ_time_evaluate())   ;
			
			if(star==-1){
				MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("副本评价计算    配置错误"));
				return;
			}
			
			ResZonePassShowMessage showMsg = new ResZonePassShowMessage();
			showMsg.setType((byte)1);
			showMsg.setZoneid(zoneBean.getQ_id());
			//获取扫荡奖励
			ZonesFlopManager.getInstance().getZoneReward(player, zoneBean.getQ_id(), star, showMsg);
			
			MessageUtil.tell_player_message(player, showMsg);//发送通关
			
			ZoneLog zlog = new ZoneLog();
			zlog.setPlayerid(player.getId());
			zlog.setZonemodelid(zoneBean.getQ_id());
			zlog.setType(1);
			LogService.getInstance().execute(zlog);	
	}
	
	/**设定生成副本
	 * 
	 * @param player
	 * @param others
	 * @param zoneBean
	 * @return
	 */
	
		public ZoneContext setZone(String name, HashMap<String, Object> others ,List<Integer> mapidList, int zoneModelid) {
			List<MapConfig> configs = new ArrayList<MapConfig>();
		for (Integer mapid : mapidList) {
			MapConfig config = new MapConfig();
			config.setLineId(1);
			config.setServerId(WServer.getInstance().getServerId());
			config.setMapModelId(mapid);
			configs.add(config);
		}
		ZoneContext zone = ManagerPool.zonesManager.createZone(name, zoneModelid, configs);
		if (zone != null) {
			zone.setOthers(others);	
		}else {
			log.error(zoneModelid+"副本创建失败");
		}
		return zone;
	}
	
	/**副本怪物死亡触发
	 * 
	 * @param monster
	 * @param map
	 */
	public void zonesMonsterDie(Monster monster){
		Map map = ManagerPool.mapManager.getMap(monster.getServerId(),monster.getLine(),monster.getMap());
		if (map != null) {
			ZoneContext con = getContexts().get(map.getZoneId());
			int zonetype = 0;
			if (con == null) {
				return ;
			}
			
			if (con.getOthers().containsKey("zonetype")) {
				zonetype = (Integer)con.getOthers().get("zonetype");
			}
			
			if(zonetype == 1){	//如果是扫荡
				int monsternum = ManagerPool.mapManager.getMonsterNum(map);
				ResZoneKillProgressRateMessage cmsg = new ResZoneKillProgressRateMessage();
				cmsg.setMonstrsnum(monsternum);
				cmsg.setMonstrssum(map.getMonsters().size());
				cmsg.setZoneid(map.getZoneModelId());
				int zoneModelId=map.getZoneModelId();
				if (monsternum == 0) {	//当前怪物数量
					int isboss = 0;
					if (con.getOthers().containsKey("boss")) {
						isboss = (Integer)con.getOthers().get("boss");
					}
					
					if (isboss == 1) {	//怪物全死，刷新BOSS
						con.getOthers().put("boss", 0);
						Q_clone_activityBean zoneBean = getContainer(zoneModelId);
						if (zoneBean.getQ_map_boss() != null && !zoneBean.getQ_map_boss().equals("")) {
							int bossid = Integer.parseInt(zoneBean.getQ_map_boss());
							Monster bossmon = ManagerPool.monsterManager.createMonsterAndEnterMap(bossid, monster.getServerId(), monster.getLine(), monster.getMap(), monster.getPosition());
							
							Iterator<Entry<Long, Player>> playersdata = map.getPlayers().entrySet().iterator();
							Q_monsterBean mondata = ManagerPool.dataManager.q_monsterContainer.getMap().get(bossid);
							ZoneMonstrInfo bossInfo = new ZoneMonstrInfo();
							ResZoneBossAppearMessage bossmsg = new ResZoneBossAppearMessage();
							bossInfo.setMonstrmodid(bossid);
							bossInfo.setMonstrnum(1);
							bossmsg.setZoenmonstrinfo(bossInfo);
							while (playersdata.hasNext()) {
								Entry<Long, Player> playedata = playersdata.next();
								Player player = playedata.getValue();
								MessageUtil.notify_player(player, Notifys.SROLL, ResManager.getInstance().getString("BOSS很生气，后果很严重！{1}出现在{2}，{3}"),mondata.getQ_name(),(bossmon.getPosition().getX()/25)+"",(bossmon.getPosition().getY()/25)+"" );
								MessageUtil.tell_player_message(player, bossmsg);
								try {
									log.error(map.getZoneModelId()+"战役副本刷BOSS：modelid:"+ bossmon.getModelId() + ", 地图唯一id："+ map.getId() + ",玩家名：" + player.getName() );
								} catch (Exception e) {
									log.error(e);
								}
								
							}
							return;
						}else {
							log.error(map.getZoneModelId()+"副本没有设置boss.");
						}
					}
					
					if(isboss == 0) {	//BOSS没了，给奖励
						int time = (Integer)con.getOthers().get("time");
						int newtime=(int)(System.currentTimeMillis()/1000 - time);
						HashMap<Long, Player> players = map.getPlayers();
						Iterator<Entry<Long, Player>> playersdata = players.entrySet().iterator();
						while (playersdata.hasNext()) {
							Entry<Long, Player> playedata = playersdata.next();
							Player player = playedata.getValue();
							MessageUtil.notify_player(player, Notifys.CUTOUT, ResManager.getInstance().getString("恭喜您副本通关!!!!!!!!!"));
							//从地图里读出时间，在这里计算通关时间zonetype
							int teamtype = (Integer)con.getOthers().get("teamtype");
							Raid raidinfo = player.getRaidinfo();
							HashMap<String, Integer> zoneinfo = player.getZoneinfo();
							//raidinfo.setKillmonsternum(raidinfo.getKillmonsternum() + 1);
							if (zoneinfo.containsKey(killmonsternum+"_"+zoneModelId)) {
								int num = zoneinfo.get(killmonsternum+"_"+zoneModelId)+1;
								zoneinfo.put(killmonsternum+"_"+zoneModelId, num);
							}else {
								zoneinfo.put(killmonsternum+"_"+zoneModelId, 1);
							}
							
							zoneinfo.put(Manualendtime+"_"+zoneModelId, newtime);
							//raidinfo.setManualendtime(newtime);	//记录当前副本本次通关花费时间
							
							
							if( teamtype == 0){		//如果是单人,更新最快通关时间
								boolean is = false;
								if(raidinfo.getZoneraidtimes().containsKey(map.getZoneModelId()+"")){
									int ytime = raidinfo.getZoneraidtimes().get(map.getZoneModelId()+"");
									if (ytime > newtime) {
										Q_clone_activityBean zoneBean = getContainer(map.getZoneModelId());
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜您刷新了战役{1}的最快通关时间，提速了：{2}秒"), zoneBean.getQ_duplicate_name(),(ytime - newtime)+"");
										raidinfo.getZoneraidtimes().put(map.getZoneModelId()+"", newtime);
										is = true;
									}
								}else {
									raidinfo.getZoneraidtimes().put(map.getZoneModelId()+"", newtime);
									is = true;
								}
								if (is) {	//发送到世界服务器排行
									DiTuZoneTop dituzonetop = new DiTuZoneTop(player.getId(), player.getName(), newtime,map.getZoneModelId());
									TopListManager.getInstance().completeZone(1, player, JSON.toJSONString(dituzonetop,SerializerFeature.WriteClassName));
								}
							}
							ZoneMonstrInfo info = new ZoneMonstrInfo();
							info.setMonstrmodid(monster.getModelId());
							info.setMonstrnum(1);
							cmsg.setZoenmonstrinfo(info);
							cmsg.setDeathnum(raidinfo.getDeathnum());
							cmsg.setStatus((byte) 1);
							MessageUtil.tell_player_message(player, cmsg);

							int starnum = computeStar(player,zoneModelId);//计算星星数量
							HashMap<String, Integer> zonestar = raidinfo.getZonestar();
							if((zonestar.containsKey(map.getZoneModelId()+"") == false) || (zonestar.get(map.getZoneModelId()+"") < starnum)) {
								zonestar.put(map.getZoneModelId()+"", starnum);	//写入当前副本最佳星级
							}

							ManagerPool.zonesFlopManager.addZoneReward(player,map.getZoneModelId(), false);
							ManagerPool.zonesFlopManager.stZonePassShow(player,0,zoneModelId);		//发送奖励列表到前端
							ManagerPool.zonesFlopManager.LoginRaidRewarded(player);
							stResZoneSurplusSum(player);
							ZoneLog zlog = new ZoneLog();
							zlog.setPlayerid(player.getId());
							zlog.setZonemodelid(zoneModelId);
							zlog.setType(0);
							zlog.setSid(player.getCreateServerId());
							LogService.getInstance().execute(zlog);
						}
					}
					
				}else {
						HashMap<Integer, Integer>monstrnum = getMapMonsterSum(map.getRevives());
						int dangqian= 1;
						if (monstrnum.containsKey(monster.getModelId())) {
							dangqian = (int)(monstrnum.get(monster.getModelId())) + 1 ;//得到已经死亡的怪物数量
						}
						
						HashMap<Long, Player> players = map.getPlayers();
						Iterator<Entry<Long, Player>> playersdata = players.entrySet().iterator();
						ZoneMonstrInfo info = new ZoneMonstrInfo();
						info.setMonstrmodid(monster.getModelId());
						info.setMonstrnum(dangqian);
						while (playersdata.hasNext()) {
							Entry<Long, Player> playedata = playersdata.next();
							Player player = playedata.getValue();
							MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("剩余 {1} 敌人。"), monsternum+"");
							Raid raidinfo = player.getRaidinfo();
							HashMap<String, Integer> zoneinfo = player.getZoneinfo();
							if (zoneinfo.containsKey(killmonsternum+"_"+zoneModelId)) {
								int num = zoneinfo.get(killmonsternum+"_"+zoneModelId)+1;
								zoneinfo.put(killmonsternum+"_"+zoneModelId, num);
							}else {
								zoneinfo.put(killmonsternum+"_"+zoneModelId, 1);
							}
							//raidinfo.setKillmonsternum(1+raidinfo.getKillmonsternum());
							cmsg.setDeathnum(raidinfo.getDeathnum());
							cmsg.setStatus((byte) 1);
							cmsg.setZoenmonstrinfo(info);
							MessageUtil.tell_player_message(player, cmsg);
						}
				}
			}   
		}
	}
	
	
	/**
	 * 暂时用来做副本排行存储
	 */
	public static volatile HashMap<String, String> extras = new HashMap<String, String>();
	
	/**自动扫荡立即完成（使用钻石）消息
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqZoneImmediatelyMessage(Player player,ReqZoneImmediatelyMessage msg) {
		Raid raidinfo = player.getRaidinfo();
		int ms = (int)(System.currentTimeMillis()/1000);
		if (msg.getType() == 1) {
			int time = raidinfo.getRaidautoendtime() - ms;
			if (raidinfo.getRaidzonemodelid() > 0 && time > 0) {
				double dtime = time;
				double n =Math.ceil(dtime/60);
				Q_clone_activityBean zoneBean = ManagerPool.zonesManager.getContainer(raidinfo.getRaidzonemodelid());
				int yb = (int)(n*zoneBean.getQ_raids_min_yuanbao());	//每分钟消耗钻石
				if(yb > 0 && ManagerPool.backpackManager.checkGold(player, yb)){
					//检测2级密码
					if ( ManagerPool.protectManager.checkProtectStatus(player) ) {
						return;
					}
					ManagerPool.backpackManager.changeGold(player,-yb, Reasons.RAID_YB, Config.getId());
					int zonemodelid = raidinfo.getRaidzonemodelid();
					
					raidinfo.setRaidautoendtime(0);
					ManagerPool.zonesFlopManager.addZoneReward(player,zonemodelid, true);
					
					ResAutoRaidInfoMessage reidmsg = new ResAutoRaidInfoMessage();
					
					reidmsg.setSurplustime(0);
					reidmsg.setZoneid(zonemodelid);
					MessageUtil.tell_player_message(player, reidmsg);
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("您花费了{1}钻石，自动扫荡完成"), yb+"");
					ManagerPool.zonesFlopManager.LoginRaidRewarded(player);
					ZoneLog zlog = new ZoneLog();
					zlog.setPlayerid(player.getId());
					zlog.setType(1);
					zlog.setZonemodelid(zonemodelid);
					zlog.setSid(player.getCreateServerId());
					LogService.getInstance().execute(zlog);
				}else {
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("立即完成当前自动扫荡需要{1}钻石"), yb+"");
				}
			}
		}else if (msg.getType() == 4) {
			int time = raidinfo.getQiyaoraidautoendtime() - ms;
			if (raidinfo.getQiyaoraidzonemodelid() > 0 && time > 0) {
				double dtime = time;
				double n =Math.ceil(dtime/60);
				Q_clone_activityBean zoneBean = ManagerPool.zonesManager.getContainer(raidinfo.getQiyaoraidzonemodelid());
				int yb = (int)(n*zoneBean.getQ_raids_min_yuanbao());	//每分钟消耗钻石
				if(yb > 0 && ManagerPool.backpackManager.checkGold(player, yb)){
					//检测2级密码
					if ( ManagerPool.protectManager.checkProtectStatus(player) ) {
						return;
					}
					
					ManagerPool.backpackManager.changeGold(player,-yb, Reasons.RAID_YB, Config.getId());
					int zonemodelid = raidinfo.getQiyaoraidzonemodelid();
				
					raidinfo.setQiyaoraidautoendtime(0);
					ManagerPool.zonesFlopManager.addZoneReward(player,zonemodelid, true);
				
					ResAutoRaidInfoMessage reidmsg = new ResAutoRaidInfoMessage();
					
					reidmsg.setSurplustime(0);
					reidmsg.setZoneid(zonemodelid);
					MessageUtil.tell_player_message(player, reidmsg);
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("您花费了{1}钻石，自动扫荡完成"), yb+"");
					ManagerPool.zonesFlopManager.LoginRaidRewarded(player);
					ZoneLog zlog = new ZoneLog();
					zlog.setPlayerid(player.getId());
					zlog.setType(1);
					zlog.setZonemodelid(zonemodelid);
					LogService.getInstance().execute(zlog);
				}else {
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("立即完成当前自动扫荡需要{1}钻石"), yb+"");
				}
			}
		}
	}
	
	/**扫荡消耗钻石消息
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqZoneImmediatelyByCostMessage(Player player, int zoneModelid) {
		//判断副本是否可以一键完成
		if(zoneModelid!=3001 && zoneModelid!=3002 && zoneModelid!=3003 && zoneModelid!=3004){
			return;
		}
		
		Q_clone_activityBean zoneBean = ManagerPool.zonesManager.getContainer(zoneModelid);
		if (zoneBean.getQ_min_lv() > player.getLevel()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("不要闹了，您的等级不够。"));	
			return;
		}
		
		
		long manual = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+zoneModelid);
		//初始化计数器
		HashMap<String, Integer> timedata = getJsDeserialization(zoneBean.getQ_reset_time());
		if (manual == 0) {//现在改成手动和自动共用一个变量
			ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, ""+zoneModelid,timedata.get("type"), 0, timedata.get("time"));
		}
		
		if (manual >= zoneBean.getQ_raids_num()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("此副本的今日扫荡次数已达到上限。"));	
			return;
		}
		
		int yb = zoneBean.getQ_raids_yuanbao();

		if(yb > 0 && ManagerPool.backpackManager.checkGold(player, yb)){
			//检测2级密码
			if ( ManagerPool.protectManager.checkProtectStatus(player) ) {
				return;
			}
			ManagerPool.backpackManager.changeGold(player,-yb, Reasons.RAID_YB, Config.getId());

			ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, zoneModelid+"", 1);	//现在改成手动和自动共用一个变量
			
			ManagerPool.zonesFlopManager.addZoneReward(player, zoneModelid, true);
		
			ResAutoRaidInfoMessage reidmsg = new ResAutoRaidInfoMessage();
			
			reidmsg.setSurplustime(0);
			reidmsg.setZoneid(zoneModelid);
			MessageUtil.tell_player_message(player, reidmsg);
			MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("您花费了{1}钻石，自动扫荡完成"), yb+"");
			ManagerPool.zonesFlopManager.LoginRaidRewarded(player);
			ZoneLog zlog = new ZoneLog();
			zlog.setPlayerid(player.getId());
			zlog.setType(1);
			zlog.setZonemodelid(zoneModelid);
			LogService.getInstance().execute(zlog);
		}else {
			MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("立即完成当前自动扫荡需要{1}钻石"), yb+"");
		}
	}
	
	
	
	/**玩家在副本内死亡
	 * 
	 * @param player
	 */
	public void zoneplayerdie(Player player) {
		Map map = ManagerPool.mapManager.getMap(player);
		if (map != null) {
			if(map.getZoneModelId() > 0){
				ZoneContext con = getContexts().get(map.getZoneId());
				if (con.getOthers().containsKey("zonetype")) {
					int zonetype = (Integer)con.getOthers().get("zonetype");
					if (zonetype == 1 || zonetype == 3 || zonetype == 4) {
						int num = 0;
						if (!player.getZoneinfo().containsKey(ManualDeathnum+"_"+map.getZoneModelId())) {
							num= 1;
						}else {
							num = player.getZoneinfo().get(ManualDeathnum+"_"+map.getZoneModelId())+1;
						}
						
						player.getZoneinfo().put(ManualDeathnum+"_"+map.getZoneModelId(), num);
						//player.getRaidinfo().setDeathnum(num);
						int monsternum = ManagerPool.mapManager.getMonsterNum(map);
						ResZoneKillProgressRateMessage cmsg = new ResZoneKillProgressRateMessage();
						cmsg.setMonstrsnum(monsternum);
						cmsg.setMonstrssum(map.getMonsters().size());
						cmsg.setZoneid(map.getZoneModelId());
						cmsg.setDeathnum(num);
						cmsg.setStatus((byte) 1);
						cmsg.setZoenmonstrinfo(new ZoneMonstrInfo());
						MessageUtil.tell_player_message(player, cmsg);
					}
				}
			}
		}
	}
	
	
	
	
	/**GM测试副本奖励
	 * 
	 * @param player
	 */
	public void testzonesreward(Player player,int type){
		//ManagerPool.zonesFlopManager.createFruitRewardList(player,type);
//		ManagerPool.zonesFlopManager.stZonePassShow(player,type);
		
		ManagerPool.zonesFlopManager.LoginRaidRewarded(player);
		
		ManagerPool.zonesFlopManager.giveZoneFixedReward(player, 1,0);
	}
	
	
	
	/**计算星星数量
	 * @return 
	 * 
	 */
	public int computeStar(Player player,int zonemodelid){
		int num = 0;
//		try {
//			int dnum = player.getZoneinfo().get(ManualDeathnum+"_"+zonemodelid);
//			if (dnum == 0) {//人物在副本内死亡次数
//				num = num + 1;
//			}
//			int monnum = player.getZoneinfo().get(killmonsternum+"_"+zonemodelid);
//			if (monnum >= 1) {	//副本内杀怪数量
//				num = num + 1;
//			}
//			Q_clone_activityBean zoneBean = ManagerPool.zonesManager.getContainer(zonemodelid);
//			if (zoneBean != null) {
//				int time = player.getZoneinfo().get(Manualendtime+"_"+zonemodelid);
//				if (time <= zoneBean.getQ_time_evaluate()/1000) {//副本时间评价
//					num = num + 1;
//				}
//			}
//		} catch (Exception e) {
//			log.error(e);
//		}

		return num;
	}
	
	
	
	
	
	
	
	/**离开副本
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqZoneOutMessage(Player player, ReqZoneOutMessage msg) {
		outZone(player);
	}

	
	
	
	
	
	
	/**
	 * 测试用离开副本 
	 */
	public void outZone(Player player){
		
	
		
			//移除玩家 的副本 BUFF ID 
			ManagerPool.buffManager.removeByBuffId(player, 1101, 1102, 1128);
			
			ManagerPool.backpackManager.removeItem(player, 720101,1, Reasons.GOODUSE, Config.getId());
			ManagerPool.backpackManager.removeItem(player, 720102,1, Reasons.GOODUSE, Config.getId());
			ManagerPool.backpackManager.removeItem(player, 720100,1, Reasons.GOODUSE, Config.getId());
			
			
			
		
		int formerline = player.getFormerline();
		int formermapid= player.getFormermapid();
		Position formerpos = player.getFormerposition();

		Map map = ManagerPool.mapManager.getMap(player.getServerId(), player.getLine(), player.getMap());
		
		//玩家手动离开  移除 BUFF 添加记录
		map.getParameters().put(player.getId()+"_Buff_Number_"+1101, 0);
		map.getParameters().put(player.getId()+"_Buff_Number_"+1102, 0);
		map.getParameters().put(player.getId()+"_Buff_Number_"+1128, 0);
		
		if(map.getMapModelid()==CsysManger.CSYS_MAPID){
			ManagerPool.playerManager.changePkState(player, 0, 0);
		}
		
		if(map==null){
			log.error("server " + player.getServerId() + " line " + player.getLine() + " map " + player.getMap() + "  is null!");
		}
		if(map.isCopy()){
			if(player.isDie()){
				ManagerPool.playerManager.revive(player, 1);
			}
			//Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());
			Grid[][] grids = ManagerPool.mapManager.getMapBlocks(formermapid);
			Grid grid = null;
			int mapid = formermapid;
			//比武岛和圣盟争夺战地图不允许在副本内返回
			if (grids == null  || mapid == ManagerPool.countryManager.SIEGE_MAPID || mapid == BiWuDaoManager.BIWUDAO_MAPID) {
				mapid = 100001;
				grids = ManagerPool.mapManager.getMapBlocks(mapid);
				grid = MapUtils.getGrid(394,171, grids);
				ManagerPool.mapManager.changeMap(player,mapid,mapid, 0, grid.getCenter(), this.getClass().getName() + ".outZone 1");
			}else {
				ManagerPool.mapManager.changeMap(player,mapid,mapid, formerline, formerpos, this.getClass().getName() + ".outZone 2");
			}
			
		}
		//发送副本状态信息
		ZonesTeamManager.getInstance().stReqZoneTeamOpenToGameMessage(player, 0);
	}

	
	/**地图副本最快记录保持着
	 * 
	 * @param msg
	 */
	public void stResfastestClearanceToGameMessage(ResfastestClearanceToGameMessage msg) {
		@SuppressWarnings("unchecked")
		HashMap<Integer,List<DiTuZoneTop> > diTuZoneTop = JSON.parseObject(msg.getZonetopjsonstr(), HashMap.class);
		if (diTuZoneTop != null) {
			Set<Entry<Integer,List<DiTuZoneTop>>> entrySet = diTuZoneTop.entrySet();
			for (Entry<Integer, List<DiTuZoneTop>> entry : entrySet) {
				getTopmap().put(entry.getKey(), entry.getValue().get(0));
			}
		}
	}
	
	
	
	
	/**
	 * 创建副本
	 * @param name 名字
	 * @param zoneModelId 副本模板Id
	 * @param mapConfigs 地图参数
	 * @return 副本上下文
	 */
	public ZoneContext createZone(String name, int zoneModelId, List<MapConfig> mapConfigs){
		long zoneId = Config.getId();
		MServer server = ManagerPool.mapManager.createMap(name, zoneId, zoneModelId, mapConfigs);
		mServers.put(zoneId, server);
		
		ZoneContext context = new ZoneContext();
		context.setId(zoneId);
		context.setZonemodelid(zoneModelId);
		context.setConfigs(mapConfigs);
		
		context.setCktime(System.currentTimeMillis()+1000*60*5 );//设定检查时间，多加5分钟，加上检查间隔
		contexts.put(zoneId, context);
		
		if(zoneModelId==6001){
			ManagerPool.mapManager.getCsysContexts().add(context);
		}
		
		return context;
	}
	
	/**
	 * 移除副本
	 * @param zoneId 副本Id
	 */
	public void removeZone(long zoneId){
		MServer server = mServers.remove(zoneId);
		ZoneContext context = contexts.remove(zoneId);
		IZoneDeleteScript script = (IZoneDeleteScript) ManagerPool.scriptManager.getScript(ScriptEnum.ZONE_DELETE);
		if (script != null) {
			try {
				script.onDelete(server, context);
			} catch (Exception e) {
				log.error(e, e);
			}
		} else {
			log.error("副本移除事件脚本不存在！");
		}
		WServer.getInstance().removeMapServer(server);
		
//		System.out.println("副本销毁 id --- " + zoneId + " 原型ID - " + context.getZonemodelid());
	}
	
	public ConcurrentHashMap<Long, MServer> getmServers() {
		return mServers;
	}

	public ConcurrentHashMap<Long, ZoneContext> getContexts() {
		return contexts;
	}

	public HashMap<Integer, DiTuZoneTop> getTopmap() {
		return topmap;
	}

	public void setTopmap(HashMap<Integer, DiTuZoneTop> topmap) {
		this.topmap = topmap;
	}


	
	/**获取地图怪物分类数量
	 * @return 
	 * 
	 */
	public List<ZoneMonstrInfo> getMapMonsterInfo(HashMap<Integer, Integer> monnuMap){
		List<ZoneMonstrInfo> infolist = new ArrayList<ZoneMonstrInfo>();
		Iterator<Entry<Integer, Integer>> it2 = monnuMap.entrySet().iterator();
		while (it2.hasNext()) {
			Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it2.next();
			ZoneMonstrInfo info = new ZoneMonstrInfo();
			info.setMonstrmodid(entry.getKey());
			info.setMonstrnum(entry.getValue());
			infolist.add(info);
		}
		return infolist;
	}

	
	/**获取怪物分布总量
	 * 
	 * @param map
	 * @return
	 */
	public HashMap<Integer, Integer> getMapMonsterSum(HashMap<Long, Monster> mons){
		HashMap<Integer, Integer> monnuMap = new HashMap<Integer, Integer>();
		Iterator<Entry<Long, Monster>> it = mons.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<Long, Monster> entry = (Entry<Long, Monster>) it.next();
			Monster mon = entry.getValue();
			if(monnuMap.containsKey(mon.getModelId())){
				int s = monnuMap.get(mon.getModelId());
				monnuMap.put(mon.getModelId(), s+1);
			}else {
				monnuMap.put(mon.getModelId(), 1);
			}
		}
		return monnuMap;
	}

	public int getZONEMAX() {
		return ZONEMAX;
	}

	public void setZONEMAX(int zONEMAX) {
		ZONEMAX = zONEMAX;
	}
	
	
	
	/**副本剩余次数显示
	 * 
	 * @param player
	 */
	public void stResZoneSurplusSum(Player player)
	{
//		ResZoneSurplusSumMessage cmsg = new ResZoneSurplusSumMessage();
		//VIP附加副本次数
//		int vipnum = 0;
		// int vipid = VipManager.getInstance().getPlayerVipId(player);
		// if(vipid>0){
		// Q_vipBean vipdb = DataManager.getInstance().q_vipContainer.getMap().get(vipid);
		// if (vipdb != null) {
		// vipnum = vipdb.getQ_zone_time();
		// }
		// }
		
		//世界地图副本总次数
//		List<Q_clone_activityBean> data = ManagerPool.dataManager.q_clone_activityContainer.getList();
//		long sum = 0;
//		long manual = 0;
//		int zhanyisum = 0;
		
		//zhongxiao.z    七曜副本不存在，废弃
		/*int qiyaozoneid=0;
		long qiyaonum = 0;
		long qiyaosum = 0;
		boolean qiyaoisshow = false;*/
		
		
//		boolean zhanyiisshow = false;
//		Raid raid = player.getRaidinfo();
//		HashMap<String, Integer> raidmap = raid.getZoneraidtimes();
//		for (Q_clone_activityBean q_clone_activityBean : data) {
//			if (q_clone_activityBean.getQ_zone_type() == 1) {
//				
//				if (player.getLevel() >= q_clone_activityBean.getQ_min_lv()) {
//					zhanyisum = zhanyisum+1;
//					zhanyiisshow = true;
//					if (raidmap.containsKey(""+q_clone_activityBean.getQ_id())) {
//						sum = vipnum + sum + q_clone_activityBean.getQ_manual_num();
//						manual =   manual + ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+q_clone_activityBean.getQ_id());
//					}else {
//						if (ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+q_clone_activityBean.getQ_id() ) > 0) {
//							sum = sum - (vipnum+1);
//						}
//					}
//				}
//				
//			}
			/*	hongxiao.z		2014.1.9 七曜副本不存在，废弃
			else if (q_clone_activityBean.getQ_zone_type() == 4) {		//七曜战将可调整副本
				if (player.getLevel() >= q_clone_activityBean.getQ_min_lv()) {
					qiyaoisshow = true;
					if (raidmap.containsKey(""+q_clone_activityBean.getQ_id())) {
						qiyaosum =  qiyaosum + q_clone_activityBean.getQ_manual_num();
						qiyaonum =   qiyaonum + ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+q_clone_activityBean.getQ_id());	
						if (ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+q_clone_activityBean.getQ_id()) == 0) {
							if (qiyaozoneid == 0) {//得到最近的可挑战副本
								qiyaozoneid = q_clone_activityBean.getQ_id();
							}
						}
					}else {
						if (qiyaozoneid == 0) {	//得到最近的可挑战副本
							qiyaozoneid = q_clone_activityBean.getQ_id();
						}
					}
				}
			}
			*/
//		}
		
//		if (zhanyiisshow ) {
//			if (sum < zhanyisum * (vipnum+1)) {
//				sum = sum + (vipnum + 1);
//			}
//			if (sum - manual >  0) {
//				cmsg.setNum((int) (sum - manual));
//			}
//		}
		
		//七曜副本不存在，废弃 hongxiao.z 2014.1.9
		/*if (qiyaoisshow) {
			if (qiyaosum < 7 ) {
				qiyaosum = qiyaosum +1;
			}
			if (qiyaosum- qiyaonum > 0) {
				cmsg.setQiyaonum((int) (qiyaosum- qiyaonum));
			}
			cmsg.setQiyaozoneid(qiyaozoneid);
		}*/

//		MessageUtil.tell_player_message(player, cmsg);
	}


	/**给副本增加删除标记
	 * 
	 * @param zondid
	 */
	public void deleteZone(long zondid){
		MServer server = getmServers().get(zondid);
		if(server!=null){
			server.setDelete(true);
		}
	}
	
	
	/**连续扫荡开始
	 * 
	 * @param player
	 */
	public void ContinuousRaids(Player player , ReqZoneContinuousRaidsMessage msg ){
		// if (ManagerPool.vipManager.getPlayerVipId(player) < 3) {
		// MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("只有至尊VIP才可以使用此功能"));
		// return;
		// }
		Map map = ManagerPool.mapManager.getMap(player);
		if (map== null || (map!= null && map.isCopy())) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("副本内不能使用连续扫荡"));
			return;
		}
		
		//加快速点击冷却
		if (ManagerPool.cooldownManager.isCooldowning(player,CooldownTypes.STALL_MONEYSEARCH,null) == false) {
			ManagerPool.cooldownManager.addCooldown(player,CooldownTypes.STALL_MONEYSEARCH,null,3000);
		}else {
			return;
		}
		
		if (msg.getZonetype() == 1) {
			List<ContinuousRaidsInfo> raidcontinuouslist = player.getRaidcontinuouslist();
			Raid raid = player.getRaidinfo();
			HashMap<String, Integer> raidmap = raid.getZoneraidtimes();
			ResZoneContinuousRaidsMessage cmsg= new ResZoneContinuousRaidsMessage();
			cmsg.setZonetype(1);
			int vipnum = 0;	//VIP增加次数
			// int vipid = VipManager.getInstance().getPlayerVipId(player);
			// if(vipid>0){
			// Q_vipBean vipdb = DataManager.getInstance().q_vipContainer.getMap().get(vipid);
			// if (vipdb != null) {
			// vipnum = vipdb.getQ_zone_time();
			// }
			// }
			
			int znum = 0;
			int sumtime = 0;
			int rewnum = 0;
			int currtime = (int)(System.currentTimeMillis()/1000);

			List<Integer> keys= new ArrayList<Integer>();
			
			for (String strzid : raidmap.keySet()) {
				keys.add(Integer.parseInt(strzid));
			}
			
			Collections.sort(keys, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					if (o1 > o2) {
						return 1;
					}
					return 0;
				}
			} );

			for (int i = 0; i < 10; i++) {  //最多连续循环10次
				for (Integer zid : keys) {
					String strzid =  zid+"";
					if (raid.getRaidzonemodelid() == zid && currtime < raid.getRaidautoendtime()) {
						int ms = raid.getRaidautoendtime() - currtime;
						sumtime= sumtime + ms/2;//VIP花费时间减半
						ContinuousRaidsInfo info = new ContinuousRaidsInfo();
						info.setStatus(0);
						info.setTime(currtime+sumtime);
						info.setZonemodelid(zid);
						raidcontinuouslist.add(info);
						znum = znum + 1;
						raid.setRaidautoendtime(0);
						raid.setRaidzonemodelid(0);
						continue;
					}
					
					Q_clone_activityBean zoneBean = getContainer(zid);
					long manual = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, strzid);
					HashMap<String, Integer> timedata = getJsDeserialization(zoneBean.getQ_reset_time());
					if (manual== 0) {//现在改成手动和自动共用一个变量
						ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, strzid,timedata.get("type"), 0, timedata.get("time"));
					}
					if (zoneBean.getQ_zone_type() == 1 && vipnum + zoneBean.getQ_raids_num() > manual) {
						sumtime= sumtime + raidmap.get(strzid)/2;		//VIP花费时间减半
						ContinuousRaidsInfo info = new ContinuousRaidsInfo();
						info.setStatus(0);
						info.setTime(currtime+sumtime);
						info.setZonemodelid(zid);
						raidcontinuouslist.add(info);//最后一个就表示所有副本完成扫荡的总时间
						ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, strzid, 1);
						znum = znum + 1;
					}
				}
			}

			HashMap<String, Integer> rewardmap = player.getZonerewardmap();
			Iterator<Entry<String, Integer>> it = rewardmap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Integer> entry = it.next();
				rewnum = rewnum + entry.getValue();
			}
			
			player.setRaidcontinuoustime(currtime);	//存开始时间
			
			cmsg.setSumtime(sumtime);
			cmsg.setZonenum(znum);
			cmsg.setRewardnum(rewnum);
			MessageUtil.tell_player_message(player, cmsg);
			
			if (znum == 0) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("今日已没有可连续扫荡的副本"));
				return;
			}
			
			stReqZoneOpenPanelMessage(player,null);//刷新剩余次数
			stResZoneSurplusSum(player);
			
		}else if (msg.getZonetype() == 4) {	//七曜战将连续扫荡
			List<ContinuousRaidsInfo> qiyaocontinuouslist = player.getQiyaocontinuouslist();
			Raid raid = player.getRaidinfo();
			HashMap<String, Integer> raidmap = raid.getZoneraidtimes();
			ResZoneContinuousRaidsMessage cmsg= new ResZoneContinuousRaidsMessage();
			cmsg.setZonetype(4);

			
			int znum = 0;
			int sumtime = 0;
			int rewnum = 0;
			int currtime = (int)(System.currentTimeMillis()/1000);

			List<Integer> keys= new ArrayList<Integer>();
			
			for (String strzid : raidmap.keySet()) {
				keys.add(Integer.parseInt(strzid));
			}
			
			Collections.sort(keys, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					if (o1 > o2) {
						return 1;
					}
					return 0;
				}
			} );


			for (Integer zid : keys) {
				String strzid =  zid+"";
				//当前正在扫荡的副本
				if (raid.getQiyaoraidzonemodelid() == zid && currtime < raid.getQiyaoraidautoendtime()) {
					int ms = raid.getQiyaoraidautoendtime() - currtime;
					sumtime= sumtime + ms/2;//VIP花费时间减半
					ContinuousRaidsInfo info = new ContinuousRaidsInfo();
					info.setStatus(0);
					info.setTime(currtime+sumtime);
					info.setZonemodelid(zid);
					qiyaocontinuouslist.add(info);
					znum = znum + 1;
					raid.setQiyaoraidautoendtime(0);
					raid.setQiyaoraidzonemodelid(0);
					continue;
				}
				
				Q_clone_activityBean zoneBean = getContainer(zid);
				long manual = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, strzid);
				HashMap<String, Integer> timedata = getJsDeserialization(zoneBean.getQ_reset_time());
				if (manual== 0) {//现在改成手动和自动共用一个变量
					ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, strzid,timedata.get("type"), 0, timedata.get("time"));
				}
				if (zoneBean.getQ_zone_type() == 4 &&  zoneBean.getQ_raids_num() > manual) {
					sumtime= sumtime + raidmap.get(strzid)/2;		//VIP花费时间减半
					ContinuousRaidsInfo info = new ContinuousRaidsInfo();
					info.setStatus(0);
					info.setTime(currtime+sumtime);
					info.setZonemodelid(zid);
					qiyaocontinuouslist.add(info);//最后一个就表示所有副本完成扫荡的总时间
					ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, strzid, 1);
					znum = znum + 1;
				}
			}
		

			HashMap<String, Integer> rewardmap = player.getZoneqiyaorewardmap();
			Iterator<Entry<String, Integer>> it = rewardmap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Integer> entry = it.next();
				rewnum = rewnum + entry.getValue();
			}
			
			player.setQiyaocontinuoustime(currtime);	//存开始时间
			
			cmsg.setSumtime(sumtime);
			cmsg.setZonenum(znum);
			cmsg.setRewardnum(rewnum);
			MessageUtil.tell_player_message(player, cmsg);
			
			if (znum == 0) {
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("今日已没有可连续扫荡的七曜副本"));
				*/
				return;
			}
			
			stReqZoneOpenPanelMessage(player,null);//刷新剩余次数
			stResZoneSurplusSum(player);

			
		}
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**停止连续扫荡
	 * 
	 * @param parameter
	 */
	public void stReqZoneContinuousRaidsStopMessage(Player player,ReqZoneContinuousRaidsStopMessage msg) {
		ResZoneContinuousRaidsEndMessage cmsg = new ResZoneContinuousRaidsEndMessage();
		if (msg.getZonetype() == 1) {
			cmsg.setType((byte) 2);	//取消扫荡
			cmsg.setZonetype(1);
			MessageUtil.tell_player_message(player, cmsg);
			for (ContinuousRaidsInfo info : player.getRaidcontinuouslist()) {
				if(info.getStatus() == 0){
					long manual = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, info.getZonemodelid()+"");
					if (manual > 0) {
						ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, info.getZonemodelid()+"", -1);
					}
				}
			}

			player.getRaidcontinuouslist().clear();
			stReqZoneOpenPanelMessage(player,null);//刷新剩余次数	
			stResZoneSurplusSum(player);
		}else if (msg.getZonetype() == 4) {
			cmsg.setType((byte) 2);	//取消扫荡
			cmsg.setZonetype(4);
			MessageUtil.tell_player_message(player, cmsg);
			for (ContinuousRaidsInfo info : player.getQiyaocontinuouslist()) {
				if(info.getStatus() == 0){
					long manual = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, info.getZonemodelid()+"");
					if (manual > 0) {
						ManagerPool.countManager.addCount(player, CountTypes.ZONE_MANUAL, info.getZonemodelid()+"", -1);
					}
				}
			}
			player.getQiyaocontinuouslist().clear();
			stReqZoneOpenPanelMessage(player,null);//刷新剩余次数	
			stResZoneSurplusSum(player);
		}

		
		
		
	}

	
	
	/**使用钻石立即完成连续扫荡
	 * 
	 * @param parameter
	 */
	public void stReqZoneContinuousRaidsYBMessage(Player player,ReqZoneContinuousRaidsYBMessage msg ) {
		if (msg.getZonetype() == 1) {
			List<ContinuousRaidsInfo> list = player.getRaidcontinuouslist();
			if (list.size() == 0) {
				return;
			}

			int ms = (int)(System.currentTimeMillis()/1000);
			int time = list.get(list.size()-1).getTime() - ms;
			if (time > 0) {
				double dtime = time;
				double n =Math.ceil(dtime/60);
				Q_clone_activityBean zoneBean = ManagerPool.zonesManager.getContainer(list.get(list.size()-1).getZonemodelid());
				int yb = (int)(n*zoneBean.getQ_raids_min_yuanbao());	//每分钟消耗钻石
				if(yb > 0 && ManagerPool.backpackManager.checkGold(player, yb)){
					//检测2级密码
					if ( ManagerPool.protectManager.checkProtectStatus(player) ) {
						return;
					}
					ManagerPool.backpackManager.changeGold(player,-yb, Reasons.RAID_LIANXU_YB, Config.getId());
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getStatus()  == 0) {
							list.get(i).setStatus(1);
							ManagerPool.zonesFlopManager.addZoneReward(player,list.get(i).getZonemodelid(), true);
							Q_clone_activityBean data = ManagerPool.zonesManager.getContainer(list.get(i).getZonemodelid());
							MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("【{1}】扫荡完成"), data.getQ_duplicate_name());
							ZoneLog zlog = new ZoneLog();
							zlog.setPlayerid(player.getId());
							zlog.setType(2);
							zlog.setZonemodelid(list.get(i).getZonemodelid());
							zlog.setSid(player.getCreateServerId());
							LogService.getInstance().execute(zlog);
						}
					}
					ResZoneContinuousRaidsEndMessage cmsg = new ResZoneContinuousRaidsEndMessage();
					cmsg.setType((byte) 1);	//全部完成了
					cmsg.setZonetype(1);
					MessageUtil.tell_player_message(player, cmsg);
					ManagerPool.zonesFlopManager.LoginRaidRewarded(player);
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("您花费了{1}钻石，连续自动扫荡完成"), yb+"");
					player.getRaidcontinuouslist().clear();
				
				}else {
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("立即完成当前连续自动扫荡需要{1}钻石"), yb+"");
				}
			}	
			
		}
		//hongxiao.z 2014.1.11
		/*else if (msg.getZonetype() == 4) {
			List<ContinuousRaidsInfo> list = player.getQiyaocontinuouslist();
			if (list.size() == 0) {
				return;
			}

			int ms = (int)(System.currentTimeMillis()/1000);
			int time = list.get(list.size()-1).getTime() - ms;
			if (time > 0) {
				double dtime = time;
				double n =Math.ceil(dtime/60);
				Q_clone_activityBean zoneBean = ManagerPool.zonesManager.getContainer(list.get(list.size()-1).getZonemodelid());
				int yb = (int)(n*zoneBean.getQ_raids_min_yuanbao());	//每分钟消耗钻石
				if(yb > 0 && ManagerPool.backpackManager.checkGold(player, yb)){
					//检测2级密码
					if ( ManagerPool.protectManager.checkProtectStatus(player) ) {
						return;
					}
					
					ManagerPool.backpackManager.changeGold(player,-yb, Reasons.RAID_LIANXU_YB, Config.getId());
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getStatus()  == 0) {
							list.get(i).setStatus(1);
							ManagerPool.zonesFlopManager.addZoneReward(player,list.get(i).getZonemodelid(), true);
							Q_clone_activityBean data = ManagerPool.zonesManager.getContainer(list.get(i).getZonemodelid());
							MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("【{1}】扫荡完成"), data.getQ_duplicate_name());
							ZoneLog zlog = new ZoneLog();
							zlog.setPlayerid(player.getId());
							zlog.setType(2);
							zlog.setZonemodelid(list.get(i).getZonemodelid());
							zlog.setSid(player.getCreateServerId());
							LogService.getInstance().execute(zlog);
						}
					}
					ResZoneContinuousRaidsEndMessage cmsg = new ResZoneContinuousRaidsEndMessage();
					cmsg.setType((byte) 1);	//全部完成了
					cmsg.setZonetype(4);
					MessageUtil.tell_player_message(player, cmsg);
					ManagerPool.zonesFlopManager.LoginRaidRewarded(player);
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("您花费了{1}钻石，连续自动扫荡完成"), yb+"");
					player.getQiyaocontinuouslist().clear();
				
				}else {
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("立即完成当前连续自动扫荡需要{1}钻石"), yb+"");
				}
			}	
		}*/

	}
	

	/**检查整套副本内的人数
	 * @return 
	 * 
	 */
	public int checkZonePlayerNum(ZoneContext zcon){
		int num = 0;
		if (zcon != null) {
			List<MapConfig> mapConfiglist = zcon.getConfigs();
			for (MapConfig mapconfig : mapConfiglist) {
				Map map = ManagerPool.mapManager.getMap(mapconfig.getServerId(), mapconfig.getLineId(), mapconfig.getMapId());
				if (map != null ) {
					num = num + map.getPlayers().size();
				}
			}
		}
		return num;
	}

	
	/**获取副本当前层数
	 * -1表示没有
	 */
	public int getFloor(Player player){
		Map map = ManagerPool.mapManager.getMap(player);
		if (map != null) {
			ZoneContext zcon = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			if (zcon != null) {
				for (int i = 0; i < zcon.getConfigs().size(); i++) {
					if (zcon.getConfigs().get(i).getMapId() == map.getId()) {
						return i;
					}
				}
			}
		}
		
		return -1;
	}
	
	
	
	/**七曜副本奖励设定
	 * 
	 * @param player
	 * @param zoneModelId
	 * @param type  ==4表示七曜副本
	 */
	public void qiyaoReward(Player player, int zoneModelId, int type){
		Map map = ManagerPool.mapManager.getMap(player);
		if (map.getZoneId() > 0) {
			ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			if (zone == null) {
				return;
			}
			Raid raid = player.getRaidinfo();
			if (!player.getZoneinfo().containsKey(ZonesManager.ManualDeathnum+"_"+zoneModelId)) {
				player.getZoneinfo().put(ZonesManager.ManualDeathnum+"_"+zoneModelId,0);
			}

			player.getZoneinfo().put(ZonesManager.killmonsternum+"_"+zoneModelId,1);
			
			int time = (Integer)zone.getOthers().get("time");	//副本创建时间
			int newtime = (int)(System.currentTimeMillis()/1000 - time);	//本次完成时间
			player.getZoneinfo().put(ZonesManager.Manualendtime+"_"+zoneModelId, newtime);

			if(raid.getZoneraidtimes().containsKey(map.getZoneModelId()+"")){
				int ytime = raid.getZoneraidtimes().get(map.getZoneModelId()+"");
				if (ytime > newtime) {
					raid.getZoneraidtimes().put(map.getZoneModelId()+"", newtime);
				}
			}else {
				raid.getZoneraidtimes().put(map.getZoneModelId()+"", newtime);
			}
			
			int starnum = computeStar(player,zoneModelId);//计算星星数量
			HashMap<String, Integer> zonestar = raid.getZonestar();
			if((zonestar.containsKey(map.getZoneModelId()+"") == false) || (zonestar.get(map.getZoneModelId()+"") < starnum)) {
				zonestar.put(map.getZoneModelId()+"", starnum);	//写入当前副本最佳星级
			}
			
			ManagerPool.zonesFlopManager.addZoneReward(player, zoneModelId, false);
			ManagerPool.zonesFlopManager.stZonePassShow(player, type, zoneModelId); //类型4表示七曜战将副本
		}
	}
	
	/**
	 * 打开爬塔面板请求
	 * @param parameter
	 * @create	hongxiao.z      2014-1-4 上午9:54:38
	 */
	/*public void openPtMain(Player player) 
	{
		//默认取角色身上的挑战记录
		String challengeIndex = player.getZoneSaveInfoMap().get("towerindex");
		
		int zoneModelid;
		
		//没有数据, 默认获取第一个
		if(challengeIndex == null)
		{
			zoneModelid = ZonesConstantConfig.PT_MIN_ID;
		}
		else
		{
			//转换为副本ID
			zoneModelid = Integer.parseInt(challengeIndex);
		}
		
		ResPtMainMessage msg = new ResPtMainMessage();
		
		msg.setZoneid(zoneModelid);
		
		MessageUtil.tell_player_message(player, msg);
	}*/
	
	/**
	 * 爬塔副本ID识别
	 * @param zonesid
	 * @return
	 * @create	hongxiao.z      2014-1-4 下午4:13:40
	 */
	public boolean isPt(int zonesid)
	{
		return isPt(ManagerPool.zonesManager.getContainer(zonesid));
	}
	
	/**
	 * 爬塔副本ID识别
	 * @param zonesid
	 * @return
	 * @create	hongxiao.z      2014-1-4 下午4:13:40
	 */
	public boolean isPt(Q_clone_activityBean zoneBean)
	{
		if(zoneBean == null)
		{
			return false;
		}
		
		return zoneBean.getQ_zone_type() == ZonesConstantConfig.PT_TYPE ? true : false;
	}
	
	/**
	 * 获取爬塔位置信息
	 * @param player
	 * @create	hongxiao.z      2014-2-12 下午8:19:37
	 */
	public void getPtIndexMsg(Player player)
	{
		ResTowerIndexMessage msg = new ResTowerIndexMessage();
		
		//获取层数信息
		Integer zoneId = player.getZoneinfo().get(ZonesConstantConfig.PT_CHALLENGE);
		
		if(zoneId == null)//没有初始化
		{
			//获得最低层
			Q_clone_activityBean temp = DataManager.getInstance().q_clone_activityContainer.getTowerMin();
			
			if(player.getLevel() >= temp.getQ_min_lv())	//等级满足
			{
				zoneId = temp.getQ_id();
				
				//初始化数据
				player.getZoneinfo().put(ZonesConstantConfig.PT_CHALLENGE, zoneId);
			}
			else
			{
				return;
			}
		}
		
		//标识是否已通关了
		boolean isComp = false;
		
		//一层通关后ID会变成负数存储，这里要做处理
		if(zoneId < 0)
		{
			zoneId = Math.abs(zoneId);
			isComp = true;
		}
		
		msg.setZoneId(zoneId);
		
		Q_clone_activityBean bean = DataManager.getInstance().q_clone_activityContainer.get(zoneId);
		
		msg.setStage(bean.getQ_dungeon_stage());
		msg.setLevel(isComp ? 0 : bean.getQ_dungeon_level());
		
		//获取记录信息
		PtRecord record = getPtRecord(zoneId);
		
		String bestName 		= "";
		int    bestTime 		= 0;
		String latelyName 		= "";
		byte   latelyJob    	= 0;
		int    latelyFightPower = 0; 
		
		if(record != null)
		{
			bestName 			= record.getBestName();
			bestTime 			= record.getBestTime();
			latelyName 			= record.getLatelyName();
			latelyJob			= record.getLatelyJob();
			latelyFightPower	= record.getLatelyFightPower();
		}
		
		msg.setBestName(bestName);
		msg.setBestTime(bestTime);
		msg.setLatelyName(latelyName);
		msg.setLatelyJob(latelyJob);
		msg.setLatelyFightPower(latelyFightPower);
		
//		System.out.println("爬塔信息 --- 玩家[" + player.getName() + "], 当前层[" + msg.getStage() + "], 当前关卡[" + msg.getLevel() + "]," +
//					" 最佳通关者[" + msg.getBestName() + "], 最佳通关时间[" + msg.getBestTime() + "秒], 最近通关者[" + msg.getLatelyName() + "]," +
//					" 最近通关职业[" + msg.getLatelyJob() + "], 最近通关战斗力[" + msg.getLatelyFightPower() + "]");
		
//		log.error("爬塔信息 --- 玩家[" + player.getName() + "], 当前层[" + msg.getStage() + "], 当前关卡[" + msg.getLevel() + "]," +
//					" 最佳通关者[" + msg.getBestName() + "], 最佳通关时间[" + msg.getBestTime() + "秒], 最近通关者[" + msg.getLatelyName() + "]," +
//					" 最近通关职业[" + msg.getLatelyJob() + "], 最近通关战斗力[" + msg.getLatelyFightPower() + "]");
		
		MessageUtil.tell_player_message(player, msg);
		
		getPtGainRewardInfo(player);
	}
	
	/**
	 * 根据副本编号获取爬塔副本的记录信息
	 * @param zoneId
	 * @return
	 * @create	hongxiao.z      2014-2-13 下午12:32:33
	 */
	@SuppressWarnings("unchecked")
	public PtRecord getPtRecord(int zoneId)
	{
		if(towerRecord.isEmpty())	//获取记录列表数据
		{
			String value = ServerParamUtil.getNormalParamMap().get(ZonesConstantConfig.PT_RECORD);
			
			if(value != null)
			{
				try
				{
					towerRecord  = JSON.parseObject(VersionUpdateUtil.dateLoad(value), ConcurrentHashMap.class);
				} catch (Exception e) 
				{
					log.error("从数据库读取爬塔记录数据时格式转换错误！", e);
				}
			}
		}
		
		PtRecord record = towerRecord.get(zoneId);
		
		return record;
	}
	
	/**
	 * 保存爬塔记录信息
	 * @param zoneId
	 * @param record
	 * @create	hongxiao.z      2014-2-13 下午12:21:25
	 */
	public void savePtRecord(int zoneId, PtRecord record)
	{
		towerRecord.put(zoneId, record);
		
		//放入缓存更新队列
		String json = JSON.toJSONString(towerRecord, SerializerFeature.WriteClassName);
		ServerParamUtil.threadSaveNormal(ZonesConstantConfig.PT_RECORD, VersionUpdateUtil.dateSave(json));
		
		//立即更新到DB
//		ServerParamUtil.immediateSaveNormal(ZonesConstantConfig.PT_RECORD, JSON.toJSONString(towerRecord, SerializerFeature.WriteClassName));
	}
	
	/**
	 * 切换爬塔层
	 * @param player
	 * @create	hongxiao.z      2014-2-13 下午2:33:24
	 */
	public void changePtLevel(Player player)
	{
		//获取当前的副本ID
		Integer zoneId = player.getZoneinfo().get(ZonesConstantConfig.PT_CHALLENGE);
		
		//未开通爬塔系统
		if(zoneId == null) return;
		
		//通关后ID会变成负数
		if(zoneId > 0 ) return;
		
		zoneId = Math.abs(zoneId);
		
		//获取本层的数据
		Q_clone_activityBean bean = DataManager.getInstance().q_clone_activityContainer.get(zoneId);
		
		//下一层
		int stage = bean.getQ_dungeon_stage() + 1;
		int level = 1;
		
		//获取下一层数据
		Q_clone_activityBean nextBean = DataManager.getInstance().q_clone_activityContainer.getTowerBean(stage, level);
		
		//没有数据表示通关
		if(nextBean == null)
		{
			log.error("副本原型数据不存在! 层[" + stage + "], 关卡[" + level + "], 数据缺失或已通关所有关卡！");
			return;
		}
		
		//设置副本索引
		player.getZoneinfo().put(ZonesConstantConfig.PT_CHALLENGE, nextBean.getQ_id());
		getPtIndexMsg(player);
		
		//重置副本奖励领取信息
		int count = (int)ManagerPool.countManager.getCount(player, CountTypes.PT_GAIN_REWARD, null);
		ManagerPool.countManager.addCount(player, CountTypes.PT_GAIN_REWARD, null, 1, -count, 1);
		getPtGainRewardInfo(player);
		
		MessageUtil.tell_player_message(player, new ResTowerNextLvMessage());
	}
	
	/**
	 * 领取爬塔奖励
	 * @param player
	 * @create	hongxiao.z      2014-2-13 下午5:51:34
	 */
	public void gainPtAward(Player player)
	{
		//获取当前副本ID信息
		Integer zoneId = player.getZoneinfo().get(ZonesConstantConfig.PT_CHALLENGE);
		
		if(zoneId == null)
		{
			return;
		}
		
		//查看是否还有领取次数
		int count = (int)ManagerPool.countManager.getCount(player, CountTypes.PT_GAIN_REWARD, null);
		
		//次数已经全部领取
		if(getPtGainRewardNum(player) <= count)
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("今天的领取次数已经用完！"));	
			return;
		}
		
		zoneId = Math.abs(zoneId);
		
		//获取副本数据
		Q_clone_activityBean bean = DataManager.getInstance().q_clone_activityContainer.get(zoneId);
		
		//获取层数
		int stage = bean.getQ_dungeon_stage();
		
		//获取消耗
		Q_pt_reward_consumeBean consume = DataManager.getInstance().q_pt_reward_consumeContainer.get(count + 1);
		
		if(consume == null)
		{
			log.error("获取领取爬塔奖励消耗数据 --- 次数[" + (count+1) + "]的数据缺失！");
			return;
		}
		
		int golds = consume.getQ_golds();
		
		int playerGold = 0;
		if(player.getGold() != null)
		{
			playerGold = player.getGold().getGold();
		}
		
		if(playerGold < golds) //货币不足
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("钻石不满足领取条件，请先充值！"));	
			return;
		}
		
		//获取奖励
		Q_pt_awardBean award = DataManager.getInstance().q_pt_awardContainer.get(stage);
		
		//查看道具空间
		int size = 0;
		for (Entry<Integer, Integer> entry : award.gainItems()) 
		{
			if(entry.getKey() > 0)
			{
				size ++;
			}
		}
		
		//判断背包空间是否足够
		int emptynum = BackpackManager.getInstance().getEmptyGridNum(player);
		
		if(size > emptynum)
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("背包空间不足领取奖励，请整理背包后再领取！"));	
			return;
		}
		
		//扣除货币
		BackpackManager.getInstance().changeGold(player, -golds, Reasons.PT_GAIN_AWARD, Config.getId());
		
		//增加一次领取次数
		ManagerPool.countManager.addCount(player, CountTypes.PT_GAIN_REWARD, null, 1, 1, 1);
		
		//道具入包
		awardToBackpack(player, award.gainItems());
		
		//推送信息
		getPtGainRewardInfo(player);
		
		ResTowerRewardMessage msg = new ResTowerRewardMessage();
		MessageUtil.tell_player_message(player, msg);
	}
	
	/**
	 * 奖励入背包
	 * @param player
	 * @param items	
	 * 			key 道具ID  -1 铜钱   -2 钻石   -3 真气   -4 经验   -5 绑定钻石   -6 精魄
	 * 			value数量
	 * @create	hongxiao.z      2014-2-13 下午8:08:12
	 */
	private void awardToBackpack(Player player, Set<Entry<Integer, Integer>> items)
	{
		List<Item> list = new ArrayList<Item>();
		for (Entry<Integer, Integer> entry : items) 
		{
			switch(entry.getKey())
			{
				case -1:	//铜钱
					BackpackManager.getInstance().changeMoney(player, entry.getValue(), Reasons.PT_GAIN_AWARD, Config.getId());
					break;
				case -2:	//钻石
					BackpackManager.getInstance().changeGold(player, entry.getValue(), Reasons.PT_GAIN_AWARD, Config.getId());
					break;
				case -3:	//真气 暂无 
					break;
				case -4:	//经验
					PlayerManager.getInstance().addExp(player, (long)entry.getValue(), AttributeChangeReason.PT_GAIN_AWARD);
					break;
				case -5:	//绑定钻石
					BackpackManager.getInstance().changeBindGold(player, entry.getValue(), Reasons.PT_GAIN_AWARD, Config.getId());
					break;
				case -6:	//精魄
					BackpackManager.getInstance().changeSpirit(player, entry.getValue(), Reasons.PT_GAIN_AWARD, Config.getId());
					break;
				default:	//道具
					list.addAll(Item.createItems(entry.getKey(), entry.getValue(), true, 0));
			}
		}
		
		BackpackManager.getInstance().addItems(player, list, Reasons.PT_REWARD, Config.getId());
	}
	
	
	
	/**
	 * 爬塔奖励领取信息
	 * @param player
	 * @create	hongxiao.z      2014-2-13 下午3:11:55
	 */
	public void getPtGainRewardInfo(Player player)
	{
		//领取的次数
		int count = (int)ManagerPool.countManager.getCount(player, CountTypes.PT_GAIN_REWARD, null);
		
		//剩余的次数
		int surplus = getPtGainRewardNum(player) - count;
		
		//计算再次领取的花费
		int golds = 0;
		
		//获取下次领取消耗数据
		Q_pt_reward_consumeBean bean = DataManager.getInstance().q_pt_reward_consumeContainer.get(count + 1);
		
		if(bean != null)
		{
			golds = bean.getQ_golds();
		}
		else
		{
			log.error("爬塔领取奖励次数[" + (count + 1) + "]的数据不存在！");
		}
		
		ResTowerRewardSurplusMessage msg = new ResTowerRewardSurplusMessage();
		msg.setSurplus(surplus);
		msg.setGolds(golds);
//		System.out.println("爬塔副本奖励领取信息 --- 剩余次数[" + msg.getSurplus() + "], 领取需要花费钻石[" + msg.getGolds() + "]");
		
		MessageUtil.tell_player_message(player, msg);
	}
	
	/**
	 * 获取角色可以领取爬塔奖励的次数
	 * @param player
	 * @return
	 * @create	hongxiao.z      2014-2-13 下午3:25:28
	 */
	public int getPtGainRewardNum(Player player)
	{
		return ZonesConstantConfig.PT_GAIN_REWARD_COUNT + VipManager.getInstance().getPtAddition(player);
	}
	
	/**
	 * 创建首通奖励
	 * 通过副本原型的首通字段解析
	 * 道具id_数量;道具id_数量
	 * 如果没有_数量，则默认数量取1
	 * 
	 * @param bean
	 * @return
	 * @create	hongxiao.z      2014-1-10 上午9:40:57
	 */
	public List<Item> creationFirstReward(Q_clone_activityBean bean)
	{
		List<Item> temps = new ArrayList<Item>();
		String[] strs = bean.getQ_first_award().split(";");
		
		for (String str : strs) 
		{
			String[] infos = str.split("_");
			int itemid = Integer.parseInt(infos[0]);
			int itemCount = 1;
			
			if(infos.length > 1)
			{
				itemCount = Integer.parseInt(infos[1]);
			}
			
			List<Item> temp = Item.createItems(itemid, itemCount, false, 0);
			
			temps.addAll(temp);
		}
		
		return temps;
	}
	
	//爬塔GM命令测试	hongxiao.z
	//type		0.表示手动进入 1.获取位置信息 2.获取领奖信息 3.进入下一关卡
	public void testPt(Player player, int type)
	{
		switch(type)
		{
			case 0:
				ReqZoneIntoMessage msg = new ReqZoneIntoMessage();
				msg.setIsauto((byte)-4);
				stResReqZoneIntoMessage(player, msg);
				break;
			case 1:
				getPtIndexMsg(player);
				break;
			case 2:
				getPtGainRewardInfo(player);
				break;
			case 3:
				changePtLevel(player);
				break;
			case 4:
				gainPtAward(player);
				break;
		}
	}
	
	/**
	 * 个人爬塔信息发送世界服务器
	 * @param player
	 * @param stage
	 * @param level
	 * @create	hongxiao.z      2014-2-27 上午9:42:17
	 */
	public void sendRecord(Player player, int pataId)
	{
		long time = System.currentTimeMillis();
		player.setPataId(pataId);
		player.setPataTime(time);
		ReqSyncPlayerPataMessage msg = new ReqSyncPlayerPataMessage();
		msg.setPlayerId(player.getId());
		msg.setPataTime(time);
		msg.setPataMapId(pataId);
		MessageUtil.send_to_world(msg);
	}
}
