package com.game.goldraffle.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.backpack.bean.ItemInfo;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.chat.bean.GoodsInfoRes;
import com.game.config.Config;
import com.game.cooldown.structs.CooldownTypes;
import com.game.data.bean.Q_fractionBean;
import com.game.data.bean.Q_globalBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_lotteryBean;
import com.game.data.manager.DataManager;
import com.game.db.dao.GoldRaffleDao;
import com.game.db.dao.GoldRaffleEventDao;
import com.game.dblog.LogService;
import com.game.goldraffle.bean.GoldRaffleBoxInfo;
import com.game.goldraffle.bean.GoldRaffleEventInfo;
import com.game.goldraffle.bean.GoldRaffleGridInfo;
import com.game.goldraffle.log.FractionLog;
import com.game.goldraffle.log.GoldRaffleLog;
import com.game.goldraffle.message.ResFractionToClientMessage;
import com.game.goldraffle.message.ResGoldRaffleBoxUseItemToClientMessage;
import com.game.goldraffle.message.ResGoldRaffleEventInfoToClientMessage;
import com.game.goldraffle.message.ResGoldRaffleToClientMessage;
import com.game.goldraffle.message.ResOpenGoldRaffleBoxInfoToClientMessage;
import com.game.goldraffle.message.ResOpenGoldRaffleInfoToClientMessage;
import com.game.goldraffle.structs.GoldRaffleBoxData;
import com.game.goldraffle.structs.GoldRaffleData;
import com.game.goldraffle.structs.GoldRaffleEventData;
import com.game.goldraffle.structs.GoldRaffleGridData;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.server.impl.WServer;
import com.game.structs.Reasons;
import com.game.utils.CommonConfig;
import com.game.utils.Global;
import com.game.utils.MessageUtil;
import com.game.utils.ParseUtil;
import com.game.utils.VersionUpdateUtil;
import com.game.utils.ParseUtil.ItemParm;
import com.game.utils.ParseUtil.PlayerParm;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;
import com.game.utils.TimeUtil;
import com.game.vip.manager.VipManager;
import com.game.vip.struts.GuideType;

/**
 * 钻石抽奖管理类
 *
 * @author xiaozhuoming
 * 
 * @since 2014-01-07
 */
public class GoldRaffleManager {

	private Logger log = Logger.getLogger(GoldRaffleManager.class);
	
	private static GoldRaffleManager manager;
	private static Object obj = new Object();

	private GoldRaffleManager() {
	}

