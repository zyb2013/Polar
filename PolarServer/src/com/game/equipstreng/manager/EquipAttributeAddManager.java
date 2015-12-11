package com.game.equipstreng.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.game.backpack.bean.ItemInfo;
import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.backpack.structs.ItemTypeConst;
import com.game.chat.bean.GoodsInfoRes;
import com.game.config.Config;
import com.game.data.bean.Q_globalBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_item_add_attributeBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.equipstreng.Log.EquipAddAttributeLog;
import com.game.equipstreng.Log.EquipStageLog;
import com.game.equipstreng.message.ReqAddAttributeItemToServerMessage;
import com.game.equipstreng.message.ReqStageUpItemToServerMessage;
import com.game.equipstreng.message.ResErrorInfoToClientMessage;
import com.game.equipstreng.message.ResStageUpItemToClientMessage;
import com.game.equipstreng.message.ResStrengItemToClientMessage;
import com.game.equipstreng.structs.EquipStreng;
import com.game.fightpower.manager.FightPowerManager;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttributeType;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;
import com.game.utils.ParseUtil;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;
import com.game.utils.ParseUtil.PlayerParm;
import com.game.vip.manager.VipManager;
import com.game.vip.struts.GuideType;

/**
 * 武器追加
 * 
 */
public class EquipAttributeAddManager {

	private static Object obj = new Object();

	// 武器追加管理类实例
	private static EquipAttributeAddManager manager;

