package com.game.drop.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.game.backpack.bean.ItemInfo;
import com.game.backpack.manager.BackpackManager;
import com.game.backpack.message.ResTakeUpSuccessMessage;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.IAutoUseItem;
import com.game.backpack.structs.Item;
import com.game.backpack.structs.ItemTypeConst;
import com.game.challenge.manager.ChallengeManager;
import com.game.chat.bean.GoodsInfoRes;
import com.game.config.Config;
import com.game.data.bean.Q_boss_dropBean;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_mapBean;
import com.game.data.bean.Q_monster_dropgroupBean;
import com.game.data.manager.DataManager;
import com.game.drop.structs.DropItem;
import com.game.drop.structs.MapDropInfo;
import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.bean.DropGoodsInfo;
import com.game.map.manager.MapManager;
import com.game.map.message.ResRoundGoodsDisappearMessage;
import com.game.map.message.ResRoundMonsterDisappearMessage;
import com.game.map.structs.Area;
import com.game.map.structs.Map;
import com.game.monster.script.IMonsterDieDropScript;
import com.game.monster.structs.Hatred;
import com.game.monster.structs.Monster;
import com.game.monster.structs.MonsterState;
import com.game.monster.structs.Morph;
import com.game.monster.structs.MorphType;
import com.game.newactivity.NewActivityManager;
import com.game.newactivity.impl.Exchange;
import com.game.pet.struts.Pet;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.prompt.structs.Notifys;
import com.game.script.structs.ScriptEnum;
import com.game.server.config.MapConfig;
import com.game.server.impl.WServer;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.structs.Reasons;
import com.game.summonpet.struts.SummonPet;
import com.game.team.manager.TeamManager;
import com.game.utils.BeanUtil;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;
import com.game.zones.message.ResZoneLifeTimeMessage;
import com.game.zones.structs.ZoneContext;
/**
 * 掉落 管理类
 * @author xiaozhuoming
 * 2013-11-19
 */
public class DropManager {
	private static final Logger log=Logger.getLogger(DropManager.class);
	/**
	 * 最大掉落数
	 */
	private static final int MAXDROPGOODNUM=50;
	
	/**
	 * BOSS追加掉落最大配置数
	 */
	private static final int MAXBOSSDROPGOODNUM=20;
	
	/**
	 * 单个组包最大可配置数量
	 */
	public static final int MAXGROUPDROPGOODNUM=20;
	
	/**
	 * 最大拾取距离(像素)
	 */
	private static final int TAKEUPDISTANCE=125;
	
	/**
	 * 拾取后自动使用的最大检测距离
	 */
	private static final int AUTOUSEDISTANCE=100;
	/**
	 * 是否可丢弃
	 */
	private static final int DROPABLE=1;
//	/**
//	 * 掉落的所有物品
//	 */
//	private static  HashMap<Long,Item> items=new HashMap<Long, Item>();
	
	private static HashMap<Integer,Integer> groups=new HashMap<Integer, Integer>();
	
	/**
	 * 杀怪计数(KEY=角色ID_怪物模型ID_物品模型ID)用于一定数量必定掉一个的逻辑
	 */
	private static HashMap<String,Integer> counter=new HashMap<String, Integer>();
	
	private static HashMap<Integer,Integer> itemCounter = new HashMap<Integer, Integer>();
	
	public static int count(final String key){
		Integer count=null;
		synchronized (counter) {
			 count= counter.get(key);
			 count=count==null?1:++count;
			 if(count<0){
				 //防止溢出
				 count=Integer.MAX_VALUE;
			 }
			counter.put(key,count);
			if(log.isDebugEnabled()){
				log.debug("kill count"+key+"_"+count);	
			}
		}
		return count;
	}
	
	public static int getCount(String key){
		synchronized (counter) {
			try{
				Integer integer = counter.get(key);
				
				return integer==null?0:integer;	
			}catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			return 0;
		}
	}
	