	public static GoldRaffleManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new GoldRaffleManager();
			}
		}
		return manager;
	}
	
	private static int GOLDRAFFLE_BOX_MAX_SIZE = 1000; //钻石抽奖宝箱的最大容量
	private static int GOLDRAFFLE_EVENT_MAX_NUM = 500;//钻石抽奖日志保存最大数量
	private static int GOLDRAFFLE_COUPON_ITEM_ID = 700095;//优惠券物品ID
	
	/**
	 *  玩家钻石抽奖物品
	 */
	private static Map<Long/*playerId*/, GoldRaffleBoxData> goldRaffleMap = new HashMap<Long, GoldRaffleBoxData>();
	
	/**
	 * 玩家钻石抽奖事件列表
	 */
	private static List<GoldRaffleEventData> goldRaffleEventDataList = new ArrayList<GoldRaffleEventData>();
	
	/**
	 * 玩家钻石抽奖物品数据库
	 */
	private GoldRaffleDao goldRaffleDao = new GoldRaffleDao(); 
	
	/**
	 * 玩家钻石抽奖事件数据库
	 */
	private GoldRaffleEventDao goldRaffleEventDao = new GoldRaffleEventDao(); 
	
	/**
	 * 打开钻石抽奖界面
	 * @param player
	 */
	public void reqOpenGoldRaffleInfo(Player player) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}

		ResOpenGoldRaffleInfoToClientMessage sendMessage = new ResOpenGoldRaffleInfoToClientMessage();
		sendMessage.setCoupon(ManagerPool.backpackManager.getItemNum(player, GOLDRAFFLE_COUPON_ITEM_ID));
		sendMessage.setFraction(player.getFraction());
		GoldRaffleBoxData goldRaffleBoxData = goldRaffleMap.get(player.getId());
		if(goldRaffleBoxData == null) goldRaffleBoxData = new GoldRaffleBoxData();
		Iterator<GoldRaffleGridData> iterator = goldRaffleBoxData.getGoldRaffleGridDataList().iterator();
		while(iterator.hasNext()) {
			GoldRaffleGridData goldRaffleGridData = iterator.next();
			if(goldRaffleGridData == null || goldRaffleGridData.getItem() == null) {
				iterator.remove();
			}
		}
		sendMessage.setCapacity(goldRaffleBoxData.getGoldRaffleGridDataList().size());
		sendMessage.setGoldRaffleEventInfoList(this.getGoldRaffleEventInfo(goldRaffleEventDataList));
		MessageUtil.tell_player_message(player, sendMessage);
	}
	
	/**
	 * 钻石抽奖
	 * @param player
	 * @param type 抽奖类型,1-表示抽1次;2-表示抽10次;3-表示抽50次
	 */
	public void reqGoldRaffle(Player player, byte type) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}
		
		List<Q_lotteryBean> q_lotteryBeanList = ManagerPool.dataManager.q_lotteryContainer.getList(); 
		if(q_lotteryBeanList == null || q_lotteryBeanList.size() < 1) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("钻石抽奖配置信息出错"));
			return;
		}
		
		int count = 0;
		Q_globalBean golbalBean = null;
		switch (type) {
		case 1: //抽1次
			count = 1;
			golbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.GOLD_RAFFLE_COST_1.getValue());
			break;
		case 2: //抽10次
			count = 10;
			golbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.GOLD_RAFFLE_COST_10.getValue());
			break;
		case 3: //抽50次
			count = 50;
			golbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.GOLD_RAFFLE_COST_50.getValue());
			break;
		default:
			log.error("reqGoldRaffle type error! type = " + type);
			return;
		}
		
		if(golbalBean == null || golbalBean.getQ_int_value() <= 0) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("钻石抽奖配置信息出错"));
			return;
		}
		
		//计算需要的钻石和优惠券
		int gold = 0;
		int coupon = 0;
		int unitCost = golbalBean.getQ_int_value() / count;
		int itemNum = ManagerPool.backpackManager.getItemNum(player, GOLDRAFFLE_COUPON_ITEM_ID);
		int couponCost = itemNum * unitCost;
		if(couponCost >= golbalBean.getQ_int_value()) {
			coupon = count;
			
			//扣除优惠券
			if(ManagerPool.backpackManager.removeItem(player, GOLDRAFFLE_COUPON_ITEM_ID, coupon, Reasons.GOLDRAFFLE_DEL_ITEM, Config.getId()) == false) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("扣除优惠券出错"));
				return;
			}
		} else {
			coupon = itemNum;
			gold = golbalBean.getQ_int_value() - couponCost;
			
			//扣除优惠券
			if(coupon > 0 && ManagerPool.backpackManager.removeItem(player, GOLDRAFFLE_COUPON_ITEM_ID, coupon, Reasons.GOLDRAFFLE_DEL_ITEM, Config.getId()) == false) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("扣除优惠券出错"));
				return;
			}
			
			//扣除钻石
			int playerGold = player.getGold() == null ? 0 : player.getGold().getGold();
			if(gold <= 0 || playerGold < gold) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("钻石数量不足"));
				return;
			}
			
			if(ManagerPool.backpackManager.changeGold(player, -gold, Reasons.GOLDRAFFLE_DEL_GOLD, Config.getId()) == false) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("扣除钻石失败"));	
				return;
			}
		}
		
		GoldRaffleBoxData goldRaffleBoxData = goldRaffleMap.get(player.getId());
		if(goldRaffleBoxData == null) {
			goldRaffleBoxData = new GoldRaffleBoxData();
			goldRaffleMap.put(player.getId(), goldRaffleBoxData);
		}
		
		//随机抽取物品
		List<Integer> probabilityList = new ArrayList<Integer>();
		List<Q_lotteryBean> dataList =  new ArrayList<Q_lotteryBean>();
		for(Q_lotteryBean q_lotteryBean : q_lotteryBeanList) {
			if(q_lotteryBean == null || (q_lotteryBean.getQ_dlottery_job() != 0 && q_lotteryBean.getQ_dlottery_job() != player.getJob())) {
				continue; 
			}

			if(goldRaffleBoxData.getOpennum() == 0 && q_lotteryBean.getQ_dlottery_item_special_probability() > 0) { //第一次抽奖
				probabilityList.add(q_lotteryBean.getQ_dlottery_item_special_probability());
				dataList.add(q_lotteryBean);
			} else if(q_lotteryBean.getQ_dlottery_item_probability() > 0){ //非第一次抽奖
				probabilityList.add(q_lotteryBean.getQ_dlottery_item_probability());
				dataList.add(q_lotteryBean);
			}
		}
		
		int fraction = 0;
		int minFraction = 1;
		int maxFraction = 3;
		Q_globalBean minFractionGlobalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.GOLD_RAFFLE_MIN_FRACTION.getValue());
		if(minFractionGlobalBean != null) {
			minFraction = minFractionGlobalBean.getQ_int_value();
		}
		Q_globalBean maxFractionGlobalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.GOLD_RAFFLE_MAX_FRACTION.getValue());
		if(maxFractionGlobalBean != null) {
			maxFraction = maxFractionGlobalBean.getQ_int_value();
		}
		
		List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
		List<GoldRaffleLog> goldRaffleLogList = new ArrayList<GoldRaffleLog>();
		List<GoldRaffleGridData> goldRaffleGridDataList = new ArrayList<GoldRaffleGridData>();
		List<GoldRaffleEventData> goldRaffleEventList = new ArrayList<GoldRaffleEventData>();
		for(int i = 0; i < count; i++) {
			int index = RandomUtils.randomIndexByProb(probabilityList);
			if(index < 0 || index >= dataList.size()) continue;
			
			Q_lotteryBean q_lotteryBean = dataList.get(index);
			if(q_lotteryBean == null) continue;
			
			Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(q_lotteryBean.getQ_dlottery_item_id());
			if(q_itemBean == null) continue;
			
			String zhuoyue = "";
			int gradeNum = 0, addAttributeLevel = 0;
			if(q_lotteryBean.getQ_dlottery_item_addition() != null && !q_lotteryBean.getQ_dlottery_item_addition().trim().equals("")) {
				String[] split = q_lotteryBean.getQ_dlottery_item_addition().trim().split(Symbol.XIAHUAXIAN_REG);
				if(split.length == 1) {
					gradeNum = Integer.parseInt(split[0]);
				} else if(split.length == 2) {
					gradeNum = Integer.parseInt(split[0]);
					addAttributeLevel = Integer.parseInt(split[1]);
				} else if(split.length == 3) {
					gradeNum = Integer.parseInt(split[0]);
					addAttributeLevel = Integer.parseInt(split[1]);
					zhuoyue = split[2];
				}
			}
			
			boolean isBind = false;
			if(i + 1 <= coupon) isBind = true;
			List<Item> createItems = Item.createItems(q_lotteryBean.getQ_dlottery_item_id(), 1, isBind, 0, gradeNum, addAttributeLevel, zhuoyue);
			if(createItems == null || createItems.size() < 1) continue;
			GoldRaffleGridData goldRaffleGridData = new GoldRaffleGridData();
			goldRaffleGridData.setGrade(q_lotteryBean.getQ_dlottery_item_value());
			Item item = createItems.get(0);
			item.setNum(q_lotteryBean.getQ_dlottery_item_amount());
			goldRaffleGridData.setItem(item);
			goldRaffleGridDataList.add(goldRaffleGridData);
			
			//记录钻石抽奖日志
			if(q_lotteryBean.getQ_dlottery_item_value() >= 1) {
				GoldRaffleEventData goldRaffleEventData = new GoldRaffleEventData();
				goldRaffleEventData.setEventId(Config.getId());
				goldRaffleEventData.setMessageType("钻石抽奖日志");
				goldRaffleEventData.setMessageTime((int) (System.currentTimeMillis() / 1000));
				ParseUtil parseUtil = new ParseUtil();
				String parseString = "{@}刚刚抽中了{@}";
				if(q_lotteryBean.getQ_dlottery_item_amount() > 1) {
					parseString = String.format("{@}刚刚抽中了{@}*%d", q_lotteryBean.getQ_dlottery_item_amount());
				}
				parseUtil.setValue(parseString, new PlayerParm(player.getId(), player.getName()), new ItemParm(item));
				goldRaffleEventData.setMessage(parseUtil.toString());
				goldRaffleEventDataList.add(goldRaffleEventData);
				goldRaffleEventList.add(goldRaffleEventData);
				if (goldRaffleEventDataList.size() > GOLDRAFFLE_EVENT_MAX_NUM) {
					GoldRaffleEventData goldRaffleEvent = goldRaffleEventDataList.remove(0);
					this.dealGoldRaffleEventData(goldRaffleEvent, 1);
				}
				this.dealGoldRaffleEventData(goldRaffleEventData, 0);
			}
			
			//钻石抽奖公告
			if(q_lotteryBean.getQ_dlottery_item_value() == 2) {
				//! changed by xuliang
				ItemInfo itemInfo = item.buildItemInfo();
				GoodsInfoRes goodsInfo = new GoodsInfoRes();
				goodsInfo.setItemInfo(itemInfo);
				goodsInfos.add(goodsInfo);
			}
			
			//获得随机积分
			fraction += RandomUtils.random(minFraction, maxFraction);
			
			//记录数据库日志
			GoldRaffleLog goldRaffleLog = new GoldRaffleLog();
			goldRaffleLog.setSid(player.getServerId());
			goldRaffleLog.setRoleid(player.getId());
			goldRaffleLog.setType(type);
			goldRaffleLog.setCoupon(coupon);
			goldRaffleLog.setGold(gold);
			goldRaffleLog.setFraction(count);
			goldRaffleLog.setOpennum(goldRaffleBoxData.getOpennum() + 1);
			goldRaffleLog.setGoldRaffleGridInfo(JSON.toJSONString(goldRaffleGridData));
			goldRaffleLogList.add(goldRaffleLog);
		}
		
		//钻石抽奖公告
		if(goodsInfos.size() > 0) {
			String content = "{@}刚刚抽中了";
			for(int i = 0; i < goodsInfos.size(); i ++) {
				content += "{$}";
			}
			content += " {@}";
			ParseUtil parseUtil = new ParseUtil();
			parseUtil.setValue(content, new PlayerParm(player.getId(), player.getName()),
										new ParseUtil.VipParm(VipManager.getInstance().getVIPLevel(player), GuideType.GOLDRAFFLE.getValue()));
			
			String notify = "";
			
			notify = MessageUtil.getNotifyType(Notifys.CUTOUT_ROLE.getValue(), Notifys.CHAT_SYSTEM.getValue());
			MessageUtil.notify_All_player(notify, parseUtil.toString(), goodsInfos, 0);
		}
		
		//如果超出钻石抽奖宝箱容量,物品通过邮件发送给玩家
		int size = goldRaffleBoxData.getGoldRaffleGridDataList().size();
		if(size + goldRaffleGridDataList.size() > GOLDRAFFLE_BOX_MAX_SIZE) {
			Collections.sort(goldRaffleGridDataList, new Comparator<GoldRaffleGridData>() {
				@Override
				public int compare(GoldRaffleGridData goldRaffleGridData1, GoldRaffleGridData goldRaffleGridData2) {
					if(goldRaffleGridData1 != null && goldRaffleGridData2 != null) {
						return goldRaffleGridData2.getGrade() - goldRaffleGridData1.getGrade();
					}
					return 0;
				}
			});
			
			String title = "钻石抽奖奖品";
			String content = TimeUtil.getNowStringDateForMail() + "，由于玩家抽奖时背包满了，抽奖物品以附件的形式发送";
			List<GoldRaffleGridData> subList = goldRaffleGridDataList.subList(GOLDRAFFLE_BOX_MAX_SIZE - size, goldRaffleGridDataList.size());
			goldRaffleGridDataList = goldRaffleGridDataList.subList(0, GOLDRAFFLE_BOX_MAX_SIZE - size);
			List<Item> itemList = new ArrayList<Item>();
			for(int i = 0; i < subList.size(); i ++) {
				GoldRaffleGridData goldRaffleGridData = subList.get(i);
				if(goldRaffleGridData == null || goldRaffleGridData.getItem() == null) continue; 
				
				if(i > 0 && i % 10 == 0) {
					ManagerPool.mailServerManager.sendSystemMail(player.getId(), player.getName(), title, content, (byte) 1, 0, itemList);
					itemList = new ArrayList<Item>();
				}
				itemList.add(goldRaffleGridData.getItem());
			}
			if(itemList.size() > 0) ManagerPool.mailServerManager.sendSystemMail(player.getId(), player.getName(), title, content, (byte) 1, 0, itemList);
		}
		
		int grididx = size;
		List<GoldRaffleGridInfo> goldRaffleGridInfoList = new ArrayList<GoldRaffleGridInfo>();
		for(GoldRaffleGridData goldRaffleGridData : goldRaffleGridDataList) {
			if(goldRaffleGridData == null || goldRaffleGridData.getItem() == null) continue;
			GoldRaffleGridInfo goldRaffleGridInfo = new GoldRaffleGridInfo();
			goldRaffleGridInfo.setGrididx(grididx);
			goldRaffleGridInfo.setIteminfo(goldRaffleGridData.getItem().buildItemInfo());
			goldRaffleGridInfoList.add(goldRaffleGridInfo);
			++ grididx;
		}
		
		//把物品放入钻石抽奖宝箱
		goldRaffleBoxData.getGoldRaffleGridDataList().addAll(goldRaffleGridDataList);
		//记录抽奖次数
		goldRaffleBoxData.setOpennum(goldRaffleBoxData.getOpennum() + count);
		//增加积分
		player.setFraction(player.getFraction() + fraction);
		//钻石寻宝调用
		ManagerPool.livenessManager.huntTreasure(player);
		
		//返回消息给客户端
		ResGoldRaffleToClientMessage sendMessage = new ResGoldRaffleToClientMessage();
		sendMessage.setCoupon(ManagerPool.backpackManager.getItemNum(player, GOLDRAFFLE_COUPON_ITEM_ID));
		sendMessage.setFraction(player.getFraction());
		GoldRaffleBoxInfo goldRaffleBoxInfo = new GoldRaffleBoxInfo();
		goldRaffleBoxInfo.setGoldRaffleGridList(goldRaffleGridInfoList);
		sendMessage.setGoldRaffleBoxInfo(goldRaffleBoxInfo);
		MessageUtil.tell_player_message(player, sendMessage);
		
		//广播钻石抽奖日志
		ResGoldRaffleEventInfoToClientMessage sendClientMessage = new ResGoldRaffleEventInfoToClientMessage();
		sendClientMessage.setGoldRaffleEventInfoList(this.getGoldRaffleEventInfo(goldRaffleEventList));
		MessageUtil.tell_player_message(player, sendClientMessage);
