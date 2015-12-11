package com.game.country.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.game.backpack.structs.Item;
import com.game.batter.message.ResMonsterBatterToClientMessage;
import com.game.buff.manager.BuffManager;
import com.game.buff.structs.Buff;
import com.game.config.Config;
import com.game.cooldown.structs.CooldownTypes;
import com.game.count.structs.Count;
import com.game.count.structs.CountTypes;
import com.game.country.bean.CountryTopInfo;
import com.game.country.bean.CountryWarInfo;
import com.game.country.bean.JobAwardInfo;
import com.game.country.log.CountryLog;
import com.game.country.message.ReqCountrySiegeSelectToClientMessage;
import com.game.country.message.ReqCountryStructureInfoToWoridMessage;
import com.game.country.message.ReqCountrySyncKingCityToWoridMessage;
import com.game.country.message.ReqCountryWarCarInAdvanceToGameMessage;
import com.game.country.message.ReqCountryWarCarToGameMessage;
import com.game.country.message.ReqGetTopGuildListToWorldMessage;
import com.game.country.message.ResCountryArtilleryLocusToClientMessage;
import com.game.country.message.ResCountryJobAwardInfoToClientMessage;
import com.game.country.message.ResCountrySiegeHomingYuxiToClientMessage;
import com.game.country.message.ResCountrySiegeSelectToGameMessage;
import com.game.country.message.ResCountrySiegeWarStateToClientMessage;
import com.game.country.message.ResCountrySiegeYuXiImmediateToClientMessage;
import com.game.country.message.ResCountrySyncKingCityToGameMessage;
import com.game.country.message.ResCountryTopInfoToClientMessage;
import com.game.country.message.ResCountryOverTopInfoToClientMessage;
import com.game.country.message.ResCountryWarCarDamageToClientMessage;
import com.game.country.message.ResGetTopGuildListToGameMessage;
import com.game.country.structs.CountryData;
import com.game.country.structs.CountryFightStatus;
import com.game.country.structs.GuildPlayerInfo;
import com.game.country.structs.KingCity;
import com.game.country.structs.KingData;
import com.game.country.structs.SiegeSMS;
import com.game.country.timer.TransferMirrorBackEvent;
import com.game.csys.message.ResPlayerKillMessage;
import com.game.data.bean.Q_activity_monstersBean;
import com.game.data.bean.Q_characterBean;
import com.game.data.bean.Q_globalBean;
import com.game.data.bean.Q_mapBean;
import com.game.data.bean.Q_npcBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.drop.structs.MapDropInfo;
import com.game.guild.bean.GuildInfo;
import com.game.guild.manager.GuildServerManager;
import com.game.guild.structs.GuildTmpInfo;
import com.game.guild.structs.GuildTmpMember;
import com.game.languageres.manager.ResManager;
import com.game.mail.manager.MailServerManager;
import com.game.manager.ManagerPool;
import com.game.map.bean.DropGoodsInfo;
import com.game.map.manager.MapManager;
import com.game.map.message.ResRoundObjectsMessage;
import com.game.map.structs.Area;
import com.game.map.structs.Effect;
import com.game.map.structs.Map;
import com.game.message.Message;
import com.game.monster.manager.MonsterManager;
import com.game.monster.structs.Monster;
import com.game.npc.manager.NpcManager;
import com.game.npc.struts.NPC;
import com.game.pet.struts.Pet;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.prompt.structs.Notifys;
import com.game.server.impl.WServer;
import com.game.skill.structs.Skill;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.structs.Reasons;
import com.game.util.TimerUtil;
import com.game.utils.CommonConfig;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.ServerParamUtil;
import com.game.utils.Symbol;
import com.game.utils.TimeUtil;

public class CountryManager {

	/**
	 * 王城争霸战
	 * 
	 */
	protected Logger log = Logger.getLogger(CountryManager.class);
	// 玩家管理类实例
	private static CountryManager manager;
	private static Object obj = new Object();

	public static CountryManager getInstance() {
		
		synchronized (obj) {
			if (manager == null) {
				manager = new CountryManager();
			}
		}
		return manager;
	}

	public CountryManager() {

	}

	public static int KILL = 1; // 杀敌数量
	public static int DEATH = 2; // 死亡次数
	public static int HURT = 3; // 总伤害输出
	public static int BEENHURT = 4; // 被伤害总数

	/**
	 * 王城争霸战，排行榜
	 * 
	 */
	HashMap<Long, SiegeSMS> SiegeSMSTopMap = new HashMap<Long, SiegeSMS>();

	/**
	 * 王城雕像是否展示 KEY
	 * 
	 */
	public static String WANGCHENGDIAOXIANG = "WANGCHENGDIAOXIANG";

	// 王城简要信息，储存所有国家圣盟
	public static HashMap<Integer, Long> kingcitymap = new HashMap<Integer, Long>();

	// 攻城状态： 0没有攻城，1攻城进行中,2结束
	private int siegestate;

	// 王城信息
	private KingCity kingcity = new KingCity();

	// 攻城战时间记录 用来做倒计时(秒)
	private long siegecountdown;

	// 进入战场随机传送点
	private int[][] ALONE_XY = { { 136, 233 }, { 146, 273 }, { 126, 253 } };

	// 王城防守方复活点
	private int[] GUARD_XY = { 458, 43 };

	// 进攻者复活点
	private int[] ATTACK_XY = { 219, 275 };

	// 王座NPC
	private int YuXiNpc = 12300;

	// 开区多少天后攻城
	private int OpenArea = 3;

	// 点击Npc玉玺开始夺取的时间,脚本用来控制公告出现的频繁度
	private long yuxitime;

	// 刷怪标记
	private int monstatus;

	// 到点全部传送回城
	private long movetime;

	// 王座所属阵营
	private int kingGroupmark;
	
	//普通玩家阵营
	private int playerGoruoupmark=2;
	

	public int getPlayerGoruoupmark() {
		return playerGoruoupmark;
	}

	public void setPlayerGoruoupmark(int playerGoruoupmark) {
		this.playerGoruoupmark = playerGoruoupmark;
	}

	public int getKingGroupmark() {
		return kingGroupmark;
	}

	public void setKingGroupmark(int kingGroupmark) {
		this.kingGroupmark = kingGroupmark;
	}

	/**
	 * 攻城战地图id
	 * 
	 */
	public static int SIEGE_MAPID = 300010;

	/**
	 * 拔起玉玺需要的帮贡仓库金币数量
	 * 
	 * @return
	 */
	public int getYuXiGuildGold() {
		return ManagerPool.dataManager.q_globalContainer.getMap().get(110)
				.getQ_int_value();
	}

	public long getYuxitime() {
		return yuxitime;
	}

	public void setYuxitime(long yuxitime) {
		this.yuxitime = yuxitime;
	}

	public KingCity getKingcity() {
		return kingcity;
	}

	public void setKingcity(KingCity kingcity) {
		this.kingcity = kingcity;
	}

	/**
	 * 攻城战状态
	 * 
	 * @return
	 */
	public int getSiegestate() {
		return siegestate;
	}

	public void setSiegestate(int siegestate) {
		this.siegestate = siegestate;
	}

	/**
	 * 攻城开始时间
	 * 
	 * @return
	 */
	public long getSiegecountdown() {
		return siegecountdown;
	}

	public void setSiegecountdown(long siegecountdown) {
		this.siegecountdown = siegecountdown;
	}

	public int getYuXiNpc() {
		return YuXiNpc;
	}

	public void setYuXiNpc(int yuXiNpc) {
		YuXiNpc = yuXiNpc;
	}

	public int getMonstatus() {
		return monstatus;
	}

	public void setMonstatus(int monstatus) {
		this.monstatus = monstatus;
	}

	// 0代表 死亡
	// 1代表 可攻击
	// 2 代表 不可攻击
	// 城门1 状态
	private int cm1Status;
	// 城门2 状态
	private int cm2Status;
	// 城门3 状态
	private int cm3Status;
	// 王座 状态
	private int kingStatus;

	public int getCm1Status() {
		return cm1Status;
	}

	public void setCm1Status(int cm1Status) {
		this.cm1Status = cm1Status;
	}

	public int getCm2Status() {
		return cm2Status;
	}

	public void setCm2Status(int cm2Status) {
		this.cm2Status = cm2Status;
	}

	public int getCm3Status() {
		return cm3Status;
	}

	public void setCm3Status(int cm3Status) {
		this.cm3Status = cm3Status;
	}

	public int getKingStatus() {
		return kingStatus;
	}

	public void setKingStatus(int kingStatus) {
		this.kingStatus = kingStatus;
	}
	
	
	// 城门1 状态
	private int sx1Status;
	// 城门2 状态
	private int sx2Status;
	// 城门3 状态
	private int sx3Status;
	
	public int getSx1Status() {
		return sx1Status;
	}

	public void setSx1Status(int sx1Status) {
		this.sx1Status = sx1Status;
	}

	public int getSx2Status() {
		return sx2Status;
	}

	public void setSx2Status(int sx2Status) {
		this.sx2Status = sx2Status;
	}

	public int getSx3Status() {
		return sx3Status;
	}

	public void setSx3Status(int sx3Status) {
		this.sx3Status = sx3Status;
	}


	//参与攻城战的 玩家集合
	private HashMap<Long, GuildPlayerInfo> map = new HashMap<Long, GuildPlayerInfo>();
	
	public void playerData(Player player, String name) {
		MessageUtil.notify_player(player, Notifys.SUCCESS, "player.getGuildId():"+player.getGuildId());
		MessageUtil.notify_player(player, Notifys.SUCCESS, "getKingcity().getGuildid():"+getKingcity().getGuildid());
		MessageUtil.notify_player(player, Notifys.SUCCESS, "player.getMemberInfo().getGuildPowerLevel():"+player.getMemberInfo().getGuildPowerLevel());
	}
	
	/**
	 * 王城之主 登录时 全服广播
	 * @param player
	 */
	public void kingLogin(Player player) {
		
	
		KingCity city = CountryManager.getInstance().getKingcity();
		if(player.getGuildId()==city.getGuildid()&&player.getMemberInfo().getGuildPowerLevel() == 1 ){
			
			MessageUtil.notify_All_player(
					Notifys.CUTOUT_ROLE,
					 ResManager.getInstance().getString("王城之主 {1}驾到，众人回避！"),player.getName());
			MessageUtil.notify_All_player(
					Notifys.CHAT_SYSTEM,
					 ResManager.getInstance().getString("王城之主  {1}驾到，众人回避！"),player.getName());
			
		}
	}
	
