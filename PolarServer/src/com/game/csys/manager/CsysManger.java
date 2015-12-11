package com.game.csys.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.backpack.bean.ItemInfo;
import com.game.buff.manager.BuffManager;
import com.game.config.Config;
import com.game.csys.bean.CsysTopInfo;
import com.game.csys.message.ResChangeIntegralMessage;
import com.game.csys.message.ResCsysTopListMessage;
import com.game.csys.message.ResEnterActivitieMessage;
import com.game.csys.message.ResPlayerKillMessage;
import com.game.csys.structs.CsysSMS;
import com.game.data.bean.Q_activity_monstersBean;
import com.game.data.bean.Q_characterBean;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.bean.Q_csysBean;
import com.game.data.manager.DataManager;
import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.map.structs.Map;
import com.game.monster.structs.Monster;
import com.game.monster.structs.MonsterState;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.server.config.MapConfig;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.structs.Reasons;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.TimeUtil;
import com.game.zones.manager.ZonesManager;
import com.game.zones.message.ResZoneLifeTimeMessage;
import com.game.zones.message.ResZonePassShowMessage;
import com.game.zones.structs.ZoneContext;

/**
 * 
 * @author wzh
 * 
 */
public class CsysManger {

	protected Logger log = Logger.getLogger(CsysManger.class);
	/**
	 * 积分排行榜 key 为 副本ID value 为 <玩家ID，玩家排行榜数据>
	 */
	private HashMap<Long, HashMap<Long, CsysSMS>> csysSMSTopMap = new HashMap<Long, HashMap<Long, CsysSMS>>();

	public HashMap<Long, HashMap<Long, CsysSMS>> getCsysSMSTopMap() {
		return csysSMSTopMap;
	}

	public void setCsysSMSTopMap(
			HashMap<Long, HashMap<Long, CsysSMS>> csysSMSTopMap) {
		this.csysSMSTopMap = csysSMSTopMap;
	}

	private int index = 0;

	private static Object obj = new Object();

	private static CsysManger manager;

	public static CsysManger getInstance() {

		synchronized (obj) {
			if (manager == null) {
				manager = new CsysManger();
			}
		}
		return manager;
	}

	public static int KILLMONSTER = 1; // 杀敌数量
	public static int DEATH = 2; // 死亡次数
	public static int KILLPLAYER = 3; // 击杀玩家
	public static int COLLECT = 4; // 采集数量

	// 副本状态 0没有开启，1进入中，2进行中,3结束(发奖励需要)
	private int cstsstate = 0;

	private List<ZoneContext> listZone = new ArrayList<ZoneContext>();

	private HashMap<Long, ZoneContext> playerMap = new HashMap<Long, ZoneContext>();

	/**
	 * 赤色要塞 地图ID
	 */
	public static int CSYS_MAPID = 600001;

	/**
	 * 赤色要塞 副本ID
	 */
	public static int CSYS_ZONEID = 6001;

	public HashMap<Long, ZoneContext> getPlayerMap() {
		return playerMap;
	}

	public void setPlayerMap(HashMap<Long, ZoneContext> playerMap) {
		this.playerMap = playerMap;
	}

	public List<ZoneContext> getListZone() {
		return listZone;
	}

	public void setListZone(List<ZoneContext> listZone) {
		this.listZone = listZone;
	}

	public int getCstsstate() {
		return cstsstate;
	}

	public void setCstsstate(int cstsstate) {
		this.cstsstate = cstsstate;
	}

	// 进入战场随机传送点 [[144,86],[113,105]]
	private int[][] ALONE_XY = { { 44, 74 }, { 59, 65 }, { 59, 80 },
			{ 71, 73 }, { 74, 90 }, { 93, 91 }, { 105, 97 }, { 118, 106 },
			{ 132, 97 }, { 150, 89 }, { 175, 75 }, { 172, 84 }, { 183, 75 },
			{ 167, 66 }, { 154, 58 }, { 143, 51 }, { 131, 45 }, { 119, 40 },
			{ 110, 42 }, { 118, 35 }, { 95, 50 }, { 85, 57 }, { 68, 63 },
			{ 85, 72 }, { 117, 56 }, { 117, 89 }, { 150, 73 }, { 101, 73 },
			{ 131, 72 }, { 117, 63 }, { 118, 81 }, { 117, 72 } };

