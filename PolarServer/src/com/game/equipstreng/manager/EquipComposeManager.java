package com.game.equipstreng.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Attribute;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_equip_composeBean;
import com.game.data.bean.Q_equip_compose_appendBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.equipstreng.Log.EquipComposeLog;
import com.game.equipstreng.message.ReqComposeEquipToServerMessage;
import com.game.equipstreng.message.ResComposeItemToClientMessage;
import com.game.equipstreng.message.ResErrorInfoToClientMessage;
import com.game.equipstreng.message.bean.ComposeAddInfo;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Attributes;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;


/**
 * 装备合成
 * 
 */
public class EquipComposeManager {
	private static Object obj = new Object();

	private Logger logger = Logger.getLogger(EquipComposeManager.class);
	// 装备追加管理类实例
	private static EquipComposeManager manager;
	public static EquipComposeManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new EquipComposeManager();
			}
		}
		return manager;
	}
	private EquipComposeManager() {
	}

	public static boolean opengm = false;
	/**
	 * 获得合成物品的配制信息
	 * @param id
	 * @return
	 */
	public Q_equip_composeBean getEquipComposeConfigData(int id){
		return ManagerPool.dataManager.q_equip_composeContainer.getMap().get(id);
	}
	/**
	 * 获得物品的配制信息
	 * @param id
	 * @return
	 */
	public Q_itemBean getItemData(int id) {
		return ManagerPool.dataManager.q_itemContainer.getMap().get(id);
	}
	/**
	 * 合成处理
	 * @param player
	 * @param id
	 */
	public void equipCompose(Player player, ReqComposeEquipToServerMessage msg) {
		int id=msg.getCompose_id();
		Q_equip_composeBean composeEquip=getEquipComposeConfigData(id);
		if(composeEquip == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("不能合成这个物品！"));
			return;
		}
		String notSignItem=composeEquip.getUse_not_sign_item();
		// 需要消耗一个玩家指定的符合多个条件的装备
		if (!StringUtils.isBlank(notSignItem)) {
			if (!hasEquipByUnsignItems(player, notSignItem, msg.getEquip_id())) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("缺少合成装备材料。"));
				return;
			}
		}
		// 需要消耗一个系统指定的装备
		String use_not_sign_item_id = composeEquip.getUse_not_sign_item_id();
		if (!StringUtils.isBlank(use_not_sign_item_id)) {
			if (!hasEquipByUnsignEquipId(player, use_not_sign_item_id, msg.getEquip_id())) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("缺少合成装备材料。"));
				return;
			}
		}
		int moneyNeed = composeEquip.getMoney_need();
		if (player.getMoney() < moneyNeed) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，所需金币不足。"));
			stResErrorInfoToClient(player, (byte) 1,moneyNeed, null);
			return;
		}
		int diamond_need = composeEquip.getDiamond_need();
		// 优先绑定的
		if (diamond_need != 0 && (player.getBindGold() < diamond_need && (player.getGold() == null || player.getGold().getGold() < diamond_need))) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，所需钻石不足。"));
			stResErrorInfoToClient(player, (byte) 3, diamond_need, null);
			return;
		}
		if(BackpackManager.getInstance().getEmptyGridNum(player)<2) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("背包空间不够！"));
			return;
		}

		String useItems = composeEquip.getUse_item();
		MaterailResult materailResult = checkTakeMaterial(player, useItems, msg.getType() == 0);
		if (!materailResult.removeResult) {// 检测并收道具
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("缺少合成装备材料。"));
			return;
		}
		if (msg.getEquip_id() != 0) {
			if (!materailResult.hasBindItem) {
				Item itemById = BackpackManager.getInstance().getItemById(player, msg.getEquip_id());
				
				//add hongxiao.z
				if(itemById == null)
				{
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("缺少合成装备材料。"));
					return;
				}
				
				if (itemById.isBind()) {
					materailResult.hasBindItem = true;
				}
			}
