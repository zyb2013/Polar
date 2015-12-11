package com.game.casting.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.backpack.bean.ItemInfo;
import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.EffectType;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.backpack.structs.ItemTypeConst;
import com.game.casting.bean.CastingBoxInfo;
import com.game.casting.bean.CastingGridInfo;
import com.game.casting.log.CastingExchangeLog;
import com.game.casting.log.CastingRewardLog;
import com.game.casting.log.CastingUseItemLog;
import com.game.casting.message.ResCastingDecomposeToClientMessage;
import com.game.casting.message.ResCastingExchangeToClientMessage;
import com.game.casting.message.ResCastingOpenToClientMessage;
import com.game.casting.message.ResCastingRewardToClientMessage;
import com.game.casting.message.ResCastingSellToClientMessage;
import com.game.casting.message.ResCastingUseItemToClientMessage;
import com.game.casting.structs.CastingBoxData;
import com.game.casting.structs.CastingGridData;
import com.game.chat.bean.GoodsInfoRes;
import com.game.config.Config;
import com.game.data.bean.Q_casting_add_attributeBean;
import com.game.data.bean.Q_casting_costBean;
import com.game.data.bean.Q_casting_exchangeBean;
import com.game.data.bean.Q_casting_rewardBean;
import com.game.data.bean.Q_casting_strengthBean;
import com.game.data.bean.Q_itemBean;
import com.game.dblog.LogService;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.Global;
import com.game.utils.MessageUtil;
import com.game.utils.ParseUtil;
import com.game.utils.ParseUtil.PlayerParm;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;

/**
 * 铸造工厂管理类
 *
 * @author xiaozhuoming
 * 
 * @since 2014-02-18
 * 
 */
public class CastingManager {

	private Logger log = Logger.getLogger(CastingManager.class);
	
	private static CastingManager manager;
	private static Object obj = new Object();

	private CastingManager() {
	}

