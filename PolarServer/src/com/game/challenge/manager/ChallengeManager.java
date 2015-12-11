package com.game.challenge.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.game.config.Config;
import com.game.backpack.bean.ItemInfo;
import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.biwudao.message.ResBiWuDaoStatusShowToClientMessage;
import com.game.challenge.bean.BossEventInfo;
import com.game.challenge.bean.BossKillEventInfo;
import com.game.challenge.bean.BossKillInfo;
import com.game.challenge.bean.ChallengeInfo;
import com.game.challenge.bean.ChallengeRewardInfo;
import com.game.challenge.message.ReqOpenChallengeToGameMessage;
import com.game.challenge.message.ReqSelectChallengeToGameMessage;
import com.game.challenge.message.ResBossEventMessage;
import com.game.challenge.message.ResBossKillMessage;
import com.game.challenge.message.ResOpenChallengeToClientMessage;
import com.game.challenge.message.ResRewardChallengeToClientMessage;
import com.game.chat.bean.GoodsInfoRes;
import com.game.count.structs.CountTypes;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.bean.Q_globalBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_mapBean;
import com.game.data.bean.Q_vipBean;
import com.game.data.manager.DataManager;
import com.game.epalace.structs.Epalace;
import com.game.goldraffle.structs.GoldRaffleEventData;
import com.game.guild.bean.EventInfo;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.monster.structs.Monster;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerSpeedReport;
import com.game.prompt.structs.Notifys;
import com.game.utils.CommonConfig;
import com.game.utils.MessageUtil;
import com.game.utils.ParseUtil;
import com.game.utils.Symbol;
import com.game.utils.TimeUtil;
import com.game.utils.VersionUpdateUtil;
import com.game.vip.manager.VipManager;
import com.game.vip.struts.GuideType;
import com.game.db.bean.BossEventBean;
import com.game.db.dao.BossEventDao;

/**
 * @author xuliang
 * 
 * @version 1.0.0
 * 
 * @since 2014-03-25
 * 
 * 打包地图管理类
 */

public class ChallengeManager {
	private static Object obj = new Object();
	protected Logger log = Logger.getLogger(ChallengeManager.class);
	//玩家管理类实例
	private static ChallengeManager manager;
	
	//! 掉落事件最大记录数
	static int dropEventMaxNum = 30;
	//! 击杀BOSS最大记录数
	static int killBossMaxNum = 5;
	
	//! 一般boss掉落事件
	private List<BossEventInfo> normalDropEvent = new ArrayList<BossEventInfo>();
	//! 需置顶的boss掉落事件
	private List<BossEventInfo> topDropEvent = new ArrayList<BossEventInfo>();
	//! Boss击杀事件
	private ConcurrentHashMap<Integer, List<BossKillEventInfo>> killBossEvent = new ConcurrentHashMap<Integer, List<BossKillEventInfo>>();
	//! Boss死亡状态
	private ConcurrentHashMap<Integer, Integer> bossState = new ConcurrentHashMap<Integer, Integer>();
	
	private BossEventDao bossEventDao = new BossEventDao();
	
	//! 普通掉落事件
	private static byte EVENT_NORMALDROP = 1;
	//! 置顶掉落事件
	private static byte EVENT_TOPDROP = 2;
	//! BOSS击杀事件
	private static byte EVENT_KILLBOSS = 3;
	
	private ChallengeManager(){}
	
	public static ChallengeManager getInstance(){
		synchronized (obj) {
			if(manager == null){
				manager = new ChallengeManager();
			}
		}
		return manager;
	}