	public static EquipAttributeAddManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new EquipAttributeAddManager();
			}
		}
		return manager;
	}

	private EquipAttributeAddManager() {
	}

	public static boolean opengm = false;
	// 武器 1
	// 衣服 2
	// 头盔 3
	// 项链 4
	// 护腕 5
	// 护腿 6
	// 戒指 7
	// 绶佩 8
	// 腰带 9
	// 鞋子 10
	private static String[] posname = { " ", "武器", "武器", "衣服", "头盔", "项链", "护腕",
		"护腿", "鞋子", "戒指", "戒指", "宠物", "翅膀" };

	public String getPosname(int idx) {
		if (posname.length >= idx) {
			return ResManager.getInstance().getString(posname[idx]);
		}
		return " ";
	}

	/**
	 * 得到追加数据库模版
	 * 
	 * @param idx
	 * @return
	 */
	public Q_item_add_attributeBean getAddAttributeItemData(String id) {
		return ManagerPool.dataManager.q_item_add_attributeContainer.getMap()
				.get(id);
	}

	/**
	 * 立即完成功能消耗钻石数量
	 * 
	 * @return
	 */
	public int getGoldPrompt() {
		/* xuliang
		Q_globalBean global = ManagerPool.dataManager.q_globalContainer.getMap().get(80);
		*/
		return 0/*global.getQ_int_value()*/;
	}

	/**
	 * 得到道具数据库模版
	 * 
	 * @param idx
	 * @return
	 */
	public Q_itemBean getItemData(int id) {
		return ManagerPool.dataManager.q_itemContainer.getMap().get(id);
	}

	/**
	 * 对指定道具进行追加操作
	 * 
	 * @param player
	 * @param msg
	 */
	public void equipAddAttribute(Player player, ReqAddAttributeItemToServerMessage msg) {
		Equip equip = ManagerPool.equipManager.getEquipById(player,msg.getItemid());
		if(equip==null){
			equip=(Equip)ManagerPool.backpackManager.getBackPackItem(player,msg.getItemid());
		}
		if(equip==null){
			equip=(Equip)ManagerPool.backpackManager.getWarehouseItem(player,msg.getItemid());
		}
		if (equip != null) {
			Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(equip.getItemModelId());
			if (q_itemBean.getQ_kind() == 11) {
				// 如果是萌宠部位
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("萌宠不能追加哦!"));
				return;
			}
			int currentLevel = equip.getAddAttributeLevel();// 得到追加次数
			int snum = currentLevel + 1; // 当前要加的追加等级
			Q_item_add_attributeBean addAttributeBean = getAddAttributeItemData(equip.getItemModelId() + "_" + snum);
			if (addAttributeBean != null) {
				if(msg.getType()==2){
					//钻石追加
					boolean deleteResult=ManagerPool.backpackManager.changeGold(player,-addAttributeBean.getQ_must_success_price(),Reasons.STRENG_QH_YUANBAO, Config.getId());
					if(deleteResult){
						byte result=1;
						changeEquip(player, equip, addAttributeBean, result, snum, false); // 改变装备
						ResStrengItemToClientMessage smsg = new ResStrengItemToClientMessage();
						smsg.setEquipInfo(ManagerPool.equipManager.getEquipInfo(equip));
						smsg.setIssuccess(result);
						smsg.setItemlevel((byte) equip.getGradeNum());
						smsg.setAddAttributLevel((byte) equip.getAddAttributeLevel());
						MessageUtil.tell_player_message(player, smsg);
					}else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，所需钻石不足。"));
						return;
					}
				}else{
					// 正常追加，优先使用超级追加石
					if (player.getMoney() < addAttributeBean.getQ_streng_money()) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("金币不足"));
						stResErrorInfoToClient(player, (byte) 1,
								addAttributeBean.getQ_streng_money(), null);
						return;
					}
					// if (ManagerPool.backpackManager.removeItem(player, 16026, 1,
					// Reasons.STRENG_SUPER, Config.getId()) == false) {
					MaterailResult materailResult = checkTakeMaterial(player, addAttributeBean.getQ_streng_item(), msg.getType() == 4);
					if (!materailResult.removeResult) {// 检测并收道具
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("材料不足"));
						return;
					}
					// }
					if (ManagerPool.backpackManager.changeMoney(player,-addAttributeBean.getQ_streng_money(),Reasons.STRENG_QH_GOLD, Config.getId())) {
						byte result = 0;// 追加结果，1成功，0失败

						// 若当前所操作的星数<曾进行追加的最高星数，则读取“服务器端计算用成功几率”进行计算在1-10000之间随机一个数
						if (opengm || RandomUtils.isGenerate2(10000, addAttributeBean.getQ_streng_pby())) { // 进入随机
							result = 1;
						}
						//追加结果
						changeEquip(player, equip, addAttributeBean, result, snum, materailResult.hasBindItem); // 改变装备
						ResStrengItemToClientMessage smsg = new ResStrengItemToClientMessage();
						smsg.setEquipInfo(ManagerPool.equipManager.getEquipInfo(equip));
						smsg.setIssuccess(result);
						smsg.setItemlevel((byte) equip.getGradeNum());
						smsg.setAddAttributLevel((byte) equip.getAddAttributeLevel());
						MessageUtil.tell_player_message(player, smsg);
					}else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("金币不足"));
						return;
					}
				}
				PlayerManager.getInstance().savePlayer(player);
			} else {
				if (currentLevel >= 10) {
					MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("很抱歉，该装备已经追加到顶级。"));
				} else {
					MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("很抱歉，该装备不能追加。"));
				}
			}
		}
	}

	/**
	 * 改变装备（追加结果）
	 * 
	 * @param player
	 * @param equip
	 * @param addAttributeBean
	 * @param result
	 * @param snum
	 */
	private void changeEquip(Player player, Equip equip, Q_item_add_attributeBean addAttributeBean, byte result, int snum, boolean useBind) {
		// String name = equip.acqItemModel().getQ_name();
		EquipAddAttributeLog log = new EquipAddAttributeLog(); // 日志
		log.setPlayerid(player.getId());
		log.setStartlv(equip.getAddAttributeLevel());
		log.setEquiponlyid(equip.getId());
		// boolean isreduce = false; // 是否降星
		if (result == 1) {// 成功
		 int oldfightpower = FightPowerManager.getInstance().calAllFightPower(player);
			if (useBind)
				equip.setBind(true);
			equip.setAddAttributeLevel(snum);
			// 重新计算属性
			ManagerPool.playerAttributeManager.countPlayerAttribute(player,
					PlayerAttributeType.EQUIP, 0);
			int newfightpower = FightPowerManager.getInstance().calAllFightPower(player);
			// if (snum >= equip.getHighestgrade()) {
			// equip.setHighestgrade((byte) snum);
			if (addAttributeBean.getQ_streng_notice() == 1 || snum >= 9) {
				Q_itemBean ydata = getItemData(equip.getItemModelId());
//				String pname = getPosname(ydata.getQ_kind());
				int fightpower = newfightpower - oldfightpower;
				String equipname =BackpackManager.getInstance().getName(equip.acqItemModel().getQ_id());
				ParseUtil parseUtil = new ParseUtil();
				parseUtil.setValue(String.format(ResManager.getInstance().getString("恭喜 {@} 将{$}追加至【追加%s级】!{@}"), String.valueOf(snum)),new ParseUtil.PlayerParm(player.getId(), player.getName()), new ParseUtil.VipParm(VipManager.getInstance().getVIPLevel(player), GuideType.EQUIP_UP.getValue()));
				String parseString = parseUtil.toString();
				
				List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
				GoodsInfoRes goodsInfo = new GoodsInfoRes();
				
				goodsInfo.setItemInfo(equip.buildItemInfo());
				goodsInfos.add(goodsInfo);
				
				MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM,parseString,goodsInfos,GuideType.EQUIP.getValue());
				MessageUtil.notify_All_player(Notifys.CUTOUT_ROLE,parseString,goodsInfos,GuideType.EQUIP.getValue());
			 }
			// }
			//! add by xuliang
			notifyImportantMessage(player, equip);
			MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("恭喜您，追加成功,目前追加为【追加{1}级】，战斗力提升。"),String.valueOf(snum));

		} else { // 失败
		// if (snum >= equip.getHighestgrade()) {
		// equip.setHighestgrade((byte) snum);
		// equip.setGradefailnum((short) (equip.getGradefailnum() + 1));
		// }
			if (addAttributeBean.getQ_streng_fail_reduce() > 0) { // 降星
				/*
				 * luminghua hide
				 * // 9星装备保护符：使用后在12小时以内,7星以前,9星装备追加失败不掉星,VIP为24小时
				List<Buff> buffs = ManagerPool.buffManager.getBuffByModelId(
						player, 1406);
				// 5星装备保护符：使用后在12小时以内,7星以前,9星装备追加失败不掉星,VIP为24小时
				List<Buff> buffs2 = ManagerPool.buffManager.getBuffByModelId(
						player, 1413);
				// 完美追加卡：使用后在12小时以内,装备追加不掉星
				List<Buff> buffs3 = ManagerPool.buffManager.getBuffByModelId(
						player, 1414);

				if ((equip.getAddAttributeLevel() == 9 && buffs.size() > 0) || (equip.getAddAttributeLevel() == 5 && buffs2.size() > 0)
						|| (buffs3.size() > 0)) {
					MessageUtil.notify_player(
							player,
							Notifys.EQST,
							ResManager.getInstance().getString(
									"很遗憾，追加操作失败了(装备保护符保护不掉星)，再接再厉哦。"));
				} else {*/
					int failNum = equip.getAddAttributeLevel();
					int xnum = equip.getAddAttributeLevel()
							- addAttributeBean.getQ_streng_fail_reduce();
					if (xnum < 0) {
						xnum = 0;
					}
					log.setBackwardslv(xnum);
					equip.setAddAttributeLevel(xnum);
					failNum -= xnum;
					String txt = randomGiveItem(player,
							addAttributeBean.getQ_streng_fail_item()); // 随机道具奖励
					if (txt != null && txt.length() > 0) {
						log.setFailgiveitem(txt);
						MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("很遗憾，追加失败爆了[{1}]颗星，但您获得了：{2}。"), String.valueOf(failNum), txt);
					} else {
						MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("很遗憾，追加失败爆了[{1}]颗星。"), String.valueOf(failNum));
					}
					// isreduce = true;