//			if (!BackpackManager.getInstance().removeItem(player, msg.getEquip_id(), Reasons.COMPOSE_EQUIP, Config.getId())) {
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("缺少合成装备材料。"));
//				return;
//			}
		}

		if (ManagerPool.backpackManager.changeMoney(player, -moneyNeed,Reasons.COMPOSE_COIN, Config.getId())) {
			if (player.getBindGold() >= diamond_need) {
				boolean changeBindGold = ManagerPool.backpackManager.changeBindGold(player, -diamond_need, Reasons.COMPOSE_BINDGOLD, Config.getId());
				if (!changeBindGold) {
					logger.error("绑钻不够！");
					return;
				}
			} else if (player.getGold() != null && player.getGold().getGold() >= diamond_need) {
				boolean changeGold = ManagerPool.backpackManager.changeGold(player, -diamond_need, Reasons.COMPOSE_GOLD, Config.getId());
				if (!changeGold) {
					logger.error("钻石不够！");
					return;
				}
			} else {
				logger.error("绑钻和钻石都不够！");
				return;
			}
			boolean result = false;// 强化结果，1成功，0失败
			
			//add start hongxiao.z	
			//扣除物品
			List<ComposeAddInfo> materials = msg.getMaterials();
			
			materials.add(new ComposeAddInfo(msg.getEquip_id()));
			
			//概率的计算
			int per = perAdd(player, msg.getCompose_id(), msg.getMaterials(), composeEquip.getQ_Nonfinite_num());	//概率加成
			//add end
			
			//出错了
			if(per == -1) return;
			
			int endPer = composeEquip.getSuccess_percent() + per;
			//最大成功率
			if(endPer > composeEquip.getSuccess_percent_max()) endPer = composeEquip.getSuccess_percent_max();
			
			for (ComposeAddInfo composeAddInfo : materials) 
			{
				if(composeAddInfo == null) continue;
				
				BackpackManager.getInstance().removeItem(player, composeAddInfo.getEquip_id(), Reasons.COMPOSE_EQUIP, Config.getId());
			}
			
			if (!player.isEquipComposed() || opengm || RandomUtils.isGenerate2(10000, endPer)) { // 进入随机
				result = true;
				player.setEquipComposed(true);
			}
			EquipComposeLog log = new EquipComposeLog();
			if(result){
				Item composeNewEquip = composeNewEquip_2(player, id, materailResult.hasBindItem);
				MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("恭喜你合成物品成功！"));
				log.setItemid(composeNewEquip.getId());
				log.setModelid(composeNewEquip.getItemModelId());
			}else {
//				logger.error("[合成失败概率数据] --- 加成的概率[" + endPer + "] --- 目标合成道具[" + id + "] --- 玩家[" + player.getName() + "]");
				MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("非常遗憾！合成失败！"));
			}
			ResComposeItemToClientMessage response = new ResComposeItemToClientMessage();
			response.setResult((byte) (result ? 1 : 0));
			MessageUtil.tell_player_message(player, response);
			log.setComposeid(id);
			log.setPlayerid(player.getId());
			log.setResult(result?1:0);
			log.setMatrial(useItems+","+msg.getEquip_id());
			LogService.getInstance().execute(log);
		}
	}
	
	/**
	 * 额外加成概率材料计算
	 * @param composeId
	 * @param list
	 * @param maxSzie
	 * @return
	 * @create	hongxiao.z      2014-2-25 下午4:18:06
	 */
	private int perAdd(Player player, int composeId, List<ComposeAddInfo> list, int maxSzie)
	{
		Q_equip_compose_appendBean bean = DataManager.getInstance().q_equip_compose_appendContainer.get(composeId);
		
		//不能额外加成概率
		if(bean == null) return 0;
		
		//空材料
		if(list.isEmpty()) return 0;
		
		//超出材料数量限制
		if(list.size() > maxSzie + 1) return -1;
		
		int pers = 0;
		//概率计算
		for (ComposeAddInfo info : list) 
		{
			if(info == null) continue;
			
			Item item = BackpackManager.getInstance().getItemById(player, info.getEquip_id());
			if(item == null)
			{
				logger.error("[合成概率查找] --- 背包中找不到实体ID为[" + info.getEquip_id() + "]的道具！");
				continue;
			}
				
			
			
			if(!(item instanceof Equip)) continue;
			
			//计算
			Equip equip = (Equip)item;
			
			//品阶计算
			Q_itemBean itemBean = DataManager.getInstance().q_itemContainer.get(equip.getItemModelId());
			int stageLv = itemBean.getQ_equip_steplv();
			pers += bean.getStageLvPres(stageLv);
			
			//强化加成
			pers += bean.getStrengLvPres(equip.getGradeNum());
			
			//追加加成
			pers += bean.getAddLvPres(equip.getAddAttributeLevel());
			
			//卓越计算
			pers += bean.getRemarkablePres(equip.remarkableSize());
			
			//会心一击计算
			if(equip.luck()) pers += bean.getLuckPre();
			
			//无视一击计算
			if(equip.ignoring()) pers += bean.getIgnorePre();
		}
		
		return pers;
	}
	
	/**
	 * @param player
	 * @param strengthLevel
	 * @param addLevel
	 */
	private boolean hasEquipByUnsignItems(Player player, String unSignItems, long costEquipId) {
		String[] notSignItemList = unSignItems.split(Symbol.DOUHAO);
		if (notSignItemList.length < 4) {
			logger.error("装备合成表配置错误：" + unSignItems);
			return false;
		}
//		int type = Integer.parseInt(notSignItemList[0]);// 装备类型
		int isRemarkable = Integer.parseInt(notSignItemList[1]);// 是否卓越
		int strengthLevel = Integer.parseInt(notSignItemList[2]);// 强化等级
		int addLevel = Integer.parseInt(notSignItemList[3]);// 追加等级
		if (isRemarkable == 0 && strengthLevel == 0 && addLevel == 0) {
			return true;
		}
		Iterator<Item> iter = BackpackManager.getInstance().getAllItem(player).iterator();
		while (iter.hasNext()) {
			Item next = iter.next();
			if(!(next instanceof Equip)) {
				continue;
			}
			Equip equip = (Equip) next;
			if ((equip.getId() == costEquipId)) {
				Q_itemBean data = ManagerPool.dataManager.q_itemContainer.getMap().get(equip.getItemModelId());
				if ((equip.getGradeNum() >= strengthLevel) && (equip.getAddAttributeLevel() >= addLevel) 
					&& (isRemarkable == data.getQ_remarkable()) || (isRemarkable == 0)) {
					return true;
				} else {
					return false;
				}
			}
		}
//		Iterator<Item> iter2 = player.getStoreItems().values().iterator();
//		while (iter2.hasNext()) {
//			Item next = iter2.next();
//			if(!(next instanceof Equip)) {
//				continue;
//			}
//			Equip equip2 = (Equip) next;
//			Q_itemBean data =ManagerPool.dataManager.q_itemContainer.getMap().get(equip2.getItemModelId());
//			int eType=data.getQ_type();
//			if(equip2.getGradeNum()>=strengthLevel&&equip2.getAddAttributLevel()>=addLevel&&eType==type){
//				endValueStr.append(equip2.getId());
//				endValueStr.append(",");
//			}
//		}
		return false;
	}

	private boolean hasEquipByUnsignEquipId(Player player, String unSignEquipIds, long costEquipId) {
		String[] split = unSignEquipIds.split(Symbol.FENHAO_REG);
		Item backPackItem = BackpackManager.getInstance().getBackPackItem(player, costEquipId);
		if (backPackItem != null) {
			for (String sp : split) {
				int modelId = Integer.parseInt(sp);
				if (backPackItem.getItemModelId() == modelId) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 2014.3.12 合成优化增加了装备合成后的概率生成追加属性
	 * @param player		角色
	 * @param id
	 * @param bind
	 * @return
	 * @create	hongxiao.z      2014-3-12 下午2:38:57
	 */
	public Item composeNewEquip_2(Player player, int id, boolean bind)
	{
		Item item = composeNewEquip(player, id, bind);
		
		if(item instanceof Equip)
		{
			Equip equip = (Equip)item;
			
			Q_equip_composeBean composeEquip=getEquipComposeConfigData(id);
			
			if(!composeEquip.getAddProbs().isEmpty())
			{
				//获取追加等级
				int prob = RandomUtils.random(1, composeEquip.getMaxAddProb());
				int addLv = composeEquip.getAddProbs(prob);
				equip.setAddAttributeLevel(addLv);
			}
		}
		
		return item;
	}
	
	/**
	 * 合成了新的物品
	 * @param player
	 * @param id
	 */
	private Item composeNewEquip(Player player, int id, boolean bind) {
		Q_equip_composeBean composeEquip=getEquipComposeConfigData(id);
		
		String resultItems=composeEquip.getOther_result_items();
		int otherId=getRandomOtherItem(resultItems);
		long action = Config.getId();
//		List<Item> createItems2 = Item.createItems(id, 1 ,false,0 ,0, null);
//		ManagerPool.backpackManager.addItems(player, createItems2, Reasons.EQUIP_COMPOSE, action);
		if(otherId != 0) {
			List<Item> createItems = Item.createItems(otherId, 1, bind, 0, 0, null);
			ManagerPool.backpackManager.addItems(player, createItems, Reasons.EQUIP_COMPOSE, action);
//			TaskManager.getInstance().action(player, Task.ACTION_TYPE_RANK, TaskEnum.ITEM_COMPOSE,otherId);
			return createItems.get(0);
		}else {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("没有可以合成的物品！"));
		}
		return null;
	}
	
	/**
	 * 附加合成结果
	 * @param resultItems
	 */
	public int getRandomOtherItem(String resultItems){
		String[] resultItemsList=resultItems.split(Symbol.FENHAO);
		int count=resultItemsList.length;
		if(count<=0){
			return 0;
		}
		int index = RandomUtils.random(count);
		return Integer.parseInt(resultItemsList[index]);
	}
	
	/**
	 * 检查并收取材料
	 * 
	 * @return
	 */
	public MaterailResult checkTakeMaterial(Player player, String string, boolean useBind) {
		MaterailResult result = new MaterailResult();
		ArrayList<Integer[]> itemlist = getAnalyzeString(string);
		boolean is = true;
		String txt = "";
		int itemid = 0;
		long action = Config.getId();
		if (itemlist.size() > 0) {
			for (Integer[] integers : itemlist) {
				int num = 0;
				if(useBind) {
					num =  ManagerPool.backpackManager.getItemNum(player,
							integers[0]);
				}else {
					num =  ManagerPool.backpackManager.getItemNum(player,
							integers[0],useBind);
				}
				if (num < integers[1]) {
					Q_itemBean itemBean = ManagerPool.dataManager.q_itemContainer
							.getMap().get(integers[0]);
					is = false;
					txt = txt
							+ " "
							+ (integers[1] - num)
							+ ResManager.getInstance().getString("个")
							+ BackpackManager.getInstance().getName(
									itemBean.getQ_id());
					itemid = integers[0];
				}
			}

			if (is) {
				for (Integer[] integers : itemlist) {
					if(useBind) {
						int itemNum = ManagerPool.backpackManager.getItemNum(player, integers[0], useBind);
						if (itemNum > 0) {
							result.hasBindItem = true;
						}
						if (ManagerPool.backpackManager.removeItem(player,
								integers[0], integers[1],Reasons.COMPOSE_EQUIP,
								action) == false) {
							result.removeResult = false;
							return result;
						}
					}else {
						if (ManagerPool.backpackManager.removeItem(player,
								integers[0], integers[1], useBind,Reasons.COMPOSE_EQUIP,
								action) == false) {
							result.removeResult = false;
							return result;
						}
					}
					
				}
				result.removeResult = true;
				return result;
			} else {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，所需材料不足,缺少{1}。"), txt);
				stResErrorInfoToClient(player, (byte) 2, itemid, null);
			}
		} else {
			MessageUtil.notify_player(player, Notifys.EQST, ResManager
					.getInstance().getString("合成装备没有设定所需道具，停止。"));
		}
		result.removeResult = false;
		return result;
	}
	
	/**
	 * 解析字符串（获取道具ID和数量）
	 * 
	 * @param strengthBean
	 * @return
	 */
	public ArrayList<Integer[]> getAnalyzeString(String str) {
		String[] itemstr = str.split(Symbol.FENHAO);
		ArrayList<Integer[]> itemlist = new ArrayList<Integer[]>();
		for (String itemtab : itemstr) {
			String[] items = itemtab.split(Symbol.DOUHAO);
			if (items.length == 2) {
				Integer[] tab = { Integer.valueOf(items[0]),
						Integer.valueOf(items[1]) };
				itemlist.add(tab);
			}
		}
		return itemlist;
	}
	
	/**
	 * 发送错误消息给前端 类型 ：1金币不足，2道具不足，3，钻石不足
	 * 
	 * @param player
	 */
	public void stResErrorInfoToClient(Player player, byte type, int num,
			String str) {
		ResErrorInfoToClientMessage smsg = new ResErrorInfoToClientMessage();
		smsg.setType(type);
		smsg.setErrint(num);
		smsg.setErrstr(str);
		MessageUtil.tell_player_message(player, smsg);
	}

	private static class MaterailResult {
		boolean removeResult;// 扣除物品结果

		boolean hasBindItem;// 扣除的物品中是否包含有绑定物品
	}
	
	/**
	 * 获取卓越的条目
	 * @param attributes
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午9:54:00
	 */
	List<Attribute> gainSuperiors(List<Attribute> attributes)
	{
		List<Attribute> list = new ArrayList<Attribute>();
		for (Attribute type : attributes) 
		{
			if(Attributes.inSuperior(type)) list.add(type);
		}
		
		return list;
	}
	
	/**
	 * 获取幸运(会心一击)条目
	 * @param attributes
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午10:05:37
	 */
	List<Attribute> gainLuck(List<Attribute> attributes)
	{
		List<Attribute> list = new ArrayList<Attribute>();
		for (Attribute type : attributes) 
		{
			if(Attributes.inLuck(type)) list.add(type);
		}
		
		return list;
	}
	
	/**
	 * 获取无视一击条目
	 * @param attributes
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午10:05:37
	 */
	List<Attribute> gainIgnoring(List<Attribute> attributes)
	{
		List<Attribute> list = new ArrayList<Attribute>();
		for (Attribute type : attributes) 
		{
			if(Attributes.inIgnoring(type)) list.add(type);
		}
		
		return list;
	}
}	
