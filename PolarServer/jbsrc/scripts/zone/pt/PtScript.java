package scripts.zone.pt;


import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.fight.structs.Fighter;
import com.game.liveness.manager.LivenessManager;
import com.game.manager.ManagerPool;
import com.game.map.script.IEnterMapScript;
import com.game.map.structs.Map;
import com.game.monster.script.IMonsterDieScript;
import com.game.monster.structs.Monster;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.server.config.MapConfig;
import com.game.structs.Grid;
import com.game.task.struts.Task;
import com.game.task.struts.TaskEnum;
import com.game.util.TimerUtil;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.ScriptsUtils;
import com.game.zones.log.ZoneLog;
import com.game.zones.manager.ZonesConstantConfig;
import com.game.zones.manager.ZonesFlopManager;
import com.game.zones.manager.ZonesManager;
import com.game.zones.message.ResZoneLifeTimeMessage;
import com.game.zones.message.ResZonePassShowMessage;
import com.game.zones.script.ICreateZoneScript;
import com.game.zones.script.IZoneEventTimerScript;
import com.game.zones.structs.PtRecord;
import com.game.zones.structs.ZoneContext;
import com.game.zones.timer.ZoneLoopEventTimer;

/**
 * 爬塔副本脚本
 * @author hongxiao.z
 * @date   2014-1-2  下午5:06:54
 */
public class PtScript implements ICreateZoneScript, IMonsterDieScript, IZoneEventTimerScript, IEnterMapScript
{
	private static Logger log = Logger.getLogger(PtScript.class);
	
	@Override
	public int getId() 
	{
		return ZonesConstantConfig.PT_SCRIPT_ID;
	}
	
	@Override
	public void onEnterMap(Player player, Map map) 
	{
		if (!ZonesManager.getInstance().isPt(map.getZoneModelId())) 
		{
			return;
		}
		
		Boolean bool =  (Boolean)map.getParameters().get("ref");
		if(bool != null)
		{
			ResZoneLifeTimeMessage msg = new ResZoneLifeTimeMessage();
			msg.setZoneid(map.getZoneModelId());
			
			Q_clone_activityBean bean = DataManager.getInstance().q_clone_activityContainer.getMap().get(map.getZoneModelId());
			//获取剩余时间
			int sTime = (int)(map.getCreate() + bean.getQ_exist_time() - System.currentTimeMillis());
			
			msg.setSurplustime(sTime / 1000);
//			System.out.println(msg);
			MessageUtil.tell_player_message(player, msg);
			
			return;
		}
		
		map.getParameters().put("ref", false);
		
		ScriptsUtils.call(this.getId(), "refreshMonster", map.getZoneId(), 
				map.getZoneModelId(), map.getCreate() + PT_REF_MONSTER_TIME, map);
		
		//移除 智力MM 添加的 BUFF 效果
		ManagerPool.buffManager.removeByBuffId(player, 1070,1071,1072,1073);
		
		
//		MessageUtil.notify_map(map, Notifys.CUTOUT, String.format(ResManager.getInstance().getString("BOSS【%1】10秒后来袭，请做好准备"), monster.getQ_name()));
		
//		ResScriptCommonPlayerToClientMessage sendMessage = new ResScriptCommonPlayerToClientMessage();
//		sendMessage.setScriptid(getId());
//		sendMessage.setType(1);
//		sendMessage.setMessageData(JSON.toJSONString(paramMap));
//		MessageUtil.tell_player_message(player, sendMessage);
	}
	
	/**
	 * 怪物刷新调用
	 * @param parameters
	 * @create	hongxiao.z      2014-1-3 下午7:25:55
	 */
	public void refreshMonster(List<Object> parameters) 
	{
		
		long zoneId = (Long)parameters.get(0);
		int zoneModelId = (Integer)parameters.get(1);
		
		if (!ZonesManager.getInstance().isPt(zoneModelId)) 
		{
			return;
		}
		
		ZoneLoopEventTimer timer = new ZoneLoopEventTimer(this.getId(), zoneId, zoneModelId, parameters, 1000);
		TimerUtil.addTimerEvent(timer);
	}
	