	/**
	 * 复活 赤色要塞内的玩家
	 * 
	 * @param player
	 * @param map
	 */
	public void revive(Player player, Map map) {

		if (getCstsstate() == 2) {
			// [[144,86],[113,105]]
			Position position = new Position();
			int rnd = RandomUtils.random(1, ALONE_XY.length) - 1;
			int x = ALONE_XY[rnd][0];
			int y = ALONE_XY[rnd][1];
			position.setX((short) (x * MapUtils.GRID_BORDER));
			position.setY((short) (y * MapUtils.GRID_BORDER));

			// ManagerPool.mapManager.changeMap(player, (int) map.getId(),
			// map.getMapModelid(), 0, position, ChangeReason.revive, this
			// .getClass().getName() + ".revive");

			List<Grid> gridlist = MapUtils.getRoundNoBlockGrid(position,
					2 * MapUtils.GRID_BORDER, CSYS_MAPID);
			int rnd2 = RandomUtils.random(1, gridlist.size()) - 1;

			BuffManager.getInstance().addBuff(player, player,
					Global.CSYS_PROTECT_FOR_KILLED, 0, 0, 0);

			ManagerPool.mapManager.changeMap(player, (int) map.getId(),
					CSYS_MAPID, 1, gridlist.get(rnd2).getCenter(), this
							.getClass().getName()
							+ ".stResCountrySiegeSelectToGameMessage 1");
		}

	}

	/**
	 * 赤色要塞 开启 定时任务
	 */
	public void loopcall() {
		long millis = System.currentTimeMillis();
		long min = TimeUtil.getDayOfMin(millis);
		long hour = TimeUtil.getDayOfHour(millis);

		// 提前五分钟创建
		if (hour == 14 && min == 50) {
			// 创建副本
			if (listZone.size() == 0) {
				creatZones();
				setCstsstate(0);
			}

		} else if (hour == 14 && min == 55) {
			if (getCstsstate() == 0 && listZone.size() != 0) {
				showCsys();
				MessageUtil.notify_All_player(Notifys.SROLL, ResManager
						.getInstance().getString("赤色要塞于14点55分正式开始 ，请各位玩家准备入场"));
			}
		}

		else if (hour == 14 && min == 59) {
			if (getCstsstate() == -1 && listZone.size() != 0) {
				startCsys();
			}
		}

		else if (hour == 15 && min == 0) {
			if (getCstsstate() == 1) {
				fight();

			}
		} else if (hour == 15 && min == 2) {
			if (getCstsstate() == 2) {
				say1();
			}
		} else if (hour == 15 && min == 5) {
			if (getCstsstate() == 2) {
				say2();
			}
		} else if (hour == 15 && min == 10) {
			if (getCstsstate() == 2) {
				endCsys();
				MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM, ResManager
						.getInstance().getString("赤色要塞已经结束! 感谢大家的参与 "));
			}
		}

		// 提前五分钟创建
		if (hour == 20 && min == 55) {
			// 创建副本
			if (listZone.size() == 0) {
				creatZones();
				setCstsstate(0);
			}

		} else if (hour == 21 && min == 0) {
			if (getCstsstate() == 0 && listZone.size() != 0) {
				showCsys();
				MessageUtil.notify_All_player(Notifys.SROLL, ResManager
						.getInstance().getString("赤色要塞于21点5分正式开始 ，请各位玩家准备入场"));
			}
		}

		else if (hour == 21 && min == 4) {
			if (getCstsstate() == -1 && listZone.size() != 0) {
				startCsys();
			}
		}

		else if (hour == 21 && min == 5) {
			if (getCstsstate() == 1) {
				fight();

			}
		}

		else if (hour == 21 && min == 7) {
			if (getCstsstate() == 2) {
				say1();
			}
		}