	public void loadAll(){
		List<BossEventBean> events = new ArrayList<BossEventBean>();
		
		events = bossEventDao.select();
		Iterator<BossEventBean> iter = events.iterator();
		
		while(iter.hasNext()){
			BossEventBean bean = iter.next();
			
			try {				
				if (bean.getType() == EVENT_TOPDROP){
					BossEventInfo info = new BossEventInfo();
					
					info.setBossEventInfo(bean);
					topDropEvent.add(info);
				}else if (bean.getType() == EVENT_NORMALDROP){
					BossEventInfo info = new BossEventInfo();
					
					info.setBossEventInfo(bean);
					normalDropEvent.add(info);
				}else if (bean.getType() == EVENT_KILLBOSS){
					BossKillEventInfo info = new BossKillEventInfo();
					
					info.setBossKillEventInfo(bean);
					if (killBossEvent.containsKey(bean.getBossId())){
						killBossEvent.get(bean.getBossId()).add(info);
					}else{
						List<BossKillEventInfo> lst = new ArrayList<BossKillEventInfo>();
						lst.add(info);
						killBossEvent.put(info.getBossId(), lst);
					}
				}
				
			} catch (Exception e) {
				log.error(e,e);
			}
		}
	}
	
	public void BossChangeState(int monsterModuleId, Boolean isDead){
		int value = isDead == true ? 1 : 0;
		bossState.put(monsterModuleId, value);
	}
	
	public void addBossDieEvent(Player player, Monster monster){
		if (isNeedBoss(monster)){
			BossChangeState(monster.getModelId(), true);
			addBossEvent(player, monster, EVENT_KILLBOSS);
		}
	}
	
	public void addBossDropItemEvent(Player player, Monster monster, Item item){		
		Q_globalBean global = ManagerPool.dataManager.q_globalContainer
				.getMap().get(CommonConfig.TOP_BOSS_EVENT.getValue());
		
		String[] topDropItems = global.getQ_string_value().split(Symbol.FENHAO_REG);

		for (int i = 0; i < topDropItems.length; i++) {
			if (Integer.valueOf(topDropItems[i]) == item.getItemModelId()){
				addBossEvent(player, monster, EVENT_TOPDROP, item);
				return;
			}
		}
		
		if(isNeedToRecordItem(item)){
			addBossEvent(player, monster, EVENT_NORMALDROP, item);
		}
	}
	
	public boolean isNeedBoss(Monster monster){
		Q_globalBean global = ManagerPool.dataManager.q_globalContainer
				.getMap().get(CommonConfig.BOSS_QUERY_ID.getValue());
		
		String[] bossIDs = global.getQ_string_value().split(Symbol.FENHAO_REG);
		
		for(int i = 0; i < bossIDs.length; i ++){
			if (Integer.valueOf(bossIDs[i]) == monster.getModelId()){
				return true;
			}
		}
		return false;
	}
	
	private boolean isNeedToRecordItem(Item item){
		Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(item.getItemModelId());
		
		if(item instanceof Equip){
			Equip equip = (Equip)item;
			if(equip.getGradeNum() > (byte)7 &&
			   q_itemBean.getQ_equip_steplv() > 4){
				return true;
			}
		}
		
		if(q_itemBean.getQ_remarkable() == 1&&
		   q_itemBean.getQ_equip_steplv() > 2){
			return true;
		}
		/*
		 * 物品品质：0灰色，1白色，2蓝色，3橙色，4紫色，5紫红，6绿色
		 */
		if(q_itemBean.getQ_quality_lv() == 3 ||
		   q_itemBean.getQ_quality_lv() == 4 ||
		   q_itemBean.getQ_quality_lv() == 6)
			return true;
		
		return false;
	}
	