	@Override
	public void action(long zoneId, int zoneModelId, List<Object> parameters) 
	{
		if (!ZonesManager.getInstance().isPt(zoneModelId)) 
		{
			return;
		}
		
		Map map = (Map)parameters.get(3);
		
		Boolean isRef = (Boolean) map.getParameters().get("ref");
		if(isRef)	//已经刷新就返回 
		{
			return;
		}
		
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(zoneId);
		if(zone == null){
			return;
		}
		//刷新时间到了
		if(System.currentTimeMillis() >= (Long)parameters.get(2))
		{
			//获取BOSS信息
			Q_clone_activityBean bean = DataManager.getInstance().q_clone_activityContainer.getMap().get(zoneModelId);
			//创建BOSS
			String[] boosInfo = bean.getQ_map_boss().split(",");
			int bossid = Integer.parseInt(boosInfo[0]);
			int refX = Integer.parseInt(boosInfo[1]);
			int refY = Integer.parseInt(boosInfo[2]);
			MapConfig config = zone.getConfigs().get(0);
			Grid grid = MapUtils.getGrid(refX, refY, config.getMapModelId());
			Monster monster = ManagerPool.monsterManager.createMonster(bossid , config.getServerId(), config.getLineId(), config.getMapId(), grid.getCenter());
			monster.setDirection((byte)0);
			ManagerPool.mapManager.enterMap(monster);
			map.getParameters().put("ref", true);
			map.getParameters().put("boss_name", monster.getName());
		}
	}
	
	/**
	 * 副本创建
	 */
	@Override
	public ZoneContext onCreate(Player player, int zoneModelId) 
	{
		if (!ZonesManager.getInstance().isPt(zoneModelId)) 
		{
			return null;
		}
		
		Q_clone_activityBean bean = DataManager.getInstance().q_clone_activityContainer.getMap().get(zoneModelId);
		
		if(bean == null)
		{
			return null;
		}
		
		//创建副本
		HashMap<String, Object> others = new HashMap<String, Object>();
		
		//采集地图信息
		List<Integer> maplist = JSON.parseArray(bean.getQ_mapid(),Integer.class); 
		
		ZoneContext zone = ManagerPool.zonesManager.setZone(bean.getQ_duplicate_name(), others, maplist, zoneModelId);	//创建副本，返回副本消息
		
		//获取地图配置
		MapConfig config = zone.getConfigs().get(0);
		Map zoneMap = ManagerPool.mapManager.getMap(config.getServerId(), config.getLineId(), config.getMapId());
		zone.getOthers().put("time", (int)(System.currentTimeMillis() / 1000));
		zone.getOthers().put("zoneprocess", 1);
		zoneMap.setRound(Math.max(zoneMap.getWidth(), zoneMap.getHeight()) * 2 + 1);
		
		log.info(new StringBuilder("[爬塔副本创建][玩家ID:").append(player.getId())
				.append("]-[副本原型ID:").append(zoneModelId).append("]-[副本实体ID[")
				.append(zone.getId()).append("]-[地图编号:").append(zoneMap.getMapModelid()).append("]").toString());
		
//		System.out.println(player.getName() + "创建了副本[" + bean.getQ_duplicate_name() + " - " + bean.getQ_id() + " > " + zone.getId() + "]");
		
		return zone;
	}

	//爬塔副本延迟刷怪时长
	public final static int PT_REF_MONSTER_TIME 	= 1000 * 10;

	//爬塔副本触发全服播报的层数系数
//	public final static int PT_BROADCAST 			= 10;
	