		else if (hour == 21 && min == 10) {
			if (getCstsstate() == 2) {
				say2();
			}
		} else if (hour == 21 && min == 15) {
			if (getCstsstate() == 2) {
				endCsys();
				MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM, ResManager
						.getInstance().getString("赤色要塞已经结束! 感谢大家的参与 "));
			}
		}

	}

	public void say1() {
		for (int i = 0; i < listZone.size(); i++) {
			MapConfig m = listZone.get(i).getConfigs().get(0);
			Map map = MapManager.getInstance().getMap(m.getServerId(),
					m.getLineId(), m.getMapId());
			if (map != null) {
				// MessageUtil.notify_All_player(Notifys.CUTOUT,
				// "<font color ='#00FF00'>BOSS-炽炎魔</fort> <font color ='#FFD47F'>即将在场景中央刷新</fort> <font color ='#FF0000'>请各位注意</font>");
				MessageUtil
						.notify_map(
								map,
								Notifys.CUTOUT,
								ResManager
										.getInstance()
										.getString(
												"<font color ='#00FF00'>BOSS-炽炎魔</fort> <font color ='#FFD47F'>即将在场景中央刷新</fort> <font color ='#FF0000'>请各位注意</font> "));
			}
		}
	}

	public void say2() {
		for (int i = 0; i < listZone.size(); i++) {
			MapConfig m = listZone.get(i).getConfigs().get(0);
			Map map = MapManager.getInstance().getMap(m.getServerId(),
					m.getLineId(), m.getMapId());
			if (map != null) {
				// MessageUtil.notify_All_player(Notifys.CUTOUT,
				// "<font color ='#00FF00'>BOSS-炽炎魔</fort> <font color ='#FFD47F'>即将在场景中央刷新</fort> <font color ='#FF0000'>请各位注意</font>");
				MessageUtil
						.notify_map(
								map,
								Notifys.CUTOUT,
								ResManager
										.getInstance()
										.getString(
												"<font color ='#00FF00'>天魔菲尼克斯</fort> <font color ='#FFD47F'>即将在场景中央刷新</fort> <font color ='#FF0000'>请各位注意</font> "));
			}
		}
	}

	public void fight() {

		// 开始战斗
		setCstsstate(2);
		for (int i = 0; i < listZone.size(); i++) {
			MapConfig m = listZone.get(i).getConfigs().get(0);
			Map map = MapManager.getInstance().getMap(m.getServerId(),
					m.getLineId(), m.getMapId());

			listZone.get(i).getOthers().put("zoneprocess", 2);
			for (Player player : map.getPlayers().values()) {
				// 切换至PK模式
				ManagerPool.playerManager.changePkState(player, 2, 0);

			}
			ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
			timemsg.setZoneid(CSYS_ZONEID);
			timemsg.setPlayerCount(1);
			timemsg.setZoneprocess(2);
			timemsg.setSurplustime((Integer) listZone.get(i).getOthers()
					.get("time")
					+ 20 * 60 - (int) (System.currentTimeMillis() / 1000));

			MessageUtil.tell_map_message(map, timemsg);
			MessageUtil.notify_map(map, Notifys.CHAT_SYSTEM, ResManager
					.getInstance().getString("活动正式开始了！ "));
		}

	}

	/**
	 * 获得玩家排行榜 KEY
	 * 
	 * @param player
	 * @return
	 */
	public static String getplayerKey(Player player) {

		return player.getMap() + "_" + player.getId();
	}

	/**
	 * 获得玩家排行榜信息
	 * 
	 * @param player
	 * @return
	 */
	public CsysSMS getplayerTopData(Player player) {
		CsysSMS csysSMS = null;

		Map map = MapManager.getInstance().getMap(player);

		if (csysSMSTopMap.containsKey(map.getZoneId())) {

			if (csysSMSTopMap.get(map.getZoneId()).containsKey(player.getId())) {

				csysSMS = csysSMSTopMap.get(map.getZoneId())
						.get(player.getId());
			} else {
				csysSMS = new CsysSMS(player);
				csysSMSTopMap.get(map.getZoneId()).put(player.getId(), csysSMS);
			}

		} else {

			csysSMS = new CsysSMS(player);

			HashMap<Long, CsysSMS> hashMap = new HashMap<>();
			hashMap.put(player.getId(), csysSMS);

			csysSMSTopMap.put(map.getZoneId(), hashMap);
		}
		return csysSMS;
	}

	List<Integer> monsters;

	// 副本可进入的最大人数
	public static int MAXPLAYERS = 39;

	// 副本可进入的最大人数
	public static int MAXZONE = 10;

	// 副本创建 到副本开始 经历的时间
	public static int REFRESTIME = 60 * 10;

	/**
	 * 提前创建好 需要 进入的 10个副本
	 */
	public void creatZones() {
		if (listZone.size() > 0) {
			MessageUtil.notify_All_player(Notifys.ERROR, "清先关闭 赤色要塞  &csysend");
			return;
		}
		REFRESTIME = DataManager.getInstance().q_globalContainer.getMap()
				.get(214).getQ_int_value();
		MAXZONE = DataManager.getInstance().q_globalContainer.getMap().get(215)
				.getQ_int_value();
		MAXPLAYERS = DataManager.getInstance().q_globalContainer.getMap()
				.get(216).getQ_int_value();

		List<Q_activity_monstersBean> li = DataManager.getInstance().q_activity_monstersContainer
				.getList();

		for (Q_activity_monstersBean bean : li) {
			if (bean.getQ_map() != CSYS_MAPID) {
				continue;
			}
			if (TimeUtil.checkRangeTime(bean.getQ_refreshtime())) {
				monsters = JSON.parseArray(bean.getQ_monsters(), Integer.class);
			}
		}

		int zoneId = 6001;
		Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer
				.getMap().get(zoneId);
		List<Integer> mapidlist = JSON.parseArray(zoneBean.getQ_mapid(),
				Integer.class);
		for (int i = 0; i < MAXZONE; i++) {
			// 创建副本
			HashMap<String, Object> others = new HashMap<String, Object>();
			ZoneContext zone = ManagerPool.zonesManager.setZone("赤色要塞", others,
					mapidlist, zoneId); // 创建副本，返回副本消息

			MapConfig config = zone.getConfigs().get(0);
			Map zoneMap = ManagerPool.mapManager.getMap(config.getServerId(),
					config.getLineId(), config.getMapId());
			zone.getOthers().put("time",
					(int) (System.currentTimeMillis() / 1000));
			zone.getOthers().put("kill", 0);
			zone.getOthers().put("zoneprocess", 1);
			zone.getOthers().put("scriptId", 55012);
			zoneMap.setRound(Math.max(zoneMap.getWidth(), zoneMap.getHeight()) * 2 + 1);
			listZone.add(zone);

			for (int j = 0; j < monsters.size(); j++) {
				reviveMonster(monsters.get(j), zoneMap);
			}

		}
	}

	/**
	 * 创建 副本怪物 通通过复活 机制
	 * 
	 * @param monstaerId
	 * @param map
	 */
	public void reviveMonster(int monstaerId, Map map) {
		// 寻找坐标
		Position position = new Position();
		int rnd = RandomUtils.random(1, ALONE_XY.length) - 1;
		int x = ALONE_XY[rnd][0];
		int y = ALONE_XY[rnd][1];
		position.setX((short) (x * MapUtils.GRID_BORDER));
		position.setY((short) (y * MapUtils.GRID_BORDER));

		List<Grid> gridlist = MapUtils.getRoundNoBlockGrid(position,
				2 * MapUtils.GRID_BORDER, CSYS_MAPID);
		int rnd2 = RandomUtils.random(1, gridlist.size()) - 1;

		Monster gcmonster = ManagerPool.monsterManager.createMonster(
				monstaerId, map.getServerId(), map.getLineId(),
				(int) map.getId(), gridlist.get(rnd2).getCenter());
		gcmonster.setDirection((byte) 2);
		gcmonster.getParameters().put("cmIndex", index);
		gcmonster.setGroupmark(1);
		// Area area = MapManager.getInstance().getArea(gcmonster.getPosition(),
		// map);
		// area.getMonsters().put(gcmonster.getId(), gcmonster);
		Q_csysBean csysBean = DataManager.getInstance().q_csysContainer
				.getMap().get(monstaerId + "");
		if (csysBean == null) {
			log.error("csycBean is null:" + monstaerId);
		}

		gcmonster.setRevive(CsysManger.REFRESTIME
				+ RandomUtils.random(csysBean.getQ_rad_min(),
						csysBean.getQ_rad_max()));
		gcmonster.setState(MonsterState.DIE);
		gcmonster.setDistributeId(1);
		gcmonster.setDistributeType(3);
		// 添加到复活队列
		map.getRevives().put(gcmonster.getId(), gcmonster);
		// 添加到地图中
		map.getMonsters().put(gcmonster.getId(), gcmonster);

	}

	/**
	 * 根据 玩家 获得 已创建的 副本
	 * 
	 * @param player
	 * @return
	 */
	public ZoneContext getZone(Player player) {
		if (playerMap.get(player.getId()) != null) {
			return playerMap.get(player.getId());
		}
		if (listZone.size() == 0) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager
					.getInstance().getString("活动还未开始"));
			return null;
		}
		if (index >= listZone.size()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager
					.getInstance().getString("副本人数已满"));
			return null;
		}
		ZoneContext zone = listZone.get(index);
		// System.out.println("当前副本 玩家数："+((List<Long>)(zone.getOthers().get("playerList"))).size());
		if (zone.getOthers().get("playerList") == null) {
			zone.getOthers().put("playerList", new ArrayList<Long>());
		}
		if (((List<Long>) (zone.getOthers().get("playerList"))).size() >= MAXPLAYERS) {
			index++;
		}
		if (index >= listZone.size()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager
					.getInstance().getString("副本人数已满"));
			return null;
		}
		return listZone.get(index);
	}

	/**
	 * 启动赤色要塞前端秀出 图标
	 */
	public void showCsys() {

		setCstsstate(-1);
		ResEnterActivitieMessage message = new ResEnterActivitieMessage();
		message.setType(-1);
		MessageUtil.tell_world_message(message);
	}

	/**
	 * 启动赤色要塞
	 */
	public void startCsys() {

		setCstsstate(1);
		ResEnterActivitieMessage message = new ResEnterActivitieMessage();
		message.setType(1);
		MessageUtil.tell_world_message(message);
	}

	/**
	 * 发送赤色要塞 状态信息
	 */
	public void sendCsysstauts() {
		ResEnterActivitieMessage message = new ResEnterActivitieMessage();
		message.setType(getCstsstate());
		MessageUtil.tell_world_message(message);
	}

	public void sendCsysstauts(Player player) {
		ResEnterActivitieMessage message = new ResEnterActivitieMessage();
		message.setType(getCstsstate());
		MessageUtil.tell_player_message(player, message);
	}

	public void initMonster() {

	}

	/**
	 * 结束活动
	 */
	public void endCsys() {

		setCstsstate(0);
		// 发放玩家物品奖励

		//

		for (int i = 0; i < listZone.size(); i++) {

			List<CsysTopInfo> li = getCsysEndtopinfo(listZone.get(i).getId(),
					50);
			for (int j = 0; j < li.size(); j++) {
				Long playerid = li.get(j).getPlayerid();
				Player player = PlayerManager.getInstance().getPlayer(playerid);

				if (player != null) {
					Map map = MapManager.getInstance().getMap(player);
					if (map != null) {
						if (MapManager.getInstance().getMap(player)
								.getMapModelid() == CSYS_MAPID) {

							// int star = 5 - j;
							// if (star < 0) {
							// star = 0;
							// }
							// ManagerPool.zonesFlopManager.getZoneReward(player,
							// CSYS_ZONEID, star, 0, (byte) 1);

							if (player.isDie()) {
								PlayerManager.getInstance().revive(player, 2);
							}
							// 切换模式为和平模式
							ManagerPool.playerManager.changePkState(player, 0,
									0);
							givePlayerReward(player);
							ZonesManager.getInstance().outZone(player);
						}

					}
				}
			}
		}
		index = 0;
		listZone.clear();
		playerMap.clear();
		csysSMSTopMap.clear();

		ResEnterActivitieMessage message = new ResEnterActivitieMessage();
		message.setType(0);
		MessageUtil.tell_world_message(message);

	}

	public void sayend() {
		MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM, ResManager
				.getInstance().getString("赤色要塞已经结束! 感谢大家的参与 "));
	}

	public void givePlayerReward(Player player) {
		ResZonePassShowMessage smsg = new ResZonePassShowMessage();
		smsg.setZoneid(CSYS_ZONEID);
		List<ItemInfo> li = smsg.getItemlist();

		Q_characterBean q = DataManager.getInstance().q_characterContainer
				.getMap().get(
						DataManager.getInstance().q_characterContainer.getKey(
								player.getJob(), player.getLevel()));

		CsysSMS csysSMS = getplayerTopData(player);
		int jifen = 0;

		if (csysSMS.getIntegral() >= DataManager.getInstance().q_globalContainer
				.getMap().get(206).getQ_int_value()) {

			jifen = csysSMS.getIntegral();
		}

		// 金币：=remoney*1200+remoney*6*积分/100
		// 经验：=rexp*120+rexp/10*6*积分/100

		int moneyNumbver = q.getQ_remoney() * 1200 + q.getQ_remoney() * 6
				* jifen / 100;
		int expNumber = q.getQ_rexp() * 120 + q.getQ_rexp() / 10 * 6 * jifen
				/ 100;

		ManagerPool.backpackManager.changeMoney(player, moneyNumbver,
				Reasons.RAID_MONEY, Config.getId());
		ManagerPool.playerManager.addExp(player, expNumber,
				AttributeChangeReason.FUBENG);
		ItemInfo info = new ItemInfo();
		info.setItemModelId(-1);
		info.setNum(moneyNumbver);

		li.add(info);

		ItemInfo info2 = new ItemInfo();
		info2.setItemModelId(-4);
		info2.setNum(expNumber);

		li.add(info2);

		MessageUtil.tell_player_message(player, smsg);

	}

	public void monsterKill(Player player) {
		CsysSMS csysSMS = getplayerTopData(player);

		// 重置连斩数
		csysSMS.setKillplayer(0);
		// 死亡次数+1
		csysSMS.setDeath(csysSMS.getDeath() + 1);

		ResPlayerKillMessage cmsg = new ResPlayerKillMessage();
		cmsg.setPlayerId(player.getId());
		cmsg.setCount(csysSMS.getKillplayer());
		MessageUtil.tell_round_message(player, cmsg);

		ResPlayerKillMessage cmsg2 = new ResPlayerKillMessage();
		cmsg2.setPlayerId(player.getId());
		cmsg2.setCount(csysSMS.getKillplayer());
		MessageUtil.tell_player_message(player, cmsg2);

	}

	public void sendPlayerKillCount(Player player) {

		CsysSMS csysSMS = getplayerTopData(player);

		ResPlayerKillMessage cmsg2 = new ResPlayerKillMessage();
		cmsg2.setPlayerId(player.getId());
		cmsg2.setCount(csysSMS.getKillplayer());
		MessageUtil.tell_player_message(player, cmsg2);
	}

	/**
	 * 改变排行榜数据
	 */
	public void changeCsysSMSTopData(Player player, int type, int id,
			Fighter killer) {

		CsysSMS csysSMS = getplayerTopData(player);

		if (type == KILLMONSTER) { // 杀怪
			int integral = csysSMS.getIntegral();
			int addintegral = getIntegralById(id + "");
			csysSMS.setIntegral(integral + addintegral);
			csysSMS.setIntegralTime(System.currentTimeMillis());

			ResChangeIntegralMessage msg = new ResChangeIntegralMessage();
			msg.setChangeType(KILLMONSTER);
			msg.setPlayerIntegral(integral);
			msg.setIntegral(addintegral);
			msg.setAddedIntegral(0);
			MessageUtil.tell_player_message(player, msg);

			sendCsystopinfo(player);
		} else if (type == KILLPLAYER) {// 击杀玩家

			// 计算该玩家 值多少积分
			int addint = DataManager.getInstance().q_csysContainer
					.getIntegralByPlayer(csysSMS.getKillplayer());
			int addint2 = 0;

			if (addint != 0 && csysSMS.getKillplayer() != 0) {
				addint2 = addint;

			}
			addint = DataManager.getInstance().q_csysContainer
					.getIntegralByPlayer(0);
			// 重置连斩数
			csysSMS.setKillplayer(0);
			// 死亡次数+1
			csysSMS.setDeath(csysSMS.getDeath() + 1);

			Player kill = PlayerManager.getInstance().getPlayer(killer.getId());
			CsysSMS killCsysSMS = CsysManger.getInstance().getplayerTopData(
					kill);

			int integral = killCsysSMS.getIntegral();
			// 连斩数+1
			killCsysSMS.setKillplayer(killCsysSMS.getKillplayer() + 1);
			// 杀人数+1
			killCsysSMS.setKill(killCsysSMS.getKill() + 1);
			// 杀人获得积分
			killCsysSMS.setIntegral(integral + addint + addint2);
			killCsysSMS.setIntegralTime(System.currentTimeMillis());

			killMapMessage(killCsysSMS.getKillplayer(), player);

			ResChangeIntegralMessage msg = new ResChangeIntegralMessage();
			msg.setChangeType(KILLPLAYER);
			msg.setPlayerIntegral(integral);
			msg.setIntegral(addint);
			msg.setAddedIntegral(addint2);
			MessageUtil.tell_player_message(kill, msg);

			ResPlayerKillMessage cmsg = new ResPlayerKillMessage();
			cmsg.setPlayerId(kill.getId());
			cmsg.setCount(killCsysSMS.getKillplayer());
			MessageUtil.tell_round_message(kill, cmsg);

			ResPlayerKillMessage cmsg2 = new ResPlayerKillMessage();
			cmsg2.setPlayerId(kill.getId());
			cmsg2.setCount(killCsysSMS.getKillplayer());
			MessageUtil.tell_player_message(kill, cmsg2);

			 sendCsystopinfo(kill);
			sendCsystopinfo(player);
		} else if (type == COLLECT) {// 采集

			int addintegral = getIntegralById(id + "");

			if (addintegral != 0) {

				int integral = csysSMS.getIntegral();

				csysSMS.setCollectCount(csysSMS.getCollectCount() + 1);
				csysSMS.setIntegral(integral + addintegral);
				csysSMS.setIntegralTime(System.currentTimeMillis());

				ResChangeIntegralMessage msg = new ResChangeIntegralMessage();
				msg.setChangeType(COLLECT);
				msg.setPlayerIntegral(integral);
				msg.setIntegral(addintegral);
				msg.setAddedIntegral(0);
				MessageUtil.tell_player_message(player, msg);
				sendCsystopinfo(player);
			} else {
				// 获得BUFF ID
				String parm = DataManager.getInstance().q_csysContainer
						.getMap().get(id + "").getQ_parm();
				int buffid = getBuffId(parm, player);
				if (buffid != 0) {
					ManagerPool.buffManager.addBuff(player, player, buffid, 1,
							0, 0, 0, 0);
				}
			}
		}

	}

	/**
	 * 地图广播 击杀 事件
	 * 
	 * @param kill
	 * @param player
	 */
	public void killMapMessage(int kill, Player player) {
		if (kill < 7 || kill > 14) {
			return;
		}
		switch (kill) {
		case 7:
			MessageUtil.notify_map(
					MapManager.getInstance().getMap(player),
					Notifys.CUTOUT,
					ResManager.getInstance().getString(
							"位于（{1}）的某位玩家已经变态杀戮了，拜托谁去杀了他吧！！"), player
							.getPosition().getX()
							/ 25
							+ ","
							+ player.getPosition().getY() / 25);
			break;
		case 8:
			MessageUtil.notify_map(
					MapManager.getInstance().getMap(player),
					Notifys.CUTOUT,
					ResManager.getInstance().getString(
							"位于（{1}）的某位玩家已经妖怪般了，拜托谁去杀了他吧！！"), player
							.getPosition().getX()
							/ 25
							+ ","
							+ player.getPosition().getY() / 25);
			break;
		case 9:
			MessageUtil.notify_map(
					MapManager.getInstance().getMap(player),
					Notifys.CUTOUT,
					ResManager.getInstance().getString(
							"位于（{1}）的某位玩家已经如同神一般了，拜托谁去杀了他吧！！"), player
							.getPosition().getX()
							/ 25
							+ ","
							+ player.getPosition().getY() / 25);
			break;
		case 10:
			MessageUtil.notify_map(
					MapManager.getInstance().getMap(player),
					Notifys.CUTOUT,
					ResManager.getInstance().getString(
							"位于（{1}）的某位玩家已经超越神的杀戮了，拜托谁去杀了他吧！！"), player
							.getPosition().getX()
							/ 25
							+ ","
							+ player.getPosition().getY() / 25);
			break;
		default:
			break;
		}

	}

	/**
	 * 解析BUFF ID
	 */
	public int getBuffId(String parm, Player player) {
		int buffid = 0;

		String[] bufs = parm.split(",");
		int allrate = 0;
		for (int i = 0; i < bufs.length; i++) {
			int rate = Integer.parseInt(bufs[i].split("_")[0]);
			allrate += rate;
			BuffManager.getInstance().removeByBuffId(player,
					Integer.parseInt(bufs[i].split("_")[1]));
		}
		Random r = new Random();
		int rannum = r.nextInt(allrate + 1);
		int rate = 0;
		buffid = Integer.parseInt(bufs[0].split("_")[1]);
		for (int i = 0; i < bufs.length; i++) {

			rate += Integer.parseInt(bufs[i].split("_")[0]);
			if (rate >= rannum) {
				buffid = Integer.parseInt(bufs[i].split("_")[1]);
				return buffid;
			}
		}

		return buffid;
	}

	/**
	 * 获取排行榜
	 * 
	 * @return
	 * 
	 */
	public List<CsysTopInfo> getCsystopinfo(Player player, int num) {

		Map map = MapManager.getInstance().getMap(player);

		List<CsysTopInfo> infos = new ArrayList<CsysTopInfo>();
		try {
			List<CsysSMS> toplist = new ArrayList<CsysSMS>();
			if (csysSMSTopMap.get(map.getZoneId()) != null) {
				toplist.addAll(csysSMSTopMap.get(map.getZoneId()).values());

				if (toplist.size() > 0) {
					Collections.sort(toplist, new CsysSMSSort());

					if (toplist.size() < num) {
						num = toplist.size();
					}

					for (int i = 0; i < toplist.size(); i++) {

						CsysSMS top = toplist.get(i);
						if (i >= num) {
							if (player == null) {
								break;
							} else if (top.getPlayerid() == player.getId()) {
								CsysTopInfo info = top.getinfo();
								info.setRanking(i + 1);
								infos.add(info);
								break;
							}
						} else {
							CsysTopInfo info = top.getinfo();
							info.setRanking(i + 1);
							infos.add(info);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("赤色要塞排行榜：" + e, e);
		}
		return infos;
	}

	/**
	 * 获取排行榜  去地图 前五  或全部
	 * 
	 * @return
	 * 
	 */
	public List<CsysTopInfo> getCsysEndtopinfo(Long zoneId, int num) {

		List<CsysTopInfo> infos = new ArrayList<CsysTopInfo>();
		try {
			List<CsysSMS> toplist = new ArrayList<CsysSMS>();
			if (csysSMSTopMap.get(zoneId) != null) {
				toplist.addAll(csysSMSTopMap.get(zoneId).values());

				if (toplist.size() > 0) {
					Collections.sort(toplist, new CsysSMSSort());
					if (toplist.size() < num) {

						num = toplist.size();
					}
					for (int i = 0; i < toplist.size(); i++) {

						if(i<num){
							CsysSMS top = toplist.get(i);
	
							CsysTopInfo info = top.getinfo();
							info.setRanking(i + 1);
							infos.add(info);
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("赤色要塞排行榜：" + e, e);
		}
		return infos;
	}

	/**
	 * 地图广播
	 * 
	 * @param map
	 */
	public void sendCsystopinfo(Map map) {
		ResCsysTopListMessage cmsg = new ResCsysTopListMessage();
		List<CsysTopInfo> list = getCsysEndtopinfo(map.getZoneId(), 5);
		cmsg.setInfos(list);
		if (list.size() > 0) {
			cmsg.setInfos(list);
//			log.error("地图list.size():" + list.size());
			MessageUtil.tell_map_message(map, cmsg);
		}
	}

	/**
	 * 玩家广播
	 * 
	 * @param map
	 */
	public void sendCsystopinfo(Player player) {

		ResCsysTopListMessage cmsg = new ResCsysTopListMessage();
		List<CsysTopInfo> list = getCsystopinfo(player, 5);
		// log.error("list.size():"+list.size());
		if (list.size() > 0) {
			cmsg.setInfos(list);
			MessageUtil.tell_map_message(player, cmsg);
		}
	}

	/**
	 * 获得应该获得多少积分
	 * 
	 * @param id
	 * @return
	 */
	public int getIntegralById(String id) {
		int integral = DataManager.getInstance().q_csysContainer.getMap()
				.get(id).getQ_integral();
		return integral;
	}

}

/**
 * 排序列
 * 
 * @author wzh
 * 
 */
class CsysSMSSort implements Comparator<CsysSMS> {
	public int compare(CsysSMS arg0, CsysSMS arg1) {
		
		if (arg0.getIntegral() > arg1.getIntegral()) {
			return -1;
		}
		if (arg0.getIntegral() == arg1.getIntegral()) {

			if (arg0.getIntegralTime() < arg1.getIntegralTime()) {
				return -1;
			}
		}
		return 1;
	}
}
