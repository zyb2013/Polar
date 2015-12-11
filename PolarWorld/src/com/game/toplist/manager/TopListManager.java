package com.game.toplist.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.game.backpack.structs.Attribute;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.data.bean.Q_gem_activationBean;
import com.game.data.bean.Q_titleBean;
import com.game.data.manager.DataManager;
import com.game.db.bean.GameUser;
import com.game.db.bean.Role;
import com.game.db.dao.RoleDao;
import com.game.db.dao.UserDao;
import com.game.equip.bean.EquipAttribute;
import com.game.equip.bean.EquipInfo;
import com.game.gem.bean.GemInfo;
import com.game.gem.bean.PosGemInfo;
import com.game.gem.struts.Gem;
import com.game.guild.bean.GuildInfo;
import com.game.guild.manager.GuildWorldManager;
import com.game.guild.structs.Guild;
import com.game.languageres.manager.ResManager;
import com.game.mail.manager.MailWorldManager;
import com.game.manager.ManagerPool;
import com.game.newactivity.model.SimpleRankInfo;
import com.game.pet.struts.Pet;
import com.game.player.bean.PlayerAttributeItem;
import com.game.player.manager.PlayerManager;
import com.game.player.message.ReqScriptCommonServerToWorldMessage;
import com.game.player.message.ResScriptCommonPlayerWorldToClientMessage;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerWorldInfo;
import com.game.skill.bean.SkillInfo;
import com.game.skill.structs.Skill;
import com.game.structs.Attributes;
import com.game.toplist.bean.TopInfo;
import com.game.toplist.message.ReqGetTopGuildListToWorldMessage;
import com.game.toplist.message.ReqGetTopListToWorldMessage;
import com.game.toplist.message.ReqWorShipToWorldMessage;
import com.game.toplist.message.ReqZoneTopToWorldMessage;
import com.game.toplist.message.ResGetTopAwardToServerMessage;
import com.game.toplist.message.ResGetTopGuildListToGameMessage;
import com.game.toplist.message.ResGetTopListToClientMessage;
import com.game.toplist.message.ResGetTopTitleToServerMessage;
import com.game.toplist.message.ResWorShipToClientMessage;
import com.game.toplist.structs.ArrowTop;
import com.game.toplist.structs.DiTuZoneTop;
import com.game.toplist.structs.EvenCutTop;
import com.game.toplist.structs.FightPowerTop;
import com.game.toplist.structs.GestTop;
import com.game.toplist.structs.HorseTop;
import com.game.toplist.structs.JiaoChangZoneTop;
import com.game.toplist.structs.LevelTop;
import com.game.toplist.structs.LongYuanTop;
import com.game.toplist.structs.PataTop;
import com.game.toplist.structs.PetTop;
import com.game.toplist.structs.RechargeTop;
import com.game.toplist.structs.TopData;
import com.game.toplist.structs.TopPlayer;
import com.game.toplist.structs.ZoneTop;
import com.game.utils.MessageUtil;
import com.game.utils.ServerParamUtil;
import com.game.utils.TimeUtil;
import com.game.utils.VersionUpdateUtil;
import com.game.zones.message.ResZoneTimeRecordNoticeMessage;
import com.game.zones.message.ResfastestClearanceToGameMessage;

/**
 * 排行榜
 *
 * @author  
 */
public class TopListManager {

	public TreeMap<TopData, Long> getGuildTopMap() {
		return guildTopMap;
	}

	public void setGuildTopMap(TreeMap<TopData, Long> guildTopMap) {
		this.guildTopMap = guildTopMap;
	}
	private Logger log = Logger.getLogger(TopListManager.class);
	private static Object obj = new Object();
	//排行榜管理类实例
	private static TopListManager manager;

	private TopListManager() {
	}

	public static TopListManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new TopListManager();
			}
		}
		return manager;
	}
	/**
	 * 进入排行榜人物等级
	 */
	public static int SYNC_PLAYER_LEVEL = 70;
	/**
	 * 进入排行榜坐骑阶数   //测试用暂时改成 0
	 */
	public static int SYNC_HORSE_STAGE = 0;
	/**
	 * 进入排行榜技能层数
	 */
	public static int SYNC_SKILL_LEVEL = 50;
	/**
	 * 进入排行榜连斩最小次数
	 */
	public static int SYNC_EVENT_CUT = 100;
	/**
	 * 进入排行榜龙元心法层数
	 */
	public static int SYNC_LONGYUAN = 4;
	/**
	 * 进入排行榜侍宠等阶
	 */
	public static int SYNC_PET = 4;
	/**
	 * 进入排行榜战斗力数值
	 */
	public static int SYNC_FIGHTPOWER = 10;
	/**
	 * 进入排行榜弓箭等阶
	 */
	public static int SYNC_ARROW = 1;
	/**
	 * 进入充值排行的最低充值数
	 */
	public static int SYNC_RECHARGE = 100;
	/**
	 * 进入爬塔排行的爬塔ID
	 */
	public static int SYNC_PATA = 8011;
	//----------------------------------------------//
	private byte Error_Succes = 0;
	private byte Error_Fail = -1;
	private int MAX_TOP_NUM = 5;	//显示的最大排行数
	//排行类型
	public final static int TOPTYPE_DEFAULT = 0;
	public final static int TOPTYPE_LEVEL = 1;
	public final static int TOPTYPE_HORSE = 2;
	public final static int TOPTYPE_GEST = 3;
	public final static int TOPTYPE_LONGYUAN = 4;
	public final static int TOPTYPE_EVENCUT = 5;
	public final static int TOPTYPE_PET = 6;
	public final static int TOPTYPE_FIGHTPOWER = 7;
	public final static int TOPTYPE_ARROW = 8;
	public final static int TOPTYPE_RECHARGE = 9;
	public final static int TOPTYPE_PATA = 10;
	public final static int TOPTYPE_MAX = 11;
	
	//数据接口
	private int TOP_LOAD_MAXCOUNT = 1000;				//数据库读取每次最大值
	private int TOP_SORT_MAXCOUNT = 1000;				//排行榜排序最大值
	private UserDao userDao = new UserDao();			//玩家帐号

	public UserDao getUserDao() {
		return userDao;
	}
	private RoleDao roleDao = new RoleDao();

	public RoleDao getRoleDao() {
		return roleDao;
	}
	//数据Map
	private static HashMap<Long, TopPlayer> topplayerMap = new HashMap<Long, TopPlayer>();

	public static HashMap<Long, TopPlayer> getTopplayerMap() {
		return topplayerMap;
	}
	//称号列表 1等级 2坐骑 3武功 4龙元 5连斩 6侍宠 7战斗力 8弓箭
	private List<TopData> oldTopDatas = new ArrayList<TopData>();
	private HashMap<Integer, List<TopData>> toptitleMap = new HashMap<Integer, List<TopData>>();
	//排行榜分类 1等级 2坐骑 3武功 4龙元 5连斩 6侍宠 7战斗力 8弓箭
	private TreeMap<TopData, Long> levelTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	private TreeMap<TopData, Long> horseTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	private TreeMap<TopData, Long> gestTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	private TreeMap<TopData, Long> longyuanTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	private TreeMap<TopData, Long> evencutTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	private TreeMap<TopData, Long> petTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	private TreeMap<TopData, Long> fightpowerTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	private TreeMap<TopData, Long> arrowTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	
	//战盟战斗力排名
	private TreeMap<TopData, Long> guildTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	
	//! 充值排行榜
	private TreeMap<TopData, Long> rechargeTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	//! 爬塔排行榜
	private TreeMap<TopData, Long> pataTopMap = new TreeMap<TopData, Long>(new ComparatorTreeMap());
	
	
	
	

	public TreeMap<TopData, Long> getEvencutTopMap() {
		return evencutTopMap;
	}

	public TreeMap<TopData, Long> getGestTopMap() {
		return gestTopMap;
	}

	public TreeMap<TopData, Long> getHorseTopMap() {
		return horseTopMap;
	}

	public TreeMap<TopData, Long> getLevelTopMap() {
		return levelTopMap;
	}

	public TreeMap<TopData, Long> getLongyuanTopMap() {
		return longyuanTopMap;
	}

	public TreeMap<TopData, Long> getPetTopMap() {
		return petTopMap;
	}

	public TreeMap<TopData, Long> getFightpowerTopMap() {
		return fightpowerTopMap;
	}

	public TreeMap<TopData, Long> getArrowTopMap() {
		return arrowTopMap;
	}
	
	public TreeMap<TopData, Long> getRechargeTopMap(){
		return rechargeTopMap;
	}
	
	public TreeMap<TopData, Long> getPataTopMap(){
		return pataTopMap;
	}

	public TreeMap<TopData, Long> getTreeMap(int topType) {
		TreeMap<TopData, Long> getTreeMap = null;
		switch (topType) {
			case TOPTYPE_LEVEL: {
				getTreeMap = levelTopMap;
			}
			break;
			case TOPTYPE_HORSE: {
				getTreeMap = horseTopMap;
			}
			break;
			case TOPTYPE_GEST: {
				getTreeMap = gestTopMap;
			}
			break;
			case TOPTYPE_LONGYUAN: {
				getTreeMap = longyuanTopMap;
			}
			break;
			case TOPTYPE_EVENCUT: {
				getTreeMap = evencutTopMap;
			}
			break;
			case TOPTYPE_PET: {
				getTreeMap = petTopMap;
			}
			break;
			case TOPTYPE_FIGHTPOWER: {
				getTreeMap = fightpowerTopMap;
			}
			break;
			case TOPTYPE_ARROW: {
				getTreeMap = arrowTopMap;
			}
			break;
			case TOPTYPE_RECHARGE:{
				getTreeMap = rechargeTopMap;
			}
			break;
			case TOPTYPE_PATA:{
				getTreeMap = pataTopMap;
			}
			break;
		}
		return getTreeMap;
	}

	static class ComparatorTreeMap implements Comparator<TopData> {

		public ComparatorTreeMap() {
		}

		@Override
		public int compare(TopData o1, TopData o2) {
			return (o1.getId() == o2.getId() ? 0 : o1.compare(o2));
//			int ret = (o1.getId() == o2.getId() ? 0 : o1.compare(o2));
//			System.out.println("o1: " + JSON.toJSONString(o1));
//			System.out.println("o2: " + JSON.toJSONString(o2));
//			System.out.println("比较结果：" + ret);
//			return ret;
		}
	}
	//副本排行
	private int JIAOCHANGZONEID = 3;
	private List<ZoneTop> jiaochangZoneTopList = new ArrayList<ZoneTop>();
	//地图副本排行
	private HashMap<Integer, List<ZoneTop>> DiTuZoneTopmap = new HashMap<Integer, List<ZoneTop>>();

	static class ComparatorList implements Comparator<ZoneTop> {

		public ComparatorList() {
		}

		@Override
		public int compare(ZoneTop o1, ZoneTop o2) {
			return o1.compare(o2);
		}
	}