	@Override
	public void onMonsterDie(Monster monster, Fighter killer) 
	{
		Map map = ManagerPool.mapManager.getMap(monster);
		
		if(map == null)
		{
			return;
		}
		
		String bossName = (String) map.getParameters().get("boss_name");
		
		if(!bossName.equals(monster.getName()))
		{
			return;
		}
		
		if (!ZonesManager.getInstance().isPt(map.getZoneModelId())) 
		{
			return;
		}
		
		ResZonePassShowMessage passMsg = new ResZonePassShowMessage();
		
		//杀怪数量
		passMsg.setKillmonstrnum(1);
		
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
		Player player = PlayerManager.getInstance().getPlayer(killer.getId());
		
		passMsg.setZoneid(zone.getZonemodelid()); //设置副本编号
		passMsg.setIspass((byte)1);	//设置成功
		passMsg.setType((byte)0);	//0手动，1自动扫荡
		
		//通关用时
		int fTime = (int)(System.currentTimeMillis() - (map.getCreate() + PT_REF_MONSTER_TIME)) / 1000;
		passMsg.setTime(fTime);
		
		//获取奖励
		Q_clone_activityBean bean = DataManager.getInstance().q_clone_activityContainer.getMap().get(zone.getZonemodelid());
		//计算星级
//		int sTime = bean.getQ_exist_time() - passMsg.getTime();	//剩余时间
		int star = 5;
		passMsg.setStar((byte)star);	//设置星级
		
		ZonesFlopManager.getInstance().getZoneReward(player, bean.getQ_id(), star, passMsg);
		
		zone.getOthers().put("zoneprocess", -1);
		
		passMsg.setIsfirst(1);
		
		//副本通关额外处理
		completePt(player, zone.getZonemodelid(), fTime, bossName);
		
		//设置最快通关时间
		PtRecord record = ZonesManager.getInstance().getPtRecord(zone.getZonemodelid());
		passMsg.setThroughtime(record.getBestTime());
		
		try
		{
			//通过副本：
			ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.COMPLETECLASSICBATTLE, map.getZoneModelId());
			//通过副本并且等到s级评价：
			/*
			 * luminghua
			 * if(star>=3){
				ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.S_ZONE, map.getZoneModelId());
				if(star>=4){
					ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.SS_ZONE, map.getZoneModelId());
					if(star>=5){
						ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.SSS_ZONE, map.getZoneModelId());
					}
				}
			}*/
		}
		catch(Exception e)
		{
			log.error("副本通关时触发任务接口异常！", e);
		}
		
		//记录通关log
		ZoneLog logEntity = new ZoneLog();
		logEntity.setPlayerid(player.getId());
		logEntity.setTime(fTime);
		logEntity.setType(0);
		logEntity.setZonemodelid(zone.getZonemodelid());
		logEntity.setSid(player.getCreateServerId());
		LogService.getInstance().execute(logEntity);
		
		MessageUtil.tell_player_message(player, passMsg);//发送通关
	}
	
	/**
	 * 爬塔挑战完成处理
	 * @param player
	 * @create	hongxiao.z      2014-2-13 下午5:54:19
	 */
	public void completePt(Player player, int zoneModelId, int fTime, String npcName)
	{
		try
		{
			LivenessManager.getInstance().completePT(player);
		}
		catch(Exception e)
		{
			log.error(e, e);
		}
		
		//获取当前副本ID信息
		Integer zoneId = player.getZoneinfo().get(ZonesConstantConfig.PT_CHALLENGE);
		//获取副本数据
		Q_clone_activityBean bean = DataManager.getInstance().q_clone_activityContainer.get(zoneId);
		
		//是否破纪录
		PtRecord record = ZonesManager.getInstance().getPtRecord(zoneModelId);
		
		if(record == null) record = new PtRecord();
		
		boolean isFist = false;
		
		//破纪录
		if(record.getBestTime() == 0 || record.getBestTime() > fTime)
		{
			record.setBestName(player.getName());
			record.setBestId(player.getId());
			record.setBestTime(fTime);
			isFist = true;
		}
		
		record.setLatelyName(player.getName());
		record.setLatelyFightPower(player.getFightPower());
		record.setLatelyJob(player.getJob());
		
		//保存纪录
		ZonesManager.getInstance().savePtRecord(zoneModelId, record);
		
		//获取下一关卡信息
		Q_clone_activityBean nextBean = DataManager.getInstance().q_clone_activityContainer
								.getTowerBean(bean.getQ_dungeon_stage(), bean.getQ_dungeon_level() + 1);
		
		int nextZoneId = 0;
		
		if(nextBean == null)	//关卡不存在
		{
			nextZoneId = -zoneId;
		}
		else
		{
			nextZoneId = nextBean.getQ_id();
		}
		
		player.getZoneinfo().put(ZonesConstantConfig.PT_CHALLENGE, nextZoneId);
		
		//推送
		if(nextZoneId < 0 && isFist)
		{
			//算出时间
			int minute = record.getBestTime() / 60;
			String str = minute == 0 ? "" : minute + "分钟";
			int s = record.getBestTime() % 60;
			str = s == 0 ? str + "" : str + (s + "秒");
			
			MessageUtil.notify_All_player(Notifys.CUTOUT, 
					"【精英挑战】{1}实力非凡，在" + str + "内将{2}击倒在地!", "<font color = '#d20404'>" + player.getName() + "</font>" ,
					"<font color = '#fcf600'>" + "【" + npcName + "】" + "</font>");
		}
		
		//记录信息
		ZonesManager.getInstance().sendRecord(player, bean.getQ_id());
	}
}