	public static CastingManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new CastingManager();
			}
		}
		return manager;
	}
	
	private static final int BUYBACK_ABLE = 1; //可回收
	private static final int CASTING_BOX_MAX_SIZE = 9; //铸造工厂奖励仓库的最大容量
	
	/**
	 * 打开铸造工厂界面
	 * @param player
	 */
	public void reqCastingOpen(Player player) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}

		ResCastingOpenToClientMessage sendMessage = new ResCastingOpenToClientMessage();
		sendMessage.setTechnologyPoint(player.getTechnologyPoint());
		CastingBoxInfo castingBoxInfo = new CastingBoxInfo();
		CastingBoxData castingBoxData = player.getCastingBoxData();
		List<CastingGridInfo> castingGridInfoList = new ArrayList<CastingGridInfo>();
		for(int i = 0; i < castingBoxData.getCastingGridDataList().size(); i ++) {
			CastingGridData castingGridData = castingBoxData.getCastingGridDataList().get(i);
			if(castingGridData == null || castingGridData.getItem() == null) continue;
			CastingGridInfo castingGridInfo = new CastingGridInfo();
			castingGridInfo.setGrididx(i);
			castingGridData.setGrididx(i);
			castingGridInfo.setIteminfo(castingGridData.getItem().buildItemInfo());
			castingGridInfoList.add(castingGridInfo);
		}
		castingBoxInfo.setCastingGridInfoList(castingGridInfoList);
		sendMessage.setCastingBoxInfo(castingBoxInfo);
		MessageUtil.tell_player_message(player, sendMessage);
	}
	
	/**
	 * 铸造和一键锻造
	 * @param player
	 */
	public void reqCastingReward(Player player) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}
		
		List<Q_casting_costBean> q_casting_costBeanList = ManagerPool.dataManager.q_casting_costContainer.getList(); 
		if(q_casting_costBeanList == null || q_casting_costBeanList.size() < 1) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造工厂配置信息出错"));
			return;
		}
		
		Q_casting_costBean q_casting_cost = null;
		for(Q_casting_costBean q_casting_costBean : q_casting_costBeanList) {
			if(q_casting_costBean != null && player.getLevel() >= q_casting_costBean.getQ_min_level() && 
			   player.getLevel() <= q_casting_costBean.getQ_max_level()) {
				q_casting_cost = q_casting_costBean;
				break; 
			}
		}
		
		if(q_casting_cost == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("等级不足，无法铸造"));
			return;
		}
		
		//检查金币数量
		if(player.getMoney() < q_casting_cost.getQ_cost_coin()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的金币不足，无法铸造"));
			return;
		}
		
		List<Q_casting_rewardBean> q_casting_rewardBeanList = ManagerPool.dataManager.q_casting_rewardContainer.getList();
		if(q_casting_rewardBeanList == null || q_casting_rewardBeanList.size() < 1) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造工厂配置信息出错"));
			return;
		}
		
		//根据配置的等级和职业获得概率
		List<Integer> probabilityList = new ArrayList<Integer>();
		List<Q_casting_rewardBean> q_casting_rewardList = new ArrayList<Q_casting_rewardBean>();
		for(Q_casting_rewardBean q_casting_rewardBean : q_casting_rewardBeanList) {
			if(q_casting_rewardBean == null) {
				continue;  
			}
			
			if(player.getLevel() < q_casting_rewardBean.getQ_min_level() || player.getLevel() > q_casting_rewardBean.getQ_max_level()) {
				continue;
			}
			
			if(q_casting_rewardBean.getQ_job() == null || q_casting_rewardBean.getQ_job().trim().equals("")) {
				continue;
			}
			
			String[] strs = q_casting_rewardBean.getQ_job().trim().split(Symbol.SHUXIAN_REG);
			for(String value : strs) {
				if(value.equals("0") || Byte.valueOf(value) == player.getJob()) {
					q_casting_rewardList.add(q_casting_rewardBean);
					probabilityList.add(q_casting_rewardBean.getQ_weight());
				}
			}
		}
		
		if(q_casting_rewardList.size() < 1) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造工厂配置信息出错"));
			return;
		}
		
		//根据总概率random一个随机概率
		int index = RandomUtils.randomIndexByProb(probabilityList);
		if(index < 0 || index >= q_casting_rewardList.size()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造出错"));
			return;
		}
		
		Q_casting_rewardBean q_casting_rewardBean = q_casting_rewardList.get(index);
		if(q_casting_rewardBean == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造工厂配置信息出错"));
			return;
		}
		
		Q_itemBean q_itemBean = ManagerPool.dataManager.q_itemContainer.getMap().get(q_casting_rewardBean.getQ_item_id());
		if(q_itemBean == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造工厂配置信息出错"));
			return;
		}
		
		int gradeNum = 0, addAttributeLevel = 0;
		if(q_itemBean.getQ_type() == ItemTypeConst.EQUIP) {
			//根据配置表随机生成装备的强化等级
			List<Q_casting_strengthBean> q_casting_strengthBeanList = ManagerPool.dataManager.q_casting_strengthContainer.getList();
			if(q_casting_strengthBeanList != null && q_casting_strengthBeanList.size() > 0) {
				List<Integer> probabilityList1 = new ArrayList<Integer>();
				List<Q_casting_strengthBean> q_casting_strengthList = new ArrayList<Q_casting_strengthBean>();
				for(Q_casting_strengthBean q_casting_strengthBean : q_casting_strengthBeanList) {
					if(q_casting_strengthBean == null) {
						continue;
					}
					
					probabilityList1.add(q_casting_strengthBean.getQ_weight());
					q_casting_strengthList.add(q_casting_strengthBean);
				}
				
				index = RandomUtils.randomIndexByProb(probabilityList1);
				if(index >= 0 && index < q_casting_strengthList.size()) {
					gradeNum = q_casting_strengthList.get(index).getQ_strength_level();
				}
			}
			
			//根据配置表随机生成装备的追加等级
			List<Q_casting_add_attributeBean> q_casting_add_attributeBeanList = ManagerPool.dataManager.q_casting_add_attributeContainer.getList();
			if(q_casting_add_attributeBeanList != null && q_casting_add_attributeBeanList.size() > 0) {
				List<Integer> probabilityList2 = new ArrayList<Integer>();
				List<Q_casting_add_attributeBean> q_casting_add_attributeList = new ArrayList<Q_casting_add_attributeBean>();
				for(Q_casting_add_attributeBean q_casting_add_attributeBean : q_casting_add_attributeBeanList) {
					if(q_casting_add_attributeBean == null) {
						continue;
					}
					
					probabilityList2.add(q_casting_add_attributeBean.getQ_weight());
					q_casting_add_attributeList.add(q_casting_add_attributeBean);
				}
				
				index = RandomUtils.randomIndexByProb(probabilityList2);
				if(index >= 0 && index < q_casting_add_attributeList.size()) {
					addAttributeLevel = q_casting_add_attributeList.get(index).getQ_add_attribute_level();
				}
			}
		}
		
		List<Item> createItems = Item.createItems(q_casting_rewardBean.getQ_item_id(), 1, false, 0, gradeNum, addAttributeLevel, "");
		if(createItems == null || createItems.size() < 1) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造出错"));
			return;
		}
		
		Item item = createItems.get(0);
		if(hasAddSpace(player, item) == false) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造工厂奖励仓库已满"));
			return;
		}
		
		//扣除金币
		if (ManagerPool.backpackManager.changeMoney(player, -q_casting_cost.getQ_cost_coin(), Reasons.CASTING_REWARD, Config.getId()) == false) {
			return;
		}
		
		//添加到铸造工厂奖励仓库
		CastingGridData castingGridData = this.addItem(player, item);
		if(castingGridData == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品加入铸造工厂奖励仓库出错"));
			return;
		}
			
		//珍稀物品公告
		if(item instanceof Equip && (gradeNum > 9 || addAttributeLevel > 3)) {
			//! changed by xuliang 
			ItemInfo itemInfo = item.buildItemInfo();
			List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
			GoodsInfoRes goodsInfo = new GoodsInfoRes();
			goodsInfo.setItemInfo(itemInfo);
			goodsInfos.add(goodsInfo);
			
			ParseUtil parseUtil = new ParseUtil();
			String parseString = ResManager.getInstance().getString("{@}人品大爆发，在铸造工厂铸造出一件{$}");
			parseUtil.setValue(parseString, new PlayerParm(player.getId(), player.getName()));
			MessageUtil.notify_All_player(Notifys.CUTOUT_ROLE, parseUtil.toString(), goodsInfos, 0);
			MessageUtil.notify_All_player(Notifys.CHAT_BULL, parseUtil.toString(), goodsInfos, 0);
		}
		
		//记录数据库日志
		CastingRewardLog castingRewardLog = new CastingRewardLog();
		castingRewardLog.setSid(player.getServerId());
		castingRewardLog.setRoleid(player.getId());
		castingRewardLog.setLevel(player.getLevel());
		castingRewardLog.setJob(player.getJob());
		castingRewardLog.setCost(q_casting_cost.getQ_cost_coin());
		castingRewardLog.setCastingGridInfo(JSON.toJSONString(castingGridData));
		try {
			LogService.getInstance().execute(castingRewardLog);
		} catch (Exception e) {
			log.error(e, e);
		}
		
		//返回客户端消息
		ResCastingRewardToClientMessage sendMessage = new ResCastingRewardToClientMessage();
		CastingGridInfo castingGridInfo = new CastingGridInfo();
		castingGridInfo.setGrididx(castingGridData.getGrididx());
		castingGridInfo.setIteminfo(castingGridData.getItem().buildItemInfo());
		sendMessage.setCastingGridInfo(castingGridInfo);
		MessageUtil.tell_player_message(player, sendMessage);
		
		ManagerPool.livenessManager.expenseZZGC(player);
	}
	
	/**
	 * 使用铸造奖励仓库物品
	 * 
	 * @param player
	 * @param grididx 格子编号,>=0表示格子编号
	 * @param type 操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
	 */
	public void reqCastingUseItem(Player player, int grididx, int type) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}
		
		int ret = 0;
		switch (type) {
		case 1:
			ret = addCastingItemToBackpack(player, grididx);
			break;
		case 2:
			ret = reqCastingSell(player, grididx);
			break;
		case 3:
			/*xiaozhuoming: 暂时屏蔽
			ret = reqCastingDecompose(player, grididx);*/
			break;
		default:
			log.error("reqCastingUseItem type error! type" + type);
			return;
		}
		
		//返回消息给客户端
		if(ret >= 0 && grididx != -1) {
			ResCastingUseItemToClientMessage sendMessage = new ResCastingUseItemToClientMessage();
			sendMessage.setType(type);
			sendMessage.setIsSuccess(1);
			sendMessage.setGrididx(grididx);
			sendMessage.setValue(ret);
			MessageUtil.tell_player_message(player, sendMessage);
		}
	}
	
	/**
	 * 添加物品到背包
	 * 
	 * @param player
	 * @param grididx
	 */
	private int addCastingItemToBackpack(Player player, int grididx) {
		if (player == null) {
			log.error("玩家不存在");
			return -1;
		}
		
		CastingGridData castingGridData = null;
		List<CastingGridData> castingGridDataList = player.getCastingBoxData().getCastingGridDataList();
		for(CastingGridData temp : castingGridDataList) {
			if(temp == null || temp.getItem() == null) {
				continue;
			}
			
			Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(temp.getItem().getItemModelId());
			if (model == null || model.getQ_type() == ItemTypeConst.TECHNOLOGY) {
				continue;
			}
			
			if(temp.getGrididx() == grididx){
				castingGridData = temp;
				break;
			}
		}
		
		if(castingGridData == null) {
			return -1;
		}
		
		//检查背包容量
		if(ManagerPool.backpackManager.hasAddSpace(player, castingGridData.getItem()) == false) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("背包已满，请清理背包"));
			return -1;
		}
		
		//添加物品到背包
		if(BackpackManager.getInstance().addItem(player, castingGridData.getItem(), Reasons.CASTING_ADD_BACKPACK, Config.getId()) == false) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("添加物品到背包失败"));
			return -1;
		}
		
		//记录数据库日志
		CastingUseItemLog castingUseItemLog = new CastingUseItemLog();
		castingUseItemLog.setSid(player.getServerId());
		castingUseItemLog.setRoleid(player.getId());
		castingUseItemLog.setType(1);
		castingUseItemLog.setCastingGridInfo(JSON.toJSONString(castingGridData));
		try {
			LogService.getInstance().execute(castingUseItemLog);
		} catch (Exception e) {
			log.error(e, e);
		}
		
		//删除奖励仓库物品
		castingGridDataList.remove(castingGridData);
		return 0;
	}
	
	/**
	 * 出售物品和一键出售物品
	 * 
	 * @param player
	 * @param grididx 格子编号,-1表示使用全部格子;>=0表示格子编号
	 * @return 0表示出售成功;-1表示出售失败
	 */
	public int reqCastingSell(Player player, int grididx) {
		if (player == null) {
			log.error("玩家不存在");
			return -1;
		}
		
		List<CastingGridData> castingGridDataList = new ArrayList<CastingGridData>();
		for(CastingGridData castingGridData : player.getCastingBoxData().getCastingGridDataList()) {
			if(castingGridData == null || castingGridData.getItem() == null) {
				continue;
			}
			
			Item item = castingGridData.getItem();
			Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(item.getItemModelId());
			if (model == null) {
				continue;
			}
			
			if (model.getQ_sell() != BUYBACK_ABLE) {
				log.error("出售失败，系统不回收该物品");
				continue;
			}
			
			if (item.isTrade()) {
				log.error("出售失败，物品正在交易中");
				continue;
			}
			
			/*xiaozhuoming: 暂时屏蔽
			if(item instanceof Equip) {
				if(((Equip) item).getGradeNum() >= 7 && ((Equip) item).getAddAttributeLevel() >= 1) {
					continue;
				}
			}else if(model.getQ_type() == ItemTypeConst.TECHNOLOGY) {
				continue;
			}*/
			
			if(grididx == -1 && model.getQ_type() == 1) {
				castingGridDataList.add(castingGridData);
			} else if(castingGridData.getGrididx() == grididx){
				castingGridDataList.add(castingGridData);
				break;
			}
		}
		
		List<Integer> grididxList = new ArrayList<Integer>();
		List<Integer> goldList = new ArrayList<Integer>();
		List<CastingGridData> removeList = new ArrayList<CastingGridData>();
		for(CastingGridData castingGridData : castingGridDataList) {
			if(castingGridData == null || castingGridData.getItem() == null) {
				continue;
			}
			
			Item item = castingGridData.getItem();
			Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(item.getItemModelId());
			if (model == null) {
				continue;
			}
			
			long action = Config.getId();
			int price = model.getQ_sell_price() * item.getNum();
			if (ManagerPool.backpackManager.changeMoney(player, price, Reasons.CASTING_SEll, action) == true) {
				goldList.add(price);
				grididxList.add(castingGridData.getGrididx());
				removeList.add(castingGridData);
				
				List<Item> buybackList = player.getBuybackList();
				buybackList.add(item);
				if (buybackList.size() > Global.BUYBACK_LIST_MAX) {
					buybackList.remove(0);
				}
			} else {
				break;
			}
			
			//记录数据库日志
			CastingUseItemLog castingUseItemLog = new CastingUseItemLog();
			castingUseItemLog.setSid(player.getServerId());
			castingUseItemLog.setRoleid(player.getId());
			castingUseItemLog.setType(2);
			castingUseItemLog.setValue(price);
			castingUseItemLog.setCastingGridInfo(JSON.toJSONString(castingGridData));
			try {
				LogService.getInstance().execute(castingUseItemLog);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		if(removeList.size() < 1) {
			return -1;
		}
		
		//删除奖励仓库物品
		player.getCastingBoxData().getCastingGridDataList().removeAll(removeList);
		
		//一键出售物品返回客户端消息
		if(grididx == -1) {
			ResCastingSellToClientMessage sendMessage = new ResCastingSellToClientMessage();
			sendMessage.setGrididxList(grididxList);
			sendMessage.setGoldList(goldList);
			MessageUtil.tell_player_message(player, sendMessage);
			return 0;
		}
		return goldList.get(0);
	}
	
	/**
	 * 分解物品和一键分解物品
	 * 
	 * @param player
	 * @param grididx 格子编号,-1表示使用全部格子;>=0表示格子编号
	 * @return 0表示出售成功;-1表示出售失败
	 */
	public int reqCastingDecompose(Player player, int grididx) {
		if (player == null) {
			log.error("玩家不存在");
			return -1;
		}
		
		List<CastingGridData> castingGridDataList = new ArrayList<CastingGridData>();
		for(CastingGridData castingGridData : player.getCastingBoxData().getCastingGridDataList()) {
			if(castingGridData == null || castingGridData.getItem() == null) {
				continue;
			}
			
			Item item = castingGridData.getItem();
			Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(item.getItemModelId());
			if (model == null) {
				continue;
			}
			
			if(item instanceof Equip) {
				if(((Equip) item).getGradeNum() < 7 || ((Equip) item).getAddAttributeLevel() < 1) {
					continue;
				}
			} else if(model.getQ_type() != ItemTypeConst.TECHNOLOGY) {
				continue;
			}
			
			if(grididx == -1) {
				castingGridDataList.add(castingGridData);
			} else if(castingGridData.getGrididx() == grididx){
				castingGridDataList.add(castingGridData);
				break;
			}
		}
		
		List<Integer> grididxList = new ArrayList<Integer>();
		List<Integer> technologyPointList = new ArrayList<Integer>();
		List<CastingGridData> removeList = new ArrayList<CastingGridData>();
		for(CastingGridData castingGridData : castingGridDataList) {
			if(castingGridData == null || castingGridData.getItem() == null) {
				continue;
			}
			
			Item item = castingGridData.getItem();
			Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(item.getItemModelId());
			if (model == null) {
				continue;
			}

			int technologyPoint = 0;
			if(model.getQ_type() == ItemTypeConst.TECHNOLOGY) {
				technologyPoint = ManagerPool.backpackManager.getEffictValue(EffectType.ADDTECHNOLOGY, model);
			} else {
				//获得的工艺度为random(9,15)随机从9-15点中获得一个值,包括9和15
				technologyPoint = RandomUtils.random(9, 15);
			}
			player.setTechnologyPoint(player.getTechnologyPoint() + technologyPoint); 
			technologyPointList.add(technologyPoint);
			grididxList.add(castingGridData.getGrididx());
			removeList.add(castingGridData);
			
			MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, "获得{1}工艺度", technologyPoint + "");
			
			//记录数据库日志
			CastingUseItemLog castingUseItemLog = new CastingUseItemLog();
			castingUseItemLog.setSid(player.getServerId());
			castingUseItemLog.setRoleid(player.getId());
			castingUseItemLog.setType(3);
			castingUseItemLog.setValue(technologyPoint);
			castingUseItemLog.setCastingGridInfo(JSON.toJSONString(castingGridData));
			try {
				LogService.getInstance().execute(castingUseItemLog);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		if(removeList.size() < 1) {
			return -1;
		}
		
		//删除奖励仓库物品
		player.getCastingBoxData().getCastingGridDataList().removeAll(removeList);
		
		//一键分解物品返回客户端消息
		if(grididx == -1) {
			ResCastingDecomposeToClientMessage sendMessage = new ResCastingDecomposeToClientMessage();
			sendMessage.setGrididxList(grididxList);
			sendMessage.setTechnologyPointList(technologyPointList);
			sendMessage.setTechnologyPoint(player.getTechnologyPoint());
			MessageUtil.tell_player_message(player, sendMessage);
			return 0;
		}
		return technologyPointList.get(0);
	}
	
	/**
	 * 铸造兑换
	 * 
	 * @param player
	 * @param exchangeID 物品编号
	 */
	public void reqCastingExchange(Player player, int exchangeID) {
		if (player == null) {
			log.error("玩家不存在");
			return;
		}
		
		Q_casting_exchangeBean q_casting_exchangeBean = ManagerPool.dataManager.q_casting_exchangeContainer.getMap().get(exchangeID);
		if(q_casting_exchangeBean == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("铸造兑换信息出错"));
			return;
		}
		
		//检查玩家的等级
		if(player.getLevel() < q_casting_exchangeBean.getQ_character_level()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("对不起，您的等级不够"));	
			return;	
		}
		
		//检查玩家的工艺度
		if(player.getTechnologyPoint() < q_casting_exchangeBean.getQ_technology_point()) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("对不起，您的工艺度不够"));	
			return;
		}
		
		//检查背包容量
		List<Item> createItems = Item.createItems(q_casting_exchangeBean.getQ_item_id(), 1, true, 0, 0, 0, "");
		if(ManagerPool.backpackManager.hasAddSpace(player, createItems.get(0)) == false) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("背包已满，请清理背包"));
			return;
		}
		
		//添加物品到背包
		if(BackpackManager.getInstance().addItems(player, createItems, Reasons.CASTING_EXCHANGE, Config.getId()) == false) {
			return;
		}
		
		//扣除兑换工艺度
		player.setTechnologyPoint(player.getTechnologyPoint() - q_casting_exchangeBean.getQ_technology_point());
		
		//记录数据库日志
		CastingExchangeLog castingExchangeLog = new CastingExchangeLog();
		castingExchangeLog.setSid(player.getServerId());
		castingExchangeLog.setRoleid(player.getId());
		castingExchangeLog.setTechnologyPoint(q_casting_exchangeBean.getQ_technology_point());
		castingExchangeLog.setItemInfo(JSON.toJSONString(createItems));
		try {
			LogService.getInstance().execute(castingExchangeLog);
		} catch (Exception e) {
			log.error(e, e);
		}
		
		//返回消息给客户端
		ResCastingExchangeToClientMessage sendMessage = new ResCastingExchangeToClientMessage();
		sendMessage.setTechnologyPoint(player.getTechnologyPoint());
		sendMessage.setItemID(q_casting_exchangeBean.getQ_item_id());
		MessageUtil.tell_player_message(player, sendMessage);
	}
	
	/**
	 * 添加物品到铸造奖励仓库
	 * 
	 * @param player
	 * @param item
	 * @return
	 */
	private CastingGridData addItem(Player player, Item item) {
		if (player == null || item == null) {
			return null;
		}
		
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(item.getItemModelId());
		if (model == null) {
			return null;
		}
		
		//检查数量
		int num = item.getNum();
		if (num <= 0 || num > model.getQ_max()) {
			return null;
		}
		
		//检查是否有足够的空间添加物品
		if (hasAddSpace(player, item) == false) {
			return null;
		}
		
		// 用于记录关联操作
		if (model.getQ_bind() == 1) {
			item.setBind(true);
		}
		
		CastingGridData castingGridData = null;
		List<CastingGridData> castingGridDataList = player.getCastingBoxData().getCastingGridDataList();
		if (model.getQ_max() == 1) { //不可堆叠物品
			castingGridData = new CastingGridData();
			castingGridData.setItem(item);
			if(castingGridDataList.size() > 0) {
				castingGridData.setGrididx(castingGridDataList.get(castingGridDataList.size() - 1).getGrididx() + 1);
			}
			castingGridDataList.add(castingGridData);
		} else { //可堆叠物品
			int count = item.getNum();
			for (int i = 0; i < castingGridDataList.size() && count > 0; i ++) {
				CastingGridData tempCastingGridData = castingGridDataList.get(i);
				if(tempCastingGridData == null || tempCastingGridData.getItem() == null) {
					continue;
				}

				Item target = tempCastingGridData.getItem();
				if (target == null) {
					continue;
				}
				
				if (!ManagerPool.backpackManager.isMerage(item, target)) {
					continue;
				}
				
				if (target.getNum() >= model.getQ_max()) {
					continue;
				}
				
				int ablenum = model.getQ_max() - target.getNum();
				if (count + target.getNum() <= model.getQ_max()) {
					target.setNum(target.getNum() + count);
					count = 0;
				} else {
					count -= ablenum;
					target.setNum(model.getQ_max());
				}
				castingGridData = tempCastingGridData;
			}
			
			if (count > 0) {
				item.setNum(count);
				CastingGridData tempCastingGridData = new CastingGridData();
				tempCastingGridData.setItem(item);
				if(castingGridDataList.size() > 0) {
					tempCastingGridData.setGrididx(castingGridDataList.get(castingGridDataList.size() - 1).getGrididx() + 1);
				}
				castingGridDataList.add(tempCastingGridData);
				castingGridData = tempCastingGridData;
			}
		}

		if (item instanceof Equip) {
			Equip equip = (Equip) item;
			equip.setCanUse(true);
		}
		return castingGridData;
	}
	
	/**
	 * 检查铸造奖励仓库是否有足够的空间添加物品,只需要检查空格子数量
	 * 
	 * @param player
	 * @param item
	 * @return
	 */
	private boolean hasAddSpace(Player player, Item item) {
		if (player == null || item == null) {
			return false;
		}
		
		int emptyGridNum = CASTING_BOX_MAX_SIZE - player.getCastingBoxData().getCastingGridDataList().size();
		if (emptyGridNum > 0) {
			return true;
		} /*else { xiaozhuoming: 不需要检查叠加情况
			int count = 0;
			Q_itemBean q_itemBean = ManagerPool.dataManager.q_itemContainer.getMap().get(item.getItemModelId());
			if (q_itemBean == null) {
				return false;
			}
			
			List<CastingGridData> castingGridDataList = player.getCastingBoxData().getCastingGridDataList();
			for (CastingGridData castingGridData : castingGridDataList) {
				if(castingGridData == null || castingGridData.getItem() == null) {
					continue;
				}
				
				Item target = castingGridData.getItem();
				if (!ManagerPool.backpackManager.isMerage(item, target)) {
					continue;
				}
				
				count += q_itemBean.getQ_max() - target.getNum();
				if (count >= item.getNum()) {
					return true;
				}
			}
		}*/
		return false;
	}
	
	/**
	 * 根据物品ID获得铸造奖励仓库中的物品
	 * 
	 * @param player
	 * @param itemID
	 * @return
	 */
	public Item getCastingBoxItemByID(Player player, long itemID) {
		if(player == null) {
			return null;
		}
		
		List<CastingGridData> castingGridDataList = player.getCastingBoxData().getCastingGridDataList();
		for(CastingGridData castingGridData : castingGridDataList) {
			if(castingGridData == null || castingGridData.getItem() == null || castingGridData.getItem().getId() != itemID) {
				continue;
			}
			
			return castingGridData.getItem();
		}
		return null;
	}
	
	/**
	 * 修改玩家的工艺度的GM命令
	 * 
	 * @param player
	 * @param technologyPoint
	 */
	public void setPlayerTechnologyPoint(Player player, int technologyPoint) {
		if(player !=  null && technologyPoint > 0) {
			player.setTechnologyPoint(technologyPoint);
			ResCastingExchangeToClientMessage sendMessage = new ResCastingExchangeToClientMessage();
			sendMessage.setTechnologyPoint(player.getTechnologyPoint());
			MessageUtil.tell_player_message(player, sendMessage);
		}
	}
}