	private void addBossEvent(Player player, Monster monster, byte type, Item item){
		BossEventInfo info = new BossEventInfo();
		ItemInfo itemInfo = item.buildItemInfo();
		
		info.setType(type);
		info.setBossId(monster.getModelId());
		info.setBossMapId(monster.getMapModelId());
		info.setEventId(Config.getId());
		info.setItemInfo(itemInfo);
		info.setPlayerId(player.getId());
		info.setPlayerName(player.getName());
		info.setTouchTime(System.currentTimeMillis());
		
		Q_mapBean mapInfo = ManagerPool.dataManager.q_mapContainer.getMap().get(monster.getMapModelId());
		ParseUtil parseUtil = new ParseUtil();
		String parseString = String.format(String.format(ResManager.getInstance().getString("{@}在%s击杀%s掉落{$} {@}"), 
														mapInfo.getQ_map_name(), 
														monster.getName()));
		parseUtil.setValue(parseString, new ParseUtil.PlayerParm(player.getId(), player.getName()),
										new ParseUtil.VipParm(VipManager.getInstance().getVIPLevel(player), GuideType.DABAO.getValue()));
		
		List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
		GoodsInfoRes goodsInfo = new GoodsInfoRes();
		goodsInfo.setItemInfo(itemInfo);
		goodsInfos.add(goodsInfo);
		
		String notify = "";
		
		if (info.getType() == EVENT_NORMALDROP){
			notify = Notifys.CHAT_SYSTEM.getValue() + "";
			normalDropEvent.add(info);
		}else if(info.getType() == EVENT_TOPDROP){
			notify = MessageUtil.getNotifyType(Notifys.CHAT_SYSTEM.getValue(), Notifys.CUTOUT_ROLE.getValue());
//			MessageUtil.notify_All_player(
//					Notifys.CUTOUT_ROLE,
//					parseUtil.toString(),
//					goodsInfos,0);
			
			topDropEvent.add(info);
		}
		
		MessageUtil.notify_All_player(
				notify,
				parseUtil.toString(),
				goodsInfos,0);
		
		if (normalDropEvent.size() + topDropEvent.size() > dropEventMaxNum){
			//! 检查是否超过最大记录
			if(normalDropEvent.size() > 0){
				BossEventInfo removeInfo = normalDropEvent.remove(0);
				deleteBossEvent(removeInfo.getEventId());
			}else{
				BossEventInfo removeInfo = topDropEvent.remove(0);
				deleteBossEvent(removeInfo.getEventId());
			}
		}
		
		BossEventBean bean = info.getBossEventBean(item);
		bossEventDao.insert(bean);
		
		ResBossEventMessage msg = new ResBossEventMessage();
		msg.setType((byte)3);
		msg.getEvents().add(info);
		
		MessageUtil.tell_world_message(msg);
	}
	
	private void addBossEvent(Player player, Monster monster, byte type){
		BossKillEventInfo info = new BossKillEventInfo();
		
		info.setType(type);
		info.setBossId(monster.getModelId());
		info.setEventId(Config.getId());
		info.setPlayerId(player.getId());
		info.setPlayerName(player.getName());
		info.setTouchTime(System.currentTimeMillis());
		
		if (killBossEvent.containsKey(info.getBossId())){
			killBossEvent.get(info.getBossId()).add(info);
			// 检查是否超过最大记录
			if(killBossEvent.get(info.getBossId()).size() > killBossMaxNum){
				BossKillEventInfo removeInfo = killBossEvent.get(info.getBossId()).remove(0);
				deleteBossEvent(removeInfo.getEventId());
			}
		}else{
			List<BossKillEventInfo> lst = new ArrayList<BossKillEventInfo>();
			lst.add(info);
			killBossEvent.put(info.getBossId(), lst);
		}
		
		BossEventBean bean = info.getBossEventBean();
		bossEventDao.insert(bean);
	}
	
	private void deleteBossEvent(long eventId){
		bossEventDao.delete(eventId);
	}
	