	public void jinyan(Player player, String name) {
		if(player.getGuildId()== getKingcity().getGuildid()&&player.getMemberInfo().getGuildPowerLevel() == 1 ){
			long curday = TimeUtil.GetCurTimeInMin(4);
			
		if	( kingcity.getSalarymap().get("jinyan")!=null)
		{
		
			if(curday<= (int)kingcity.getSalarymap().get("jinyan")){
				MessageUtil.notify_player(player, Notifys.SUCCESS, "已经使用过此权限");
				return;
			}
		}
//		long manual =ManagerPool.countManager.getCount(player, CountTypes.JINYAN, "");
//		if(manual>=1){
//			MessageUtil.notify_player(player, Notifys.SUCCESS, "禁言次数已经使用完毕");
//			return;
//		}
//		if(TimeUtil.isToday(player.getLastChangePowerLevelTime())){
//		
//		}
		
		Player onlinePlayerByName = PlayerManager.getInstance()
				.getOnlinePlayerByName(name);
		if (onlinePlayerByName == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, name
					+ "不存在或者已离线");
			return;
		}
		long time = 60 * 60 * 1000;
		// if(ChatManager.getInstance().isProhibitChat(player)){
		// 如果在禁言中则替换原时间
		onlinePlayerByName.setStartProhibitChatTime(System
				.currentTimeMillis());
		onlinePlayerByName.setProhibitChatTime(time);
		// }else{
		// player.setStartProhibitChatTime(System.currentTimeMillis());
		// player.setProhibitChatTime(time);
		// }
		//MessageUtil.notify_player(player, Notifys.SUCCESS, name
		//		+ "成功禁言1小时");
		MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM, 
				ResManager.getInstance().getString("勇者城城主{1}行驶了禁言特权，{2}被禁言1小时"),player.getName(),name);
		MessageUtil.notify_All_player(Notifys.CUTOUT, 
				ResManager.getInstance().getString("勇者城城主{1}行驶了禁言特权，{2}被禁言1小时"),player.getName(),name);
		MessageUtil.notify_player(onlinePlayerByName,Notifys.CHAT_SYSTEM, 
				ResManager.getInstance().getString("您被勇者城城主{1}禁言1小时，期间不能在世界、战盟、队伍、私聊频道发言！"),player.getName());		
		MessageUtil.notify_player(onlinePlayerByName,Notifys.STATE, 
				ResManager.getInstance().getString("您被勇者城城主{1}禁言1小时，期间不能在世界、战盟、队伍、私聊频道发言！"),player.getName());		
//		　　。
		kingcity.getSalarymap().put("jinyan", (int)curday);
		}else{
			MessageUtil.notify_player(player, Notifys.ERROR,"您不是勇者城城主，没有禁言权限！");
		}
	}
	

	private int[][] cm1positions = {{186,226},{187,226},
			{186,227},{187,227},{187,227}, {188,227},
			{186,228},{187,228},{187,228}, {188,228},{189,228},
											{187,229}, {188,229},{189,229},{190,229}, {191,229},{192,229},
															 {188,230},{189,230},{190,230}, {191,230},{192,230},
																			 {189,231},{190,231}, {191,231},{192,231},{193,231},
																											  {191,232},{192,232},{193,232}};
	private int[][] cm2positions = {{305,167},{306,167},
			{305,168},{306,168},{307,168}, {308,168},
			{305,169},{306,169},{307,169}, {308,169},{309,169},
			{305,170},{306,170},{307,170}, {308,170},{309,170},{310,170}, {311,170},{312,170},
														     {308,171},{309,171},{310,171}, {311,171},{312,171},
																			 {309,172},{310,172}, {311,172},{312,172},{313,172},
																											  {311,173},{312,173},{313,173}};
	private int[][] cm3positions = {{407,115},{408,115},{409,115},
			{407,116},{408,116},{409,116},{410,116}, {411,116},
			{407,117},{408,117},{409,117},{410,117}, {411,117},{412,117},
							{408,118},{409,118},{410,118}, {411,118},{412,118},{413,118}, {414,118},{415,118},
														     {411,119},{412,119},{413,119}, {414,119},{415,119},
																			 {412,120},{413,120}, {414,120},{415,120},{416,120},
																											  {414,121},{415,121},{416,121}};
	
	//0 是阻挡  2 是可行
	public void opendoor1(int option){
		
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(SIEGE_MAPID);
		// 判断起点是否可以站立
		for (int i = 0; i < cm1positions.length; i++) {
			Position position = new Position((short)(cm1positions[i][0]*25), (short)(cm1positions[i][1]*25));
			Grid startGrid = MapUtils.getGrid(position, blocks);	
			startGrid.setBlock(option);
//			System.out.println("封住的点："+startGrid.getX() + "," + startGrid.getY());
		}
	}
	
	public void opendoor2(int option){
		
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(SIEGE_MAPID);
		// 判断起点是否可以站立
		for (int i = 0; i < cm2positions.length; i++) {
			Position position = new Position((short)(cm2positions[i][0]*25), (short)(cm2positions[i][1]*25));
			Grid startGrid = MapUtils.getGrid(position, blocks);	
			startGrid.setBlock(option);
		}
	}
	
	public void opendoor3(int option){
		
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(SIEGE_MAPID);
		// 判断起点是否可以站立
		for (int i = 0; i < cm3positions.length; i++) {
			Position position = new Position((short)(cm3positions[i][0]*25), (short)(cm3positions[i][1]*25));
			Grid startGrid = MapUtils.getGrid(position, blocks);	
			startGrid.setBlock(option);
		}
	}
	
	
	/**
	 * 载入攻城战王城内容
	 * 
	 */
	public void loadkingcity(int sid) {
		int countryid = WServer.getGameConfig().getCountryByServer(sid);
		for (int i = 0; i < 10; i++) {
			if (ServerParamUtil.getImportantParamMap().containsKey(
					ServerParamUtil.KINGCITYWAR + i)) {
				if (countryid == i) {
					String dataString = ServerParamUtil.getImportantParamMap()
							.get(ServerParamUtil.KINGCITYWAR + countryid);
					KingCity jskingcity = JSON.parseObject(dataString,
							KingCity.class);
					setKingcity(jskingcity);
					kingcitymap.put(i, jskingcity.getGuildid());
					setdiaoxiang(true, sid);
				} else {
					String otherdata = ServerParamUtil.getImportantParamMap()
							.get(ServerParamUtil.KINGCITYWAR + countryid);
					if (otherdata != null) {
						KingCity otherking = JSON.parseObject(otherdata,
								KingCity.class);
						kingcitymap.put(i, otherking.getGuildid());
					}

				}
			}
		}
	}

	/**
	 * 同步所其他国家王城
	 * 
	 * @param msg
	 */
	public void stResCountrySyncKingCityToGameMessage(
			ResCountrySyncKingCityToGameMessage msg) {
		kingcitymap.put(msg.getCountryid(), msg.getGuildid());
	}

	/**
	 * 得到攻城战地图
	 * 
	 * @return
	 */
	public Map getSiegeMap() {
		return ManagerPool.mapManager.getMap(WServer.getInstance()
				.getServerId(), 1, SIEGE_MAPID);
	}

	/**
	 * 攻城战循环调用
	 * 
	 */
	String playerName="";
	String kingName= "";
	
	
	public void say1() {
		 playerName="";
		 kingName= "";
		 StringBuffer playerNames= new StringBuffer();
		
		if(getKingcity().getGuildid()==0){
			for (int i = 0; i < top5infolist.size(); i++) {
				playerNames.append(top5infolist.get(i).getGuildName());
				playerNames.append("、");
			}
			if(playerNames.charAt(playerNames.length()-1)=='、') playerNames.deleteCharAt(playerNames.length()-1);
			playerName = playerNames.toString();
			
			MessageUtil.notify_All_player(
					Notifys.SROLL,
					ResManager.getInstance().getString(
							"今日攻城战将在半小时后开启，总战斗力排行前4的战盟可以获得参赛资格！当前前4的战盟为：{1}"),playerName);
			MessageUtil.notify_All_player(
					Notifys.CHAT_SYSTEM,
					ResManager.getInstance().getString(
							"今日攻城战将在半小时后开启，总战斗力排行前4的战盟可以获得参赛资格！当前前4的战盟为：{1}"),playerName);
		}else{
			int index = 0;
			boolean infour = false;
			for (int i = 0; i < top5infolist.size(); i++) {
				if(getKingcity().getGuildid()==top5infolist.get(i).getGuildId()){
					index = i;
					infour = true;
				}
			}
			if(infour){
				for (int i = 0; i < top5infolist.size(); i++) {
					if(i==index){
						continue;
					}
					playerNames.append(top5infolist.get(i).getGuildName());
					playerNames.append("、");
				}
				if(playerNames.charAt(playerNames.length()-1)=='、') playerNames.deleteCharAt(playerNames.length()-1);
				playerName = playerNames.toString();
				kingName = top5infolist.get(index).getGuildName();
			MessageUtil.notify_All_player(
					Notifys.SROLL,
					ResManager.getInstance().getString(
							"今日攻城战将在半小时后开启，总战斗力排行前3的战盟可以获得攻方资格！当前前3的战盟为：{1} 守方战盟为：{2}"),playerName,kingName);
			MessageUtil.notify_All_player(
					Notifys.CHAT_SYSTEM,
					ResManager.getInstance().getString(
							"今日攻城战将在半小时后开启，总战斗力排行前3的战盟可以获得攻方资格！当前前3的战盟为：{1} 守方战盟为：{2}"),playerName,kingName);
			}else{
				
				for (int i = 0; i < top5infolist.size(); i++) {
					if(i<3){
						playerNames.append(top5infolist.get(i).getGuildName());
						playerNames.append("、");

					}
				}
				
				if(playerNames.charAt(playerNames.length()-1)=='、') playerNames.deleteCharAt(playerNames.length()-1);
				playerName = playerNames.toString();
				kingName =getKingcity().getGuildname();
			MessageUtil.notify_All_player(
					Notifys.SROLL,
					ResManager.getInstance().getString(
							"今日攻城战将在半小时后开启，总战斗力排行前3的战盟可以获得攻方资格！当前前3的战盟为：{1}  守方战盟为：{2}"),playerName,kingName);
			MessageUtil.notify_All_player(
					Notifys.CHAT_SYSTEM,
					ResManager.getInstance().getString(
							"今日攻城战将在半小时后开启，总战斗力排行前3的战盟可以获得攻方资格！当前前3的战盟为：{1}  守方战盟为：{2}"),playerName,kingName);
				
				
				
			}
		}
	}
	public void say2() {
		if(kingName!=null){
			kingName ="、"+kingName;
		}
		MessageUtil.notify_All_player(
				Notifys.SROLL,
				ResManager.getInstance().getString(
						"今日攻城战将在10分钟后开启，请{1}做好战前准备"),playerName+" "+kingName);
		MessageUtil.notify_All_player(
				Notifys.CHAT_SYSTEM,
				ResManager.getInstance().getString(
						"今日攻城战将在10分钟后开启，请{1}做好战前准备"),playerName+" "+kingName);
	}
	public void say3() {
		MessageUtil.notify_All_player(
				Notifys.SROLL,
				ResManager.getInstance().getString(
						"今日攻城战即将开启，请{1}做好战前准备"),playerName+" "+kingName);
		MessageUtil.notify_All_player(
				Notifys.CHAT_SYSTEM,
				ResManager.getInstance().getString(
						"今日攻城战即将开启，请{1}做好战前准备"),playerName+" "+kingName);
	}
	public void say4() {
		MessageUtil.notify_All_player(Notifys.SROLL, ResManager
				.getInstance()
				.getString("攻城战已经开启，本次参与活动的战盟为：{1}！以上战盟成员可以打开攻城战界面传送到战场进行激战！"),playerName+kingName);
		MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM, ResManager
				.getInstance()
				.getString("攻城战已经开启，本次参与活动的战盟为：{1}！以上战盟成员可以打开攻城战界面传送到战场进行激战！"),playerName+kingName);
	}
	
	public void loopcall() {
		long millis = System.currentTimeMillis();
		long week = TimeUtil.getDayOfWeek(millis);
		long min = TimeUtil.getDayOfMin(millis);
		long hour = TimeUtil.getDayOfHour(millis);
		int kday = TimeUtil.getOpenAreaDay();
		
		
		if (kday >= 3) {
			if (week == 5 || kday == 3) {
//				//判断week 的第一个星期
				
//				if(week==5){
//					
//					if(kday<=5 && kday!=3){
//						return;
//					}
//					
//				}
				if (hour == 19 && min == 28) {
					ReqGetTopGuildListToWorldMessage cmsg = new ReqGetTopGuildListToWorldMessage();
					MessageUtil.send_to_world(cmsg);
				}
				
				if (hour == 19 && min == 30) {
					setMonstatus(0);
					setSiegestate(0);
					say1();
				} else if (hour == 19 && min == 50) {
					say2();
				} else if (hour == 19 && min == 58) {
					say3();
				} else if (hour == 20 && (min == 0 )) { // 开始战斗
					
					if (getSiegestate() == 0) {
						say4();
						backtocitymove();
						startSiegeWar();
					}
				} else if (hour == 20 && min == 30) { // 刷怪
					// if (getMonstatus() == 0) {
					// appearMonster();
					// }

				} else if (hour == 21 && (min == 0 || min == 1)) {// 战斗结束
					if (getSiegestate() == 1) {
						SiegeEnd(0);
					}
				} else if (hour == 21 && min == 2) {// 传送
					backtocitymove();
				} else if (hour == 23 && min >= 58) {
					setSiegestate(0);
				}

				// if (hour == 20 && min >= 10 && min <=50 && min % 10 == 0) {
				// RefreshMapItemDrop();
				// }

				// }
				// if (week == 4 ) {
				// if (hour == 16 && min == 00) {
				// setMonstatus(0);
				// setSiegestate(0);
				// MessageUtil.notify_All_player(Notifys.SROLL,
				// ResManager.getInstance().getString("今日王城攻城战将在半小时后开启，请参战各方做好战前准备"));
				// }else if (hour == 16 && min == 00) {
				// MessageUtil.notify_All_player(Notifys.SROLL,
				// ResManager.getInstance().getString("今日王城攻城战将在10分钟后开启，请参战各方做好战前准备"));
				// }else if (hour == 16 && (min == 10 || min == 31)) { //开始战斗
				// if (getSiegestate() == 0) {
				// backtocitymove();
				// startSiegeWar();
				// }
				// }else if (hour == 16 && min == 10) { //刷怪
				// if (getMonstatus() == 0) {
				// appearMonster();
				// }
				//
				// }else if (hour == 17 && (min == 0 || min == 1) ){//战斗结束
				// if (getSiegestate() == 1) {
				// SiegeEnd(0);
				// }
				// }else if (hour == 17 && min == 2){//传送
				// backtocitymove();
				// }else if (hour == 17 && min >= 17) {
				// setSiegestate(0);
				// }

				// if (hour == 17 && min >= 10 && min <=50 && min % 10 == 0) {
				// RefreshMapItemDrop();
				// }

			}
		}
	}

	
	public void initMonster() {
		
		List<Q_activity_monstersBean> li  = DataManager.getInstance().q_activity_monstersContainer.getList();
		
		for(Q_activity_monstersBean bean:li){
			if(bean.getQ_map()!=SIEGE_MAPID){
				continue;
			}
			if( TimeUtil.checkRangeTime(bean.getQ_refreshtime())){
				List<Integer> mapidlist = JSON.parseArray(bean.getQ_monsters(),Integer.class);
				CountryFightStatus.SX1 = mapidlist.get(0);
				CountryFightStatus.CM1 = mapidlist.get(1);
				CountryFightStatus.SX2 = mapidlist.get(2);
				CountryFightStatus.CM2 = mapidlist.get(3);
				CountryFightStatus.SX3 = mapidlist.get(4);
				CountryFightStatus.CM3 = mapidlist.get(5);
				CountryFightStatus.KING = mapidlist.get(6);
				
				
			}
		}
		
		
		
	}
	private long kingguildId;
	
	private int holdday ;
	
	private int addBuffId =0;
	
	/**
	 * 攻城战开始初始化
	 * 
	 */
	public void startSiegeWar() {
		addBuffId=0;
	
		ManagerPool.monsterManager.removeMonster(ManagerPool.countryManager.getSiegeMap());
		KingCity city = getKingcity();
		kingguildId = city.getGuildid();

		if(city.getGuildid()>0){
				holdday=(int)city.getCurrytime();
				
				if(holdday>=10){
					addBuffId = 1164;
				}else if(holdday>=9){
					addBuffId = 1163;
				}else if(holdday>=8){
					addBuffId = 1162;
				}else if(holdday>=7){
					addBuffId = 1161;
				}else if(holdday>=6){
					addBuffId = 1160;
				}else if(holdday>=5){
					addBuffId = 1159;
				}else if(holdday>=4){
					addBuffId = 1158;
				}else if(holdday>=3){
					addBuffId = 1157;
				}else if(holdday>=2){
					addBuffId = 1156;
				}else if(holdday>=1){
					addBuffId = 1155;
				}
				
				
		}
		
		//初始化城门
		initMonster() ;
		
		//设置 城门状态
		setSx1Status(1);
		setSx2Status(2);
		setSx3Status(2);
		setCm1Status(2);
		setCm2Status(2);
		setCm3Status(2);
		setKingStatus(2);
		
		//阻挡 城门
		opendoor1(0);
		opendoor2(0);
		opendoor3(0);
		
		
		
		//隐藏NPC
		NPC npc1 = NpcManager.getInstance().findNpc(getSiegeMap(), CountryFightStatus.NPC1).get(0);
		NPC npc2 = NpcManager.getInstance().findNpc(getSiegeMap(), CountryFightStatus.NPC2).get(0);
		NPC npc3 = NpcManager.getInstance().findNpc(getSiegeMap(), CountryFightStatus.NPC3).get(0);
		
		NpcManager.getInstance().hideNpc(npc1);
		NpcManager.getInstance().hideNpc(npc2);
		NpcManager.getInstance().hideNpc(npc3);

		GuildServerManager.getInstance().reqInnerKingCityEventToWorld(null, 2,
				getKingcity().getGuildid() + "");// 删除BUFF
		log.error("攻城战开始前拥有圣盟的战盟ID是：" + getKingcity().getGuildid());

		try {
			CountryLog clog = new CountryLog();
			clog.setCountrydata(JSON.toJSONString(getKingcity(),
					SerializerFeature.WriteClassName));
			clog.setType(0);
			LogService.getInstance().execute(clog);
		} catch (Exception e) {
			log.error(e);
		}

//		getKingcity().setGuildid(0);
//		getKingcity().setGuildname("");
//		getKingcity().setHoldplayerid(0);
//		getKingcity().setHoldplayername("");
//		getKingcity().setHoldplayersex(0);
		getKingcity().setHoldtime((int) (System.currentTimeMillis() / 1000));
		setSiegestate(1); // 设置为战斗状态
		setMonstatus(0); // 清除刷怪标记
		setSiegecountdown(System.currentTimeMillis() / 1000); // 记录开始时间
		

//		MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance()
//				.getString("攻城战今日即将点燃，请大家注意活动时间，准时通过圣盟面板处进入争夺战地图"));

//		Map map = getSiegeMap();

		 ReqCountrySiegeSelectToClientMessage cmsg = new
		 ReqCountrySiegeSelectToClientMessage();
		 MessageUtil.tell_world_message(cmsg); //弹出面板询问是否参与//TODO
		// 这里到攻城战时候要改成国家通知

//		List<NPC> npclist = ManagerPool.npcManager.findNpc(map, getYuXiNpc());
//		if (npclist.size() > 0) {
//			ManagerPool.npcManager.showNpc(npclist.get(0)); // 显示玉玺NPC
//		}
//		setdiaoxiang(false);
		SiegeSMSTopMap.clear();

//		for (Monster monster:map.getMonsters().values()) {
//			Area area =MapManager.getInstance().getArea(monster.getPosition(), map);
//			area.getMonsters().clear();
//		}	
//		map.getMonsters().clear();
		
		
		
		
		creatMonster(0, 5);
		
		//清空 盟主 BUFF 
		
		if(getKingcity().getHoldplayerid()>0){
			Player player = PlayerManager.getInstance().getPlayer(getKingcity().getHoldplayerid());
			CountryAwardManager.getInstance().removeKingCityBuff(player);
			
		}

	}

	// 城墙

	/**
	 * GM测试开始攻城
	 * 
	 */
	public void testSiege(Player player) {
		
		ReqGetTopGuildListToWorldMessage cmsg = new ReqGetTopGuildListToWorldMessage();
		MessageUtil.send_to_world(cmsg);
		
		startSiegeWar();

		player.setKingcityrewtime(0);
		player.setKingcityexp(0);
		player.setKingcityzq(0);

		stcountryWarInfo(null, true);
	}

	/**
	 * 创建攻城怪物
	 * 
	 * @param groupmark
	 * @param index
	 */
	public void creatMonster(int groupmark, int index) {
		Map map = getSiegeMap();
		// 刷新城墙

		int[] CQ = null;
		int CM = 0;
		switch (index) {
		case 1:
			CQ = CountryFightStatus.CQ1POSITION;
			CM = CountryFightStatus.CM1;
			break;
		case 2:
			CQ = CountryFightStatus.CQ2POSITION;
			CM = CountryFightStatus.CM2;
			break;
		case 3:
			CQ = CountryFightStatus.CQ3POSITION;
			CM = CountryFightStatus.CM3;
			break;
		case 4:
			CQ = CountryFightStatus.KINGPOSITION;
			CM = CountryFightStatus.KING;
			break;
		case 5:
			CQ = CountryFightStatus.SX1POSITION;
			CM = CountryFightStatus.SX1;
			break;
		case 6:
			CQ = CountryFightStatus.SX2POSITION;
			CM = CountryFightStatus.SX2;
			break;
		case 7:
			CQ = CountryFightStatus.SX3POSITION;
			CM = CountryFightStatus.SX3;
			break;
		default:
			break;
		}
		Grid grid = MapUtils.getGrid(CQ[0], CQ[1], SIEGE_MAPID);
		Monster gcmonster = ManagerPool.monsterManager.createMonster(CM,
				map.getServerId(), map.getLineId(), (int) map.getId(),
				grid.getCenter());
		gcmonster.setDirection((byte) 2);
		gcmonster.getParameters().put("cmIndex", index);

		gcmonster.setGroupmark(1);

		ManagerPool.mapManager.enterMap(gcmonster);
			BuffManager.getInstance().addBuff(gcmonster, gcmonster, 1192, 0, 0, 0);
//		1192

	}
	public void creatMonster2(int groupmark, int index,Player player) {
		Map map = getSiegeMap();
		// 刷新城墙
		
		MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, CountryFightStatus.CQ1POSITION+"------"+ CountryFightStatus.CM1);
		
		int[] CQ = null;
		int CM = 0;
		switch (index) {
		case 1:
			CQ = CountryFightStatus.CQ1POSITION;
			CM = CountryFightStatus.CM1;
			break;
		case 2:
			CQ = CountryFightStatus.CQ2POSITION;
			CM = CountryFightStatus.CM2;
			break;
		case 3:
			CQ = CountryFightStatus.CQ3POSITION;
			CM = CountryFightStatus.CM3;
			break;
		case 4:
			CQ = CountryFightStatus.KINGPOSITION;
			CM = CountryFightStatus.KING;
			break;
		case 5:
			CQ = CountryFightStatus.SX1POSITION;
			CM = CountryFightStatus.SX1;
			break;
		case 6:
			CQ = CountryFightStatus.SX2POSITION;
			CM = CountryFightStatus.SX2;
			break;
		case 7:
			CQ = CountryFightStatus.SX3POSITION;
			CM = CountryFightStatus.SX3;
			break;
		default:
			break;
		}
		Grid grid = MapUtils.getGrid(CQ[0], CQ[1], SIEGE_MAPID);
		Monster gcmonster = ManagerPool.monsterManager.createMonster(CM,
				map.getServerId(), map.getLineId(), (int) map.getId(),
				grid.getCenter());
		gcmonster.setDirection((byte) 2);
		gcmonster.getParameters().put("cmIndex", index);

		gcmonster.setGroupmark(1);
		MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, "怪物初始化完毕");
		ManagerPool.mapManager.enterMap(gcmonster);
		MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, "怪物进入地图");
			BuffManager.getInstance().addBuff(gcmonster, gcmonster, 1192, 0, 0, 0);
			MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, "创建怪物成功");