//		MessageUtil.tell_world_message(sendClientMessage);
		
		//保存和更新数据库
		this.saveGoldRaffleLog(goldRaffleLogList);
		this.saveGoldRaffle(player.getId(), goldRaffleBoxData);
	}
	
	/**
	 * 打开钻石抽奖宝箱
	 * @param player
	 */
	public void reqOpenGoldRaffleBoxInfo(Player player) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}
		
		ResOpenGoldRaffleBoxInfoToClientMessage sendMessage = new ResOpenGoldRaffleBoxInfoToClientMessage();
		GoldRaffleBoxData goldRaffleBoxData = goldRaffleMap.get(player.getId());
		if(goldRaffleBoxData == null) goldRaffleBoxData = new GoldRaffleBoxData();

		int grididx = 0;
		GoldRaffleBoxInfo goldRaffleBoxInfo = new GoldRaffleBoxInfo();
		List<GoldRaffleGridInfo> goldRaffleGridInfoList = new ArrayList<GoldRaffleGridInfo>();
		Iterator<GoldRaffleGridData> iterator = goldRaffleBoxData.getGoldRaffleGridDataList().iterator();
		while(iterator.hasNext()) {
			GoldRaffleGridData goldRaffleGridData = iterator.next();
			if(goldRaffleGridData == null || goldRaffleGridData.getItem() == null) {
				iterator.remove();
				continue;
			}
			
			GoldRaffleGridInfo goldRaffleGridInfo = new GoldRaffleGridInfo();
			goldRaffleGridInfo.setGrididx(grididx);
			goldRaffleGridInfo.setIteminfo(goldRaffleGridData.getItem().buildItemInfo());
			goldRaffleGridInfoList.add(goldRaffleGridInfo);
			++ grididx;
		}
		
		goldRaffleBoxInfo.setGoldRaffleGridList(goldRaffleGridInfoList);
		sendMessage.setGoldRaffleBoxInfo(goldRaffleBoxInfo);
		MessageUtil.tell_player_message(player, sendMessage);
	}
	
	/**
	 * 使用钻石抽奖宝箱物品
	 * @param player
	 * @param grididx 格子编号,-1表示使用全部格子;>=0表示格子编号
	 * @param type 操作钻石抽奖宝箱物品的类型, 1表示使用物品或者装备;2表示把物品放入背包
	 */
	public void reqGoldRaffleBoxUseItem(Player player, int grididx, int type) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}
		
		GoldRaffleBoxData goldRaffleBoxData = goldRaffleMap.get(player.getId());
		if(goldRaffleBoxData == null || goldRaffleBoxData.getGoldRaffleGridDataList().size() < 1) {
			return;
		}
		
		List<GoldRaffleGridData> tempGoldRaffleGridDataList = new ArrayList<GoldRaffleGridData>();
		List<GoldRaffleGridData> goldRaffleGridDataList = goldRaffleBoxData.getGoldRaffleGridDataList();
		if(grididx == -1) {
			tempGoldRaffleGridDataList.addAll(goldRaffleGridDataList);
		} else if (grididx >= 0 && grididx < goldRaffleGridDataList.size()) {
			if(goldRaffleGridDataList.get(grididx) != null) {
				tempGoldRaffleGridDataList.add(goldRaffleGridDataList.get(grididx));
			}
		}
		
		if(tempGoldRaffleGridDataList.size() < 1) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("寻宝仓库物品不存在"));
			return;
		}
		
		List<GoldRaffleGridData> removeList = new ArrayList<GoldRaffleGridData>();
		if(grididx != -1 && type == 1) { //使用物品或者装备
			GoldRaffleGridData goldRaffleGridData = tempGoldRaffleGridDataList.get(0);
			Item item = goldRaffleGridData.getItem();
			Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(item.getItemModelId());
			if (item == null || q_itemBean == null) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品配置信息错误"));
				return;
			}
			
			if(ManagerPool.backpackManager.checkItem(player, item, 1) == false) {
				return;
			}

			Item cloneItem = null;
			try {
				cloneItem = item.clone();
				cloneItem.setNum(1);
			} catch (CloneNotSupportedException e) {
				log.error(e, e);
				return;
			}
			
			int emptyGridNum = ManagerPool.backpackManager.getEmptyGridNum(player);
			if (item instanceof Equip) { //使用装备
				if(q_itemBean.getQ_kind() - 1 < player.getEquips().length) {
					Equip equip = player.getEquips()[q_itemBean.getQ_kind() - 1];
					if(equip != null) { //如果原来位置有装备，先卸下装备
						if(emptyGridNum <= 0) {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("对不起，您的背包空格不足，请清理后再进行本操作"));
							return;
						}
						ManagerPool.equipManager.unwear(player, equip.getId());
					}
				}
				
				emptyGridNum = ManagerPool.backpackManager.getEmptyGridNum(player);
				if(emptyGridNum <= 0) player.setBagCellsNum(player.getBagCellsNum() + 1);
				if(ManagerPool.backpackManager.addItem(player, cloneItem, Reasons.GOLDRAFFLE_GET_ITEM, Config.getId()) == false) {
					return;
				}
				ManagerPool.equipManager.wear(player, cloneItem.getId(), q_itemBean.getQ_kind());
			} else { //使用物品
				if(emptyGridNum <= 0) player.setBagCellsNum(player.getBagCellsNum() + 1);
				if(ManagerPool.backpackManager.addItem(player, cloneItem, Reasons.GOLDRAFFLE_GET_ITEM, Config.getId()) == false) {
					return;
				}
				Item temp = ManagerPool.backpackManager.getItemByCellId(player, cloneItem.getGridId());
				ManagerPool.backpackManager.useItem(player, temp.getId(), 1);
			}
			if(emptyGridNum <= 0) player.setBagCellsNum(player.getBagCellsNum() - 1);
			if(ManagerPool.backpackManager.removeItem(player, cloneItem.getId(), Reasons.GOLDRAFFLE_GET_ITEM, Config.getId()) == true) {
				return;
			}
			
			if(item.getNum() > 1) {
				item.setNum(item.getNum() - 1);
			} else {
				removeList.add(goldRaffleGridData);
			}
		} else if(grididx == -1 || type == 2) { //把物品放入背包
			Collections.sort(tempGoldRaffleGridDataList, new Comparator<GoldRaffleGridData>() {
				@Override
				public int compare(GoldRaffleGridData goldRaffleGridData1, GoldRaffleGridData goldRaffleGridData2) {
					if(goldRaffleGridData1 != null && goldRaffleGridData2 != null) {
						return goldRaffleGridData2.getGrade() - goldRaffleGridData1.getGrade();
					}
					return 0;
				}
			});
			
			//添加到背包
			boolean isAddItem = false;
			for(GoldRaffleGridData goldRaffleGridData : tempGoldRaffleGridDataList) {
				if(goldRaffleGridData == null || goldRaffleGridData.getItem() == null) {
					continue;
				}
				
				isAddItem = true;
				if (ManagerPool.backpackManager.hasAddSpace(player, goldRaffleGridData.getItem()) == false) {
					continue;
				}
				
				if(ManagerPool.backpackManager.addItem(player, goldRaffleGridData.getItem(), Reasons.GOLDRAFFLE_GET_ITEM, Config.getId()) == true) {
					removeList.add(goldRaffleGridData);
				}
			}
			
			if(isAddItem == true && removeList.size() < 1) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("背包已满，请清理背包"));
				return;
			}
		} else {
			log.error("reqGoldRaffleBoxUseItem type error! type = " + type);
			return;
		}
		
		//删除钻石抽奖宝箱物品
		for(GoldRaffleGridData goldRaffleGridData : removeList) {
			if(goldRaffleGridData == null) continue;
			goldRaffleGridData.setItem(null);
		}
		
		//返回消息给客户端
		ResGoldRaffleBoxUseItemToClientMessage sendMessage = new ResGoldRaffleBoxUseItemToClientMessage();
		sendMessage.setGrididx(grididx);
		sendMessage.setType(type);
		sendMessage.setIsSuccess(1);
		MessageUtil.tell_player_message(player, sendMessage);
		if(grididx == -1) reqOpenGoldRaffleBoxInfo(player);
		
		//保存和更新数据库
		this.saveGoldRaffle(player.getId(), goldRaffleBoxData);
	}
	
	/**
	 * 整理钻石抽奖宝箱
	 * 
	 * @param player
	 */
	public void reqGoldRaffleBoxArrange(Player player) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}
		
		GoldRaffleBoxData goldRaffleBoxData = goldRaffleMap.get(player.getId());
		if(goldRaffleBoxData == null || goldRaffleBoxData.getGoldRaffleGridDataList().size() < 1) {
			return;
		}
		
		//检查是否在冷却中
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.GOLD_RAFFLE_CLEAR, null)) {
			long cooldownTime = ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.GOLD_RAFFLE_CLEAR, null);
			MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("{1}秒之后才可以整理"), String.valueOf(cooldownTime / 1000l));
			return;
		}
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.GOLD_RAFFLE_CLEAR, null, Global.CLEARUP_TIME_INTERVAL * 1000);

		List<GoldRaffleGridData> goldRaffleGridDataList = goldRaffleBoxData.getGoldRaffleGridDataList();
		List<GoldRaffleGridData> list = new ArrayList<GoldRaffleGridData>(goldRaffleGridDataList);
		for (int i = 0; i < list.size(); i ++) {
			GoldRaffleGridData goldRaffleGridData1 = list.get(i);
			if(goldRaffleGridData1 == null || goldRaffleGridData1.getItem() == null) {
				continue;
			}
			
			Item item1 = goldRaffleGridData1.getItem();
			Q_itemBean itemModel = ManagerPool.dataManager.q_itemContainer.getMap().get(item1.getItemModelId());
			if(itemModel == null) {
				log.error("整理背包时发现异常物品：" + item1.getItemModelId());
				return;
			}

			//如果第一个物品是满的那么直接略过
			if (itemModel.getQ_max() > 1 && itemModel.getQ_max() > item1.getNum()) {
				for (int j = i + 1; j < list.size(); j ++) {
					GoldRaffleGridData goldRaffleGridData2 = list.get(j);
					if(goldRaffleGridData2 == null || goldRaffleGridData2.getItem() == null) {
						continue;
					}

					Item item2 = goldRaffleGridData2.getItem();
					if (ManagerPool.backpackManager.isMergeAble(item1, item2)) {
						// 合并
						if (item1.getNum() + item2.getNum() <= itemModel.getQ_max()) {
							item1.setNum(item1.getNum() + item2.getNum());
							// 移除原来物品,这里直接移除的话会倒致下标溢出
							item2.setNum(0);
						} else {
							int all = item1.getNum() + item2.getNum();
							item1.setNum(itemModel.getQ_max());
							item2.setNum(all - itemModel.getQ_max());
						}
						if (item1.getNum() >= itemModel.getQ_max()) {
							break;
						}
					}
				}
			}
		}
		
		//清除数量为零的
		List<GoldRaffleGridData> sortList = new ArrayList<GoldRaffleGridData>();
		for(GoldRaffleGridData goldRaffleGridData : list) {
			if (goldRaffleGridData == null || goldRaffleGridData.getItem() == null || goldRaffleGridData.getItem().getNum() == 0) {
				continue;
			}
			sortList.add(goldRaffleGridData);
		}
		
		//排序
		Collections.sort(sortList);
		goldRaffleGridDataList.clear();
		goldRaffleGridDataList.addAll(sortList);
		
		//返回消息给客户端
		this.reqOpenGoldRaffleBoxInfo(player);
		
		//保存和更新数据库
		this.saveGoldRaffle(player.getId(), goldRaffleBoxData);
	}
	
	/**
	 * 积分兑换
	 * @param player
	 * @param fractionID 物品编号
	 */
	public void reqFraction(Player player, int fractionID) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}
		
		Q_fractionBean q_fractionBean = ManagerPool.dataManager.q_fractionContainer.getMap().get(fractionID);
		if(q_fractionBean == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("积分兑换配置信息出错"));
			return;			
		}
		
		//检查积分
		if(player.getFraction() < q_fractionBean.getQ_dfraction_type()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("对不起，您的积分不够"));	
			return;			
		}
		
		String zhuoyue = "";
		int gradeNum = 0, addAttributeLevel = 0;
		if(q_fractionBean.getQ_dfraction_item_addition() != null && !q_fractionBean.getQ_dfraction_item_addition().trim().equals("")) {
			String[] split = q_fractionBean.getQ_dfraction_item_addition().trim().split(Symbol.XIAHUAXIAN_REG);
			if(split.length == 1) {
				gradeNum = Integer.parseInt(split[0]);
			} else if(split.length == 2) {
				gradeNum = Integer.parseInt(split[0]);
				addAttributeLevel = Integer.parseInt(split[1]);
			} else if(split.length == 3) {
				gradeNum = Integer.parseInt(split[0]);
				addAttributeLevel = Integer.parseInt(split[1]);
				zhuoyue = split[2];
			}
		}
		
		//检查背包容量
		List<Item> createItems = Item.createItems(q_fractionBean.getQ_dfraction_item_id(), q_fractionBean.getQ_dfraction_item_amount(), 
				false, 0, gradeNum, addAttributeLevel, zhuoyue);
		if(ManagerPool.backpackManager.hasAddSpace(player, createItems) == false) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("背包已满，请清理背包"));
			return;
		}
		
		//添加物品到背包
		if(ManagerPool.backpackManager.addItems(player, createItems, Reasons.FRACTION, Config.getId()) == false) {
			return;
		}
		
		//扣除兑换积分
		player.setFraction(player.getFraction() - q_fractionBean.getQ_dfraction_type());

		//记录数据库日志
		FractionLog fractionLog = new FractionLog();
		fractionLog.setSid(player.getServerId());
		fractionLog.setRoleid(player.getId());
		fractionLog.setFraction(q_fractionBean.getQ_dfraction_type());
		fractionLog.setItemInfo(JSON.toJSONString(createItems));
		try {
			LogService.getInstance().execute(fractionLog);
		} catch (Exception e) {
			log.error(e, e);
		}
		
		//返回消息给客户端
		ResFractionToClientMessage sendMessage = new ResFractionToClientMessage();
		sendMessage.setFraction(player.getFraction());
		sendMessage.setItemID(q_fractionBean.getQ_dfraction_item_id());
		MessageUtil.tell_player_message(player, sendMessage);
	}
	
	/**
	 * 玩家钻石抽奖日志数据格式转换
	 * @param goldRaffleEventList
	 * @return
	 */
	private List<GoldRaffleEventInfo> getGoldRaffleEventInfo(List<GoldRaffleEventData> goldRaffleEventList) {
		List<GoldRaffleEventInfo> goldRaffleEventInfoList = new ArrayList<GoldRaffleEventInfo>();
		if(goldRaffleEventList != null && goldRaffleEventList.size() > 0) {
			for(GoldRaffleEventData goldRaffleEventData : goldRaffleEventList) {
				if(goldRaffleEventData == null) continue;
				GoldRaffleEventInfo goldRaffleEventInfo = new GoldRaffleEventInfo();
				goldRaffleEventInfo.setEventId(goldRaffleEventData.getEventId());
				goldRaffleEventInfo.setMessageTime(goldRaffleEventData.getMessageTime());
				goldRaffleEventInfo.setMessageType(goldRaffleEventData.getMessageType());
				goldRaffleEventInfo.setMessage(goldRaffleEventData.getMessage());
				goldRaffleEventInfoList.add(goldRaffleEventInfo);
			}
		}
		return goldRaffleEventInfoList;
	}
	
	/**
	 * 保存钻石抽奖数据
	 * @param playerId 玩家id
	 * @param goldRaffleData 钻石抽奖数据
	 * @return
	 */
	public void saveGoldRaffle(long playerId, GoldRaffleBoxData goldRaffleBoxData) {
		if(goldRaffleBoxData == null) return;
		try {
			GoldRaffleData goldRaffleData = new GoldRaffleData();
			goldRaffleData.setPlayerId(playerId);
			goldRaffleData.setData(VersionUpdateUtil.dateSave(JSONserializable.toString(goldRaffleBoxData)));
			WServer.getInstance().getwSaveGoldRaffleThread().addGoldRaffleData(goldRaffleData);
			/*xiaozhuoming: 暂时屏蔽
			this.goldRaffleDao.save(goldRaffleData);*/
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 加载玩家钻石抽奖物品
	 * @param playerId
	 */
	public void loadGoldRaffleListByPlayerID(long playerId) {
		try {
			if(goldRaffleMap.get(playerId) == null) {
				GoldRaffleBoxData goldRaffleBoxData = new GoldRaffleBoxData();
				GoldRaffleData goldRaffleData = this.goldRaffleDao.load(playerId);
				if(goldRaffleData != null) {
					goldRaffleBoxData = (GoldRaffleBoxData) JSONserializable.
							toObject(VersionUpdateUtil.dateLoad(goldRaffleData.getData()), GoldRaffleBoxData.class);
				}
				goldRaffleMap.put(playerId, goldRaffleBoxData);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 处理除玩家钻石抽奖事件
	 * @param goldRaffleEventData
	 * @param deal 0-insert 1-delete
	 */
	public void dealGoldRaffleEventData(GoldRaffleEventData goldRaffleEventData, int deal) {
		if(goldRaffleEventData == null) return;
		try {
			WServer.getInstance().getwSaveGoldRaffleEventThread().dealGoldRaffleEventData(goldRaffleEventData, deal);
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 保存玩家钻石抽奖事件
	 * @param goldRaffleEventDataList
	 */
	public void saveGoldRaffleEvent(List<GoldRaffleEventData> goldRaffleEventDataList) {
		if(goldRaffleEventDataList == null || goldRaffleEventDataList.size() < 1) return;
		try {
			for(GoldRaffleEventData goldRaffleEventData : goldRaffleEventDataList) {
				if(goldRaffleEventData == null) continue;
				this.goldRaffleEventDao.insert(goldRaffleEventData);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 加载玩家钻石抽奖事件列表
	 */
	public void loadGoldRaffleEventList() {
		try {
			goldRaffleEventDataList = this.goldRaffleEventDao.select();
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 删除玩家钻石抽奖事件
	 * @param eventId
	 */
	public void deleteGoldRaffleEvent(long eventId) {
		try {
			this.goldRaffleEventDao.delete(eventId);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 保存玩家钻石抽奖日志
	 * @param goldRaffleLogList
	 */
	public void saveGoldRaffleLog(List<GoldRaffleLog> goldRaffleLogList) {
		if(goldRaffleLogList == null || goldRaffleLogList.size() < 1) return;
		for(GoldRaffleLog goldRaffleLog : goldRaffleLogList) {
			if(goldRaffleLog == null) continue;
			try {
				LogService.getInstance().execute(goldRaffleLog);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

}