	public static void resetCount(final String key){
		synchronized (counter) {
			try {
				counter.remove(key);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	public static boolean countItemCount(int itemModelId) {
		Integer count = null;
		synchronized (itemCounter) {
			count = itemCounter.get(itemModelId);
			count = count == null ? 1 : ++count;
			if(count < 0) {
				//防止溢出
				count = Integer.MAX_VALUE;
			}
			itemCounter.put(itemModelId, count);
			if(log.isDebugEnabled()) {
				log.debug("item count" + itemModelId + "_" + count);	
			}
		}
		return false;
	}
	
	public static int getItemCount(int itemModelId) {
		synchronized (itemCounter) {
			try{
				Integer integer = itemCounter.get(itemModelId);
				
				return integer == null ? 0 : integer;	
			}catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			return 0;
		}
	}
	
	public static void resetItemCount(final int itemModelId){
		synchronized (itemCounter) {
			try {
				itemCounter.remove(itemModelId);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	public static void resetAllItemCount(){
		synchronized (itemCounter) {
			try {
				itemCounter.clear();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 定时清除掉落物品
	 * 
	 * @param hour
	 * @return
	 */
	public static int clearItemCount(int hour) {
			try {
				List<Integer> items = new ArrayList<Integer>();
				synchronized (itemCounter) {
					
				Iterator<Integer> it = itemCounter.keySet().iterator();
				while (it.hasNext()) {
					int itemId = it.next();
					Q_itemBean itemModel = ManagerPool.dataManager.q_itemContainer.getMap().get(itemId);
					if (itemModel != null && !StringUtil.isBlank(itemModel.getQ_max_create()) && itemModel.getQ_max_create().contains((Symbol.XIEGANG_REG))) {
						// 件数/小时
						String[] split = itemModel.getQ_max_create().split(Symbol.XIEGANG_REG);
						if(split.length == 2) {
							int h = Integer.parseInt(split[1]);
							if(hour % h == 0) {
								items.add(itemId);
							}						
						}
					}
				}
				}
				//开始删除
				Iterator<Integer> it2 = items.iterator();
				while (it2.hasNext()) {
					resetItemCount(it2.next());
				}
			}catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return 0;
	}
	
	/**
	 * 怪物死亡掉落
	 * @param player
	 * @param monster
	 */
	public static void monsterDieDrop(Monster monster) {
		if (monster.getKiller() != null && monster.getKiller() instanceof Player){
			Player player = (Player)monster.getKiller();
			if(player.getNonage() == 3){
				return;
			}
		}
		
		try {
			IMonsterDieDropScript script = (IMonsterDieDropScript) ManagerPool.scriptManager.getScript(ScriptEnum.MONSTER_DROP);
			if (script != null) {
				script.onMonsterDie(monster, monster.getKiller());
			} else {
				log.error("怪物掉落脚本不存在！");
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		
		processDrop(monster, monster.getModelId(), 1, "");
	}
	
	/**
	 * 掉落处理
	 * @param monster
	 * @param modelId
	 * @param type 1-普通掉落;2-组包掉落
	 * @param intensifyProb 强化等级,当type=2时有用
	 */
	public static void processDrop(Monster monster, int modelId, int type, String intensifyProb) {
		int maxDropNum = 0;
		String columnName = "";
		Object dropModel = null;
		if(type == 1) {
			columnName = "Q_goods";
			maxDropNum = MAXDROPGOODNUM;
			dropModel = ManagerPool.dataManager.q_monster_dropprobContainer.getMap().get(modelId);
		}else if(type == 2) {
			columnName = "Q_group";
			maxDropNum = MAXGROUPDROPGOODNUM;
			dropModel = ManagerPool.dataManager.q_monster_dropgroupContainer.getMap().get(modelId);
		}
		
		if(dropModel == null) {
			return;
		}
		
		Player attacter = null;
		Fighter fighter = monster.getKiller();
		if (fighter instanceof Pet) {
			attacter = ManagerPool.playerManager.getPlayer(((Pet) fighter).getOwnerId());
		} else if (fighter instanceof SummonPet) {
			attacter = ManagerPool.playerManager.getPlayer(((SummonPet) fighter).getOwnerId());
		} else if(fighter instanceof Player) {
			attacter = (Player) fighter;
		}
		
		List<DropItem> dropConfigList = new ArrayList<DropItem>();
		try {
			// 将配置中的各项掉落解析出来
			for (int i = 1; i <= maxDropNum; i++) {
				Object value = BeanUtil.getMethodValue(dropModel, columnName + i);
				if (value != null) {
					String str = (String) value;
					DropItem item = buildItem(str, attacter);
					if (item != null) {
						if(type == 2 && !StringUtil.isBlank(intensifyProb) && StringUtil.isBlank(item.getIntensifyProb())) {
							item.setIntensifyProb(intensifyProb);
						}
						dropConfigList.add(item);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		int sum = 0;
		List<Integer> probabilityList = new ArrayList<Integer>();
		List<DropItem> dropItemList = new ArrayList<DropItem>();
		for(DropItem item : dropConfigList) {
			if(item.getProbability() > 0) {
				probabilityList.add(item.getProbability());
				dropItemList.add(item);
				sum += item.getProbability();
			}
		}
		
		if(sum < Global.MILLION_PROBABILITY) {
			probabilityList.add(Global.MILLION_PROBABILITY - sum);
		}
		
		dropConfigList.removeAll(dropItemList);
		int index = RandomUtils.randomIndexByProb(probabilityList);
		if(index >= 0 && dropItemList.size() > index) {
			DropItem dropItem = dropItemList.get(index);
			dropConfigList.add(dropItem);
		}
		
		for (DropItem item : dropConfigList) {
			try {
				if (item instanceof CopperDrop) {
					// 计算机率
					boolean isDrop = true;//RandomUtils.isGenerate(item.getProbability(), Global.MAX_PROBABILITY);
					// 如果是计数类型
					if (item.getMaxNum() > 1) {
						StringBuilder stringBuilder = new StringBuilder();
						if(monster.getKiller() != null) 
						stringBuilder.append(monster.getKiller().getId()).append("_");
						stringBuilder.append(monster.getModelId()).append("_");
						stringBuilder.append(item.getItemModel());
						String key = stringBuilder.toString();
						int count = DropManager.getCount(key);
						if (item.getMaxNum() == count) {
							isDrop = true;
							DropManager.resetCount(key);
						} else {
							isDrop = false;
							DropManager.count(key);
						}
					}
					if (isDrop) {
						Morph morph = monster.getMorph().get(MorphType.MONEY.getValue());
						if (morph != null) {
							int value = morph.getValue()  / 10000;
							while (value > 0) {
								try {
									MapDropInfo buildGoodsInfo = ((CopperDrop) item).buildGoodsInfo(monster);
									if (buildGoodsInfo == null) {
										break;
									} else {
										item.drop(buildGoodsInfo);
									}
								} catch (Exception e) {
									log.error(e, e);
								}
								value--;
							}
						} else {
							MapDropInfo buildGoodsInfo = ((CopperDrop) item).buildGoodsInfo(monster);
							// MapDropInfo buildGoodsInfo = buildGoodsInfo(monster);
							if (buildGoodsInfo == null) {
								// logger.error("这里不应该为空但有时候会报出来",new Exception());
							} else {
								item.drop(buildGoodsInfo);
							}
						}
					}
				} else {
					MapDropInfo dropGoods = item.buildDropGoods(monster);
					if (dropGoods != null) {
						//! Boss掉落事件接口
						if (monster.getKiller() instanceof Player && ManagerPool.monsterManager.isBoss(monster.getModelId())){
							ChallengeManager.getInstance().addBossDropItemEvent((Player)monster.getKiller(), monster, dropGoods.getItem());	
						}
						item.drop(dropGoods);
					}
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}
	
	/**
	 * BOSS鞭尸掉落
	 * @param monster
	 */
	public static void bossCorpseDrop(Monster monster) {
		try {
			if (ManagerPool.monsterManager.isBoss(monster.getModelId()) || ManagerPool.monsterManager.isMapMonster(monster.getModelId())) {
				List<String> array=new ArrayList<String>();
				
				Q_boss_dropBean dropModel = DataManager.getInstance().q_boss_dropContainer.getMap().get(monster.getModelId());
				if(dropModel!=null){
					for (int i = 1; i <= MAXBOSSDROPGOODNUM; i++) {
						String propValue = (String) BeanUtil.getMethodValue(dropModel, "Q_goods" + i);
						if (!StringUtil.isBlank(propValue)) {
								array.add(propValue);
						}
					}	
				}
				
				if(array.size()>0){
					try{
						int random = RandomUtils.random(1,array.size()-1);
						String itemdefine = array.get(random);
						Item item =null;
						if(itemdefine.toLowerCase().startsWith("copper")){
							String[] couteTrs = itemdefine.split("[*]");
							if (couteTrs.length == 2) {
								item=Item.createMoney(Integer.parseInt(couteTrs[1]));
							}
						}else if(itemdefine.toLowerCase().startsWith("@")){
							itemdefine = itemdefine.replace("@","");
							Q_monster_dropgroupBean groupModel = getGroupModel(itemdefine);
							Q_itemBean next = DropManager.getNowGroupAndToNext(groupModel);
							List<Item> createItems = Item.createItems(next.getQ_id(),1, false, 0,DropItem.getGradeNum(monster,next.getQ_id(),""),DropItem.getAppendCount(monster,next.getQ_id()));
							if(createItems.size()<=0||createItems.get(0)==null){
								return;
							}
							item=createItems.get(0);
						}else{
							Q_itemBean q_itemBean = getItemModelByName(itemdefine);
							List<Item> list = Item.createItems(q_itemBean.getQ_id(),1, false, 0,DropItem.getGradeNum(monster,q_itemBean.getQ_id(),""),DropItem.getAppendCount(monster,q_itemBean.getQ_id()));
							if(list.size()>=0||list.get(0)==null){
								return;
							}
							item = list.get(0);
						}
						if(item==null){
							log.error("boss追加掉落 出错定义"+itemdefine);
							return;
						}
						DropGoodsInfo info=new DropGoodsInfo();
						info.setDropGoodsId(item.getId());
						info.setItemModelId(item.getItemModelId());
						info.setNum(item.getNum());
						info.setDropTime(System.currentTimeMillis());
						if(item instanceof Equip){
							Equip equip=(Equip) item;
							if(equip.getAttributes()!=null){
								info.setAttributs((byte) equip.getAttributes().size());	
							}else{
								info.setAttributs((byte) 0);
							}
							info.setIntensify((byte) equip.getGradeNum());
							info.setIsFullAppend((byte) (equip.isFullAppend()?1:0));
						}
						Hatred owner = monster.getMaxHatred();
						Map map = MapManager.getInstance().getMap(monster);
						Position ableDropPoint =DropItem.getAbleDropPoint(monster.getPosition(), map);
						info.setX(ableDropPoint.getX());
						info.setY(ableDropPoint.getY());
						if(owner!=null&&owner.getTarget()!=null){
							if(owner.getTarget() instanceof Player){
								//如果是宠物攻击 则需要另行处理
								Player player=(Player) owner.getTarget();
								info.setOwnerId(player.getId());	
							}			
						}
						MapDropInfo mapDropInfo = new MapDropInfo(info, item,map,0);
						Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(item.getItemModelId());					
						if (q_itemBean != null&&monster.isShow()) {
							if (q_itemBean.getQ_notice() == 3 || q_itemBean.getQ_notice() == 4) {
								int mapModelid = mapDropInfo.getMapModelid();
								Q_mapBean mapModel = DataManager.getInstance().q_mapContainer.getMap().get(mapModelid);
								short x = mapDropInfo.getDropinfo().getX();
								short y = mapDropInfo.getDropinfo().getY();
								int lineId = mapDropInfo.getLine();
								int serverId = mapDropInfo.getServerId();
								String name = WServer.getGameConfig().getCountryNameByServer(serverId);
								
								ItemInfo itemInfo = item.buildItemInfo();
								List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
								GoodsInfoRes goodsInfo = new GoodsInfoRes();
								goodsInfo.setItemInfo(itemInfo);
								goodsInfos.add(goodsInfo);
								
								MessageUtil.notify_All_player(Notifys.CUTOUT, ResManager.getInstance().getString("{$}掉落在{1}{2}{3}的[{4},{5}]"), goodsInfos, 0, name, mapModel.getQ_map_name(), lineId + ResManager.getInstance().getString("线"), String.valueOf(x/25),
										String.valueOf(y/25));
							}
						}
						mapDropInfo.getHideSet().addAll(monster.getHideSet());
						mapDropInfo.getShowSet().addAll(monster.getShowSet());
						mapDropInfo.setShow(monster.isShow());
						MapManager.getInstance().enterMap(mapDropInfo);
					}catch (Exception e) {
						log.error(e,e);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 人物丢弃物品
	 * @param roleId
	 * @param cellId
	 */
	public static void playerDiscard(Player player, int cellId){
		if(cellId<=0||cellId>Global.MAX_BAG_CELLS){
			return;
		}
		if(player.isDie()){//死亡不允许丢物品
			return ;
		}
		Item item = BackpackManager.getInstance().getItemByCellId(player, cellId);
		if(item!=null){
			Q_itemBean q_itemBean = ManagerPool.dataManager.q_itemContainer.getMap().get(item.getItemModelId());
			if(q_itemBean.getQ_drop()!=DROPABLE){
				MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("很抱歉，该物品无法丢弃"));
				return;
			}
			// ! xuliang 丢弃物品取消安全区判断
			/*
			if(MapManager.getInstance().isSafe(player.getPosition(),player.getMapModelId())){
				MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("很抱歉，在安全区无法丢弃物品"));
				return;
			}
			*/
			/*
			 * luminghua
			 * if(item.isBind()){
				MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("很抱歉，绑定物品无法丢弃"));
				return;
			}*/
			if(item.isTrade()){
				MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("很抱歉，物品正在交易中"));
				return;
			}
			if(PlayerState.SWIM.compare(player.getState())){
				MessageUtil.notify_player(player, Notifys.NORMAL,ResManager.getInstance().getString("游泳时不可以丢弃物品"), player.getName());
				return;
			}
			Map map = MapManager.getInstance().getMap(player);
			Position ableDropPoint = DropItem.getAbleDropPointAndAxclude(player.getPosition(), map);
			if(ableDropPoint==null){
				MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("很抱歉，该区域不允许丢弃物品"));
				return;
			}
			ManagerPool.backpackManager.removeItemByCellId(player, cellId,Reasons.GOODSDISCARD,Config.getId());
//			DropGoodsInfo info=new DropGoodsInfo();
//			info.setDropGoodsId(item.getId());
//			info.setItemModelId(item.getItemModelId());
//			info.setNum(item.getNum());
//			if(item instanceof Equip){
//				Equip equip=(Equip) item;
//				if(equip.getAttributes()!=null){
//					info.setAttributs((byte) equip.getAttributes().size());	
//				}else{
//					info.setAttributs((byte) 0);
//				}
//				info.setIntensify((byte) equip.getGradeNum());
//				info.setIsFullAppend((byte) (equip.isFullAppend()?1:0));
//			}
//			info.setX(ableDropPoint.getX());
//			info.setY(ableDropPoint.getY());
//			info.setDropTime(System.currentTimeMillis());
//			item.setGridId(-1);
//			MapDropInfo mapDropInfo = new MapDropInfo(info, item, map,0);
//			if (q_itemBean != null) {
//				if (q_itemBean.getQ_notice() == 3 || q_itemBean.getQ_notice() == 4) {
//					int mapModelid = mapDropInfo.getMapModelid();
//					Q_mapBean mapModel = DataManager.getInstance().q_mapContainer.getMap().get(mapModelid);
//					short x = mapDropInfo.getDropinfo().getX();
//					short y = mapDropInfo.getDropinfo().getY();
//					int lineId = mapDropInfo.getLine();
//					int serverId = mapDropInfo.getServerId();
//					String name = WServer.getGameConfig().getCountryNameByServer(serverId);
//					MessageUtil.notify_All_player(Notifys.CUTOUT, ResManager.getInstance().getString("{1}掉落在{2}{3}{4}的[{5},{6}]"), BackpackManager.getInstance().getName(q_itemBean.getQ_id()), name, mapModel.getQ_map_name(), lineId + ResManager.getInstance().getString("线"), String.valueOf(x/25),
//							String.valueOf(y/25));
//				}
//			}
//			MapManager.getInstance().enterMap(mapDropInfo);
		}
	}

	
	
	private static Pattern pattern = Pattern.compile("(\\d+)/(\\d+)(\\s+)(.*)");
	
	/**
	 * 解析掉落配置项
	 * @param str
	 * @param attacker
	 * @return
	 */
	private static DropItem buildItem(String str, Player attacker) {
		DropItem item = null;
		try {
			if (str != null && str.equals("")) {
				return null;
			}
			
			String[] goodStr = null;
			//韩文平台 掉落单独解析因为奇葩韩文道具名有空格
			if(WServer.getInstance().getServerWeb().equals("hgpupugame")){
				Matcher matcher = pattern.matcher(str);
				if(matcher.find()){
					String fen = matcher.group(1)+"/"+matcher.group(2);
					String drop = matcher.group(4);
					goodStr=new String[]{fen,drop};
				}
			}else {
				//其他平台正常解析
				str=str.replace("  ", " ");
				str=str.replace("  ", " ");
				goodStr = str.split(" ");
			}

			if (goodStr == null || goodStr.length < 2 || goodStr.length > 4) {
				log.error("掉落配置有错误:"+str);
				return null;
			}
			
			String jobStr = "";
			String activityStr = "";
			if(goodStr.length == 3 || goodStr.length == 4) {
				if(goodStr[2].contains("%")) {
					jobStr = goodStr[2];
				} else if(goodStr[2].contains("^")) {
					activityStr = goodStr[2];
				}
				
				if(goodStr.length == 4) {
					if(goodStr[3].contains("%")) {
						jobStr = goodStr[3];
					} else if(goodStr[3].contains("^")) {
						activityStr = goodStr[3];
					}
				}
				
				//检查玩家的职业
				if(checkPlayerJob(jobStr, attacker) == false) {
					return null;
				}
			}
			
			boolean gradeLimit = true;
			boolean bind = false;
			boolean isOwner = true;
			if (goodStr[1].contains("~")) {
				// 是否排除在爆率衰减规则之外
				gradeLimit = false;
				goodStr[1] = goodStr[1].replace("~", "");
			}
			if (goodStr[1].contains("$")) {
				// 掉落后将物品设为拾取绑定
				goodStr[1] = goodStr[1].replace("$", "");
				bind = true;
			}
			if (goodStr[1].contains("&")) {
				// 掉出后为无主物品
				goodStr[1] = goodStr[1].replace("&", "");
				isOwner = false;
			}

			if (goodStr[1].contains("@")) {
				// 组包掉落规则初始化
				goodStr[1] = goodStr[1].replace("@", "");
				String[] groupStr = goodStr[1].split("_");
				String id = groupStr[0];
				int modelId = Integer.parseInt(id);
				if(checkActivity(activityStr, modelId) == false) {
					return null;
				}
				
				Q_monster_dropgroupBean groupItemModel = getGroupModelByID(modelId);
				if(groupItemModel != null) {
					item = new GroupDrop();
					item.setGroupModel(groupItemModel.getQ_group_id());
					if(groupStr.length == 2) {
						item.setIntensifyProb(groupStr[1]);
					}
				} else {
					log.error(id+"在模型表中找不到");
				}
			} else if (goodStr[1].contains("#")) {
				// 任务掉落
				goodStr[1] = goodStr[1].replace("#", "");
				String[] split = goodStr[1].split(Symbol.XIAHUAXIAN_REG);
				int modelId = Integer.parseInt(split[1]);
				if(checkActivity(activityStr, modelId) == false) {
					return null;
				}
				
				Q_itemBean itemModel = getItemModelByID(modelId);
				if(itemModel != null) {
					item = new TaskDrop();
					TaskDrop taskDrop = (TaskDrop) item;
					int taskId = Integer.parseInt(split[0]);
					taskDrop.setTaskId(taskId);
					int maxNum = Integer.parseInt(split[2]);
					taskDrop.setTaskMaxNum(maxNum);
					taskDrop.setItemModel(itemModel.getQ_id());
				}else {
					log.error(split[1]+"在模型表中找不到");
				}
			} else if (goodStr[1].contains("!")) {
				// 脚本掉落
				item = new ScripDrop();
				goodStr[1] = goodStr[1].replace("!", "");
				Integer script=Integer.parseInt(goodStr[1]);
				ScripDrop drop=(ScripDrop) item;
				drop.setScriptId(script);
			} else if (goodStr[1].contains("copper")) {
				String[] couteTrs = goodStr[1].split(Symbol.XIAHUAXIAN_REG);
				if (couteTrs.length == 2) {
					int count = Integer.parseInt(couteTrs[1]);
					item = new CopperDrop(count);
					item.setCopperModel(count);
				}
			} else {
				String id = goodStr[1];
				int modelId = Integer.parseInt(id);
				if(checkActivity(activityStr, modelId) == false) {
					return null;
				}
				
				Q_itemBean itemModel = getItemModelByID(modelId);
				if(itemModel!=null){
					item = new CommonDrop();
					item.setItemModel(itemModel.getQ_id());
				}else{
					log.error(id+"物品模型找不到");
				}
			}
			if(item != null) {
				String[] jilvStr = goodStr[0].split("/");
				if (jilvStr.length == 2) {
					float fenzi = Float.parseFloat(jilvStr[0]);
					float fenmu = Float.parseFloat(jilvStr[1]);
					if (fenzi > fenmu) {
						// 打多少怪物必定掉一个
						item.setMaxNum((int) (fenzi / fenmu));
					} else {
						// 转换成以系统默认几率基数的几率形式
						int prob = (int) (((double)Global.MILLION_PROBABILITY / fenmu) * fenzi);
						if(prob > 0 && prob != Global.MILLION_PROBABILITY) {
							item.setProbability(prob);
						}else if(prob <= 0) {
							return null;
						}
						
					}
				}
				item.setGradeLimit(gradeLimit);
				item.setBind(bind);
				item.setOwner(isOwner);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		return item;
	}
	
	/**
	 * 检查玩家职业掉落
	 * 
	 * @param jobStr
	 * @param attacker
	 * @return
	 */
	private static boolean checkPlayerJob(String jobStr, Player attacker) {
		boolean flag1 = true;
		if(StringUtil.isBlank(jobStr) == false) {
			boolean flag2 = false;
			String[] strs = jobStr.replace("%", "").trim().split(Symbol.SHUXIAN_REG);
			for(String value : strs) {
				if(attacker != null && (value.equals("0") || Byte.valueOf(value) == attacker.getJob())) {
					flag2 = true;
					break;
				}
			}
			flag1 = flag2;
		}
		return flag1;
	}
	
	/**
	 * 检查活动掉落
	 * 
	 * @param activityStr
	 * @return
	 */
	private static boolean checkActivity(String activityStr, int modelId) {
		if(StringUtil.isBlank(activityStr) == false) {
			Integer activityId = Integer.parseInt(activityStr.replace("^", ""));
			Exchange exchange = (Exchange) NewActivityManager.getInstance().getActivityImpl(activityId);
			if(exchange != null && exchange.checkDropAble(modelId) == false) {
				return false;
			}
	    }
		return true;
	}
	
	private static Q_itemBean getItemModelByName(String name){
		List<Q_itemBean> list = ManagerPool.dataManager.q_itemContainer.getList();
		for (Q_itemBean q_itemBean : list) {
			if(name.equals(q_itemBean.getQ_name())){
				return q_itemBean;
			}
		}
		log.error(name+"在模型表中找不到");
		return null;
	}
	
	public static Q_itemBean getItemModelByID(int itemID){
		HashMap<Integer, Q_itemBean> map = ManagerPool.dataManager.q_itemContainer.getMap();
		Q_itemBean q_itemBean = map.get(itemID);
		if(q_itemBean == null) {
			log.error(itemID + "在模型表中找不到");
		}
		return q_itemBean;
	}
	
	private static Q_monster_dropgroupBean getGroupModel(String name){
		List<Q_monster_dropgroupBean> list = ManagerPool.dataManager.q_monster_dropgroupContainer.getList();
		for (Q_monster_dropgroupBean q_monster_dropgroupBean : list) {
			if(name.equals(q_monster_dropgroupBean.getQ_groupname())){
				return q_monster_dropgroupBean;
			}
		}
		return null;
	}
	
	private static Q_monster_dropgroupBean getGroupModelByID(int groupID){
		HashMap<Integer, Q_monster_dropgroupBean> map = ManagerPool.dataManager.q_monster_dropgroupContainer.getMap();
		Q_monster_dropgroupBean q_monster_dropgroupBean = map.get(groupID);
		if(q_monster_dropgroupBean == null) {
			log.error(groupID + "在模型表中找不到");
		}
		return q_monster_dropgroupBean;
	}
	
	public static Q_itemBean getNowGroupAndToNext(Q_monster_dropgroupBean model) {
		synchronized (groups) {
			try {
				Integer index = groups.get(model.getQ_group_id());
				if (index == null || index == 0) {
					index = 1;
					groups.put(model.getQ_group_id(), index);
				}
				String value = (String)BeanUtil.getMethodValue(model, "Q_group" + index);
				if(StringUtil.isBlank(value)){
					index = 1;
					groups.put(model.getQ_group_id(), index);
					return null;
				}
				Q_itemBean itemModelByName = getItemModelByName((String) value);
				if(index>MAXGROUPDROPGOODNUM){
					index=MAXGROUPDROPGOODNUM;
					groups.put(model.getQ_group_id(), index);
				}
				if(index==MAXGROUPDROPGOODNUM){
					index=1;
				}else{
					index++;
					value = (String) BeanUtil.getMethodValue(model, "Q_group" + index);
					if(StringUtil.isBlank(value)){
						index=1;
					}
				}
				groups.put(model.getQ_group_id(), index);
				return itemModelByName;
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			return null;
		}

	}
	
//	public static void groupToNext(Q_monster_dropgroupBean model){
//		synchronized (groups) {
//			try{
//				Integer index = groups.get(model.getQ_group_id());
//				if(index==null||index==0||index==MAXDROPGOODNUM){
//					index=1;
//				}else{
//					
//				}
//				groups.put(model.getQ_group_id(),index);	
//			}catch (Exception e) {
//				log.error("组包指针下移出错",e);
//			}
//		}
//	}
	
	/**
	 * 拾取物品
	 * 
	 * @param roleId
	 * @param goodsId
	 */
	public static void takeUp(Player player, long goodsId) {
		if(player.isDie()){
			//死了不能捡东西
			return;
		}
		Position position = player.getPosition();
		Item item = null;
		try {

			Map map = MapManager.getInstance().getMap(player);
			
			List<Area> round = MapManager.getInstance().getRound(map, position);
			MapDropInfo mapDropInfo=null;
			Area areas=null;
			for (Area area : round) {
				MapDropInfo info = area.getDropGoods().get(goodsId);
				if(info!=null){
					mapDropInfo=info;
					areas=area;
					break;
				}		
			}			
			if (mapDropInfo == null) {
//				MessageUtil.notify_player(player, Notifys.NORMAL, "物品不存在");
				ResRoundGoodsDisappearMessage dismsg = new ResRoundGoodsDisappearMessage();
				dismsg.getGoodsIds().add(goodsId);
				MessageUtil.tell_player_message(player, dismsg);
				return;
			}
			item = mapDropInfo.getItem();
			DropGoodsInfo dropGoodsInfo = mapDropInfo.getDropinfo();
			if (item == null||dropGoodsInfo==null) {
//				MessageUtil.notify_player(player, Notifys.NORMAL, "物品不存在");
				ResRoundGoodsDisappearMessage dismsg = new ResRoundGoodsDisappearMessage();
				dismsg.getGoodsIds().add(goodsId);
				MessageUtil.tell_player_message(player, dismsg);
				return;
			}
		
			Q_itemBean model = DataManager.getInstance().q_itemContainer.getMap().get(item.getItemModelId());
			if(model.getQ_auto_use()==1){
				autoUseTakeUp(player, mapDropInfo, areas);
			}else{
				defaultTakeUp(player, mapDropInfo, areas);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	
	private static void autoUseTakeUp(Player player, MapDropInfo mapDropInfo, Area area) {
		DropGoodsInfo dropGoodsInfo = mapDropInfo.getDropinfo();
		Item item = mapDropInfo.getItem();
		if (MapUtils.countDistance(player.getPosition(), MapUtils.buildPosition((short) dropGoodsInfo.getX(), (short) dropGoodsInfo.getY())) > AUTOUSEDISTANCE) {
//			MessageUtil.notify_player(player, Notifys.NORMAL, "离要拾取的物品太远");
			return;
		}
		// 有归属
		if (dropGoodsInfo.getOwnerId() != 0
				&& dropGoodsInfo.getOwnerId() != player.getId()) {
			long time = System.currentTimeMillis()
					- dropGoodsInfo.getDropTime();
			if(TeamManager.getInstance().isSameTeam(player, dropGoodsInfo.getOwnerId())){
				/* xiaozhuoming 队友没有时间限制
				//是队友
				if (time < 10 * 1000) {
//					 10秒之内只有本人可以拾取
					MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("有主物品,还有")
							+ (10 - time / 1000) + ResManager.getInstance().getString("秒才可拾取"));
					return;
				}*/			
			}else{
				//不是队友
				if (time < 30 * 1000) {
					MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("有主物品,还有")
							+ (30 - time / 1000) + ResManager.getInstance().getString("秒才可拾取"));
					return;
				}
			}
		}

		
		IAutoUseItem medicines = (IAutoUseItem) item;
		if(medicines.autoTakeUpUse(player)){
			area.getDropGoods().remove(item.getId());
			ResRoundGoodsDisappearMessage dismsg = new ResRoundGoodsDisappearMessage();
			dismsg.getGoodsIds().add(dropGoodsInfo.getDropGoodsId());
			MessageUtil.tell_round_message(mapDropInfo, dismsg);
		}
		return;
	}

	private static void defaultTakeUp(Player player,MapDropInfo mapDropInfo,Area area){
		DropGoodsInfo dropGoodsInfo = mapDropInfo.getDropinfo();
		Item item = mapDropInfo.getItem();
		// 有归属
		if (dropGoodsInfo.getOwnerId() != 0
				&& dropGoodsInfo.getOwnerId() != player.getId()) {
			long time = System.currentTimeMillis()
					- dropGoodsInfo.getDropTime();
			if(TeamManager.getInstance().isSameTeam(player, dropGoodsInfo.getOwnerId())){
				/* xiaozhuoming 队友没有时间限制
				//是队友
				if (time < 10 * 1000) {
					// 10秒之内只有本人可以拾取
					MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("有主物品,还有")
							+ (10 - time / 1000) + ResManager.getInstance().getString("秒才可拾取"));
					return;
				}*/			
			}else{
				//不是队友
				if (time < 30 * 1000) {
					MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("有主物品,还有")
							+ (30 - time / 1000) + ResManager.getInstance().getString("秒才可拾取"));
					return;
				}
			}
		}
		

		long action = Config.getId();

		boolean canTakeUp = canTakeUp(player, dropGoodsInfo);
		if(canTakeUp == false) {
			MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("离要拾取的物品太远"));
			return;
		}
		
		String itemName = "";
		Q_itemBean model = DataManager.getInstance().q_itemContainer.getMap().get(item.getItemModelId());
		if (model != null && model.getQ_type() == ItemTypeConst.COPPER) {
			/*
			if (MapUtils.countDistance(player.getPosition(), MapUtils.buildPosition((short) dropGoodsInfo.getX(), (short) dropGoodsInfo.getY())) > TAKEUPDISTANCE) {
				return;
			}*/
			// // 杀怪加金币 改为杀怪掉落的时候增加 luminghua
			// if (player.getAddmoney_whenkill() > 0 && item.getNum() > 0) {
			// int newNum = item.getNum() + (item.getNum() * player.getAddmoney_whenkill() / Global.MAX_PROBABILITY);
			// if (newNum > 0) {
			// item.setNum(newNum);
			// }
			// }
			int num = player.getMoney() + item.getNum();
			if (num <= 0 || num > Global.BAG_MAX_COPPER) {
				MessageUtil.notify_player(player, Notifys.ERROR, "背包金币数量已达上限，拾取失败");
				return;
			}
			// 金币拾取
			if (!BackpackManager.getInstance().changeMoney(player, item.getNum(), Reasons.TAKEUP, action)) {
				// 拾取失败
				return;
			}

			itemName = "金币";
		} else if (model != null && model.getQ_type() == ItemTypeConst.BINDGOLD) {
			// 绑钻掉落
			if (MapUtils.countDistance(player.getPosition(), MapUtils.buildPosition((short) dropGoodsInfo.getX(), (short) dropGoodsInfo.getY())) > 2000) {
				return;
			}
			int num = player.getBindGold() + item.getNum();
			if (num <= 0 || num > Global.BAG_MAX_BINDGOLD) {
				MessageUtil.notify_player(player, Notifys.ERROR, "背包绑钻数量已达上限，拾取失败");
				return;
			}
			if (!BackpackManager.getInstance().changeBindGold(player, item.getNum(), Reasons.TAKEUP, action)) {
				return;
			}
			itemName = "绑钻";
		} else {
			/*
			if (MapUtils.countDistance(player.getPosition(), MapUtils.buildPosition((short) dropGoodsInfo.getX(), (short) dropGoodsInfo.getY())) > TAKEUPDISTANCE) {
				MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("离要拾取的物品太远"));
				return;
			}*/

			if (!BackpackManager.getInstance().hasAddSpace(player, item)) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("对不起，您的背包空格不足，请清理后再进行本操作"));
				return;
			}
			if (!BackpackManager.getInstance().addItem(player, item, Reasons.TAKEUP, action)) {
				// 拾取失败
				return;
			}
			
			itemName = BackpackManager.getInstance().getName(model.getQ_id());
		}
		ResRoundGoodsDisappearMessage dismsg=new ResRoundGoodsDisappearMessage();
		dismsg.getGoodsIds().add(dropGoodsInfo.getDropGoodsId());
		area.getDropGoods().remove(item.getId());
		ResTakeUpSuccessMessage msg=new ResTakeUpSuccessMessage();
		msg.setGoodsId(item.getId());
		msg.setGoodModelId(dropGoodsInfo.getItemModelId());
		MessageUtil.tell_player_message(player, msg);
		MessageUtil.tell_round_message(mapDropInfo, dismsg);
//		if(item.getItemModelId()!=-1)
		if(model.getQ_type() != ItemTypeConst.COPPER) {
			if(item.getNum()>1){
				MessageUtil.notify_player(player,Notifys.CHAT_PERSONAL,ResManager.getInstance().getString("捡到{1}({2})"),itemName,item.getNum()+"");
//				MessageUtil.notify_player(player,Notifys.NORMAL,"捡到{1}({2})",model.getQ_name(),item.getNum()+"");
			}else{
				MessageUtil.notify_player(player,Notifys.CHAT_PERSONAL,ResManager.getInstance().getString("捡到{1}"),itemName);
//				MessageUtil.notify_player(player,Notifys.NORMAL,"捡到{1}",model.getQ_name());
			}
		}
		Map map = MapManager.getInstance().getMap(player);
		if(map.getZoneModelId()>=7001 && map.getZoneModelId()<=7008 ){
			if(item.getItemModelId()==720100 || item.getItemModelId()==720101 || item.getItemModelId()==720102){
				ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
				if(zone!=null){
					zone.getOthers().put("zoneprocess", 6);
					ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
					Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(map.getZoneModelId());
					timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)- ((int) System.currentTimeMillis()/1000 -(Integer)zone.getOthers().get("time")));
					timemsg.setZoneid(map.getZoneModelId());
					timemsg.setZoneprocess(6);
					if(zone.getOthers().get("playercount")!=null){
						timemsg.setPlayerCount((int)zone.getOthers().get("playercount"));
					}
					MessageUtil.tell_map_message(map, timemsg);
					
					map.getRevives().clear();
					Iterator<Monster> zoneMonsters =    map.getMonsters().values().iterator();
					ResRoundMonsterDisappearMessage hidemsg = new ResRoundMonsterDisappearMessage();
					while (zoneMonsters.hasNext()) {
						Monster monster = zoneMonsters.next();
						monster.setState(MonsterState.DIE);
						hidemsg.getMonstersIds().add(monster.getId());
						
					}
					for (Area areas : map.getAreas().values()){
						areas.getMonsters().clear();
					}
					map.getMonsters().clear();
					
					MessageUtil.tell_map_message(map, hidemsg);
					
					//刷新血色天使
					int[] angleCenter = {165, 57};
					String monsters[] = zoneBean.getQ_map_boss().split(",");
					int ANGLE = Integer.parseInt(monsters[2]);
					MapConfig config = zone.getConfigs().get(0);
					Grid grid = MapUtils.getGrid(angleCenter[0], angleCenter[1], config.getMapModelId());
					Monster gcmonster = ManagerPool.monsterManager.createMonster(ANGLE, config.getServerId(), config.getLineId(), config.getMapId(), grid.getCenter());
					gcmonster.setDirection((byte)0);
					ManagerPool.mapManager.enterMap(gcmonster);
					MessageUtil.notify_map(map, Notifys.CUTOUT, String.format(ResManager.getInstance().getString("大天使:不好,被血色天使发现了，只好先击败他了"), gcmonster.getName()));
				}
			}
			
		}
		
		
	}
	
	/**
	 * 检查掉落物品是否在玩家的拾取范围内
	 * 
	 * @param player
	 * @param dropGoodsInfo
	 * @return
	 */
	private static boolean canTakeUp(Player player, DropGoodsInfo dropGoodsInfo) {
		if(player == null || dropGoodsInfo == null) {
			return false;
		}
		//如果小于玩家拾取范围
		if(MapUtils.countDistance(player.getPosition(), MapUtils.buildPosition((short) dropGoodsInfo.getX(), (short) dropGoodsInfo.getY())) <= TAKEUPDISTANCE) {
			return true;
		}
		// 萌宠扩大拾取范围为玩家视野
		Equip equip = player.getEquips()[10];
		if (equip != null) {
			Q_itemBean model = DataManager.getInstance().q_itemContainer.getMap().get(equip.getItemModelId());
			if(model != null && model.getQ_third_type() == 5) {
				Map map = ManagerPool.mapManager.getMap(player);
				int areaId = ManagerPool.mapManager.getAreaId(player.getPosition());
				List<Area> rounds = ManagerPool.mapManager.getRoundAreas(map,areaId);
				for (int i = 0; i < rounds.size(); i++) {
					Area area = rounds.get(i);
					if (area != null) {
						Iterator<MapDropInfo> it = area.getDropGoods().values().iterator();
						while (it.hasNext()) {
							MapDropInfo mapDropInfo = it.next();
							if (mapDropInfo != null) {
								DropGoodsInfo areaDropGoodsInfo = mapDropInfo.getDropinfo();
								if(areaDropGoodsInfo != null && areaDropGoodsInfo.getDropGoodsId() == dropGoodsInfo.getDropGoodsId()) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
}