//		1192

	}
	public void creatMonster3(int groupmark, int index,Player player) {
		Map map = getSiegeMap();
		// 刷新城墙
		
		MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, CountryFightStatus.CQ1POSITION+"------"+ CountryFightStatus.CM1);
		
		int[] CQ = null;
		int CM = 0;
		switch (index) {
		case 1:
			CQ = CountryFightStatus.CQ1POSITION;
			CM = CountryFightStatus.CM1;
			break;
		case 2:
			CQ = CountryFightStatus.CQ2POSITION;
			CM = CountryFightStatus.CM2;
			break;
		case 3:
			CQ = CountryFightStatus.CQ3POSITION;
			CM = CountryFightStatus.CM3;
			break;
		case 4:
			CQ = CountryFightStatus.KINGPOSITION;
			CM = CountryFightStatus.KING;
			break;
		case 5:
			CQ = CountryFightStatus.SX1POSITION;
			CM = CountryFightStatus.SX1;
			break;
		case 6:
			CQ = CountryFightStatus.SX2POSITION;
			CM = CountryFightStatus.SX2;
			break;
		case 7:
			CQ = CountryFightStatus.SX3POSITION;
			CM = CountryFightStatus.SX3;
			break;
		default:
			break;
		}
		Grid grid = MapUtils.getGrid(CQ[0], CQ[1], SIEGE_MAPID);
		Monster gcmonster = ManagerPool.monsterManager.createMonster(CM,
				map.getServerId(), map.getLineId(), (int) map.getId(),
				grid.getCenter());
		gcmonster.setDirection((byte) 2);
		gcmonster.getParameters().put("cmIndex", index);

		gcmonster.setGroupmark(1);
	
		MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, "怪物初始化完毕");
		ManagerPool.mapManager.enterMap(gcmonster);
		MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, "怪物进入地图");
//			BuffManager.getInstance().addBuff(gcmonster, gcmonster, 1192, 0, 0, 0);
			MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, "创建怪物成功");
//		1192

	}


	 public void creatMonster2(int groupmark) {
	 Map map = getSiegeMap();
	 // 刷新城墙
	 int[] CQ = { 521, 62 };
	
	 int CM = CountryFightStatus.KING;
	 Grid grid = MapUtils.getGrid(CQ[0], CQ[1], SIEGE_MAPID);
	 Monster gcmonster = ManagerPool.monsterManager.createMonster(CM,
	 map.getServerId(), map.getLineId(), (int) map.getId(),
	 grid.getCenter());
	 gcmonster.setDirection((byte) 2);
	 gcmonster.getParameters().put("cmIndex", 2);
	 if(groupmark>0){
	 gcmonster.setGroupmark(groupmark);
	 }
	 ManagerPool.mapManager.enterMap(gcmonster);
	
	 }
	
	 public void creatMonster3(int groupmark) {
	 Map map = getSiegeMap();
	 // 刷新城墙
	 int[] CQ = { 411, 118 };
	
	 int CM = 130021008;
	 Grid grid = MapUtils.getGrid(CQ[0], CQ[1], SIEGE_MAPID);
	 Monster gcmonster = ManagerPool.monsterManager.createMonster(CM,
	 map.getServerId(), map.getLineId(), (int) map.getId(),
	 grid.getCenter());
	 gcmonster.setDirection((byte) 2);
	 gcmonster.getParameters().put("cmIndex", 3);
	 if(groupmark>0){
	 gcmonster.setGroupmark(groupmark);
	 }
	 ManagerPool.mapManager.enterMap(gcmonster);
	
	 }
	
	 public void creatMonster4(int groupmark) {
	 Map map = getSiegeMap();
	 // 刷新城墙
	 int[] CQ = { 521, 62 };
	
	 int CM = 130021008;
	 Grid grid = MapUtils.getGrid(CQ[0], CQ[1], SIEGE_MAPID);
	 Monster gcmonster = ManagerPool.monsterManager.createMonster(CM,
	 map.getServerId(), map.getLineId(), (int) map.getId(),
	 grid.getCenter());
	 gcmonster.setDirection((byte) 2);
	 gcmonster.getParameters().put("cmIndex", 4);
	 if(groupmark>0){
	 gcmonster.setGroupmark(groupmark);
	 }
	 ManagerPool.mapManager.enterMap(gcmonster);
	
	 }

	/**
	 * GM测试结束攻城
	 * 
	 */
	public void testSiegeend(Player player) {
		SiegeEnd(0);
	
	}

	
	public long getNextWeekFive() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if(cal.get(Calendar.DAY_OF_WEEK)!=1){
			cal.add(Calendar.WEEK_OF_YEAR, 1);
		}
		cal.set(Calendar.DAY_OF_WEEK, 6);
		 return cal.getTimeInMillis();
	}
	
	/**
	 * 获取攻城战时间信息
	 * 
	 * @param type
	 *            = 0 国家面板显示内容。1=挑战面板显示
	 * @return
	 */
	public String getstrtimeinfo(int type) {
		int day = TimeUtil.getOpenAreaDay();
		long curday = TimeUtil.getDayOfMonth(System.currentTimeMillis());
		
		//如果开服天数 大于3
		if (day > getOpenArea()) {
			long time = 0;
			

//

//			
//			//如果 开服天数 减去 今天的星期 大于等于3  则 取最近的一个星期五的
//			if(day-week>=3){

				 time = TimeUtil.getSoonWeek(5);
//			}else{
//				//开服第一周 只打 一场攻城战
//				//取下周五的时间
//				
//				time = getNextWeekFive();
//			}
			long mday = TimeUtil.getDayOfMonth(time);
			long month = TimeUtil.getMonth(time) + 1;
			if (curday == mday || day == 3) {
				if (getSiegestate() == 2) {
					if (type == 0) {
						//结束状态 默认下 下周五
						
						
						long millis = System.currentTimeMillis();
						long week = TimeUtil.getDayOfWeek(millis);
						
						//如果是周五  取下周5
						if(week == 5){
							time = getNextWeekFive();
						}
						
//						return ResManager.getInstance().getString("今日攻城战已结束");
						
						 mday = TimeUtil.getDayOfMonth(time);
						 month = TimeUtil.getMonth(time) + 1;
						return ResManager.getInstance().getString("攻城战时间：")
								+ "\n" + month
								+ ResManager.getInstance().getString("月") + mday
								+ ResManager.getInstance().getString("日20:00");
					}
				} else {
					if (type == 0) {
						return ResManager.getInstance().getString(
								"今日晚20:00将开启攻城战");
					}
				}

			} else {
				if (type == 0) {
					return ResManager.getInstance().getString("攻城战时间：")
							+ "\n" + month
							+ ResManager.getInstance().getString("月") + mday
							+ ResManager.getInstance().getString("日20:00");
				}
			}
		} else {
//			int sday = getOpenArea() - day;
//			long time = TimeUtil.getSoonWeek(System.currentTimeMillis()
//					+ (sday * 24 * 60 * 60 * 1000), 6);
			
				//开服第三天 为第一次盟战
				Date times = WServer.getGameConfig().getServerTimeByServer(WServer.getInstance().getServerId());
				long time = times.getTime()+24*3600*1000*2;
				
			long mday = TimeUtil.getDayOfMonth(time);
			long month = TimeUtil.getMonth(time) + 1;
			if (day==3) {
				if (getSiegestate() == 2) {
					if (type == 0) {
						return ResManager.getInstance().getString("今日攻城战已结束");
					}

				} else {
					if (type == 0) {
						return ResManager.getInstance().getString(
								"今日晚20:00将开启攻城战");
					}
				}
			} else {
				if (type == 0) {
					return ResManager.getInstance().getString("攻城战时间：")
							+ "\n" + month
							+ ResManager.getInstance().getString("月") + mday
							+ ResManager.getInstance().getString("日20:00");
				}
			}
		}
		return "";
	}

	/**
	 * 查看王城面板
	 * 
	 * @param parameter
	 */
	public void stReqCountryStructureInfoToGameMessage(Player player) {
		ReqCountryStructureInfoToWoridMessage wmsg = new ReqCountryStructureInfoToWoridMessage();
		wmsg.setPlayerid(player.getId());
		// int day=TimeUtil.getOpenAreaDay(player);
		// long curday = TimeUtil.getDayOfMonth(System.currentTimeMillis());
		if (getSiegestate() == 0 || getSiegestate() == 2) {
			// if (day >= getOpenArea()) {
			// long time = TimeUtil.getSoonWeek(6);
			// long mday = TimeUtil.getDayOfMonth(time);
			// long month = TimeUtil.getMonth(time)+1;
			// if (curday == mday) {
			// if (getSiegestate()==2) {
			// wmsg.setSiegetime("今日的攻城战结束了");
			// }else {
			// wmsg.setSiegetime("今日晚20:00将开启攻城战");
			// }
			//
			// }else {
			// wmsg.setSiegetime("攻城战时间："+month+"月"+mday+"日20:00");
			// }
			// }else {
			// int sday = getOpenArea() - day;
			// long time =
			// TimeUtil.getSoonWeek(System.currentTimeMillis()+(sday*24*60*60*1000),6);
			// long mday = TimeUtil.getDayOfMonth(time);
			// long month = TimeUtil.getMonth(time)+1;
			// if (curday == mday) {
			// if (getSiegestate()==2) {
			// wmsg.setSiegetime("今日的攻城战结束了");
			// }else {
			// wmsg.setSiegetime("今日晚20:00将开启攻城战");
			// }
			// }else {
			// wmsg.setSiegetime("攻城战时间："+month+"月"+mday+"日20:00");
			// }
			// }

			wmsg.setSiegetime(getstrtimeinfo(0));
			// ResCountryJobAwardInfoToClientMessage jobmsg = new
			// ResCountryJobAwardInfoToClientMessage();
			// //TODO 盟主老婆现在未定
			// for (int i = 1; i <= 6; i++) {
			// JobAwardInfo jobinfo = new JobAwardInfo();
			// jobinfo.setLevel(i);
			// if (getKingcity().checkSalary(i)) {
			// jobinfo.setStatus(1);
			// }
			// jobmsg.getDamageinfo().add(jobinfo);
			// }
			// MessageUtil.tell_player_message(player, jobmsg);

		} else if (getSiegestate() == 1) {
//			if (getKingcity().getHoldtime() > 0) {
//				
//					String timestr = TimeUtil.GetTransformTime((System
//							.currentTimeMillis() / 1000)
//							- getKingcity().getHoldtime());
////					wmsg.setSiegetime(ResManager.getInstance().getString(
////							"王座已被【")
////							+ getKingcity().getHoldplayername()
////							+ ResManager.getInstance().getString("】持有")
////							+ timestr + "");
//					wmsg.setSiegetime(ResManager.getInstance().getString("攻城战开启中"));
//
//			} else {
//				String timestr = TimeUtil
//						.GetTransformTime((getSiegecountdown() + 60 * 60)
//								- (System.currentTimeMillis() / 1000));
//				 wmsg.setSiegetime(ResManager.getInstance().getString("攻城战结束剩余："+timestr));
//			}
			
			wmsg.setSiegetime(ResManager.getInstance().getString("攻城战开启中"));
		}
		
		KingCity city = getKingcity();
		if(city.getGuildid()>0){
			int day = 	(int)city.getCurrytime();
			wmsg.setDay(day);
			
			if(player.getGuildId()==city.getGuildid()&&player.getMemberInfo().getGuildPowerLevel() == 1 ){
			
				StringBuffer result=new StringBuffer();
					for(String key : kingcity.getSalarymap().keySet()){
						if(key.indexOf("getAngleDay_")>0){
							result.append(kingcity.getSalarymap().get(key));
							result.append(",");
						}
					
						
					}
					if(result.length()>1){
						if(result.charAt(result.length()-1)==',') result.deleteCharAt(result.length()-1);
					}
					wmsg.setReward(result.toString());
			
			}
		}


		wmsg.setGuildid(city.getGuildid());
		MessageUtil.send_to_world(wmsg);
		// log.error(wmsg);
	}

	private List<GuildInfo> top5infolist = new ArrayList<GuildInfo>();
	
	public void stRescheckLV(Player player, ResGetTopGuildListToGameMessage msg) {
		
		
		top5infolist= msg.getTop5infolist();
		
			

	}

	/**
	 * 传送攻城战地图
	 * 
	 * @param parameter
	 * @param msg
	 */
	public void stResCountrySiegeSelectToGameMessage(Player player,
			ResCountrySiegeSelectToGameMessage msg) {
		Map map = ManagerPool.mapManager.getMap(player);
		if (msg.getType() == 1) {
			if (getSiegestate() != 1) { // 是否攻城期间
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager
						.getInstance().getString("现在不是攻城期间"));
				return;
			}
			long guildId = getKingcity().getGuildid();
			
			if(player.getGuildId()!=guildId || guildId==0){
				
			//是否是前三
			boolean isThree = true;
			
			int index = 10;
			for (int i = 0; i < top5infolist.size(); i++) {
				if(i>3){
					break;
				}
				if (player.getGuildId() == top5infolist.get(i)
						.getGuildId()) {
					index = i+1;
				}
				if(guildId==top5infolist.get(i).getGuildId()){
					//如果 上一次的 玩家  在前4名
					isThree = false;
				}
			}

			//判断 进前四 还是 前三
			
			if(isThree==true &&guildId!=0){
				if(index>3){
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager
						.getInstance().getString("您的战盟没有参赛资格"));
					return;
				}
			}
			if(isThree==false || guildId==0){
				if(index>4){
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager
						.getInstance().getString("您的战盟没有参赛资格"));
					return;
				}
			}
			
			}
			
			if(player.getLevel()<100){
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager
						.getInstance().getString("等级不足，需要{1}级"),"100");
					return;
			}
			
			
				long day = TimeUtil.GetCurTimeInMin(4); // 清理奖励数据
				if (player.getKingcityrewday() != day) {
					player.setKingcityrewtime(0);
					player.setKingcityrewday((int) day);
					player.setKingcityexp(0);
					player.setKingcityzq(0);
				}
				if (map == null) {
					log.error("server " + player.getServerId() + " line "
							+ player.getLine() + " map " + player.getMap()
							+ "  is null!");
				}
				// 删除原来的死亡复活保护BUFF
				ManagerPool.buffManager.removeByBuffId(player,
						Global.PROTECT_FOR_KILLED);
				// 和平保护BUFF
				List<Buff> buffs = ManagerPool.buffManager.getBuffByModelId(player,
						Global.PROTECT_IN_SIEGE);
				if (buffs.size() == 0) {
					ManagerPool.buffManager.addBuff(player, player,
							Global.PROTECT_IN_SIEGE, 0, 0, 0);
				}

				Position position = new Position();
				int x = 0;
				int y = 0;
				if (player.getGuildId() > 0 && getKingcity().checkKingCity(player)) { // 防守方
					x = GUARD_XY[0];
					y = GUARD_XY[1];
					// }else if (player.getGuildId() > 0) { //进攻者战盟
					// x=ATTACK_XY[0];
					// y=ATTACK_XY[1];
				} else { // 散人
					int rnd = RandomUtils.random(1, ALONE_XY.length) - 1;
					x = ALONE_XY[rnd][0];
					y = ALONE_XY[rnd][1];
				}
				position.setX((short) (x * MapUtils.GRID_BORDER));
				position.setY((short) (y * MapUtils.GRID_BORDER));
				List<Grid> gridlist = MapUtils.getRoundNoBlockGrid(position,
						2 * MapUtils.GRID_BORDER, SIEGE_MAPID);
				int rnd = RandomUtils.random(1, gridlist.size()) - 1;
				ManagerPool.mapManager.changeMap(player, SIEGE_MAPID, SIEGE_MAPID,
						1, gridlist.get(rnd).getCenter(), this.getClass().getName()
								+ ".stResCountrySiegeSelectToGameMessage 1");
				// PK模式 0-和平 1-强制 2-全体