//	private List<TopData> levelTopList = new ArrayList<TopData>();
//	private List<TopData> horseTopList = new ArrayList<TopData>();
//	private List<TopData> gestTopList = new ArrayList<TopData>();
//	private List<TopData> longyuanTopList = new ArrayList<TopData>();
//	private List<TopData> evencutTopList = new ArrayList<TopData>();
//	public int binarySearch(List<TopData> toplist, TopData topData) {
//		int start = 0;
//		int end = toplist.size() - 1;
//		int mid = (start + end) / 2;
//		TopData midData = toplist.get(mid);
//		int comidx = midData.compare(topData);
//		while (comidx != 0 && end > start) {
//			if (comidx == 1) {
//				end = mid - 1;
//			} else if (comidx == -1) {
//				start = mid + 1;
//			}
//			mid = (start + end) / 2;
//			midData = toplist.get(mid);
//			comidx = midData.compare(topData);
//		}
//		return mid;//返回结果 　　
//	}
	public void getTopPlayerData() {
		try {
			int roleCount = getRoleDao().getcountbylv(SYNC_PLAYER_LEVEL);
			log.error(String.format("ROLE满足条件数据量==%d", roleCount));
			int loadCount = 0;
			int maxloadCount = roleCount % TOP_LOAD_MAXCOUNT == 0 ? roleCount / TOP_LOAD_MAXCOUNT : (roleCount / TOP_LOAD_MAXCOUNT) + 1;
			log.error("ROLE本次一共要解析" + maxloadCount + "次");
			while (loadCount < maxloadCount) {
				int beginidx = loadCount * TOP_LOAD_MAXCOUNT;
				int endidx = TOP_LOAD_MAXCOUNT;
//				if (loadCount == maxloadCount - 1) {
//					endidx = roleCount;
//				}
				List<Role> roleList = getRoleDao().selectbeginandendbylv(SYNC_PLAYER_LEVEL, beginidx, endidx);
				log.error(String.format("ROLE读取成功，第%d次", loadCount + 1));
				log.error(String.format("ROLE读取本次总个数[%d]个", roleList.size()));
				log.error("ROLE本次开始解析");
				int roleJsonNum = 0;
				Iterator<Role> iter = roleList.iterator();
				while (iter.hasNext()) {
					Role role = (Role) iter.next();
					if (role != null) {
						try {
							//TopPlayer topPlayer = (TopPlayer) JSONserializable.toObject(VersionUpdateUtil.dateLoad(role.getData()), TopPlayer.class);
							TopPlayer topPlayer = JSON.parseObject(VersionUpdateUtil.dateLoad(role.getData()), TopPlayer.class);
						
							if (topPlayer != null) {								
								topPlayer.setHorseExp(role.getHorseExp());
								topPlayer.setHorseLevel(role.getHorseLevel());
								getTopplayerMap().put(topPlayer.getId(), topPlayer);
								//修改为1000名排行
								topPlayer.calSkillLinked();
								topPlayer.calEquipLinked();
								topPlayer.calAttrLinked();
								if (topPlayer.getArrowData() != null) {
									topPlayer.setArrowLevel((byte) topPlayer.getArrowData().getArrowlv());
									topPlayer.setStarlv(topPlayer.getArrowData().getStarData().getStarsublv());
									topPlayer.setBowlv(topPlayer.getArrowData().getBowData().getBowmainlv());
								}
								updateTopPlayer(topPlayer, false);
							}
						} catch (Exception e) {
							log.error(e.getMessage());
						}
					}
					roleJsonNum++;
					if (roleJsonNum % 100 == 0) {
						log.error(String.format("ROLE本次已经解析[%d]个，剩余[%d]个。", roleJsonNum, roleList.size() - roleJsonNum));
					}
				}
				log.error(String.format("ROLE本次已经解析[%d]个，剩余[%d]个。", roleJsonNum, roleList.size() - roleJsonNum));
				loadCount++;
			}
			log.error("ROLE全部解析完成");
		} catch (SQLException ex) {
			log.error(ex);
		}
	}

	public void initSortMap() {
		getTopPlayerData();
		log.error("ROLE加载完毕");
//		for (Entry<Long, TopPlayer> entry : topplayerMap.entrySet()) {
//			TopPlayer topPlayer = entry.getValue();
//			if (topPlayer != null) {
//				topPlayer.calSkillLinked();
//				topPlayer.calEquipLinked();
//				topPlayer.calAttrLinked();
//				if (topPlayer.getArrowData() != null) {
//					topPlayer.setArrowLevel((byte) topPlayer.getArrowData().getArrowlv());
//					topPlayer.setStarlv(topPlayer.getArrowData().getStarData().getStarsublv());
//					topPlayer.setBowlv(topPlayer.getArrowData().getBowData().getBowmainlv());
//				}
//				updateTopPlayer(topPlayer, false);
//			}
//		}
		getServerTopTitleList(null, TOPTYPE_DEFAULT);
		log.error("排行榜加载完毕");
//		log.info("等级排行榜size=" + levelTopMap.size());
//		for (Entry<TopData, Long> entry : levelTopMap.entrySet()) {
//			LevelTop levelTop = (LevelTop) entry.getKey();
//			if (levelTop != null) {
//				PlayerWorldInfo playerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(levelTop.getId());
//				if (playerWorldInfo != null) {
//					log.info(String.format("等级排行榜名次：[%d]，ID：[%s]，名字：[%s]，等级：[%d]，升级时间：[%s]", getTopIdx(levelTopMap, levelTop), Long.toString(levelTop.getId()), playerWorldInfo.getName(), levelTop.getLv(), Long.toString(levelTop.getLvtime())));
//				}
//			}
//		}
//		log.info("等级排行榜显示完毕");
	}

	public void initZoneData() {
		String jsonString = ServerParamUtil.getNormalParamMap().get(ServerParamUtil.JIAOCHANGZONETOP);
		if (jsonString != null) {
			List<JiaoChangZoneTop> initList = JSON.parseArray(jsonString, JiaoChangZoneTop.class);
			if (initList != null && !initList.isEmpty()) {
				jiaochangZoneTopList.addAll(initList);
			}
		}
		jsonString = ServerParamUtil.getNormalParamMap().get(ServerParamUtil.DITUZONETOPMAP);
		if (jsonString != null) {
			HashMap<Integer, List<ZoneTop>> initMap = JSON.parseObject(jsonString, HashMap.class);
			if (initMap != null && !initMap.isEmpty()) {
				DiTuZoneTopmap.putAll(initMap);
			}
		}
	}

	public void saveZoneTop(int zoneid) {
//		if (zoneid == JIAOCHANGZONEID) {
			ServerParamUtil.immediateSaveNormal(ServerParamUtil.JIAOCHANGZONETOP, JSON.toJSONString(jiaochangZoneTopList, SerializerFeature.WriteClassName));
//		}
	}

	
	
	public void getDituZoneTop(int zoneid) {
		
		List<ZoneTop> zonetops = DiTuZoneTopmap.get(zoneid);
		ZoneTop topOne = (ZoneTop) zonetops.get(0);
		
	}
	
	public void saveDituZoneTop() {
		ServerParamUtil.immediateSaveNormal(ServerParamUtil.DITUZONETOPMAP, JSON.toJSONString(DiTuZoneTopmap, SerializerFeature.WriteClassName));
		//每次更新  update一下
		syncDiTuZoneTopToGame();
	}

	public void syncDiTuZoneTopToGame() {
		ResfastestClearanceToGameMessage smsg = new ResfastestClearanceToGameMessage();
		smsg.setZonetopjsonstr(JSON.toJSONString(DiTuZoneTopmap, SerializerFeature.WriteClassName));
		MessageUtil.send_to_game(smsg);//群发到所有GAME
	}

	public void clearWorshipNum(Player player) {
		if (player != null) {
			PlayerWorldInfo playerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(player.getId());
			clearWorshipNum(playerWorldInfo);
		}
	}

	public void clearWorshipNum(PlayerWorldInfo playerWorldInfo) {
		if (playerWorldInfo != null) {
			long curday = TimeUtil.GetCurTimeInMin(4);
			playerWorldInfo.getNewRecordworshipidSet();
			if (curday != playerWorldInfo.getLastworshipday()) {
				playerWorldInfo.setWorshipNum(0);
				playerWorldInfo.setLastworshipday((int) curday);
				playerWorldInfo.getRecordworshipid().clear();
				HashMap<String, Object> worshipMap = new HashMap<String, Object>();
				worshipMap.put("LASTWORSHIPDAY", playerWorldInfo.getLastworshipday());
				worshipMap.put("WORSHIPID", playerWorldInfo.getRecordworshipid());
				playerWorldInfo.setRecordworshipiddata(JSON.toJSONString(worshipMap, SerializerFeature.WriteClassName));
				PlayerManager.getInstance().savePlayerWorldInfo(playerWorldInfo);
			}
		}
	}

	public int getTopIdx(TreeMap<TopData, Long> getMap, TopData topData) {
		return getMap.headMap(topData, true).size();
	}

	public boolean checkTopRange(TreeMap<TopData, Long> getMap, TopData topData) {
		if (getMap.size() < TOP_SORT_MAXCOUNT) {
			if (topData.checkAddCondition()) {
				return true;
			} else {
				return false;
			}
		}
		//log.error("开始getMap.size()==" + getMap.size() + "==" + topData.getClass().getName());
		while (getMap.size() > TOP_SORT_MAXCOUNT) {
			Entry<TopData, Long> pollLastEntry = getMap.pollLastEntry();
			if (pollLastEntry != null) {
				TopPlayer topPlayer = getTopplayerMap().get(pollLastEntry.getValue());
				if (topPlayer != null) {
					if (!checkAllTopRange(topPlayer)) {
						getTopplayerMap().remove(topPlayer.getId());
					}
				}
			}
			//log.error("getMap.size()==" + getMap.size() + "==" + topData.getClass().getName());
		}
		//log.error("结束getMap.size()==" + getMap.size() + "==" + topData.getClass().getName());
		if (getMap.containsKey(topData)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkAllTopRange(TopPlayer topPlayer) {
		boolean result = false;
		for (int i = TOPTYPE_LEVEL; i < TOPTYPE_MAX; i++) {
			TreeMap<TopData, Long> treeMap = getTreeMap(i);
			TopData topData = topPlayer.getTopData(i);
			if (treeMap != null && topData != null) {
				if (checkTopRange(treeMap, topData)) {
					result = true;
				}
			}
		}
		return result;
	}

	public void updateTopPlayer(TopPlayer topPlayer, boolean boadd) {
		try {
			if (topPlayer != null) {
				LevelTop levelTop = new LevelTop(topPlayer.getId(), topPlayer.getLevel(), topPlayer.getLevelUpTime());
				if (levelTop.checkAddCondition()) {
					levelTopMap.put(levelTop, levelTop.getId());
					if (boadd) {
						getServerTopTitleList(levelTop, TOPTYPE_LEVEL);
					}
				}
				HorseTop horseTop = new HorseTop(topPlayer.getId(), topPlayer.getHorseLevel(), topPlayer.getHorseLevel(), topPlayer.getCurHorse().getSkilllevelsum(), topPlayer.getCurHorse().getHorseUpTime(), topPlayer.getHorseExp());
				if (horseTop.checkAddCondition()) {
					horseTopMap.put(horseTop, horseTop.getId());
					if (boadd) {
						getServerTopTitleList(horseTop, TOPTYPE_HORSE);
					}
				}
				GestTop gestTop = new GestTop(topPlayer.getId(), topPlayer.getTotalSkillLevel(), topPlayer.getSkillUpTime());
				if (gestTop.checkAddCondition()) {
					gestTopMap.put(gestTop, gestTop.getId());
					if (boadd) {
						getServerTopTitleList(gestTop, TOPTYPE_GEST);
					}
				}
				LongYuanTop longYuanTop = new LongYuanTop(topPlayer.getId(), topPlayer.getLongyuan().getLysection(), topPlayer.getLongyuan().getLylevel(), topPlayer.getLongyuanUpTime());
				if (longYuanTop.checkAddCondition()) {
					longyuanTopMap.put(longYuanTop, longYuanTop.getId());
					if (boadd) {
						getServerTopTitleList(longYuanTop, TOPTYPE_LONGYUAN);
					}
				}
				EvenCutTop evenCutTop = new EvenCutTop(topPlayer.getId(), topPlayer.getMaxEventcut(), topPlayer.getMaxEventcutTime(), topPlayer.getEvencutmapid(), topPlayer.getEvencutmonid(), topPlayer.getEvencutmapx(), topPlayer.getEvencutmapy());
				if (evenCutTop.checkAddCondition()) {
					evencutTopMap.put(evenCutTop, evenCutTop.getId());
					if (boadd) {
						getServerTopTitleList(evenCutTop, TOPTYPE_EVENCUT);
					}
				}
				Pet maxPet = topPlayer.getMaxPet();
				if (maxPet != null) {
					PetTop petTop = new PetTop(topPlayer.getId(), maxPet.getModelId(), maxPet.getHtcount(), maxPet.getLevel());
					if (petTop.checkAddCondition()) {
						topPlayer.setPetorder(petTop.getPetorder());
						topPlayer.setMakelovenum(petTop.getMakelovenum());
						topPlayer.setPetlv(petTop.getPetlv());
						petTopMap.put(petTop, petTop.getId());
						if (boadd) {
							getServerTopTitleList(petTop, TOPTYPE_PET);
						}
					}
				}
				FightPowerTop fightPowerTop = new FightPowerTop(topPlayer.getId(), topPlayer.getFightPower(), topPlayer.getLevel());
				if (fightPowerTop.checkAddCondition()) {
					fightpowerTopMap.put(fightPowerTop, fightPowerTop.getId());
					if (boadd) {
						getServerTopTitleList(fightPowerTop, TOPTYPE_FIGHTPOWER);
					}
				}
				ArrowTop arrowTop = new ArrowTop(topPlayer.getId(), topPlayer.getArrowLevel(), topPlayer.getStarlv(), topPlayer.getBowlv(), topPlayer.getLevel(), topPlayer.getCostGold());
				if (arrowTop.checkAddCondition()) {
					arrowTopMap.put(arrowTop, arrowTop.getId());
					if (boadd) {
						getServerTopTitleList(arrowTop, TOPTYPE_ARROW);
					}
				}
				RechargeTop rechargeTop = new RechargeTop(topPlayer.getId(), topPlayer.getRechargeGold(), topPlayer.getRechargeTime());
				if (rechargeTop.checkAddCondition()){
					rechargeTopMap.put(rechargeTop, rechargeTop.getId());
					if(boadd){
						getServerTopTitleList(rechargeTop, TOPTYPE_RECHARGE);
					}
				}
				PataTop pataTop = new PataTop(topPlayer.getId(), topPlayer.getPataId(), topPlayer.getPataTime());
				if (pataTop.checkAddCondition()){
					pataTopMap.put(pataTop, pataTop.getId());
					if(boadd){
						getServerTopTitleList(pataTop, TOPTYPE_PATA);
					}
				}
				if (boadd) {
					getTopplayerMap().put(topPlayer.getId(), topPlayer);
				}

				if (!checkAllTopRange(topPlayer)) {
					getTopplayerMap().remove(topPlayer.getId());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void updateTopData(TopData topData) {
		if (topData == null) {
			return;
		}
		TopPlayer topPlayer = getTopplayerMap().get(topData.getId());
		if (topPlayer != null) {
			TopData oldTopData = topPlayer.getTopData(topData);
			if (oldTopData != null) {
				if (topData instanceof LevelTop && topData.checkAddCondition()) {
					levelTopMap.remove(oldTopData);
					levelTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_LEVEL);
				} else if (topData instanceof HorseTop && topData.checkAddCondition()) {
					horseTopMap.remove(oldTopData);
					horseTopMap.put(topData, topData.getId());
					topPlayer.setHorseLevel(((HorseTop)topData).getHorsejieshu());
					getServerTopTitleList(topData, TOPTYPE_HORSE);
				} else if (topData instanceof GestTop && topData.checkAddCondition()) {
					gestTopMap.remove(oldTopData);
					gestTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_GEST);
				} else if (topData instanceof LongYuanTop && topData.checkAddCondition()) {
					longyuanTopMap.remove(oldTopData);
					longyuanTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_LONGYUAN);
				} else if (topData instanceof EvenCutTop && topData.checkAddCondition()) {
					evencutTopMap.remove(oldTopData);
					evencutTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_EVENCUT);
				} else if (topData instanceof PetTop && topData.checkAddCondition()) {
					petTopMap.remove(oldTopData);
					petTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_PET);
				} else if (topData instanceof FightPowerTop && topData.checkAddCondition()) {
					fightpowerTopMap.remove(oldTopData);
					fightpowerTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_FIGHTPOWER);
				} else if (topData instanceof ArrowTop && topData.checkAddCondition()) {
					arrowTopMap.remove(oldTopData);
					arrowTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_ARROW);
				} else if (topData instanceof RechargeTop && topData.checkAddCondition()){
					rechargeTopMap.remove(oldTopData);
					rechargeTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_RECHARGE);
				} else if (topData instanceof PataTop && topData.checkAddCondition()){
					pataTopMap.remove(oldTopData);
					pataTopMap.put(topData, topData.getId());
					getServerTopTitleList(topData, TOPTYPE_PATA);
				}
			}
			topPlayer.updateTopData(topData);
			if (!checkAllTopRange(topPlayer)) {
				getTopplayerMap().remove(topPlayer.getId());
			}
		}
	}

	/**
	 * 得到0-9部位宝石信息
	 *
	 * @param player
	 * @return
	 */
	public ArrayList<PosGemInfo> getAllGem(Gem[][] gems) {
		ArrayList<PosGemInfo> posGemInfos = new ArrayList<PosGemInfo>();
		for (int i = 0; i < gems.length; i++) {
			Gem[] gempos = getPosGems(gems, i);	//得到位置上可显示的宝石
			posGemInfos.add(getPosGemInfo(gempos, i));
		}
		return posGemInfos;
	}

	/**
	 * 获得指定部位宝石(根据条件过滤，得到3或者5颗)
	 *
	 * @param player
	 * @param pos
	 * @return
	 */
	public Gem[] getPosGems(Gem[][] gems, int pos) {
		if (pos < 0 && pos >= 10) {
			return null;
		}
		if (gems[pos] != null) {
			Gem tmpgems[] = new Gem[5];
			tmpgems[0] = gems[pos][0];
			tmpgems[1] = gems[pos][1];
			tmpgems[2] = gems[pos][2];
			tmpgems[3] = gems[pos][3];
			tmpgems[4] = gems[pos][4];
			return tmpgems;
		}
		return null;
	}

	/**
	 * 获得部位宝石信息
	 *
	 * @param gems
	 * @param pos
	 * @return
	 */
	public PosGemInfo getPosGemInfo(Gem[] gems, int pos) {
		PosGemInfo posGemInfo = new PosGemInfo();
		posGemInfo.setPos((byte) pos);
		for (int i = 0; i < gems.length; i++) {
			GemInfo gemdata = getGeminfo(gems[i], pos, i);
			if (gemdata != null) {
				posGemInfo.getGeminfo().add(gemdata);
			}
		}
		return posGemInfo;
	}

	/**
	 * 单个宝石信息
	 *
	 * @param gem
	 * @param pos
	 * @param idx
	 * @return
	 */
	public GemInfo getGeminfo(Gem gem, int pos, int idx) {
		if (gem == null) {
			return null;
		}
		pos = pos + 1;
		idx = idx + 1;
		String id = pos + "_" + idx;
		Q_gem_activationBean gemactdata = ManagerPool.dataManager.q_gem_activationContainer.getMap().get(id);
		if (gemactdata != null) {
			GemInfo gemInfo = new GemInfo();
			gemInfo.setGrid(gem.getGrid());
			gemInfo.setId(gem.getId());
			gemInfo.setExp(gem.getExp());
			gemInfo.setIsact(gem.getIsact());
			gemInfo.setLevel((byte) gem.getLevel());
			gemInfo.setType((byte) gemactdata.getQ_gem_type());
			return gemInfo;
		}

		return null;
	}

	private PlayerAttributeItem getPlayerAttributeItem(Attributes type, int value) {
		PlayerAttributeItem item = new PlayerAttributeItem();
		item.setType(type.getValue());
		item.setValue(value);
		return item;
	}

	/**
	 * 获取装备信息
	 *
	 * @param equip 装备
	 * @return
	 */
	public EquipInfo getEquipInfo(Equip equip) {
		EquipInfo info = new EquipInfo();
		info.setItemId(equip.getId());
		info.setItemLevel((byte) equip.getGradeNum());
		info.setItemModelId(equip.getItemModelId());
		info.setItemBind((byte) (equip.isBind() ? 1 : 0));
		info.setItemLosttime(equip.getLosttime());
		info.setFightPower(equip.getFightPower());
		info.setAddAttributLevel((byte) equip.getAddAttributeLevel());
		for (Attribute attribute : equip.getAttributes()) {
			EquipAttribute equipAttribute = new EquipAttribute();
			equipAttribute.setAttributeType((byte) attribute.getType());
			equipAttribute.setAttributeValue(attribute.getValue());
			info.getItemAttributes().add(equipAttribute);
		}
		info.setAttributeCount((byte) equip.getAttributeCount());
		return info;
	}

	/**
	 * 获取技能信息
	 *
	 * @param skill 技能
	 * @return
	 */
	public SkillInfo getSkillInfo(Skill skill) {
		SkillInfo info = new SkillInfo();
		info.setSkillId(skill.getId());
		info.setSkillModelId(skill.getSkillModelId());
		info.setSkillLevel(skill.getSkillLevel());
		return info;
	}

	public TopInfo getTopInfoFromTopPlayer(Player player, TopPlayer topPlayer, int topidx, int toptype) {
		if (topPlayer != null) {
			TopInfo topInfo = new TopInfo();
			topInfo.setExp(topPlayer.getExp());
//			topInfo.setZhenqi(topPlayer.getZhenqi());
//			topInfo.setChapter(topPlayer.getChapter());
			topInfo.setAvatar(topPlayer.getAvatarid());
//			topInfo.setKingcitybuffid(topPlayer.getKingcitybuffid());
			topInfo.setVipid(topPlayer.getVipid());
//			topInfo.setPrestige(topPlayer.getPrestige());
//			topInfo.setCountry(topPlayer.getCountry());
			topInfo.setFightPower(topPlayer.getFightPower());
	
//			topInfo.setAttrfightPower(topPlayer.getAttrfightPower());
//			topInfo.setEquipfightPower(topPlayer.getEquipfightPower());
//			topInfo.setSkillfightPower(topPlayer.getSkillfightPower());
//			topInfo.setArrowInfo(topPlayer.getArrowData().toInfo());
			Guild guild = GuildWorldManager.getInstance().getGuildByUserId(topPlayer.getId());
			if (guild != null) {
				topInfo.setGuildinfo(guild.getGuildInfo());
			} else {
				topInfo.setGuildinfo(new GuildInfo());
			}
			topInfo.setHorselayer(topPlayer.getCurHorse().getLayer());
			
			topInfo.setJob((byte)topPlayer.getJob());
			topInfo.setHorselevel(topPlayer.getHorseLevel());
			/* xuliang hide
			topInfo.setHorselevel(topPlayer.getCurHorse().getLayer());
			*/
//			for (int i = 0; i < topPlayer.getCurHorse().getSkills().size(); i++) {
//				HorseSkill horseSkill = topPlayer.getCurHorse().getSkills().get(i);
//				if (horseSkill != null) {
//					topInfo.getSkillinfolist().add(horseSkill.createSkillInfo());
//				}
//			}
//			for (int i = 0; i < topPlayer.getCurHorse().getHorseequips().length; i++) {
//				HorseEquip horseEquip = topPlayer.getCurHorse().getHorseequips()[i];
//				if (horseEquip != null) {
//					topInfo.getHorseequipinfo().add(getEquipInfo(horseEquip));
//				}
//			}
			for (int i = 0; i < topPlayer.getEquips().length; i++) {
				Equip equip = topPlayer.getEquips()[i];
				
				if (equip != null) {
//					equip.
					topInfo.getItemlist().add(getEquipInfo(equip));
				}else{
					topInfo.getItemlist().add(new EquipInfo());
				}
			}
//			Pet maxPet = topPlayer.getMaxPet();
//			if (maxPet != null) {
//				topInfo.setPetmodelId(maxPet.getModelId());
//				topInfo.setPetlevel(maxPet.getLevel());
//				topInfo.setPethtcount(maxPet.getHtcount());
//				topInfo.setPethtaddhp(maxPet.getHtaddhp());
//				topInfo.setPethtaddmp(maxPet.getHtaddmp());
//				topInfo.setPethtaddattack(maxPet.getHtaddattack());
//				topInfo.setPethtadddefence(maxPet.getHtadddefence());
//				topInfo.setPethtaddcrit(maxPet.getHtaddcrit());
//				topInfo.setPethtadddodge(maxPet.getHtadddodge());
//				for (int i = 0; i < maxPet.getSkills().length; i++) {
//					Skill skill = maxPet.getSkills()[i];
//					if (skill != null) {
//						topInfo.getPetskillinfolist().add(getSkillInfo(skill));
//					}
//				}
//			}
			topInfo.setLevel(topPlayer.getLevel());
//			topInfo.setLysection(topPlayer.getLongyuan().getLysection());
//			topInfo.setLylevel(topPlayer.getLongyuan().getLylevel());
			topInfo.setMapModelId(topPlayer.getMapModelId());
			topInfo.setMaxEventcut(topPlayer.getMaxEventcut());
//			topInfo.setMaxEventcutTime((int) (topPlayer.getMaxEventcutTime() / 1000));
//			topInfo.setEvencutmapid(topPlayer.getEvencutmapid());
//			topInfo.setEvencutmonid(topPlayer.getEvencutmonid());
//			topInfo.setEvencutmapx(topPlayer.getEvencutmapx());
//			topInfo.setEvencutmapy(topPlayer.getEvencutmapy());
			topInfo.setMoney(topPlayer.getMoney());
			topInfo.setPlayerid(topPlayer.getId());
			topInfo.setPlayername(topPlayer.getName());
//			topInfo.setPosallgeminfo(getAllGem(topPlayer.getGems()));
			topInfo.setSex(topPlayer.getSex());
			topInfo.setTopidx(topidx);
			topInfo.setToptype((byte) toptype);
//			topInfo.setTotalSkillLevel(topPlayer.getTotalSkillLevel());
//			topInfo.setMoziSkillLevel(topPlayer.getMoziSkillLevel());
//			topInfo.setMozibackSkillLevel(topPlayer.getMozibackSkillLevel());
//			topInfo.setLongyuanSkillLevel(topPlayer.getLongyuanSkillLevel());
//			topInfo.setLongyuanbackSkillLevel(topPlayer.getLongyuanbackSkillLevel());
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.HP, topPlayer.getMaxHp()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.MP, topPlayer.getMaxMp()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.SP, topPlayer.getMaxSp()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.MAXHP, topPlayer.getMaxHp()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.MAXMP, topPlayer.getMaxMp()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.MAXSP, topPlayer.getMaxSp()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.ATTACK, topPlayer.getAttack()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.DEFENSE, topPlayer.getDefense()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.DODGE, topPlayer.getDodge()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.CRIT, topPlayer.getCrit()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.LUCK, topPlayer.getLuck()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.ATTACKSPEED, topPlayer.getAttackSpeed()));
//			topInfo.getAttributes().add(getPlayerAttributeItem(Attributes.SPEED, topPlayer.getSpeed()));
			PlayerWorldInfo playerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(player.getId());
			if (playerWorldInfo != null) {
				playerWorldInfo.getNewRecordworshipidSet();
//				topInfo.setWorship(playerWorldInfo.getRecordworshipid().contains(topPlayer.getId()) ? (byte) 1 : (byte) 0);
//				topInfo.setWorshipnum((byte) playerWorldInfo.getWorshipNum());
			}
			PlayerWorldInfo destPlayerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(topPlayer.getId());
			if (destPlayerWorldInfo != null) {
				topInfo.setWebvip(destPlayerWorldInfo.getWebvip());
//				topInfo.setAllworshipnum(destPlayerWorldInfo.getAllworshipNum());
			}
			return topInfo;
		}
		return null;
	}

	public TopInfo getTopInfoFromTopData(Player player, TopData topData, int topidx, int toptype) {
		if (topData != null) {
			TopPlayer topPlayer = getTopplayerMap().get(topData.getId());
			if (topPlayer != null) {
				return getTopInfoFromTopPlayer(player, topPlayer, topidx, toptype);
			}
		}
		return null;
	}
	
	//工会战斗力特殊处理  
	public void getTop5GuildInfo(List<GuildInfo> topInfos) {
		
		
		Object[] toArray = guildTopMap.keySet().toArray();
		
		for (int i = 0; i < toArray.length && i < 4; i++) {
			TopData topData = (TopData) toArray[i];
			Guild g = GuildWorldManager.getInstance().getGuild(topData.getId());
			if(g!=null){
				topInfos.add(g.getGuildInfo());
			}
		}
	}
	

	public void getTop5Info(Player player, int toptype, List<TopInfo> topInfos) {
		TreeMap<TopData, Long> getTreeMap = getTreeMap(toptype);
		if (getTreeMap != null) {
			Object[] toArray = getTreeMap.keySet().toArray();
			for (int i = 0; i < toArray.length && i < MAX_TOP_NUM; i++) {
				TopData topData = (TopData) toArray[i];
				TopInfo topInfo = getTopInfoFromTopData(player, topData, i + 1, toptype);
				if (topInfo != null) {
					topInfos.add(topInfo);
				}
			}
		}
//		switch (toptype) {
//			case TOPTYPE_LEVEL: {
//				for (int i = 0; i < levelTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//					TopData topData = (TopData) levelTopMap.keySet().toArray()[i];
//					TopInfo topInfo = getTopInfoFromTopData(player, topData, i + 1, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				}
//			}
//			break;
//			case TOPTYPE_HORSE: {
//				for (int i = 0; i < horseTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//					TopData topData = (TopData) horseTopMap.keySet().toArray()[i];
//					TopInfo topInfo = getTopInfoFromTopData(player, topData, i + 1, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				}
//			}
//			break;
//			case TOPTYPE_GEST: {
//				for (int i = 0; i < gestTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//					TopData topData = (TopData) gestTopMap.keySet().toArray()[i];
//					TopInfo topInfo = getTopInfoFromTopData(player, topData, i + 1, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				}
//			}
//			break;
//			case TOPTYPE_LONGYUAN: {
//				for (int i = 0; i < longyuanTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//					TopData topData = (TopData) longyuanTopMap.keySet().toArray()[i];
//					TopInfo topInfo = getTopInfoFromTopData(player, topData, i + 1, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				}
//			}
//			break;
//			case TOPTYPE_EVENCUT: {
//				for (int i = 0; i < evencutTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//					TopData topData = (TopData) evencutTopMap.keySet().toArray()[i];
//					TopInfo topInfo = getTopInfoFromTopData(player, topData, i + 1, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				}
//			}
//			break;
//		}
	}

	public void getTopSelfInfo(Player player, int toptype, List<TopInfo> topInfos) {
		TreeMap<TopData, Long> getTreeMap = getTreeMap(toptype);
		if (getTreeMap != null) {
			TopPlayer topPlayer = getTopplayerMap().get(player.getId());
			if (topPlayer == null) {
				int topidx = getTreeMap.size();
				int getnum = 1;
				Object[] array = getTreeMap.keySet().toArray();
				for (int i = 0; i < array.length && i < MAX_TOP_NUM; i++) {
					TopData topData = (TopData) array[topidx - getnum];
					TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
					if (topInfo != null) {
						topInfos.add(0, topInfo);
					}
					getnum++;
				}
				return;
			}
			TopData oldTopData = topPlayer.getTopData(toptype);
			if (oldTopData == null) {
				return;
			}
			int topidx = 0;
			if (getTreeMap.containsKey(oldTopData)) {
				topidx = getTopIdx(getTreeMap, oldTopData);
				TopInfo topInfo = getTopInfoFromTopData(player, oldTopData, topidx, toptype);
				if (topInfo != null) {
					topInfos.add(topInfo);
				}
			} else {
				topidx = getTreeMap.size();
				int getnum = 1;
				Object[] array = getTreeMap.keySet().toArray();
				for (int i = 0; i < array.length && i < MAX_TOP_NUM; i++) {
					TopData topData = (TopData) array[topidx - getnum];
					TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
					if (topInfo != null) {
						topInfos.add(0, topInfo);
					}
					getnum++;
				}
				return;
			}
			SortedMap tailSortedMap = getTreeMap.tailMap(oldTopData, false);//该元素后面的（不包括自己）
			SortedMap headSortedMap = getTreeMap.headMap(oldTopData, false);//该元素前面的（不包括自己）
			int tailnum = MAX_TOP_NUM - 1;
			int tailidx = 0;
			tailnum = Math.min(tailnum, tailSortedMap.size());
			tailidx = Math.min(tailidx, tailnum);
			int headnum = MAX_TOP_NUM - 1;
			int headidx = headSortedMap.size() - 1;
			headnum = Math.min(headnum, headSortedMap.size());
			headidx = Math.max(headidx, 0);
			int topjia = 1;
			int topjian = 1;
			Object[] tailArray = tailSortedMap.keySet().toArray();
			Object[] headArray = headSortedMap.keySet().toArray();
			while (tailnum > 0 || headnum > 0) {
				if (tailnum > 0) {
					TopData topData = (TopData) tailArray[tailidx];
					TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx + topjia, toptype);
					if (topInfo != null) {
						topInfos.add(topInfo);
						tailnum--;
						tailidx++;
						topjia++;
					}
				}
				if (headnum > 0) {
					TopData topData = (TopData) headArray[headidx];
					TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - topjian, toptype);
					if (topInfo != null) {
						topInfos.add(0, topInfo);
						headnum--;
						headidx--;
						topjian++;
					}
				}
				if (topInfos.size() >= MAX_TOP_NUM) {
					break;
				}
			}
		}