	/**选择挑战面板
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqSelectChallengeToGameMessage(Player player ,ReqSelectChallengeToGameMessage msg) {
		if(msg.getType() == 1){
			ResBossKillMessage sendMsg = new ResBossKillMessage();
			
			for(Integer bossId : killBossEvent.keySet()){
				int isDead = 0;
				BossKillInfo killInfo = new BossKillInfo();
				
				if (bossState.containsKey(bossId)){
					isDead = bossState.get(bossId);
				}
				
				killInfo.setBossId(bossId);
				killInfo.setEvents(killBossEvent.get(bossId));
				killInfo.setIsDead((byte)isDead);
				
				sendMsg.getBossInfos().add(killInfo);
			}
			
			sendMsg.setServerTime(System.currentTimeMillis());
			MessageUtil.tell_player_message(player, sendMsg);
		}else if(msg.getType() == 2){
			ResBossEventMessage sendMsg = new ResBossEventMessage();
			
			sendMsg.setType((byte)2);
			
			for (int i = topDropEvent.size() - 1; i >= 0; i --){
				sendMsg.getEvents().add(topDropEvent.get(i));
			}
			
			for (int i = normalDropEvent.size() - 1; i >= 0; i --){
				sendMsg.getEvents().add(normalDropEvent.get(i));
			}
			
			MessageUtil.tell_player_message(player, sendMsg);
		}
		
		//! hide by xuliang 无用的面板信息
//		if (msg.getType() == 1) {
//			ManagerPool.zonesFlopManager.LoginRaidRewarded(player);//检测是否可领奖励
//		}else if (msg.getType() == 6 ) {//迷宫进入前的展示信息
//			ResRewardChallengeToClientMessage cmsg = new ResRewardChallengeToClientMessage();
//			HashMap<Integer, Integer> map1 = ManagerPool.zonesFlopManager.getZoneCountReward(player , 7 , 0,false);
//			HashMap<Integer, Integer> map2 = ManagerPool.zonesFlopManager.getZoneCountReward(player , 7 , 1,false);
//			Iterator<Entry<Integer, Integer>> it1 = map1.entrySet().iterator();
//			while (it1.hasNext()) {
//				Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it1.next();
//				ChallengeRewardInfo info = new ChallengeRewardInfo();
//				info.setId(entry.getKey());
//				info.setNum(entry.getValue());
//				info.setType(0);
//				cmsg.getRewardInfo().add(info);
//			}
//			Iterator<Entry<Integer, Integer>> it2 = map2.entrySet().iterator();
//			while (it2.hasNext()) {
//				Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it2.next();
//				ChallengeRewardInfo info = new ChallengeRewardInfo();
//				info.setId(entry.getKey());
//				info.setNum(entry.getValue());
//				info.setType(1);
//				cmsg.getRewardInfo().add(info);
//			}
//			MessageUtil.tell_player_message(player, cmsg);
//		}else if (msg.getType() == 10) {	//比武岛活动状态
//			ResBiWuDaoStatusShowToClientMessage cmsg = new ResBiWuDaoStatusShowToClientMessage();
//			cmsg.setStatusshow( ManagerPool.biWuDaoManager.getbiwudaotimeinfo());
//			MessageUtil.tell_player_message(player, cmsg);
//		}
			
	}

	
	/**
	 * 打开挑战面板消息
	 * @param player
	 * @param msg
	 */
	public void stReqOpenChallengeToGameMessage(Player player,ReqOpenChallengeToGameMessage msg) {
		
		//! xuliang 屏蔽无用代码
//		ChallengeInfo challengeInfo = new  ChallengeInfo();
//		
//		//地宫剩余次数
//		Epalace epalace = player.getEpalace();
//		ManagerPool.epalaceManeger.restorationEpalaceTime(player);
//		if (player.getLevel() >= 40) {
//			if (epalace.getMovenum() > 0) {
//				challengeInfo.setEpalacenum(ResManager.getInstance().getString("剩余")+epalace.getMovenum()+ResManager.getInstance().getString("次"));
//			}else {
//				int ms = (int)(System.currentTimeMillis()/1000)-epalace.getTime();
//				int shengyu = 2*60*60 - ms;
//				String timestr = TimeUtil.millisecondToStr(shengyu*1000);
//				challengeInfo.setEpalacenum(timestr+ResManager.getInstance().getString("后增加次数"));
//			}
//		}else{
//			challengeInfo.setEpalacenum(ResManager.getInstance().getString("达到40级后开放"));
//		}
//
//		
//		//VIP附加副本次数
//		int vipnum = 0;
//		// int vipid = VipManager.getInstance().getPlayerVipId(player);
//		// if(vipid>0){
//		// Q_vipBean vipdb = DataManager.getInstance().q_vipContainer.getMap().get(vipid);
//		// if (vipdb != null) {
//		// vipnum = vipdb.getQ_zone_time();
//		// }
//		// }
//		
//		//世界地图副本总次数
//		List<Q_clone_activityBean> data = ManagerPool.dataManager.q_clone_activityContainer.getList();
//		long sum = 0;
//		long manual = 0;
//		for (Q_clone_activityBean q_clone_activityBean : data) {
//			if (q_clone_activityBean.getQ_zone_type() == 1) {
//				if (player.getLevel() >= q_clone_activityBean.getQ_min_lv()) {
//					sum = vipnum + sum + q_clone_activityBean.getQ_manual_num();
//					manual =   manual + ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+q_clone_activityBean.getQ_id());	
//				}
//			}
//		}
//		if (sum - manual >  0) {
//			challengeInfo.setZonesnum((int) (sum - manual));	
//		}
//		
//		//校场剩余次数
//		Q_clone_activityBean jiaochangdata = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(3);
//		long jiaochang = ManagerPool.countManager.getCount(player, CountTypes.JIAOCHANG_NUM, "4003");
//		challengeInfo.setJiaochangnum((int) (jiaochangdata.getQ_manual_num() - jiaochang));
//		long min = TimeUtil.getDayOfMin(System.currentTimeMillis());
//		long hour =TimeUtil.getDayOfHour(System.currentTimeMillis());
//		long week =TimeUtil.getDayOfWeek(System.currentTimeMillis());
//		//[*][*][*][*][8:30-8:30];[*][*][*][*][12:30-12:30];[*][*][*][*][18:30-18:30];[*][*][*][*][23:00-23:00]
//		
//		if (hour < 10 || (hour == 10 && min <= 30)) {
//			challengeInfo.setBosstime("10:30");
//		}else if (hour < 14 || (hour == 14 && min <= 30)) {
//			challengeInfo.setBosstime("14:30");
//		}else if (hour < 18 || (hour == 18 && min <= 30)) {
//			challengeInfo.setBosstime("18:30");
//		}else if (hour < 22 || (hour == 22 && min <= 30))  {
//			challengeInfo.setBosstime("22:30");
//		}else{
//			challengeInfo.setBosstime("10:30");
//		}
//		
//		
//		challengeInfo.setLingditime(ResManager.getInstance().getString("暂未开放"));
//		challengeInfo.setGongchengtime(ResManager.getInstance().getString("暂未开放"));
//		
//		if (week  >= 6) {
//			challengeInfo.setDoubletime("10:00-20:00");
//		}else {
//			if (hour <= 12 ) {
//				challengeInfo.setDoubletime("12:00-13:00");
//			}else {
//				challengeInfo.setDoubletime("19:00-20:00");
//			}
//		}
//		
//		challengeInfo.setMazetime(ResManager.getInstance().getString("周三，周四20:30-21:00")); //迷宫
//
//		challengeInfo.setGongchengtime(ManagerPool.countryManager.getstrtimeinfo(0));	//圣盟争夺战
//		challengeInfo.setBiwudaotime(ResManager.getInstance().getString("周三，周日21:00-21:30"));
//		ResOpenChallengeToClientMessage cmsg = new ResOpenChallengeToClientMessage();
//		cmsg.setChallengeInfo(challengeInfo);
//		MessageUtil.tell_player_message(player, cmsg);
	}
	
	
	

	
	
	
	
	
	
}