//				if (player.getGuildId() > 0 && player.getPkState() != 1) {
					ManagerPool.playerManager.changePkState(player, 1, 0);
//				} else if (player.getGuildId() == 0 && player.getPkState() != 2) {
//					ManagerPool.playerManager.changePkState(player, 2, 0);
//				}
				stcountryWarInfo(player, false);
				if (!SiegeSMSTopMap.containsKey(player.getId())) {
					SiegeSMS siegeSMS = new SiegeSMS(player);
					SiegeSMSTopMap.put(player.getId(), siegeSMS);
				}
				if(player.getGuildId()==getKingcity().getGuildid()){
					player.setGroupmark(1);
				}else{
					player.setGroupmark(getPlayerGoruoupmark());
				}
				
				
				ManagerPool.countryManager.stReqCountryOpenTopToGameMessage(player);
				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager
						.getInstance().getString("战斗号角已经吹响，勇士们，尽情的冲杀吧！"));
				
				GuildPlayerInfo guildPlayerInfo  = new GuildPlayerInfo();
				guildPlayerInfo.setGuildId(player.getGuildId());
				guildPlayerInfo.setPlayerId(player.getId());
				
				this.map.put(player.getId(), guildPlayerInfo);
			
				if(addBuffId!=0){
					if(player.getGuildId()!=kingguildId){
						ManagerPool.buffManager.addBuff(player, player,
								addBuffId, 1, 0,
								0);
					}
				}
			

		} else if (msg.getType() == 2) {
			// 离开攻城战地图（回城）
			if (map.getMapModelid() == SIEGE_MAPID) {
				Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer
						.getMap().get(SIEGE_MAPID);
				Position position = ManagerPool.mapManager
						.RandomDieBackCity(mapBean);
				ManagerPool.playerManager.autoRevive(player);
				List<Grid> gridlist = MapUtils.getRoundNoBlockGrid(position,
						15 * MapUtils.GRID_BORDER, mapBean.getQ_map_die());
				int rnd = RandomUtils.random(1, gridlist.size()) - 1;
				ManagerPool.mapManager.changeMap(player,
						mapBean.getQ_map_die(), mapBean.getQ_map_die(), 0,
						gridlist.get(rnd).getCenter(), this.getClass()
								.getName()
								+ ".stResCountrySiegeSelectToGameMessage 2");
				BuffManager.getInstance().removeByBuffId(player, addBuffId,1146,1147,1148,1166);
				ManagerPool.playerManager.changePkState(player, 0, 0);
			}
			player.setGroupmark(0);
		}
	}

	/**
	 * 地图内传送到指定复活点 守城方使用守方复活点，攻城方和中立方使用攻方复活点；
	 * 在承玺台地图中其他地方被杀会在复活点重生，在复活点范围内被杀死则只能返回咸阳城重生；
	 */
	public void SiegeMoveMap(Player player) {
		Position position = new Position();
		int x = 0;
		int y = 0;
		if (player.getGuildId() > 0 && getKingcity().checkKingCity(player)) { // 防守方
			x = GUARD_XY[0];
			y = GUARD_XY[1];
		} else { // 进攻者战盟,散人中立方复活点
			x = ATTACK_XY[0];
			y = ATTACK_XY[1];
		}
		position.setX((short) (x * MapUtils.GRID_BORDER));
		position.setY((short) (y * MapUtils.GRID_BORDER));
		double distance = MapUtils
				.countDistance(player.getPosition(), position); // 得到距离
		// 和平保护BUFF
		ManagerPool.buffManager.addBuff(player, player,
				Global.GCZ_PROTECT_FOR_KILLED, 0, 0, 0);

		// ManagerPool.mapManager.changePosition(player, position);
		// if (distance <= MapUtils.GRID_BORDER * 20 ) { //复活点范围格子(复活后回城)
		// MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,
		// ResManager.getInstance().getString("在攻城战复活点死亡，回到咸阳王城。"));
		// player.setAutohorse((byte) 1);
		// Q_mapBean mapBean =
		// ManagerPool.dataManager.q_mapContainer.getMap().get(SIEGE_MAPID);
		// Position cposition =
		// ManagerPool.mapManager.RandomDieBackCity(mapBean);
		ManagerPool.playerManager.autoRevive(player);
		ManagerPool.mapManager.changeMap(player, SIEGE_MAPID, SIEGE_MAPID, 0,
				position, this.getClass().getName() + ".SiegeMoveMap");
		// }else {
		// ManagerPool.mapManager.changePosition(player, position);
		//
		// }
	}

	/**
	 * 攻城战结束处理 type= 0正常结束，1持有20分钟结束
	 */
	public void SiegeEnd(int type) {
		
		
		
		playerName="";
		kingName="";
		
		setMonstatus(0);
		setSiegecountdown(0);

		KingCity kingCity = getKingcity();
		ResCountrySiegeWarStateToClientMessage cmsg = new ResCountrySiegeWarStateToClientMessage();
		cmsg.setState((byte) 0);
		MessageUtil.tell_world_message(cmsg);// TODO 这里到攻城战时候要改成国家通知

		

		ResCountryOverTopInfoToClientMessage endmsg = new ResCountryOverTopInfoToClientMessage();

		List<CountryTopInfo> list = getEndSiegeSMStopinfo();
		endmsg.setCountryTopInfolist(list);
		MessageUtil.tell_map_message(getSiegeMap(), endmsg);

		setSiegestate(2); // 设置为战斗结束状态(发奖励需要)

		try {// 储存最后攻城记录
			CountryLog clog = new CountryLog();
			clog.setCountrydata(JSON.toJSONString(kingCity,
					SerializerFeature.WriteClassName));
			clog.setType(2);
			LogService.getInstance().execute(clog);
		} catch (Exception e) {
			log.error(e);
		}
	
		
		
		
		
//		if( ServerParamUtil.getImportantParamMap().get(ServerParamUtil.COUNTRYDATA)==null){
			CountryData conutryData = new CountryData();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			conutryData.setCountryTime(simpleDateFormat.format(new Date()));
			
			for (GuildPlayerInfo guildPlayerInfo:this.map.values()) {
				if(guildPlayerInfo.getGuildId()==kingCity.getGuildid()){
					conutryData.getKingGuildList().add(guildPlayerInfo.getPlayerId());
					
				
				}else{
					conutryData.getPlayerList().add(guildPlayerInfo.getPlayerId());
				}
				
			}
			conutryData.setKingName(kingCity.getHoldplayername());
			conutryData.setKingId(kingCity.getHoldplayerid());
			conutryData.setKingGuildId(kingCity.getGuildid());
			conutryData.setKingGuildName(kingCity.getGuildname());
			
			ServerParamUtil.threadSaveImportant(ServerParamUtil.COUNTRYDATA
					, JSON.toJSONString(conutryData,
					SerializerFeature.WriteClassName));
//		}
//		KingCity jskingcity = JSON.parseObject(dataString,
//				KingCity.class);
		
		
		
		
		/*
		 * xuliang
		 * MessageUtil.notify_All_player(Notifys.SROLL,ResManager.getInstance
		 * ().getString("参与攻城战所奖励的经验，将在10秒内自动结算，请务必保持在线。") );
		 */
		// 清理怪物
		ManagerPool.monsterManager.removeMonster(getSiegeMap());
		setMovetime((System.currentTimeMillis() / 1000) + 2*60); // 60秒后人物全部回城
		setdiaoxiang(true);
		
		if (kingCity.getGuildid() > 0) {
			GuildTmpInfo guildTmpInfo = ManagerPool.guildServerManager
					.getGuildTmpInfo(kingCity.getGuildid());
			GuildServerManager.getInstance().reqInnerKingCityEventToWorld(null,
					2, getKingcity().getGuildid() + "");// 给盟主王城BUFF

			String kingname = kingCity.getHoldplayername();
			long kingpid = kingCity.getHoldplayerid();

			if (type == 1) {
				MessageUtil
						.notify_All_player(
								Notifys.SROLL,
								ResManager
										.getInstance()
										.getString(
												"{1}战盟占领王座时间超过20分钟，新的王城之主已经产生，他就是【{2}】战盟的盟主【{3}】，让我们一起来见证这个伟大的时刻！"),
												kingCity.getGuildname(),kingCity.getGuildname(), kingname);
				MessageUtil
				.notify_All_player(
						Notifys.CHAT_SYSTEM,
						ResManager
								.getInstance()
								.getString(
										"{1}战盟占领王座时间超过20分钟，新的王城之主已经产生，他就是【{2}】战盟的盟主【{3}】，让我们一起来见证这个伟大的时刻！"),
										kingCity.getGuildname(),kingCity.getGuildname(), kingname);
			} else {
				MessageUtil
						.notify_All_player(
								Notifys.SROLL,
								ResManager
										.getInstance()
										.getString(
												"攻城结束时间已到，新的王城之主已经产生，他就是【{1}】战盟的盟主【{2}】，让我们一起来见证这个伟大的时刻！"),
								kingCity.getGuildname(), kingname);
				MessageUtil
				.notify_All_player(
						Notifys.CHAT_SYSTEM,
						ResManager
								.getInstance()
								.getString(
										"攻城结束时间已到，新的王城之主已经产生，他就是【{1}】战盟的盟主【{2}】，让我们一起来见证这个伟大的时刻！"),
						kingCity.getGuildname(), kingname);
			}

			addking(kingpid, kingname);
			kingCity.setHoldtime(0);
			kingCity.setHoldplayerid(kingpid);
			
			if(kingCity.getOldGuildid()!=kingCity.getGuildid()){
				
				kingCity.setCurrytime(1);
				kingCity.setOldGuildid(kingCity.getGuildid());
				kingCity.getSalarymap().clear();
				
			}else{
				kingCity.setCurrytime(kingCity.getCurrytime()+1);
				
			}
			
			savekingcity(kingCity);
			

			

			MessageUtil.notify_All_player(
					Notifys.SROLL,
					ResManager.getInstance().getString(
							"今晚的攻城战已经结束，感谢大家的参与，让我们期待下一次的精彩战役"));
			log.error(String.format("%s战盟的盟主【%s】持有王座时间超过20分钟，成为了新的王城之主,GID=%s",
					kingCity.getGuildname(), kingname, kingCity.getGuildid()
							+ ""));
			Player player = ManagerPool.playerManager
					.getOnLinePlayer(kingCity.getHoldplayerid());
			if (player != null) {
				ManagerPool.buffManager.removeByBuffId(player,
						Global.PROTECT_IN_BANGZHU);
				kingCity.setHoldplayersex(player.getSex());
			}

			Player fuplayer = ManagerPool.playerManager
					.getOnLinePlayer(guildTmpInfo.getbangzhu(2).getMemberid());
			if (fuplayer != null) {
				ManagerPool.buffManager.removeByBuffId(fuplayer,
						Global.PROTECT_IN_BANGZHU);
			}
		} else {
			MessageUtil
					.notify_All_player(
							Notifys.SROLL,
							ResManager
									.getInstance()
									.getString(
											"争夺战时间已到，很遗憾王座并未被夺下，未来一段时间城主位置将空悬。请大家积极准备下周的攻城战，未来的城主也许就是你！"));
			log.error("攻城结束时间已到，很遗憾王座未被人持有");
		}
	
		
	}

	/**
	 * 储存王城信息
	 * 
	 * @param kingCity
	 */
	public void savekingcity(KingCity kingCity) {
		try {
			int countryid = WServer.getGameConfig().getCountryByServer(
					WServer.getInstance().getServerId());
			ServerParamUtil.threadSaveImportant(ServerParamUtil.KINGCITYWAR
					+ countryid, JSON.toJSONString(kingCity,
					SerializerFeature.WriteClassName));
			ReqCountrySyncKingCityToWoridMessage wmsg = new ReqCountrySyncKingCityToWoridMessage();
			wmsg.setCountryid(countryid);
			wmsg.setGuildid(kingCity.getGuildid());
			MessageUtil.send_to_world(wmsg);
			log.error("发送王城储存消息:");
			log.error(wmsg);
		} catch (Exception e) {
			log.error("储存王城消息出错" + e);
		}
	}

	/**
	 * 加下一任城主
	 * 
	 * @param playerid
	 * @param playername
	 */
	public synchronized void addking(long playerid, String playername) {
		KingCity kingCity = getKingcity();
		KingData old = kingCity.gCurKingData();
		if (old == null) {
			int renqi = kingCity.gKingDataKey();
			KingData kingdata = new KingData();
			kingdata.setPlayerid(playerid);
			kingdata.setPlayername(playername);
			kingdata.setTerm(renqi);
			kingdata.setReigntime(System.currentTimeMillis() / 1000);
			kingCity.putKingData(kingdata);
		} else {
			if (old.getPlayerid() != playerid) {
				int renqi = kingCity.gKingDataKey();
				KingData kingdata = new KingData();
				kingdata.setPlayerid(playerid);
				kingdata.setPlayername(playername);
				kingdata.setTerm(renqi);
				kingdata.setReigntime(System.currentTimeMillis() / 1000);
				kingCity.putKingData(kingdata);
				old.setAbdicatetime(System.currentTimeMillis() / 1000); // 上一任退位时间
			}
		}
	}

	/**
	 * 点NPC获得玉玺
	 * 
	 */

	public void SiegeGrabYuXi(long guildId,long playerId,String playerName,String guildName) {
		GuildTmpInfo guildTmpInfo = ManagerPool.guildServerManager
				.getGuildTmpInfo(guildId);
		String guildname = guildTmpInfo.getGuildname();
		KingCity city =getKingcity();
		city.setGuildid(guildId);
		city.setGuildname(guildname);
		city.setHoldplayerid(playerId);
		city.setHoldtime((int) (System.currentTimeMillis() / 1000));
		city.setHoldplayername(playerName);
		stcountryWarInfo(null, true);
//		if (player.getMemberInfo().getGuildPowerLevel() == 1) {
			MessageUtil.notify_All_player(Notifys.CUTOUT, ResManager
					.getInstance().getString("　　王座已被摧毁 ，{1}对王座积累伤害最多，成为新一轮的守方。"),
					guildname);
			MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM, ResManager
					.getInstance().getString("　　王座已被摧毁 ，{1}对王座积累伤害最多，成为新一轮的守方。"),
					guildname);
			log.error(String.format("恭喜【%s】战盟副盟主【%s】成功取得王座,GID=%s", guildName,
					playerName + "",guildId+""));