//		switch (toptype) {
//			case TOPTYPE_LEVEL: {
//				TopPlayer topPlayer = getTopplayerMap().get(player.getId());
//				if (topPlayer == null) {
//					int topidx = levelTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < levelTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) levelTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				LevelTop levelTop = new LevelTop(topPlayer.getId(), topPlayer.getLevel(), topPlayer.getLevelUpTime());
//				int topidx = 0;
//				if (levelTopMap.containsKey(levelTop)) {
//					topidx = getTopIdx(levelTopMap, levelTop);
//					TopInfo topInfo = getTopInfoFromTopData(player, levelTop, topidx, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				} else {
//					topidx = levelTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < levelTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) levelTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				SortedMap tailSortedMap = levelTopMap.tailMap(levelTop, false);//该元素后面的（不包括自己）
//				SortedMap headSortedMap = levelTopMap.headMap(levelTop, false);//该元素前面的（不包括自己）
//				int tailnum = MAX_TOP_NUM - 1;
//				int tailidx = 0;
//				tailnum = Math.min(tailnum, tailSortedMap.size());
//				tailidx = Math.min(tailidx, tailnum);
//				int headnum = MAX_TOP_NUM - 1;
//				int headidx = headSortedMap.size() - 1;
//				headnum = Math.min(headnum, headSortedMap.size());
//				headidx = Math.max(headidx, 0);
//				int topjia = 1;
//				int topjian = 1;
//				while (tailnum > 0 || headnum > 0) {
//					if (tailnum > 0) {
//						TopData topData = (TopData) tailSortedMap.keySet().toArray()[tailidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx + topjia, toptype);
//						if (topInfo != null) {
//							topInfos.add(topInfo);
//							tailnum--;
//							tailidx++;
//							topjia++;
//						}
//					}
//					if (headnum > 0) {
//						TopData topData = (TopData) headSortedMap.keySet().toArray()[headidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - topjian, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//							headnum--;
//							headidx--;
//							topjian++;
//						}
//					}
//					if (topInfos.size() >= MAX_TOP_NUM) {
//						break;
//					}
//				}
//			}
//			break;
//			case TOPTYPE_HORSE: {
//				TopPlayer topPlayer = getTopplayerMap().get(player.getId());
//				if (topPlayer == null) {
//					int topidx = horseTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < horseTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) horseTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				HorseTop horseTop = new HorseTop(topPlayer.getId(), topPlayer.getCurHorse().getLayer(), topPlayer.getCurHorse().getHorselevel(), topPlayer.getCurHorse().getSkilllevelsum(), topPlayer.getCurHorse().getHorseUpTime());
//				int topidx = 0;
//				if (horseTopMap.containsKey(horseTop)) {
//					topidx = getTopIdx(horseTopMap, horseTop);
//					TopInfo topInfo = getTopInfoFromTopData(player, horseTop, topidx, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				} else {
//					topidx = horseTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < horseTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) horseTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				SortedMap tailSortedMap = horseTopMap.tailMap(horseTop, false);//该元素后面的（不包括自己）
//				SortedMap headSortedMap = horseTopMap.headMap(horseTop, false);//该元素前面的（不包括自己）
//				int tailnum = MAX_TOP_NUM - 1;
//				int tailidx = 0;
//				tailnum = Math.min(tailnum, tailSortedMap.size());
//				tailidx = Math.min(tailidx, tailnum);
//				int headnum = MAX_TOP_NUM - 1;
//				int headidx = headSortedMap.size() - 1;
//				headnum = Math.min(headnum, headSortedMap.size());
//				headidx = Math.max(headidx, 0);
//				int topjia = 1;
//				int topjian = 1;
//				while (tailnum > 0 || headnum > 0) {
//					if (tailnum > 0) {
//						TopData topData = (TopData) tailSortedMap.keySet().toArray()[tailidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx + topjia, toptype);
//						if (topInfo != null) {
//							topInfos.add(topInfo);
//							tailnum--;
//							tailidx++;
//							topjia++;
//						}
//					}
//					if (headnum > 0) {
//						TopData topData = (TopData) headSortedMap.keySet().toArray()[headidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - topjian, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//							headnum--;
//							headidx--;
//							topjian++;
//						}
//					}
//					if (topInfos.size() >= MAX_TOP_NUM) {
//						break;
//					}
//				}
//			}
//			break;
//			case TOPTYPE_GEST: {
//				TopPlayer topPlayer = getTopplayerMap().get(player.getId());
//				if (topPlayer == null) {
//					int topidx = gestTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < gestTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) gestTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				GestTop gestTop = new GestTop(topPlayer.getId(), topPlayer.getTotalSkillLevel(), topPlayer.getSkillUpTime());
//				int topidx = 0;
//				if (gestTopMap.containsKey(gestTop)) {
//					topidx = getTopIdx(gestTopMap, gestTop);
//					TopInfo topInfo = getTopInfoFromTopData(player, gestTop, topidx, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				} else {
//					topidx = gestTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < gestTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) gestTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				SortedMap tailSortedMap = gestTopMap.tailMap(gestTop, false);//该元素后面的（不包括自己）
//				SortedMap headSortedMap = gestTopMap.headMap(gestTop, false);//该元素前面的（不包括自己）
//				int tailnum = MAX_TOP_NUM - 1;
//				int tailidx = 0;
//				tailnum = Math.min(tailnum, tailSortedMap.size());
//				tailidx = Math.min(tailidx, tailnum);
//				int headnum = MAX_TOP_NUM - 1;
//				int headidx = headSortedMap.size() - 1;
//				headnum = Math.min(headnum, headSortedMap.size());
//				headidx = Math.max(headidx, 0);
//				int topjia = 1;
//				int topjian = 1;
//				while (tailnum > 0 || headnum > 0) {
//					if (tailnum > 0) {
//						TopData topData = (TopData) tailSortedMap.keySet().toArray()[tailidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx + topjia, toptype);
//						if (topInfo != null) {
//							topInfos.add(topInfo);
//							tailnum--;
//							tailidx++;
//							topjia++;
//						}
//					}
//					if (headnum > 0) {
//						TopData topData = (TopData) headSortedMap.keySet().toArray()[headidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - topjian, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//							headnum--;
//							headidx--;
//							topjian++;
//						}
//					}
//					if (topInfos.size() >= MAX_TOP_NUM) {
//						break;
//					}
//				}
//			}
//			break;
//			case TOPTYPE_LONGYUAN: {
//				TopPlayer topPlayer = getTopplayerMap().get(player.getId());
//				if (topPlayer == null) {
//					int topidx = longyuanTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < longyuanTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) longyuanTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				LongYuanTop longYuanTop = new LongYuanTop(topPlayer.getId(), topPlayer.getLongyuan().getLysection(), topPlayer.getLongyuan().getLylevel(), topPlayer.getLongyuanUpTime());
//				int topidx = 0;
//				if (longyuanTopMap.containsKey(longYuanTop)) {
//					topidx = getTopIdx(longyuanTopMap, longYuanTop);
//					TopInfo topInfo = getTopInfoFromTopData(player, longYuanTop, topidx, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				} else {
//					topidx = longyuanTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < longyuanTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) longyuanTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				SortedMap tailSortedMap = longyuanTopMap.tailMap(longYuanTop, false);//该元素后面的（不包括自己）
//				SortedMap headSortedMap = longyuanTopMap.headMap(longYuanTop, false);//该元素前面的（不包括自己）
//				int tailnum = MAX_TOP_NUM - 1;
//				int tailidx = 0;
//				tailnum = Math.min(tailnum, tailSortedMap.size());
//				tailidx = Math.min(tailidx, tailnum);
//				int headnum = MAX_TOP_NUM - 1;
//				int headidx = headSortedMap.size() - 1;
//				headnum = Math.min(headnum, headSortedMap.size());
//				headidx = Math.max(headidx, 0);
//				int topjia = 1;
//				int topjian = 1;
//				while (tailnum > 0 || headnum > 0) {
//					if (tailnum > 0) {
//						TopData topData = (TopData) tailSortedMap.keySet().toArray()[tailidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx + topjia, toptype);
//						if (topInfo != null) {
//							topInfos.add(topInfo);
//							tailnum--;
//							tailidx++;
//							topjia++;
//						}
//					}
//					if (headnum > 0) {
//						TopData topData = (TopData) headSortedMap.keySet().toArray()[headidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - topjian, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//							headnum--;
//							headidx--;
//							topjian++;
//						}
//					}
//					if (topInfos.size() >= MAX_TOP_NUM) {
//						break;
//					}
//				}
//			}
//			break;
//			case TOPTYPE_EVENCUT: {
//				TopPlayer topPlayer = getTopplayerMap().get(player.getId());
//				if (topPlayer == null) {
//					int topidx = evencutTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < evencutTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) evencutTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				EvenCutTop evenCutTop = new EvenCutTop(topPlayer.getId(), topPlayer.getMaxEventcut(), topPlayer.getMaxEventcutTime());
//				int topidx = 0;
//				if (evencutTopMap.containsKey(evenCutTop)) {
//					topidx = getTopIdx(evencutTopMap, evenCutTop);
//					TopInfo topInfo = getTopInfoFromTopData(player, evenCutTop, topidx, toptype);
//					if (topInfo != null) {
//						topInfos.add(topInfo);
//					}
//				} else {
//					topidx = evencutTopMap.size();
//					int getnum = 1;
//					for (int i = 0; i < evencutTopMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
//						TopData topData = (TopData) evencutTopMap.keySet().toArray()[topidx - getnum];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - i, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//						}
//						getnum++;
//					}
//					return;
//				}
//				SortedMap tailSortedMap = evencutTopMap.tailMap(evenCutTop, false);//该元素后面的（不包括自己）
//				SortedMap headSortedMap = evencutTopMap.headMap(evenCutTop, false);//该元素前面的（不包括自己）
//				int tailnum = MAX_TOP_NUM - 1;
//				int tailidx = 0;
//				tailnum = Math.min(tailnum, tailSortedMap.size());
//				tailidx = Math.min(tailidx, tailnum);
//				int headnum = MAX_TOP_NUM - 1;
//				int headidx = headSortedMap.size() - 1;
//				headnum = Math.min(headnum, headSortedMap.size());
//				headidx = Math.max(headidx, 0);
//				int topjia = 1;
//				int topjian = 1;
//				while (tailnum > 0 || headnum > 0) {
//					if (tailnum > 0) {
//						TopData topData = (TopData) tailSortedMap.keySet().toArray()[tailidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx + topjia, toptype);
//						if (topInfo != null) {
//							topInfos.add(topInfo);
//							tailnum--;
//							tailidx++;
//							topjia++;
//						}
//					}
//					if (headnum > 0) {
//						TopData topData = (TopData) headSortedMap.keySet().toArray()[headidx];
//						TopInfo topInfo = getTopInfoFromTopData(player, topData, topidx - topjian, toptype);
//						if (topInfo != null) {
//							topInfos.add(0, topInfo);
//							headnum--;
//							headidx--;
//							topjian++;
//						}
//					}
//					if (topInfos.size() >= MAX_TOP_NUM) {
//						break;
//					}
//				}
//			}
//			break;
//		}
	}

	public void getPlayerTopTitleList(Player player) {
		if (player != null) {
			ResGetTopTitleToServerMessage sendMessage = new ResGetTopTitleToServerMessage();
			for (Entry<Integer, List<TopData>> entry : toptitleMap.entrySet()) {
				Integer titleKey = entry.getKey();
				List<TopData> list = entry.getValue();
				if (list != null && !list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						TopData titleTopData = list.get(i);
						if (titleTopData != null && titleTopData.getId() == player.getId()) {
							Q_titleBean q_titleBean = DataManager.getInstance().q_titleContainer.getMap().get(titleKey + "_" + (i + 1));
							if (q_titleBean != null) {
								sendMessage.getTitleidlist().add(q_titleBean.getQ_titleid());
							}
						}
					}
				}
			}
			if(sendMessage.getTitleidlist().size()>0){
				MessageUtil.send_to_game(player, sendMessage);
			}
		}
	}

	public void getPlayerTopTitleList(long playerid) {
		Player player = PlayerManager.getInstance().getPlayer(playerid);
		if (player != null) {
			getPlayerTopTitleList(player);
		}
	}

	public void getServerTopTitleList(TopData topData, int topType) {
		TreeMap<TopData, Long> getTreeMap = getTreeMap(topType);
		switch (topType) {
			case TOPTYPE_LEVEL:
			case TOPTYPE_HORSE:
			case TOPTYPE_GEST:
			case TOPTYPE_LONGYUAN:
			case TOPTYPE_EVENCUT:
			case TOPTYPE_PET:
			case TOPTYPE_FIGHTPOWER: {
				if (getTreeMap == null) {
					return;
				}
				if (topData == null) {
					List<TopData> topDatas = new ArrayList<TopData>();
					for (int i = 0; i < getTreeMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
						TopData titleTopData = (TopData) getTreeMap.keySet().toArray()[i];
						topDatas.add(titleTopData);
					}
					toptitleMap.put(topType, topDatas);
				} else {
					List<TopData> topDatas = toptitleMap.get(topType);
					if (topDatas != null) {
						oldTopDatas.clear();
						oldTopDatas.addAll(topDatas);
						if (topDatas.isEmpty() || topDatas.size() < MAX_TOP_NUM) {
							topDatas.clear();
							for (int i = 0; i < getTreeMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
								TopData titleTopData = (TopData) getTreeMap.keySet().toArray()[i];
								topDatas.add(titleTopData);
							}
							if (topDatas.contains(topData)) {
								for (int i = 0; i < topDatas.size(); i++) {
									TopData newTopData = topDatas.get(i);
									if (i < oldTopDatas.size()) {
										TopData oldTopData = oldTopDatas.get(i);
										if (newTopData.getId() != oldTopData.getId()) {
											getPlayerTopTitleList(newTopData.getId());
										}
									} else {
										getPlayerTopTitleList(newTopData.getId());
									}
								}
								//getPlayerTopTitleList(topData.getId());
							}
						} else {
//							if (topDatas.contains(topData)) {
//								return;
//							}
							TopData lastTopdata = topDatas.get(topDatas.size() - 1);
							topDatas.clear();
							for (int i = 0; i < getTreeMap.keySet().toArray().length && i < MAX_TOP_NUM; i++) {
								TopData titleTopData = (TopData) getTreeMap.keySet().toArray()[i];
								topDatas.add(titleTopData);
							}
							if (topDatas.contains(topData)) {
								for (int i = 0; i < topDatas.size(); i++) {
									TopData newTopData = topDatas.get(i);
									if (i < oldTopDatas.size()) {
										TopData oldTopData = oldTopDatas.get(i);
										if (newTopData.getId() != oldTopData.getId()) {
											getPlayerTopTitleList(newTopData.getId());
										}
									} else {
										getPlayerTopTitleList(newTopData.getId());
									}
								}
								//getPlayerTopTitleList(topData.getId());
								getPlayerTopTitleList(lastTopdata.getId());
							}
						}
					}
				}
			}
			break;
			default: {
				if (toptitleMap.isEmpty()) {
					for (int i = TOPTYPE_LEVEL; i < TOPTYPE_MAX; i++) {
						getServerTopTitleList(null, i);
					}
				} else {
					for (Entry<Integer, List<TopData>> entry : toptitleMap.entrySet()) {
						Integer topKey = entry.getKey();
						getServerTopTitleList(null, topKey);
					}
				}
			}
			break;
		}
	}

	/**
	 * 测试区排行榜发送奖励(删档测试区数据，第一个区用)
	 *
	 * @param player 玩家
	 * @return
	 */
	public void testZoneSendMailTop5(Player player) {
		try {
			if (player == null) {
				return;
			}
			String jsonString = ServerParamUtil.getImportantParamMap().get(ServerParamUtil.TESTZONETOPLIST);
			if (jsonString != null && !jsonString.isEmpty()) {
				GameUser user = userDao.selectGameUser(Long.parseLong(player.getUserId()), player.getServer());
				if (user == null) {
					log.error(player.getId() + ":USERNAME空");
					return;
				}
				HashMap<Integer, List<String>> toplistmap = JSON.parseObject(jsonString, HashMap.class);
				if (toplistmap != null && !toplistmap.isEmpty()) {
					boolean bosave = false;
					for (Entry<Integer, List<String>> entry : toplistmap.entrySet()) {
						Integer key = entry.getKey();
						List<String> list = entry.getValue();
						if (list != null && !list.isEmpty()) {
							for (int i = 0; i < list.size(); i++) {
								String account = list.get(i);
								if (account != null && account.equalsIgnoreCase(user.getUsername())) {
									Item item = Item.createBindGold(15000);
									List<Item> items = new ArrayList<Item>();
									items.add(item);
									if (MailWorldManager.getInstance().sendSystemMail(player.getName(), ResManager.getInstance().getString("系统邮件"), ResManager.getInstance().getString("恭喜您获得删档测试排行榜前5奖励。"), (byte) 0, 0, items) != -1) {
										list.set(i, "0");
										bosave = true;
									}
								}
							}
						}
					}
					if (bosave) {
						ServerParamUtil.immediateSaveImportant(ServerParamUtil.TESTZONETOPLIST, JSON.toJSONString(toplistmap, SerializerFeature.WriteClassName));
					}
				}
			}
		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}
	}

	//------------------------消息处理---------------------------//
	public void reqGetTopListToWorld(ReqGetTopListToWorldMessage message) {
		
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerid());
		if (player != null) {
			ResGetTopListToClientMessage sendMessage = new ResGetTopListToClientMessage();
			sendMessage.setErrorcode(Error_Succes);
			PlayerWorldInfo playerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(player.getId());
			if (playerWorldInfo != null) {
				clearWorshipNum(playerWorldInfo);
				sendMessage.setWorshipnum((byte) playerWorldInfo.getWorshipNum());
			}
			sendMessage.setToptype(message.getToptype());
			getTop5Info(player, message.getToptype(), sendMessage.getTop5infolist());
			getTopSelfInfo(player, message.getToptype(), sendMessage.getTopselfinfolist());
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerid())));
		}
	}
	
	public void reqGetTopGuildListToWorld(ReqGetTopGuildListToWorldMessage message) {
//		ResGetTopGuildListToGameMessage msg = new ResGetTopGuildListToGameMessage();
//		getTop5GuildInfo(msg.getTop5infolist());
//		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerid());
//		MessageUtil.send_to_game(player, msg);
		syncGuildTopToGame();
	}
	

	public void reqWorShipToWorld(ReqWorShipToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerid());
		if (player != null) {
			PlayerWorldInfo playerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(player.getId());
			PlayerWorldInfo destPlayerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(message.getWorshipplayerid());
			if (playerWorldInfo != null && destPlayerWorldInfo != null) {
				ResWorShipToClientMessage sendMessage = new ResWorShipToClientMessage();
				sendMessage.setErrorcode(Error_Fail);
				if (playerWorldInfo.getWorshipNum() < 10) {
					playerWorldInfo.getNewRecordworshipidSet();
					if (playerWorldInfo.getRecordworshipid().contains(message.getWorshipplayerid())) {
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.ERROR, "您今天已经崇拜过了这个玩家！");
						*/
					} else {
						destPlayerWorldInfo.setAllworshipNum(destPlayerWorldInfo.getAllworshipNum() + 1);
						playerWorldInfo.getRecordworshipid().add(message.getWorshipplayerid());
						HashMap<String, Object> worshipMap = new HashMap<String, Object>();
						worshipMap.put("LASTWORSHIPDAY", playerWorldInfo.getLastworshipday());
						worshipMap.put("WORSHIPID", playerWorldInfo.getRecordworshipid());
						playerWorldInfo.setRecordworshipiddata(JSON.toJSONString(worshipMap, SerializerFeature.WriteClassName));
						playerWorldInfo.setWorshipNum(playerWorldInfo.getWorshipNum() + 1);
						PlayerManager.getInstance().savePlayerWorldInfo(playerWorldInfo);
						PlayerManager.getInstance().savePlayerWorldInfo(destPlayerWorldInfo);
						sendMessage.setWorshipnum((byte) playerWorldInfo.getWorshipNum());
						sendMessage.setWorshipplayerid(destPlayerWorldInfo.getId());
						sendMessage.setAllworshipnum(destPlayerWorldInfo.getAllworshipNum());
						sendMessage.setErrorcode(Error_Succes);
						ResGetTopAwardToServerMessage topAwardToServerMessage = new ResGetTopAwardToServerMessage();
						topAwardToServerMessage.setExp(player.getLevel() * 5 + 1000);
						topAwardToServerMessage.setMoney(player.getLevel() * 5 + 1000);
						MessageUtil.send_to_game(player, topAwardToServerMessage);
					}
				} else {
					sendMessage.setWorshipnum((byte) 10);
					/*xiaozhuoming: 暂时没有用到
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您今天的崇拜次数已经达到上限10次"));
					*/
				}

				MessageUtil.tell_player_message(player, sendMessage);
			}
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerid())));
		}
	}

	public void reqZoneTopToWorld(ReqZoneTopToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerid());
		if (player != null) {
			if (message.getZonetype() == 2) {
				ZoneTop zoneTop = JSON.parseObject(message.getZonetopjsonstr(), ZoneTop.class);
				if (zoneTop != null) {
					switch (zoneTop.getZoneid()) {
						case 3: {
							JiaoChangZoneTop jiaoChangZoneTop = JSON.parseObject(message.getZonetopjsonstr(), JiaoChangZoneTop.class);
							if (jiaoChangZoneTop != null) {
								jiaochangZoneTopList.add(jiaoChangZoneTop);
								Collections.sort(jiaochangZoneTopList, new ComparatorList());
								boolean bofirst = true;
								ListIterator<ZoneTop> iterator = jiaochangZoneTopList.listIterator();
								while (iterator.hasNext()) {
									ZoneTop thisTop = iterator.next();
									if (thisTop != null) {
										if (thisTop.getId() == jiaoChangZoneTop.getId()) {
											if (bofirst) {
												bofirst = false;
											} else {
												iterator.remove();
											}
										}
									}
								}
								if (jiaochangZoneTopList.size() > 5) {
									jiaochangZoneTopList.remove(jiaochangZoneTopList.size() - 1);
								}
								JiaoChangZoneTop topOne = (JiaoChangZoneTop) jiaochangZoneTopList.get(0);
								if (topOne.compare(jiaoChangZoneTop) == 0) {//该玩家是第一名发送奖励
									ResGetTopAwardToServerMessage sendMessage = new ResGetTopAwardToServerMessage();
									sendMessage.setZonetype(message.getZonetype());
									sendMessage.setZoneid(zoneTop.getZoneid());
									MessageUtil.send_to_game(player, sendMessage);

//									MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance().getString("强中自有强中手，一山更比一山高。玩家【{1}】校场考验，神勇过人，成为新一代校场新星，引人瞩目！"), jiaoChangZoneTop.getName());

								}
								saveZoneTop(zoneTop.getZoneid());
							}
						}
						break;

					}
				}
			} else if (message.getZonetype() == 1) {//世界地图常规副本最快速度排行
				DiTuZoneTop diTuZoneTop = JSON.parseObject(message.getZonetopjsonstr(), DiTuZoneTop.class);
				if (DiTuZoneTopmap.containsKey(diTuZoneTop.getZoneid())) {
					List<ZoneTop> zonetops = DiTuZoneTopmap.get(diTuZoneTop.getZoneid());
					if (zonetops == null) {
						zonetops = new ArrayList<ZoneTop>();
						DiTuZoneTopmap.put(diTuZoneTop.getZoneid(), zonetops);
					}
					zonetops.add(diTuZoneTop);
					Collections.sort(zonetops, new ComparatorList());
					boolean bofirst = true;
					ListIterator<ZoneTop> iterator = zonetops.listIterator();
					while (iterator.hasNext()) {
						ZoneTop thisTop = iterator.next();
						if (thisTop != null) {
							if (thisTop.getId() == diTuZoneTop.getId()) {
								if (bofirst) {
									bofirst = false;
								} else {
									iterator.remove();
								}
							}
						}
					}
					if (zonetops.size() > 5) {
						zonetops.remove(zonetops.size() - 1);
					}
				} else {
					List<ZoneTop> zonetops = new ArrayList<ZoneTop>();
					DiTuZoneTopmap.put(diTuZoneTop.getZoneid(), zonetops);
					zonetops.add(diTuZoneTop);
				}
				List<ZoneTop> zonetops = DiTuZoneTopmap.get(diTuZoneTop.getZoneid());
				ZoneTop topOne = (ZoneTop) zonetops.get(0);

				if (topOne.compare(diTuZoneTop) == 0) {//该玩家是第一名发送奖励
//					ResGetTopAwardToServerMessage sendMessage = new ResGetTopAwardToServerMessage();
//					sendMessage.setZonetype(message.getZonetype());
//					sendMessage.setZoneid(diTuZoneTop.getZoneid());
//					MessageUtil.send_to_game(player, sendMessage);
					ResZoneTimeRecordNoticeMessage nomsg = new ResZoneTimeRecordNoticeMessage();
					nomsg.setFullname(diTuZoneTop.getName());
					nomsg.setFulltime((int) diTuZoneTop.getComptime());
					nomsg.setPlayerid(diTuZoneTop.getId());
					nomsg.setZoneid(diTuZoneTop.getZoneid());
					MessageUtil.tell_world_message(nomsg);

//					MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance().getString("何为风驰电掣？何为电闪雷鸣？玩家【{1}】刷新了最快副本通关记录，使得天下英雄望尘莫及，扼腕长叹！"), diTuZoneTop.getName());
				}
				ResfastestClearanceToGameMessage smsg = new ResfastestClearanceToGameMessage();
				smsg.setZonetopjsonstr(JSON.toJSONString(DiTuZoneTopmap, SerializerFeature.WriteClassName));
				MessageUtil.send_to_game(smsg);//群发到所有GAME
				saveDituZoneTop();

			}
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerid())));
		}
	}

	public void reqScriptCommonServerToWorldMessage(ReqScriptCommonServerToWorldMessage message) {
		if (message.getScriptid() == 4003) {
			switch (message.getType()) {
				case 2: {
					HashMap paramMap = JSON.parseObject(message.getMessageData(), HashMap.class);
					long playerid = (Long) paramMap.get("id");
					if (playerid != 0) {
						Player player = PlayerManager.getInstance().getPlayer(playerid);
						if (player != null) {
							ResScriptCommonPlayerWorldToClientMessage sendMessage = new ResScriptCommonPlayerWorldToClientMessage();
							sendMessage.setScriptid(message.getScriptid());
							sendMessage.setType(message.getType());
							sendMessage.setMessageData(JSON.toJSONString(jiaochangZoneTopList));
							MessageUtil.tell_player_message(player, sendMessage);
						}
					}
				}
				break;
			}
		}
	}
	
	public List<SimpleRankInfo> getSimpleRankInfoByActivity(long playerId,int type,int count){
		TreeMap<TopData, Long> treeMap = this.getTreeMap(type);
		if (treeMap != null) {
			List<SimpleRankInfo> topInfos = new LinkedList<SimpleRankInfo>();
			Object[] toArray = treeMap.keySet().toArray();
			SimpleRankInfo myself = new SimpleRankInfo();
			int limit = (playerId==-1)?toArray.length:100;
			for (int i = 0; i < toArray.length && i < limit; i++) {
				TopData topData = (TopData) toArray[i];
				if(i >= count && topData.getId() != playerId) {
					if(myself.getPlayerId() != 0)
						break;
					continue;
				}
				TopPlayer topPlayer = getTopplayerMap().get(topData.getId());
				if (topPlayer != null) {
					SimpleRankInfo info = new SimpleRankInfo();
					info.setPlayerId(topData.getId());
					info.setName(topPlayer.getName());
					info.setJob(topPlayer.getJob());
					info.setRank(i+1);
					switch(type) {
					case TopListManager.TOPTYPE_FIGHTPOWER:
						info.setData(String.valueOf(topPlayer.getFightPower()));
						break;
					case TopListManager.TOPTYPE_LEVEL:
						info.setData(String.valueOf(topPlayer.getLevel()));
						break;
					case TopListManager.TOPTYPE_HORSE:
						info.setData(String.valueOf(topPlayer.getHorseLevel()));
						break;
					case TopListManager.TOPTYPE_PATA:
						info.setData(String.valueOf(topPlayer.getPataId()));
						break;
					case TopListManager.TOPTYPE_RECHARGE:
						info.setData(String.valueOf(topPlayer.getRechargeGold()));
						break;
					}
					if(i < count)
						topInfos.add(info);
					if(info.getPlayerId() == playerId)
						myself = info;
				}else {
					break;
				}
			}
			topInfos.add(myself);
			return topInfos;
		}
		return null;
	}
	
	
	public void syncGuildTopToGame() {
		ResGetTopGuildListToGameMessage msg = new ResGetTopGuildListToGameMessage();
		getTop5GuildInfo(msg.getTop5infolist());
		MessageUtil.send_to_game(msg);//群发到所有GAME
	}
	
	
	
}