//				}
			} else {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很遗憾，追加操作失败了，再接再厉哦。"));
			}
			// 重新计算属性
			ManagerPool.playerAttributeManager.countPlayerAttribute(player,
					PlayerAttributeType.EQUIP, 0);
		}

		equip.calculateFightPower();
		// if (result == 1 && isreduce == false) {
		// // 检查并改变套装属性BUFF
		// ManagerPool.equipManager.stTaoZhuang(player, 2);
		// Q_itemBean model = getItemData(equip.getItemModelId());
		//
		// if (model.getQ_kind() == 1 && equip.getGradeNum() == 10) { //
		// 同步武器外观和追加等级10
		// // 到世界服务器
		// ManagerPool.playerManager.stSyncExterior(player);
		// ResWeaponChangeMessage msg = new ResWeaponChangeMessage();
		// msg.setPersonId(player.getId());
		// msg.setWeaponId(model.getQ_id());
		// msg.setWeaponStreng((byte) equip.getGradeNum());
		// MessageUtil.tell_round_message(player, msg);
		// }
		//
		// }
		log.setModelid(equip.getItemModelId());
		log.setResult(result);
		log.setTargetlv(snum);
		log.setConsumeitem(addAttributeBean.getQ_streng_item());
		log.setMoney(addAttributeBean.getQ_streng_money());
		LogService.getInstance().execute(log);
	}

	/**
	 * 检查并收取材料
	 * 
	 * @return
	 */
	public boolean checkTakeMaterial(Player player, String string) {
		ArrayList<Integer[]> itemlist = getAnalyzeString(string);
		boolean is = true;
		String txt = "";
		int itemid = 0;
		long action = Config.getId();
		if (itemlist.size() > 0) {
			for (Integer[] integers : itemlist) {
				int num = ManagerPool.backpackManager.getItemNum(player,
						integers[0]);
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
					if (ManagerPool.backpackManager.removeItem(player,
							integers[0], integers[1], Reasons.STRENG_ITEM,
							action) == false) {
						return false;
					}
				}
				return true;
			} else {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，所需材料不足,缺少{1}。"), txt);
				stResErrorInfoToClient(player, (byte) 2, itemid, null);
			}
		} else {
			MessageUtil.notify_player(player, Notifys.EQST, ResManager
					.getInstance().getString("升阶或追加没有设定所需道具"));
		}
		return false;
	}

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
						if (ManagerPool.backpackManager.removeItem(player, integers[0], integers[1], Reasons.ADDATTRIBUTE_ITEM,
								action) == false) {
							result.removeResult = false;
							return result;
						}
					}else {
						if (ManagerPool.backpackManager.removeItem(player, integers[0], integers[1], useBind, Reasons.ADDATTRIBUTE_ITEM,
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
					.getInstance().getString("追加没有设定所需道具"));
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
			String[] items = itemtab.split(Symbol.XIAHUAXIAN_REG);
			if (items.length == 2) {
				Integer[] tab = { Integer.valueOf(items[0]),
						Integer.valueOf(items[1]) };
				itemlist.add(tab);
			}
		}
		return itemlist;
	}

	/**
	 * 随机给奖励
	 * 
	 * @return
	 */
	public String randomGiveItem(Player player, String str) {
		String[] itemstr = str.split(Symbol.FENHAO);
		String txt = "";
		long action = Config.getId();
		for (String string : itemstr) {
			Pattern pattern = Pattern.compile("(.+?)\\|(.+?)\\_(.+?)");
			Matcher matcher = pattern.matcher(string);
			if (matcher.find()) {
				int rnd = Integer.parseInt(matcher.group(1));
				int itemid = Integer.parseInt(matcher.group(2));
				int num = Integer.parseInt(matcher.group(3));
				Q_itemBean itemBean = getItemData(itemid);
				if (itemBean != null) {
					if (RandomUtils.isGenerate2(10000, rnd)) { // 进入随机
						int gridnum = ManagerPool.backpackManager
								.getEmptyGridNum(player);
						List<Item> items = Item.createItems(itemid, num, true,
								0);
						txt = txt
								+ " "
								+ BackpackManager.getInstance().getName(
										itemBean.getQ_id()) + "*" + num;
						if (gridnum >= items.size()) {
							ManagerPool.backpackManager.addItems(player, items,
									Reasons.STRENG_QH_TO_ITEM, action);
						} else {
							ManagerPool.mailServerManager.sendSystemMail(player
									.getId(), null, ResManager.getInstance()
									.getString("系统邮件"), ResManager
									.getInstance().getString("装备追加失败获得物品"),
									(byte) 1, 0, items);
							MessageUtil
									.notify_player(
											player,
											Notifys.EQST,
											ResManager
													.getInstance()
													.getString(
															"您的包裹已满，追加失败获得{1}个『{2}』已通过邮件发送给您。"),
											String.valueOf(num),
											BackpackManager
													.getInstance()
													.getName(itemBean.getQ_id()));
						}
					}
				}
			}
		}
		return txt;
	}

	/**
	 * 对指定道具进行升阶操作消息
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqStageUpItemToServerMessage(Player player,
			ReqStageUpItemToServerMessage msg) {
		EquipStreng esdata = player.getEquipStreng();
		if (esdata.getItemid() > 0) {
			Equip xequip = ManagerPool.equipManager.getEquipById(player,
					esdata.getItemid());
			if (xequip != null) {
				Q_itemBean data = getItemData(xequip.getItemModelId());
				MessageUtil
						.notify_player(
								player,
								Notifys.EQST,
								ResManager.getInstance().getString(
										"{1}『{2}』尚未追加完成，请稍后"),
								getPosname(data.getQ_kind()),
								BackpackManager.getInstance().getName(
										xequip.getItemModelId()));
				return;
			}
		}
		Equip equip = ManagerPool.equipManager.getEquipById(player,
				msg.getItemid());
		if (equip != null) {
			int oldmodelid = equip.getItemModelId();
			int lv = equip.getGradeNum(); // 当前的追加等级
			Q_item_add_attributeBean addattributeBean = getAddAttributeItemData(equip.getItemModelId() + "_" + lv);
			if (addattributeBean == null) {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，该装备限制为：无法进阶"));
				return;
			}
			if (addattributeBean.getQ_is_up_stage() == 0) {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，该装备限制为：无法进阶"));
				return;
			}

			int newitemid = addattributeBean.getQ_stage_newitem();
			if (newitemid == 0 || equip.getItemModelId() == newitemid) {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，该装备限制为：无法进阶"));
				return;
			}

			Q_itemBean newitemBean = getItemData(newitemid);
			if (newitemBean == null) {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，该装备限制为：无法进阶"));
				return;
			}
			if (player.getLevel() < newitemBean.getQ_level()) {
				MessageUtil.notify_player(
						player,
						Notifys.EQST,
						ResManager.getInstance().getString(
								"成功进阶后的装备，需要[{1}级]才能佩戴，请先升到[{1}级]"),
						String.valueOf(newitemBean.getQ_level()));
				return;
			}
			Q_itemBean ydata = getItemData(equip.getItemModelId());
			// String yname = ydata.getQ_name();
			// String xname = newitemBean.getQ_name();
			if (player.getMoney() < addattributeBean.getQ_up_stage_money()) {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，所需金币不足。"));
				stResErrorInfoToClient(player, (byte) 1,
						addattributeBean.getQ_up_stage_money(), null);
				return;
			}

			if (checkTakeMaterial(player, addattributeBean.getQ_up_stage_item()) == false)
				return;
			if (ManagerPool.backpackManager.changeMoney(player,
					-addattributeBean.getQ_up_stage_money(),
					Reasons.STRENG_SJ_GOLD, Config.getId())) {
				byte result = 0;// 进阶结果，1成功，0失败
				if (equip.getAdvfailnum() <= addattributeBean.getQ_up_stage_min()) {
					result = 0; // 必定失败
				} else if (equip.getAdvfailnum() > addattributeBean
						.getQ_up_stage_max()) {
					result = 1; // 必定成功
				} else {
					if (RandomUtils.isGenerate2(10000,
							addattributeBean.getQ_stage_probability())) { // 进入随机
						result = 1;
					}
				}
				EquipStageLog log = new EquipStageLog(); // 日志结构
				log.setEquip(JSONserializable.toString(equip));
				if (result == 1) {
					equip.setItemModelId(newitemid);
					equip.setAdvfailnum((short) 0);
					equip.setGradeNum(addattributeBean.getQ_newitem_level());
					MessageUtil.notify_player(player, Notifys.EQST, ResManager
							.getInstance().getString("恭喜您，装备进阶成功，战斗力提升"));
					if (addattributeBean.getQ_stage_notice() == 1) {
						//! changed by xuliang
						String pname = getPosname(ydata.getQ_kind());
						
						ItemInfo itemInfo = equip.buildItemInfo();
						List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
						GoodsInfoRes goodsInfo = new GoodsInfoRes();
						goodsInfo.setItemInfo(itemInfo);
						goodsInfos.add(goodsInfo);
						
						ParseUtil parseUtil = new ParseUtil();
						String parseString = String.format(ResManager.getInstance().getString("恭喜 {@} 将%s[{%s}]成功进阶为{$}"), pname, BackpackManager.getInstance().getName(oldmodelid));
						parseUtil.setValue(parseString, new PlayerParm(player.getId(), player.getName()));
						
						MessageUtil.notify_All_player(Notifys.CHAT_BULL, parseUtil.toString(), goodsInfos, 0);

					}
					log.setTargetModel(newitemid);
				} else {
					equip.setAdvfailnum((short) (equip.getAdvfailnum() + 1));
					MessageUtil.notify_player(player, Notifys.EQST, ResManager
							.getInstance().getString("很遗憾，进阶操作失败了，再接再厉哦!"));
				}
				ResStageUpItemToClientMessage smsg = new ResStageUpItemToClientMessage();
				smsg.setIssuccess(result);
				smsg.setEquipInfo(ManagerPool.equipManager.getEquipInfo(equip));
				MessageUtil.tell_player_message(player, smsg);
				log.setEquiponlyid(equip.getId());
				log.setPlayerid(player.getId());
				log.setConsumeitem(addattributeBean.getQ_up_stage_item());
				log.setResult(result);
				log.setMoney(addattributeBean.getQ_up_stage_money());
				log.setSid(player.getCreateServerId());
				LogService.getInstance().execute(log); // 写日志
			}
		}
	}

	/**
	 * GM测试追加
	 * 
	 * @param player
	 * @param pos
	 * @param num
	 */
	public void testStrengthen(Player player, int pos, int num) {
		Equip weared = player.getEquips()[pos];
		if (weared != null) {
			weared.setGradeNum(num);
			// String yname = getItemData(weared.getItemModelId()).getQ_name();
			// 发送装备信息
			MessageUtil.tell_player_message(player,
					ManagerPool.equipManager.getWearEquipInfo(weared,0));
			MessageUtil.notify_player(
					player,
					Notifys.SUCCESS,
					ResManager.getInstance().getString("{1}追加等级变更为：{2}。"),
					BackpackManager.getInstance().getName(
							weared.getItemModelId()), num + "");
		} else {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager
					.getInstance().getString("该位置装备不存在,请输入0至9"));
		}
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

	/**
	 * 清除追加记录
	 * 
	 * @param item
	 * @return
	 */
	public Item clearStrengthenLog(Item item) {
		Q_itemBean q_itemBean = getItemData(item.getItemModelId());
		if (q_itemBean != null && q_itemBean.getQ_type() == ItemTypeConst.EQUIP) {
			Equip equip = (Equip) item;
			equip.setGradefailnum((short) 0);
			equip.setAdvfailnum((short) 0);
			equip.setHighestgrade((byte) 0);
		}
		return item;
	}

	private static class MaterailResult {
		boolean removeResult;// 扣除物品结果

		boolean hasBindItem;// 扣除的物品中是否包含有绑定物品
	}
	
	private void notifyImportantMessage(Player player, Equip equip){
		Q_item_add_attributeBean q_item_add_attribute = getAddAttributeItemData(equip.getItemModelId() + "_" + equip.getAddAttributeLevel());
		
		if (q_item_add_attribute == null){
			return;
		}
		
		List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
		GoodsInfoRes goodsInfoRes = new GoodsInfoRes();
		goodsInfoRes.setItemInfo(equip.buildItemInfo());
		goodsInfos.add(goodsInfoRes);
		
		if (q_item_add_attribute.getQ_max_physicattack() != 0){
			MessageUtil.notify_player(player, Notifys.CHAT_IMPORTANT, ResManager.getInstance().getString("恭喜您成功将[{$}]追加到{1}物理攻击力"), goodsInfos, 0, q_item_add_attribute.getQ_max_physicattack() + "");
		}else if (q_item_add_attribute.getQ_max_magicattack() != 0){
			MessageUtil.notify_player(player, Notifys.CHAT_IMPORTANT, ResManager.getInstance().getString("恭喜您成功将[{$}]追加到{1}魔法攻击力"), goodsInfos, 0, q_item_add_attribute.getQ_max_magicattack() + "");
		}
	}

}