//		} else if (player.getMemberInfo().getGuildPowerLevel() == 2) {
//			MessageUtil.notify_All_player(Notifys.CUTOUT, ResManager
//					.getInstance().getString("恭喜【{1}】战盟副盟主【{2}】成功取得王座"),
//					guildname, player.getName());
//			log.error(String.format("恭喜【%s】战盟副盟主【%s】成功取得王座,GID=%s", guildname,
//					player.getName(), player.getGuildId() + ""));
//		}
		// CountryAwardManager.getInstance().setOtherKingCityBuff(player);
		// ManagerPool.buffManager.addBuff(player, player,
		// Global.PROTECT_IN_BANGZHU, 0, 0, 0);
		CountryLog clog = new CountryLog();
		clog.setCountrydata(JSON.toJSONString(getKingcity(),
				SerializerFeature.WriteClassName));
		clog.setType(1);
		LogService.getInstance().execute(clog);
	}

	/**
	 * 全服广播玉玺归位
	 * 
	 */
	public void SiegeHomingYuXi(Player player) {
//		KingCity kingCity = getKingcity();
//		if (player.getId() == kingCity.getHoldplayerid()
//				&& getSiegestate() == 1) {
//			Map map = getSiegeMap();
//			List<NPC> npclist = ManagerPool.npcManager.findNpc(map,
//					getYuXiNpc());
//			if (npclist.size() > 0) {
//				ManagerPool.npcManager.showNpc(npclist.get(0));
//			}
//			kingCity.setGuildid(0);
//			kingCity.setGuildname("");
//			kingCity.setHoldplayerid(0);
//			kingCity.setHoldplayername("");
//			kingCity.setHoldtime(0);
//			ResCountrySiegeHomingYuxiToClientMessage cmsg = new ResCountrySiegeHomingYuxiToClientMessage();
//			MessageUtil.tell_world_message(cmsg);
//			stcountryWarInfo(player, true);
//			// CountryAwardManager.getInstance().removeKingCityBuff(player);
//			ManagerPool.buffManager.removeByBuffId(player,
//					Global.PROTECT_IN_BANGZHU);
//		}
	}

	public String getProcess() {

		return getCm1Status() + "," + getCm2Status() + "," + getCm3Status()
				+ "," + getKingStatus()+ "," + getSx1Status()+ "," + getSx2Status()+ "," + getSx3Status();
	}
	
	public String getMonsterProcess() {
		StringBuffer result = new StringBuffer();
		Map map = getSiegeMap();
		List<Monster> sx1= MonsterManager.getInstance().getMonsterByModel(map, CountryFightStatus.SX1);
		List<Monster> cm1= MonsterManager.getInstance().getMonsterByModel(map, CountryFightStatus.CM1);
		List<Monster> sx2= MonsterManager.getInstance().getMonsterByModel(map, CountryFightStatus.SX2);
		List<Monster> cm2= MonsterManager.getInstance().getMonsterByModel(map, CountryFightStatus.CM2);
		List<Monster> sx3= MonsterManager.getInstance().getMonsterByModel(map, CountryFightStatus.SX3);
		List<Monster> cm3= MonsterManager.getInstance().getMonsterByModel(map, CountryFightStatus.CM3);
		List<Monster> king= MonsterManager.getInstance().getMonsterByModel(map, CountryFightStatus.KING);
		
	
		if(cm1.size()>0){
			result.append(cm1.get(0).getHp()*100/cm1.get(0).getMaxHp());
		}else{
			if(getCm1Status()!=0){
				
				result.append("100");
			}else{
				result.append("0");
			}
		}
		result.append(",");
		
		if(cm2.size()>0){
			result.append(cm2.get(0).getHp()*100/cm2.get(0).getMaxHp());
		}else{
			if(getCm2Status()!=0){
				
				result.append("100");
			}else{
				result.append("0");
			}
		}
		result.append(",");
	
		if(cm3.size()>0){
			result.append(cm3.get(0).getHp()*100/cm3.get(0).getMaxHp());
		}else{
			if(getCm3Status()!=0){
				
				result.append("100");
			}else{
				result.append("0");
			}
		}
		result.append(",");
		if(king.size()>0){
			result.append(king.get(0).getHp()*100/king.get(0).getMaxHp());
		}else{
			if(getKingStatus()!=0){
				
				result.append("100");
			}else{
				result.append("0");
			}
		}
		result.append(",");
		if(sx1.size()>0){
			result.append(sx1.get(0).getHp()*100/sx1.get(0).getMaxHp());
		}else{
			if(getSx1Status()!=0){
			
				result.append("100");
			}else{
				result.append("0");
			}
		}
		result.append(",");
		if(sx2.size()>0){
			result.append(sx2.get(0).getHp()*100/sx2.get(0).getMaxHp());
		}else{
			if(getSx2Status()!=0){
				
				result.append("100");
			}else{
				result.append("0");
			}
		}
		result.append(",");
		
		if(sx3.size()>0){
			result.append(sx3.get(0).getHp()*100/sx3.get(0).getMaxHp());
		}else{
			if(getSx3Status()!=0){
				
				result.append("100");
			}else{
				result.append("0");
			}
		}
		return result.toString();
		
	}

	public void reLoadKing(Monster monster) {


		HashMap<Long, Long> guldeKillMap = (HashMap<Long, Long>)monster.getParameters().get("kingDamages");

		Long max = 0l;
		Long kingGuild =0l;
		
		for (java.util.Map.Entry<Long, Long> entry : guldeKillMap.entrySet()) {
			
			if(entry.getValue() > max){
				
				max = entry.getValue();
				kingGuild = entry.getKey();
				
			}
			
		}
		
		
		//设置胜利方
		GuildTmpInfo guildinfo =  GuildServerManager.getInstance().getGuildTmpInfo(kingGuild);
//		Player player = PlayerManager.getInstance().getPlayer(guildinfo.getPostmap().get(1).getMemberid());
		ManagerPool.countryManager.SiegeGrabYuXi(guildinfo.getGuildid(), guildinfo.getPostmap().get(1).getMemberid(), guildinfo.getPostmap().get(1).getMembername(), guildinfo.getGuildname());
		getSiegeMap().getParameters().put("currentWiner", guildinfo.getGuildid());
		
		//重置地图内的玩家阵营
		Map map = getSiegeMap();
		for (Entry<Long, Player> entry :map.getPlayers().entrySet()){
			if(entry.getValue().getGuildId()== guildinfo.getGuildid()){
				entry.getValue().setGroupmark(1);
			}else{
				entry.getValue().setGroupmark(getPlayerGoruoupmark());
			}
		}
	}

	/**
	 * 发送即时攻城消息
	 * 
	 * @param player
	 *            boolean false 只对个人发送 ，true = 当前地图发送
	 */
	public void stcountryWarInfo(Player player, boolean ismap) {
		try {
			long systime = System.currentTimeMillis() / 1000;
			ResCountrySiegeYuXiImmediateToClientMessage cmsg = new ResCountrySiegeYuXiImmediateToClientMessage();
			KingCity kingCity = getKingcity();
			CountryWarInfo countryWarInfo = new CountryWarInfo();

			long ms = ManagerPool.countryManager.getSiegecountdown();
			long downtime = (ms + 60 * 60) - systime;
			countryWarInfo.setWarendtime((int) downtime);// 剩余时间

			if (kingCity.getHoldplayerid() > 0) {
//				Player holderplayer = ManagerPool.playerManager
//						.getOnLinePlayer(kingCity.getHoldplayerid());
				countryWarInfo.setHolderid(kingCity.getHoldplayerid());
				countryWarInfo.setHoldertime((int) (systime - kingCity
						.getHoldtime()));
				countryWarInfo.setHolderguildid(kingCity.getGuildid());
//				countryWarInfo.setMx(holderplayer.getPosition().getX());
//				countryWarInfo.setMy(holderplayer.getPosition().getY());
				countryWarInfo.setHoldername(kingCity.getHoldplayername());

			}
			countryWarInfo.setProcess(getProcess());
			countryWarInfo.setLife(getMonsterProcess());
			cmsg.setCountrywarinfo(countryWarInfo);
//			System.out.println("状态：" + cmsg.getCountrywarinfo().getProcess());
//			System.out.println("状态：" + getMonsterProcess());
			if (ismap) {
				Map map = getSiegeMap();
				MessageUtil.tell_map_message(map, cmsg);
			} else {
				if (player != null) {
					MessageUtil.tell_player_message(player, cmsg);
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 当前是否多倍经验。
	 * 
	 */
	public int getposexpmultiple(Player player) {
		// 攻城战获取当前格子多倍经验数量
		/*
		 * 改成菱形 汪振瀚 // double x1 = 0; // double y1 = 26; // double x2 = 232; //
		 * double y2 = 159; 
		 * // int x =
		 * player.getPosition().getX()/MapUtils.GRID_BORDER; 
		 * // int y =
		 * player.getPosition().getY()/MapUtils.GRID_BORDER; 
		 * // double value =
		 * (x2-x1) * (y-y1) - (y2-y1) * (x-x1); // if (value > 0) { // return
		 * 10; // }else { // return 20; // }
		 */

//		int x1 = 334;
//		int y1 = 62;
//
//		int x2 = 292;
//		int y2 = 100;
//
//		int x3 = 359;
//		int y3 = 132;
//
//		int x = player.getPosition().getX() / MapUtils.GRID_BORDER;
//		int y = player.getPosition().getY() / MapUtils.GRID_BORDER;
//		if (((y3 - y2) * (x - x2) - (x3 - x2) * (y - y2)) > 0
//				&& ((y2 - y1) * (x - x1) - (x2 - x1) * (y - y1)) > 0) {
//			return 20;
//		}
		 int x = player.getPosition().getX()/MapUtils.GRID_BORDER; 
		 int y = player.getPosition().getY()/MapUtils.GRID_BORDER; 
		
		
				 
				
		
		int x1_up = 364;
		int y1_up = 92;
		int x1_down = 460;
		int y1_down = 138;
//		(x2-x1) * (y-y1) - (y2-y1) * (x-x1);
		 double value =(x1_down-x1_up) * (y-y1_up) - (y1_down-y1_up) * (x-x1_up); 
			if (value < 0) { 
//				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("当前位于10倍经验区"));
				return 60;
			}
		
		
			x1_up = 240;
			y1_up = 133;
			x1_down = 377;
			y1_down = 201;
		
		  value =(x1_down-x1_up) * (y-y1_up) - (y1_down-y1_up) * (x-x1_up); 
			if (value < 0) { 
//				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("当前位于7.5倍经验区"));
				return 45;
			}
		
		
		x1_up= 98;
		y1_up= 181;

		x1_down = 274;
		y1_down = 269;

		  value =(x1_down-x1_up) * (y-y1_up) - (y1_down-y1_up) * (x-x1_up); 
			if (value < 0) { 
//				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("当前位于5倍经验区"));
				return 30;
			}
//			MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("当前位于2.5倍经验区"));
		return 15;
	}

	/**
	 * 在攻城战地图，并且是攻城时间,是否可打坐
	 * 
	 * @return true 可打坐，false不可打坐
	 */
	public boolean isSiegeWarandMap(Player player) {
		if (getSiegestate() == 1) {
			Map map = ManagerPool.mapManager.getMap(player);
			if (map.getMapModelid() == ManagerPool.countryManager.SIEGE_MAPID) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 攻城战时间奖励
	 * 
	 */
	public void giveSiegeTimeReward(Player player) {
		int key = ManagerPool.dataManager.q_characterContainer.getKey(
				player.getJob(), player.getLevel());
		Q_characterBean model = ManagerPool.dataManager.q_characterContainer
				.getMap().get(key);

		// Q_characterBean model =
		// DataManager.getInstance().q_characterContainer.getMap().get(player.getLevel());
		if (player.getKingcityrewtime() > 20 * 60 && player.getLevel() >= 40) {
			int exp = model.getQ_basis_exp() * player.getKingcityrewtime() / 6;
			// int zhenqi =
			// model.getQ_basis_zhenqi()*player.getKingcityrewtime()/6;
			ManagerPool.playerManager.addExp(player, exp,
					AttributeChangeReason.COUNTRY);
			// ManagerPool.playerManager.addZhenqi(player,zhenqi,AttributeChangeReason.COUNTRY);
			player.setKingcityrewtime(0);
			MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager
					.getInstance().getString("恭喜获得攻城战时间奖励：经验{1}"), exp + "");
		}
	}

	/**
	 * 攻城战登录触发
	 * 
	 */
	public void siegeLoginHandle(Player player) {
		if (getSiegestate() == 1) {
			ResCountrySiegeWarStateToClientMessage cmsg = new ResCountrySiegeWarStateToClientMessage();
			cmsg.setState((byte) 1);
			MessageUtil.tell_player_message(player, cmsg);
		} else {
			// 如果玩家在盟战地图 传送出去
			if (player.getMapModelId() == 300010) {
				
				BuffManager.getInstance().removeByBuffId(player, addBuffId,1146,1147,1148,1166);
				
				Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer
						.getMap().get(SIEGE_MAPID);
				Position position = ManagerPool.mapManager
						.RandomDieBackCity(mapBean);
				ManagerPool.playerManager.autoRevive(player);
				List<Grid> gridlist = MapUtils.getRoundNoBlockGrid(position,
						15 * MapUtils.GRID_BORDER, mapBean.getQ_map_die());
				int rnd = RandomUtils.random(1, gridlist.size()) - 1;
				ManagerPool.mapManager.changeMap(player,
						mapBean.getQ_map_die(), mapBean.getQ_map_die(), 0,
						gridlist.get(rnd).getCenter(), this.getClass()
								.getName()
								+ ".stResCountrySiegeSelectToGameMessage 2");
				
				
			}
		}
	}

	/**
	 * 攻城车消息 1,弩箭攻城车：小范围高伤害的攻击利器，误差范围小； 2,巨型投石车：大范围低伤害的散射钝器，误差范围大； 3 超级弩箭攻城车 %10
	 * 体力清零 4 超级巨型投石车 %10魔法值清零
	 * 
	 * @param parameter
	 * @param msg
	 */
	public void stReqCountryWarCarToGameMessage(Player player,
			ReqCountryWarCarToGameMessage msg) {
		int money = 120000;
		if (msg.getType() == 1 || msg.getType() == 2) {
			money = ManagerPool.dataManager.q_globalContainer.getMap()
					.get(CommonConfig.SIEGE_MONEY.getValue()).getQ_int_value();
			if (ManagerPool.cooldownManager.isCooldowning(player,
					CooldownTypes.SIEGE_COOL, null)) {
				return;
			}
		} else {
			money = ManagerPool.dataManager.q_globalContainer.getMap()
					.get(CommonConfig.SIEGE_SUPRE_MONEY.getValue())
					.getQ_int_value();
			if (ManagerPool.cooldownManager.isCooldowning(player,
					CooldownTypes.SIEGE_SUPER_COOL, null)) {
				return;
			}
		}

		Q_npcBean npcdata = ManagerPool.dataManager.q_npcContainer.getMap()
				.get(msg.getNpcid());
		Grid npcgrid = MapUtils.getGrid(npcdata.getQ_x(), npcdata.getQ_y(),
				SIEGE_MAPID);
		double dis = MapUtils.countDistance(npcgrid.getCenter(),
				player.getPosition()); // 得到距离
		if (dis > 25 * MapUtils.GRID_BORDER) {
			/*
			 * xuliang MessageUtil.notify_player(player, Notifys.CUTOUT,
			 * ResManager.getInstance().getString("您离攻城车太远了，无法呼叫火力支援"));
			 */
			return;
		}

		if (ManagerPool.backpackManager.changeMoney(player, -money,
				Reasons.KINGCITY_ZHANCHE, Config.getId()) == false) {
			/*
			 * xuliang MessageUtil.notify_player(player, Notifys.CUTOUT,
			 * ResManager.getInstance().getString("您没有{1}金币，无法发射。"),money+"");
			 */
			return;
		}

		Map map = getSiegeMap();
		if (msg.getType() == 1 || msg.getType() == 2) {
			ManagerPool.cooldownManager.addCooldown(player,
					CooldownTypes.SIEGE_COOL, null, 5 * 1000); // 冷却时间5秒
		} else {
			ManagerPool.cooldownManager.addCooldown(player,
					CooldownTypes.SIEGE_SUPER_COOL, null, 3 * 1000); // 冷却时间3秒
		}

		ResCountryArtilleryLocusToClientMessage gmsg = new ResCountryArtilleryLocusToClientMessage();
		gmsg.setEndx(msg.getX());
		gmsg.setEndy(msg.getY());
		gmsg.setPlayerid(player.getId());
		gmsg.setType(msg.getType());
		MessageUtil.tell_map_message(map, gmsg);
		String cname = ResManager.getInstance().getString("弩箭攻城车");
		int atk = 1000; // 攻击力 (默认值)
		int radius = 10; // 攻击范围
		if (msg.getType() == 1) {
			Q_globalBean data = ManagerPool.dataManager.q_globalContainer
					.getMap().get(CommonConfig.SIEGE_CRISSBOW.getValue());
			if (data != null) {
				String[] datstr = data.getQ_string_value().split(
						Symbol.SHUXIAN_REG);
				radius = Integer.parseInt(datstr[1]);// 弩箭攻击范围
				atk = Integer.parseInt(datstr[0]); // 弩箭攻击力
			}
		} else if (msg.getType() == 2) {
			Q_globalBean data = ManagerPool.dataManager.q_globalContainer
					.getMap().get(CommonConfig.SIEGE_CATAPULTS.getValue());
			if (data != null) {
				String[] datstr = data.getQ_string_value().split(
						Symbol.SHUXIAN_REG);
				radius = Integer.parseInt(datstr[1]);// 投石车攻击范围
				atk = Integer.parseInt(datstr[0]); // 投石车攻击力
				cname = ResManager.getInstance().getString("投石车");
			}
		} else if (msg.getType() == 3) {// 超级弩箭（体力清零）
			Q_globalBean data = ManagerPool.dataManager.q_globalContainer
					.getMap().get(CommonConfig.SIEGE_SUPRE_CRISSBOW.getValue());
			if (data != null) {
				String[] datstr = data.getQ_string_value().split(
						Symbol.SHUXIAN_REG);
				radius = Integer.parseInt(datstr[1]);// 超级弩箭攻击范围
				atk = Integer.parseInt(datstr[0]); // 超级弩箭攻击力
				cname = ResManager.getInstance().getString("超级弩箭攻城车");
			}
		} else if (msg.getType() == 4) {// 超级投石车 （魔法值清零）
			Q_globalBean data = ManagerPool.dataManager.q_globalContainer
					.getMap()
					.get(CommonConfig.SIEGE_SUPRE_CATAPULTS.getValue());
			if (data != null) {
				String[] datstr = data.getQ_string_value().split(
						Symbol.SHUXIAN_REG);
				radius = Integer.parseInt(datstr[1]);// 超级投石车攻击范围
				atk = Integer.parseInt(datstr[0]); // //超级投石车攻击力
				cname = ResManager.getInstance().getString("超级投石车");
			}
		} else {
			return;
		}
		/*
		 * xuliang MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,
		 * ResManager
		 * .getInstance().getString("花费{1}金币，使用{2}进行攻击。"),money+"",cname);
		 */
		Grid grid = MapUtils.getGrid(msg.getX(), msg.getY(),
				map.getMapModelid());
		List<Player> players = getWarCharAround(grid.getCenter(), radius);
		String injured = "";
		String death = "";
		// Player mirror = transferMirror(player, grid.getCenter());
		// if(mirror!=null){
		// TransferMirrorBackEvent event = new TransferMirrorBackEvent(mirror,
		// player);
		// TimerUtil.addTimerEvent(event);
		// }

		ResCountryWarCarDamageToClientMessage dmsg = new ResCountryWarCarDamageToClientMessage();
		for (Player beattplayer : players) {
			if (beattplayer.getName() != null
					&& beattplayer.getId() != player.getId()
					&& (beattplayer.getGuildId() == 0 || (beattplayer
							.getGuildId() > 0 && beattplayer.getGuildId() != player
							.getGuildId()))) {
				List<Buff> buffs = ManagerPool.buffManager.getBuffByModelId(
						beattplayer, Global.PROTECT_IN_SIEGE);
				if (buffs.size() == 0) {
					double distance = MapUtils.countDistance(grid.getCenter(),
							beattplayer.getPosition()); // 得到距离
					int att = (int) Math.ceil(distance / MapUtils.GRID_BORDER);// 距离/25像素=格子距离
					double s = ((double) (radius - att) / (double) radius);
					if (s > 0) {
						int sum = (int) (atk * s);
						if (beattplayer.getHp() > sum) {
							injured = beattplayer.getName() + "," + injured;
							beattplayer.setHp(beattplayer.getHp() - sum);
							ManagerPool.playerManager.onHpChange(beattplayer);
							beattplayer.setState(PlayerState.FIGHT); // 进入战斗状态
							beattplayer.setLastFightTime(System
									.currentTimeMillis());
							/*
							 * xuliang MessageUtil.notify_player(beattplayer,
							 * Notifys.CHAT_SYSTEM,
							 * ResManager.getInstance().getString
							 * ("您被【{1}】发射的{2}命中，损失生命：{3}"),
							 * player.getName(),cname,sum+"");
							 */
							if (msg.getType() == 3) {// 超级弩箭（体力清零）
								ManagerPool.buffManager.addBuff(beattplayer,
										beattplayer, 24016, 0, 0, 0);
							} else if (msg.getType() == 4) {// 超级投石车 （魔法值清零）
								ManagerPool.buffManager.addBuff(beattplayer,
										beattplayer, 24017, 0, 0, 0);
							}
						} else {
							death = beattplayer.getName() + "," + death;
							beattplayer.setHp(0);
							ManagerPool.playerManager.onHpChange(beattplayer);
							ManagerPool.playerManager.die(beattplayer, player);
							/*
							 * xuliang MessageUtil.notify_player(beattplayer,
							 * Notifys.CHAT_SYSTEM,
							 * ResManager.getInstance().getString
							 * ("您被【{1}】发射的{2}命中后死亡。"), player.getName(),cname);
							 */
						}
						MessageUtil.tell_round_message(beattplayer,
								ManagerPool.fightManager
										.getAttackResultMessage(0, player,
												beattplayer, new Skill(), 0,
												sum, 0, 0, 0));
					}
				}
			}
		}
		MessageUtil.tell_player_message(player, dmsg);

		/*
		 * xuliang if (!injured.equals("") && injured.length() > 4 &&
		 * !death.equals("") && death.length() > 4) {
		 * MessageUtil.notify_player(player, Notifys.CUTOUT,
		 * ResManager.getInstance
		 * ().getString("攻城车成功发射，落至{1},{2}点，重伤玩家：{3},杀死玩家：{4}"
		 * ),msg.getX()+"",msg.getY()+"",injured,death);
		 * MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,
		 * ResManager.getInstance
		 * ().getString("攻城车成功发射，落至{1},{2}点，重伤玩家：{3},杀死玩家：{4}"
		 * ),msg.getX()+"",msg.getY()+"",injured,death); }else { if
		 * (!injured.equals("")) { MessageUtil.notify_player(player,
		 * Notifys.CUTOUT,
		 * ResManager.getInstance().getString("攻城车成功发射，落至{1},{2}点，重伤玩家：{3}，杀死玩家：无"
		 * ),msg.getX()+"",msg.getY()+"",injured);
		 * MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,
		 * ResManager.getInstance
		 * ().getString("攻城车成功发射，落至{1},{2}点，重伤玩家：{3}，杀死玩家：无"
		 * ),msg.getX()+"",msg.getY()+"",injured); }else if (!death.equals(""))
		 * { MessageUtil.notify_player(player, Notifys.CUTOUT,
		 * ResManager.getInstance
		 * ().getString("攻城车成功发射，落至{1},{2}点，重伤玩家：无，杀死玩家：{3}"
		 * ),msg.getX()+"",msg.getY()+"",death);
		 * MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,
		 * ResManager.getInstance
		 * ().getString("攻城车成功发射，落至{1},{2}点，重伤玩家：无，杀死玩家：{3}"
		 * ),msg.getX()+"",msg.getY()+"",death); }else {
		 * MessageUtil.notify_player(player, Notifys.CUTOUT,
		 * ResManager.getInstance
		 * ().getString("攻城车成功发射，落至{1},{2}点，但是什么也没打中。"),msg
		 * .getX()+"",msg.getY()+""); } }
		 */
	}

	/**
	 * 提前把玩家镜像放到目标地点
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqCountryWarCarInAdvanceToGameMessage(Player player,
			ReqCountryWarCarInAdvanceToGameMessage msg) {
		Map map = getSiegeMap();
		Grid grid = MapUtils.getGrid(msg.getX(), msg.getY(),
				map.getMapModelid());
		Player mirror = transferMirror(player, grid.getCenter());
		if (mirror != null) {
			TransferMirrorBackEvent event = new TransferMirrorBackEvent(mirror,
					player);
			TimerUtil.addTimerEvent(event);
		}
	}

	/**
	 * 设置王城盟主雕像信息
	 * 
	 * @param player
	 */
	public void setKingdiaoxoang(Player player) {
//		KingCity kdata = getKingcity();
//		if (getSiegestate() != 1) {
//			if (kdata.getGuildid() > 0
//					&& kdata.getGuildid() == player.getGuildId()) {
//				if (player.getId() != kdata.getHoldplayerid()) {
//					kdata.setHoldplayersex(player.getSex());
//					kdata.setHoldplayerid(player.getId());
//					kdata.setHoldplayername(player.getName());
//					savekingcity(kdata);
//					setdiaoxiang(true);
//				}
//			}
//		}
	}

	// /**攻击目标详细情况
	// *
	// * @param player
	// * @return
	// */
	// private PlayerDamageInfo getPlayerDamageInfo(Player player){
	// PlayerDamageInfo info = new PlayerDamageInfo();
	// info.setPersonId(player.getId());
	// info.setName(player.getName());
	// info.setSex(player.getSex());
	// info.setAvatar(player.getAvatarid());
	// info.setX(player.getPosition().getX());
	// info.setY(player.getPosition().getY());
	// info.setState(player.getState());
	// info.setHp(player.getHp());
	// info.setMaxHp(player.getMaxHp());
	// info.setDir(player.getDirection());
	// info.setGuild(player.getGuildId());
	// if (player.getEquips()[0] != null) {
	// info.setWeapon(player.getEquips()[0].getItemModelId());
	// }
	// if (player.getEquips()[1] != null) {
	// info.setArmor(player.getEquips()[1].getItemModelId());
	// }
	// Horse horse = ManagerPool.horseManager.getHorse(player);
	// if (horse != null && horse.getStatus() == 1) { //骑乘状态
	// info.setHorseid(horse.getCurlayer());
	// }
	// return info;
	// }
	//

	/**
	 * 获取坐标范围内玩家
	 * 
	 * @param position
	 * @param radius
	 * @return
	 */
	public List<Player> getWarCharAround(Position position, int radius) {
		Map map = getSiegeMap();
		if (map != null) {
			List<Area> round = MapManager.getInstance().getRound(map, position);
			if (round != null) {
				List<Player> playertab = new ArrayList<Player>();
				for (Area area : round) {
					HashSet<Player> players = area.getPlayers();
					if (players != null && players.size() > 0) {
						Iterator<Player> it = players.iterator();
						while (it.hasNext()) {
							Player splayer = (Player) it.next();
							if (!splayer.isDie()) {
								double distance = MapUtils.countDistance(
										position, splayer.getPosition()); // 得到距离
								if (distance <= MapUtils.GRID_BORDER * radius) {
									playertab.add(splayer);
								}
							}
						}
					}
				}
				return playertab;
			}
		}
		return null;
	}

	/**
	 * 攻城中刷怪
	 * 
	 */

	public void appearMonster() {
		setMonstatus(1);
		/*
		 * xuliang MessageUtil.notify_All_player(Notifys.SROLL,
		 * ResManager.getInstance
		 * ().getString("神级BOSS【吕不韦】携带残余叛军进攻承玺台，请豪侠们速速前往击杀，可获得道具及装备奖励"));
		 */
		Map map = getSiegeMap();
		String xystr = ManagerPool.dataManager.q_globalContainer.getMap()
				.get(CommonConfig.SIEGE_BRUSH_XY.getValue())
				.getQ_string_value();
		String monstr = ManagerPool.dataManager.q_globalContainer.getMap()
				.get(CommonConfig.SIEGE_MON.getValue()).getQ_string_value();
		List<Integer[]> xyList = JSON.parseArray(xystr, Integer[].class);
		List<Integer[]> monList = JSON.parseArray(monstr, Integer[].class);

		int rnd = RandomUtils.random(1, xyList.size()) - 1;
		Integer[] xydata = xyList.remove(rnd);
		Position position = new Position();
		position.setX((short) (xydata[0] * MapUtils.GRID_BORDER));
		position.setY((short) (xydata[1] * MapUtils.GRID_BORDER));
		Integer[] monboss = monList.remove(0);
		ManagerPool.monsterManager
				.createMonsterAndEnterMap(monboss[0], map.getServerId(),
						map.getLineId(), (int) map.getId(), position);

		List<Integer> mons = new ArrayList<Integer>();
		for (Integer[] mon : monList) {
			for (int i = 0; i < mon[1]; i++) {
				mons.add(mon[0]);
			}
		}

		for (int i = 0; i < 25; i++) {// 打乱顺序
			int idx = RandomUtils.random(1, mons.size()) - 1;
			int s = mons.remove(idx);
			mons.add(s);
		}

		int num = 0;
		for (int i = 0; i < mons.size(); i++) {
			if (xyList.size() <= num) {
				num = 0;
			}
			Integer[] xydb = xyList.get(num);
			num = num + 1;
			Position xposition = new Position();
			xposition.setX((short) (xydb[0] * MapUtils.GRID_BORDER));
			xposition.setY((short) (xydb[1] * MapUtils.GRID_BORDER));
			ManagerPool.monsterManager.createMonsterAndEnterMap(mons.get(i),
					map.getServerId(), map.getLineId(), (int) map.getId(),
					xposition);
		}
	}

	/**
	 * 传送回城
	 * 
	 */
	public void backtocitymove() {
		try {
			Map map = getSiegeMap();
			Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap()
					.get(SIEGE_MAPID);
			if (map.getPlayerNumber() > 0) {
				HashMap<Long, Player> players = map.getPlayers();
				List<Player> playerslist = new ArrayList<Player>();
				playerslist.addAll(players.values());
				for (Player player : playerslist) {
					
					ManagerPool.playerManager.changePkState(player, 0, 0);
					
					Position position = ManagerPool.mapManager
							.RandomDieBackCity(mapBean);
					ManagerPool.playerManager.autoRevive(player);
					ManagerPool.mapManager.changeMap(player,
							mapBean.getQ_map_die(), mapBean.getQ_map_die(), 1,
							position, this.getClass().getName()
									+ ".backtocitymove");
						player.setGroupmark(0);
						BuffManager.getInstance().removeByBuffId(player, addBuffId,1146,1147,1148,1166);
						
						//停用  邮件奖励
//						List<Item> items = new ArrayList<Item>();
//						String rewards;
//						if(player.getGuildId()==kingcity.getGuildid()){
//							rewards =DataManager.getInstance().q_countryContainer.getMap().get(20000+player.getLevel()).getQ_rewards();
//						
//							
//						}else{
//							 rewards =DataManager.getInstance().q_countryContainer.getMap().get(10000+player.getLevel()).getQ_rewards();
//						}
//						
//						String[] reward  =  rewards.split(";");
//						for (int i = 0; i < reward.length; i++) {
//							String  id = reward[i].split("_")[0];
//							String num = reward[i].split("_")[1];
//							items.addAll(Item.createItems(Integer.parseInt(id), Integer.parseInt(num), true, 0l));
//						}
//						
//						//发送奖励
//					
////						items.addAll(Item.createItems(100030, 10, true, 0l));
//						MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), ResManager.getInstance().getString("攻城战奖励"), ResManager.getInstance().getString("攻城战奖励"), (byte) 1, 0, items);
//						
//						ManagerPool.playerManager.changePkState(player, 0, 0);
						
				}
				playerslist = null;
			}
		} catch (Exception e) {
			log.error(e);
		}

	}

	public long getMovetime() {
		return movetime;
	}

	public void setMovetime(long movetime) {
		this.movetime = movetime;
	}

	// /**内测圣盟奖励标记
	// *
	// */
	// public void setkingguildReward(){
	// long gid = getKingcity().getGuildid();
	// if (gid > 0) {
	// Map map = getSiegeMap();
	// HashMap<Long, Player> players = map.getPlayers();
	// List<Player> playerslist=new ArrayList<Player>();
	// playerslist.addAll(players.values());
	// for (Player player :playerslist) {
	// if ( player.getGuildId() == gid) {
	// player.getActivitiesReward().put("REWARDLORDGUILD",
	// ""+TimeUtil.GetCurTimeInMin(4));
	// }
	// }
	// }
	// }

	/**
	 * 随机刷道具
	 * 
	 * @return
	 */
	public void RefreshMapItemDrop() {
		Map map = getSiegeMap();
		String xystr = ManagerPool.dataManager.q_globalContainer.getMap()
				.get(CommonConfig.SIEGE_ITEM_XY.getValue()).getQ_string_value();
		String itemstr = ManagerPool.dataManager.q_globalContainer.getMap()
				.get(CommonConfig.SIEGE_ITEM.getValue()).getQ_string_value();
		List<Integer[]> xyList = JSON.parseArray(xystr, Integer[].class);
		List<Integer[]> itemList = JSON.parseArray(itemstr, Integer[].class);

		for (int i = 0; i < 10; i++) {
			int idx = RandomUtils.random(1, xyList.size()) - 1;
			Integer[] xy = xyList.remove(idx);
			int itemidx = RandomUtils.random(1, itemList.size()) - 1;
			List<Item> items = Item.createItems(itemList.get(itemidx)[0],
					itemList.get(itemidx)[1], true, 0);
			if (!items.isEmpty()) {
				Item item = items.get(0);
				DropGoodsInfo info = new DropGoodsInfo();
				info.setDropGoodsId(item.getId());
				info.setItemModelId(item.getItemModelId());
				info.setNum(item.getNum());
				Grid grid = MapUtils.getGrid(xy[0], xy[1], map.getMapModelid());
				info.setX(grid.getCenter().getX());
				info.setY(grid.getCenter().getY());
				info.setDropTime(System.currentTimeMillis());
				item.setGridId(0);
				MapDropInfo mapDropInfo = new MapDropInfo(info, item, map,
						System.currentTimeMillis() + 60 * 5 * 1000);
				ManagerPool.mapManager.enterMap(mapDropInfo);
			}
		}
	}

	/**
	 * 传送镜像到指定坐标
	 * 
	 * @param player
	 * @param pos
	 * @return
	 */
	public Player transferMirror(Player player, Position pos) {
		Player mirror = new Player();
		mirror.setId(player.getId());
		mirror.setShow(false);
		mirror.setPosition(pos);
		mirror.setLocate(player.getLocate());
		mirror.setLine(player.getLine());
		mirror.setMap(player.getMap());
		mirror.setMapModelId(player.getMapModelId());
		mirror.setGateId(player.getGateId());

		Map map = ManagerPool.mapManager.getMap(player);
		if (map == null) {
			log.error("mirror entermap player " + player.getId() + " server "
					+ player.getServerId() + " line " + player.getLine()
					+ " map " + player.getMap() + " not found!");
			return null;
		}

		Grid[][] grids = ManagerPool.mapManager.getMapBlocks(map
				.getMapModelid());

		int oldAreaId = ManagerPool.mapManager.getAreaId(player.getPosition());
		int newAreaId = ManagerPool.mapManager.getAreaId(pos);

		Area newarea = map.getAreas().get(newAreaId);
		if (newarea == null) {
			log.error("mirror player " + player.getId() + " server "
					+ player.getServerId() + " line " + player.getLine()
					+ " map " + player.getMap() + " area " + newAreaId
					+ " not found, position" + player.getPosition());
			return null;
		}

		newarea.getPlayers().add(mirror);

		List<Area> oldRound = ManagerPool.mapManager.getRoundAreas(map,
				oldAreaId);
		List<Area> newRound = ManagerPool.mapManager.getRoundAreas(map,
				newAreaId);

		HashSet<Area> oldSet = new HashSet<Area>();
		for (int i = 0; i < oldRound.size(); i++) {
			// System.out.println("oldArea:" + oldRound.get(i).getId());
			oldSet.add(oldRound.get(i));
		}

		HashSet<Area> newSet = new HashSet<Area>();
		for (int i = 0; i < newRound.size(); i++) {
			// System.out.println("newArea" + newRound.get(i).getId());
			newSet.add(newRound.get(i));
		}

		ResRoundObjectsMessage msg = new ResRoundObjectsMessage();

		for (int i = 0; i < newRound.size(); i++) {
			Area area = newRound.get(i);
			if (oldSet.contains(area)) {
				continue;
			}

			Iterator<Player> iter = area.getPlayers().iterator();
			while (iter.hasNext()) {
				Player other = (Player) iter.next();
				if (other.getId() == player.getId()) {
					continue;
				}
				if (other.canSee(player)) {
					msg.getPlayer().add(
							ManagerPool.mapManager.getPlayerInfo(other, grids));
				}
			}

			Iterator<Monster> monster_iter = area.getMonsters().values()
					.iterator();
			while (monster_iter.hasNext()) {
				Monster monster = (Monster) monster_iter.next();

				if (monster.canSee(player)) {
					msg.getMonster().add(
							ManagerPool.mapManager.getMonsterInfo(monster,
									grids));
				}
			}

			Iterator<MapDropInfo> iterator = area.getDropGoods().values()
					.iterator();
			while (iterator.hasNext()) {
				MapDropInfo next = iterator.next();

				if (next.canSee(player)) {
					msg.getGoods().add(next.getDropinfo());
				}
			}

			HashSet<Pet> petset = area.getPets();
			for (Pet pet : petset) {
				if (pet.canSee(player)) {
					msg.getPets().add(
							ManagerPool.mapManager.getPetInfo(pet, grids));
				}
			}

			Iterator<NPC> npciterator = area.getNpcs().values().iterator();
			while (npciterator.hasNext()) {
				NPC npc = (NPC) npciterator.next();
				if (npc.canSee(player)) {
					msg.getNpcs().add(ManagerPool.mapManager.getNpcInfo(npc));
				}
			}

			Iterator<Effect> effectiterator = area.getEffects().values()
					.iterator();
			while (effectiterator.hasNext()) {
				Effect effect = (Effect) effectiterator.next();
				if (effect.canSee(player)) {
					msg.getEffect().add(
							ManagerPool.mapManager.getEffectInfo(effect));
				}
			}
		}

		MessageUtil.tell_player_message(player, msg);

		return mirror;
	}

	/**
	 * 传送镜像到指定坐标
	 * 
	 * @param player
	 * @param pos
	 * @return
	 */
	public void transferMirrorBack(Player mirror, Position pos) {

		Map map = ManagerPool.mapManager.getMap(mirror);
		if (map == null) {
			log.error("mirror quitmap player " + mirror.getId() + " server "
					+ mirror.getServerId() + " line " + mirror.getLine()
					+ " map " + mirror.getMap() + " not found!");
			return;
		}

		int oldAreaId = ManagerPool.mapManager.getAreaId(mirror.getPosition());
		int newAreaId = ManagerPool.mapManager.getAreaId(pos);

		Area oldarea = map.getAreas().get(oldAreaId);
		if (oldarea == null) {
			log.error("mirror player " + mirror.getId() + " server "
					+ mirror.getServerId() + " line " + mirror.getLine()
					+ " map " + mirror.getMap() + " area " + oldAreaId
					+ " not found, position" + mirror.getPosition());
			return;
		}

		boolean result = oldarea.getPlayers().remove(mirror);
		if (!result) {
			log.error("no contain mirror！");
		}

		List<Area> oldRound = ManagerPool.mapManager.getRoundAreas(map,
				oldAreaId);
		List<Area> newRound = ManagerPool.mapManager.getRoundAreas(map,
				newAreaId);

		HashSet<Area> oldSet = new HashSet<Area>();
		for (int i = 0; i < oldRound.size(); i++) {
			// System.out.println("oldArea:" + oldRound.get(i).getId());
			oldSet.add(oldRound.get(i));
		}

		HashSet<Area> newSet = new HashSet<Area>();
		for (int i = 0; i < newRound.size(); i++) {
			// System.out.println("newArea" + newRound.get(i).getId());
			newSet.add(newRound.get(i));
		}

		ResRoundObjectsMessage msg = new ResRoundObjectsMessage();

		for (int i = 0; i < oldRound.size(); i++) {
			Area area = oldRound.get(i);
			if (newSet.contains(area)) {
				continue;
			}

			Iterator<Player> iter = area.getPlayers().iterator();
			while (iter.hasNext()) {
				Player other = (Player) iter.next();
				if (other.getId() == mirror.getId()) {
					continue;
				}
				if (other.canSee(mirror)) {
					msg.getPlayerIds().add(other.getId());
				}
			}

			Iterator<Monster> monster_iter = area.getMonsters().values()
					.iterator();
			while (monster_iter.hasNext()) {
				Monster monster = (Monster) monster_iter.next();

				if (monster.canSee(mirror)) {
					msg.getMonstersIds().add(monster.getId());
				}
			}

			Iterator<MapDropInfo> iterator = area.getDropGoods().values()
					.iterator();
			while (iterator.hasNext()) {
				MapDropInfo next = iterator.next();

				if (next.canSee(mirror)) {
					msg.getGoodsIds().add(next.getId());
				}
			}

			HashSet<Pet> petset = area.getPets();
			for (Pet pet : petset) {
				if (pet.canSee(mirror)) {
					msg.getPetIds().add(pet.getId());
				}
			}

			Iterator<NPC> npciterator = area.getNpcs().values().iterator();
			while (npciterator.hasNext()) {
				NPC npc = (NPC) npciterator.next();
				if (npc.canSee(mirror)) {
					msg.getNpcid().add(npc.getId());
				}
			}

			Iterator<Effect> effectiterator = area.getEffects().values()
					.iterator();
			while (effectiterator.hasNext()) {
				Effect effect = (Effect) effectiterator.next();
				if (effect.canSee(mirror)) {
					msg.getEffectid().add(effect.getId());
				}
			}
		}

		MessageUtil.tell_player_message(mirror, msg);

	}

	public int getOpenArea() {
		return OpenArea;
	}

	public void setOpenArea(int openArea) {
		OpenArea = openArea;
	}

	/**
	 * 10条线全部 展示或者隐藏雕像
	 * 
	 * @param isshow
	 *            true展示
	 */
	public void setdiaoxiang(boolean isshow) {
		int sid = WServer.getInstance().getServerId();
		setdiaoxiang(isshow, sid);
	}

	/**
	 * 10条线全部 展示或者隐藏雕像
	 * 
	 * @param isshow
	 *            true展示
	 */
	public void setdiaoxiang(boolean isshow, int sid) {
		try {
			for (int i = 1; i <= 10; i++) {
				Map map = ManagerPool.mapManager.getMap(sid, i, 20002);
				if (map != null) {
					if (isshow) {
						map.getParameters().put(WANGCHENGDIAOXIANG, 1);
					} else {
						map.getParameters().put(WANGCHENGDIAOXIANG, 2);
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
		}

	}

	/**
	 * 是否启用新的攻城战和领地战时间 开区时间在20121110之前使用原来的时间false， 之后使用新时间true
	 * 
	 * @return
	 */
	public boolean stSiegeIntervalDay(int sid) {
		try {
			Date date = WServer.getGameConfig().getServerTimeByServer(sid);
			if (date != null) {
				long time = date.getTime();
				int year = TimeUtil.getYear(time);
				int month = TimeUtil.getMonth(time) + 1;
				int day = TimeUtil.getDayOfMonth(time);
				int sday = year * 10000 + month * 100 + day;
				if (sday >= 20121110) {
					return true;
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		setOpenArea(4);// 攻城战距离开区间隔天数
		ManagerPool.guildFlagManager.setOpenArea(1);// 领地战战距离开区间隔天数
		return false;
	}

	// --------------------------------------------------------------------------

	
	public SiegeSMS getSMSTopData(Player player) {
		SiegeSMS siegeSMS = null;
		if (SiegeSMSTopMap.containsKey(player.getId())) {
			siegeSMS = SiegeSMSTopMap.get(player.getId());
		} else {
			siegeSMS = new SiegeSMS(player);
			SiegeSMSTopMap.put(player.getId(), siegeSMS);
		}
		return siegeSMS;
	}
	
	
	/**
	 * 改变攻城战 排行榜数据
	 * 
	 */
	public void changeSiegeSMSTopData(Player player, int type, int num,Player other) {
		SiegeSMS siegeSMS = null;
		if (SiegeSMSTopMap.containsKey(player.getId())) {
			siegeSMS = SiegeSMSTopMap.get(player.getId());
		} else {
			siegeSMS = new SiegeSMS(player);
			SiegeSMSTopMap.put(player.getId(), siegeSMS);
		}

		if (type == KILL) {
			
			//击杀不同阵营的人  获得 连杀
			if(player.getGroupmark()!= other.getGroupmark()){
			// 杀敌
				siegeSMS.setKill(siegeSMS.getKill() + num);
				siegeSMS.setConKill(siegeSMS.getConKill()+num);
				
				
				
				
				if(siegeSMS.getConKill()>siegeSMS.getMaxConKill()){
					siegeSMS.setMaxConKill(siegeSMS.getConKill());
				}
				
				ResMonsterBatterToClientMessage cmsg = new ResMonsterBatterToClientMessage();
				cmsg.setId(0);
				cmsg.setNum(siegeSMS.getConKill());
				cmsg.setCountdowntime(0);
				cmsg.setType((byte) 1);
				MessageUtil.tell_player_message(player, cmsg);
				
				ResPlayerKillMessage cmsg2 = new ResPlayerKillMessage();
				cmsg2.setPlayerId(player.getId());
				cmsg2.setCount(siegeSMS.getConKill());
				MessageUtil.tell_round_message(player, cmsg2);
				
				
				
				if(siegeSMS.getConKill() >= 10 && siegeSMS.getConKill() % 10 == 0){
					tellMapMessage(player,siegeSMS.getConKill()) ;
				}
			}

		} else if (type == DEATH) {// 死亡
			siegeSMS.setDeath(siegeSMS.getDeath() + num);
			
			if(siegeSMS.getConKill()>20){
				MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}终结了{2}的{3}连杀，除去一个强有力的对手！"),other.getName(),player.getName(),siegeSMS.getConKill()+"");
			}
			siegeSMS.setConKill(0);
			
			
			ResMonsterBatterToClientMessage cmsg = new ResMonsterBatterToClientMessage();
			cmsg.setId(0);
			cmsg.setNum(siegeSMS.getConKill());
			cmsg.setCountdowntime(0);
			cmsg.setType((byte) 1);
			MessageUtil.tell_player_message(player, cmsg);
			
			ResPlayerKillMessage cmsg2 = new ResPlayerKillMessage();
			cmsg2.setPlayerId(player.getId());
			cmsg2.setCount(siegeSMS.getConKill());
			MessageUtil.tell_round_message(player, cmsg2);
			
		
			
		} else if (type == HURT) {// 伤害输出
			siegeSMS.setHurt(siegeSMS.getHurt() + num);
		} else if (type == BEENHURT) {// 被伤害
			siegeSMS.setBeenhurt(siegeSMS.getBeenhurt() + num);
		}
	}
	
	public void tellMapMessage(Player player,int count) {
		switch (count) {
		case 80:
			MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}连杀80人，他已经超越神了！"),player.getName());
			break;
		case 70:
			MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("天啊，{1}已经连杀70人了，如同神一般的存在！"),player.getName());
			break;
		case 60:
			MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString(" {1}连杀60人，真是妖一般的杀戮啊，罪过，罪过！"),player.getName());
			break;
		case 50:
			MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString(" {1}连杀50人，开始真正的变态杀戮！"),player.getName());
			break;
		case 40:
			MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString(" {1}连杀40人，在战场中竟然无人能挡！"),player.getName());
			break;
		case 30:
			MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}连杀30人，真是杀人如麻！"),player.getName());
			break;
		case 20:
			MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}连杀20人，已经主宰比赛了！"),player.getName());
			break;
		case 10:
			MessageUtil.notify_map(getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString(" {1}连杀10人，开始大杀特杀，谁能阻止他？"),player.getName());
			break;
		default:
			break;
		}
		
		
		
		
		
	}

	/**
	 * 获取攻城战排行榜
	 * 
	 * @return
	 * 
	 */
	public List<CountryTopInfo> getSiegeSMStopinfo(Player player, int num) {
		List<CountryTopInfo> infos = new ArrayList<CountryTopInfo>();
		try {
			List<SiegeSMS> toplist = new ArrayList<SiegeSMS>();
			if (SiegeSMSTopMap.size() > 0) {
				toplist.addAll(SiegeSMSTopMap.values());
				Collections.sort(toplist, new SiegeSMSSort());
				for (int i = 0; i < toplist.size(); i++) {
					SiegeSMS top = toplist.get(i);
					if (i >= num) {
						if (player == null) {
							break;
						} else if (top.getPlayerid() == player.getId()) {
							CountryTopInfo info = top.getinfo();
							info.setRanking(i + 1);
							infos.add(info);
							break;
						}
					} else {
						CountryTopInfo info = top.getinfo();
						info.setRanking(i + 1);
						infos.add(info);
					}
				}
			}
		} catch (Exception e) {
			log.error("攻城战排行榜：" + e, e);
		}
		return infos;
	}

	/**
	 * 获取攻城战排行榜
	 * 
	 * @return
	 * 
	 */
	public List<CountryTopInfo> getEndSiegeSMStopinfo() {
		KingCity kingCity = ManagerPool.countryManager.getKingcity();
	
		List<CountryTopInfo> infos = new ArrayList<CountryTopInfo>();
		try {
			List<SiegeSMS> toplist = new ArrayList<SiegeSMS>();
			if (SiegeSMSTopMap.size() > 0) {
				toplist.addAll(SiegeSMSTopMap.values());
				Collections.sort(toplist, new SiegeSMSSort());
				for (int i = 0; i < toplist.size(); i++) {
					SiegeSMS top = toplist.get(i);

					CountryTopInfo info = top.getinfo();
					Player menber = PlayerManager.getInstance().getPlayer(
							info.getPlayerid());
					if (menber != null) {
						info.setRanking(i + 1);
						info.setJob(menber.getJob());
						int key = DataManager.getInstance().q_characterContainer
								.getKey(menber.getJob(), menber.getLevel());
						Q_characterBean q = DataManager.getInstance().q_characterContainer
								.getMap().get(key);
						if (kingCity != null
								&& kingCity.getGuildid() == menber.getGuildId()) {
							ManagerPool.playerManager.addExp(menber,
									q.getQ_rexp() * 576,
									AttributeChangeReason.FUBENG);
							info.setExp(q.getQ_rexp() * 576);
						} else {
							ManagerPool.playerManager.addExp(menber,
									q.getQ_rexp() * 144,
									AttributeChangeReason.FUBENG);
							info.setExp(q.getQ_rexp() * 144);
						}

						infos.add(info);
					}

				}
			}
		} catch (Exception e) {
			log.error("攻城战排行榜：" + e, e);
		}
		return infos;
	}

	/**
	 * 给玩家发送排行榜消息
	 * 
	 * @param player
	 */
	public void stReqCountryOpenTopToGameMessage(Player player) {
		ResCountryTopInfoToClientMessage cmsg = new ResCountryTopInfoToClientMessage();
		List<CountryTopInfo> list = getSiegeSMStopinfo(player, 120);
		cmsg.setCountryTopInfolist(list);
		MessageUtil.tell_player_message(player, cmsg);
	}

	/**
	 * 地图广播排行榜
	 * 
	 */
	public void mapbroadcastTop() {
		ResCountryTopInfoToClientMessage cmsg = new ResCountryTopInfoToClientMessage();
		List<CountryTopInfo> list = getSiegeSMStopinfo(null, 120);
		cmsg.setCountryTopInfolist(list);
		MessageUtil.tell_map_message(getSiegeMap(), cmsg);
	}

	public static void main(String[] args) {
//		int x = 111;
//		int y = 222;
//
//		int x1 = 308;
//		int y1 = 53;
//
//		int x2 = 249;
//		int y2 = 106;
//
//		int x3 = 341;
//		int y3 = 146;
//
//		if (((y3 - y2) * (x - x2) - (x3 - x2) * (y - y2)) > 0) {
//			if (((y2 - y1) * (x - x1) - (x2 - x1) * (y - y1)) > 0) {
//				System.out.println("满足条件的坐标是:" + x + "," + y);
//			}
//		}

		
		
		
		// double x1 = 0;
		// double y1 = 26;
		// double x2 = 232;
		// double y2 = 159;
		// int x = player.getPosition().getX()/MapUtils.GRID_BORDER;
		// int y = player.getPosition().getY()/MapUtils.GRID_BORDER;
		// double value = (x2-x1) * (y-y1) - (y2-y1) * (x-x1);
		// if (value > 0) {
		// return 10;
		// }else {
		// return 20;
		// }

		
		
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(new Date());
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		if(cal.get(Calendar.DAY_OF_WEEK)!=1){
//			cal.add(Calendar.WEEK_OF_YEAR, 1);
//		}
//		cal.set(Calendar.DAY_OF_WEEK, 6);
//		long time = cal.getTimeInMillis();
//		
//		int m =TimeUtil.getMonth(time)+1;
//		int d = TimeUtil.getDayOfMonth(time);
//		System.out.println(m+"_"+d);
		

		Long time =CountryManager.getInstance(). getNextWeekFive();
//	}else{
//		//开服第一周 只打 一场攻城战
//		//取下周五的时间
//		
//		time = getNextWeekFive();
//	}
	long mday = TimeUtil.getDayOfMonth(time);
	long month = TimeUtil.getMonth(time) + 1;
	System.out.println(mday);
	System.out.println( month);
		
	}

}

// 1.优先排净胜数，净胜数越大排名越靠前（净胜数=击杀人数-被击杀数）
// 2.净胜数相同情况下按等级排序，等级高的在前面
// 3.净胜数相同等级相同的情况下，按用户ID排序，用户ID较小的排在前面

class SiegeSMSSort implements Comparator<SiegeSMS> {
	public int compare(SiegeSMS arg0, SiegeSMS arg1) {

		if (arg0.getKill() > arg1.getKill()) {
			return -1;
		}

		if (arg0.getKill() == arg1.getKill()) {
			if (arg0.getDeath() < arg1.getDeath()) {
				return -1;
			}

			if (arg0.getDeath() == arg1.getDeath()) {
				if (arg0.getBeenhurt() < arg1.getBeenhurt()) {
					return -1;
				}
			}
		}

		return 1;
	}
}
