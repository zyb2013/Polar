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
import com.game.data.bean.Q_item_strengthBean;
import com.game.data.bean.Q_strenghten_vipBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.equip.manager.EquipManager;
import com.game.equipstreng.Log.EquipStageLog;
import com.game.equipstreng.Log.EquipStrengLog;
import com.game.equipstreng.message.ReqStageUpItemToServerMessage;
import com.game.equipstreng.message.ReqStrengClearCoolingMessage;
import com.game.equipstreng.message.ReqStrengItemToServerMessage;
import com.game.equipstreng.message.ReqStrengthenStateMessage;
import com.game.equipstreng.message.ResErrorInfoToClientMessage;
import com.game.equipstreng.message.ResStageUpItemToClientMessage;
import com.game.equipstreng.message.ResStrengItemToClientMessage;
import com.game.equipstreng.message.ResStrengthEffectMessage;
import com.game.equipstreng.message.ResStrengthenStateMessage;
import com.game.equipstreng.structs.EquipStreng;
import com.game.fightpower.manager.FightPowerManager;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.liveness.manager.LivenessManager;
import com.game.manager.ManagerPool;
import com.game.map.message.ResStrengthLevelMessage;
import com.game.map.message.ResWeaponChangeMessage;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttributeType;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.task.manager.TaskManager;
import com.game.task.struts.Task;
import com.game.task.struts.TaskEnum;
import com.game.utils.MessageUtil;
import com.game.utils.ParseUtil;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;
import com.game.vip.manager.VipManager;
import com.game.vip.struts.GuideType;

/**
 * 武器强化
 * 
 */
public class EquipStrengManager {

	private static Object obj = new Object();

	// 武器强化管理类实例
	private static EquipStrengManager manager;

	public static EquipStrengManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new EquipStrengManager();
			}
		}
		return manager;
	}

	private EquipStrengManager() {
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
//	【左手武器，右手武器，衣服，头盔，项链，手套，裤子，鞋子，左戒指，右戒指，宠物，翅膀】
	private static String[] posname = { " ", "武器", "武器", "衣服", "头盔", "项链", "护腕",
			"护腿", "鞋子", "戒指", "戒指", "宠物", "翅膀" };

	public String getPosname(int idx) {
		if (posname.length >= idx) {
			return ResManager.getInstance().getString(posname[idx]);
		}
		return " ";
	}

	/**
	 * 得到强化数据库模版
	 * 
	 * @param idx
	 * @return
	 */
	public Q_item_strengthBean getStrengItemData(String id) {
		return ManagerPool.dataManager.q_item_strengthContainer.getMap()
				.get(id);
	}

	/**
	 * 立即完成功能消耗钻石数量
	 * 
	 * @return
	 */
	public int getGoldPrompt() {
		Q_globalBean global = ManagerPool.dataManager.q_globalContainer
				.getMap().get(80);
		return global.getQ_int_value();
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
	 * 对指定道具进行强化操作
	 * 
	 * @param player
	 * @param msg
	 */
	public void equipStreng(Player player, ReqStrengItemToServerMessage msg) {
		EquipStreng esdata = player.getEquipStreng();
		if (esdata.getItemid() > 0) {
			Equip xequip = ManagerPool.equipManager.getEquipById(player,esdata.getItemid());
			if (xequip != null) {
				Q_itemBean ydata = getItemData(xequip.getItemModelId());
				String pname = getPosname(ydata.getQ_kind());
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("{1}『{2}』尚未强化完成，请稍后"), pname,
						BackpackManager.getInstance().getName(ydata.getQ_id()));
				return;
			}
		}

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
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("萌宠不能强化哦!"));
				return;
			}
			int currentnum = equip.getGradeNum();// 得到强化次数
			int snum = currentnum + 1; // 当前要加的强化等级
			Q_item_strengthBean strengthBean = getStrengItemData(equip.getItemModelId() + "_" + snum);
			if (strengthBean != null) {
				if(msg.getType()==2){
					if(!VipManager.getInstance().canPrimeStrength(player)) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，vip等级太低。"));
						return;
					}
					//钻石强化
					boolean deleteResult=ManagerPool.backpackManager.changeGold(player,-strengthBean.getQ_must_success_price(),Reasons.STRENG_QH_YUANBAO, Config.getId());
					if(deleteResult){
						byte result=1;
						changeEquip(player, equip, strengthBean, result, snum, false); // 改变装备
						ResStrengItemToClientMessage smsg = new ResStrengItemToClientMessage();
						smsg.setEquipInfo(ManagerPool.equipManager.getEquipInfo(equip));
						smsg.setIssuccess(result);
						smsg.setItemlevel((byte) equip.getGradeNum());
						MessageUtil.tell_player_message(player, smsg);
					}else{
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，所需钻石不足。"));
					}
				}else{
					// 正常强化，优先使用超级强化石
					if (player.getMoney() < strengthBean.getQ_streng_money()) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，所需金币不足。"));
						stResErrorInfoToClient(player, (byte) 1,
								strengthBean.getQ_streng_money(), null);
						return;
					}
					// if (ManagerPool.backpackManager.removeItem(player, 16026, 1,
					// Reasons.STRENG_SUPER, Config.getId()) == false) {
						// 检测并收道具
					MaterailResult materailResult = checkTakeMaterial(player, strengthBean.getQ_streng_item(), msg.getType() == 4);
					if (!materailResult.removeResult) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("材料不足"));
						return;
					}
					
					if(msg.getsType() == -1) //特权强化	add 2014.2.24 hongxiao.z
					{
						privilegeStreng(player, equip, strengthBean, snum, materailResult);
						return;
					}
					
					// }
					if (ManagerPool.backpackManager.changeMoney(player,-strengthBean.getQ_streng_money(),Reasons.STRENG_QH_GOLD, Config.getId())) {
						byte result = 0;// 强化结果，1成功，0失败

						// 若当前所操作的星数<曾进行强化的最高星数，则读取“服务器端计算用成功几率”进行计算在1-10000之间随机一个数
						if (opengm || RandomUtils.isGenerate2(10000, strengthBean.getQ_streng_pby())) { // 进入随机
							result = 1;
						}
//						// 若当前所操作的星数>=曾进行强化的最高星数，则读取该装备的“最高星数强化失败次数”并进行判断
//						if (snum >= equip.getHighestgrade()) {
//							if (equip.getGradefailnum() < strengthBean.getQ_streng_min()) {
//								result = 0; // 必定失败
//							} else if (equip.getGradefailnum() > strengthBean.getQ_streng_max()) {
//								result = 1; // 必定成功
//							}
//						}

						try
						{
							LivenessManager.getInstance().expenseQH(player);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						
						if (strengthBean.getQ_streng_time() > 0) { // 强化耗时大于0
							esdata.setItemid(equip.getId());
							esdata.setResult(result);
							esdata.setStarttime(strengthBean.getQ_streng_time());// 强化剩余时间
							esdata.setUseBind(materailResult.hasBindItem);
							ResStrengthenStateMessage stmsg = new ResStrengthenStateMessage();
							stmsg.setItemid(equip.getId());
							stmsg.setTime(strengthBean.getQ_streng_time());
							stmsg.setTimesum(strengthBean.getQ_streng_time());
							stmsg.setYuanbao(strengthBean.getQ_fast_streng_yuanbao());
							MessageUtil.tell_player_message(player, stmsg); // 发送给前端展示强化时间
						} else { // 直接得到强化结果
							changeEquip(player, equip, strengthBean, result, snum, materailResult.hasBindItem); // 改变装备
							ResStrengItemToClientMessage smsg = new ResStrengItemToClientMessage();
							smsg.setEquipInfo(ManagerPool.equipManager.getEquipInfo(equip));
							smsg.setIssuccess(result);
							smsg.setItemlevel((byte) equip.getGradeNum());
							MessageUtil.tell_player_message(player, smsg);
						}
					}
				}
				PlayerManager.getInstance().savePlayer(player);
			} else {
				if (currentnum >= 10) {
					MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("很抱歉，该装备已经强化到顶级。"));
				} else {
					MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("很抱歉，该装备不能强化。"));
				}
			}
		}
	}
	
	/**
	 * 特权强化
	 * @param player	玩家实体
	 * @param equip
	 * @param strengthBean
	 * @param snum
	 * @create	hongxiao.z      2014-2-24 下午1:28:22
	 */
	private void privilegeStreng(Player player, Equip equip, Q_item_strengthBean strengthBean, int snum, MaterailResult materailResult)
	{
		//获得品阶
		Q_itemBean itemBean = DataManager.getInstance().q_itemContainer.get(equip.getItemModelId()); 
		
		if(itemBean == null) return;
		
		//获取特权范围
		Q_strenghten_vipBean bean = DataManager.getInstance().q_strenghten_vipContainer.get((snum - 1) + "_" + itemBean.getQ_equip_steplv());
		
		if(bean == null) return;
		
		//获取VIP等级
		int vipLevel = VipManager.getInstance().getVIPLevel(player);
		
		//是否允许特权
		if(bean.getQ_vip_level() > vipLevel)
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，vip等级太低。"));
			return;
		}
		
		//钻石强化
		boolean deleteResult = ManagerPool.backpackManager.changeGold(player,-bean.getQ_diamond(),Reasons.STRENG_QH_YUANBAO, Config.getId());
		if(bean.getQ_diamond() == 0 || deleteResult)
		{
			byte result=1;
			changeEquip(player, equip, strengthBean, result, snum, materailResult.hasBindItem); // 改变装备
			ResStrengItemToClientMessage smsg = new ResStrengItemToClientMessage();
			smsg.setEquipInfo(ManagerPool.equipManager.getEquipInfo(equip));
			smsg.setIssuccess(result);
			smsg.setItemlevel((byte) equip.getGradeNum());
			MessageUtil.tell_player_message(player, smsg);
		}else{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，所需钻石不足。"));
		}
		
		PlayerManager.getInstance().savePlayer(player);
	}

	/**
	 * 改变装备（强化结果）
	 * 
	 * @param player
	 * @param equip
	 * @param strengthBean
	 * @param result
	 * @param snum
	 */
	private void changeEquip(Player player, Equip equip, Q_item_strengthBean strengthBean, byte result, int snum, boolean useBind) {
		// String name = equip.acqItemModel().getQ_name();
		EquipStrengLog log = new EquipStrengLog(); // 日志
		log.setPlayerid(player.getId());
		log.setStartlv(equip.getGradeNum());
		log.setEquiponlyid(equip.getId());
		boolean isreduce = false; // 是否降星
		if (result == 1) {// 成功
			if (useBind)// 使用绑定材料,装备强化后也绑定
				equip.setBind(true);
			int oldfightpower = FightPowerManager.getInstance().calAllFightPower(player);
			equip.setGradeNum(snum);
			// 重新计算属性
			ManagerPool.playerAttributeManager.countPlayerAttribute(player,
					PlayerAttributeType.EQUIP, 0);
			int newfightpower = FightPowerManager.getInstance().calAllFightPower(player);
//			if (snum >= equip.getHighestgrade()) {
				equip.setHighestgrade((byte) snum);
				equip.setGradefailnum((short) 0);

				Q_itemBean ydata = getItemData(equip.getItemModelId());
				if (strengthBean.getQ_streng_notice() == 1 || snum >= 9) {
//					String pname = getPosname(ydata.getQ_kind());
					// MessageUtil.notify_All_player(Notifys.CHAT_BULL,
					// "恭喜 {1} 将{2} 【{3}】 强化至{4}级",player.getName(),pname,BackpackManager.getInstance().getName(equip.acqItemModel().getQ_id()),String.valueOf(snum));
					ItemInfo itemInfo = equip.buildItemInfo();
					List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
					GoodsInfoRes goodsInfo = new GoodsInfoRes();
					goodsInfo.setItemInfo(itemInfo);
					goodsInfos.add(goodsInfo);
					
					ParseUtil parseUtil = new ParseUtil();
//					int fightpower = newfightpower - oldfightpower;
//					String equipname = BackpackManager.getInstance().getName(equip.acqItemModel().getQ_id());
					parseUtil.setValue(String.format(ResManager.getInstance().getString("恭喜 {@} 将{$}强化至[%s]级!{@}"),
							String.valueOf(snum)),new ParseUtil.PlayerParm(player.getId(), player.getName()), new ParseUtil.VipParm(VipManager.getInstance().getVIPLevel(player), GuideType.EQUIP.getValue()));
					String parseString = parseUtil.toString();
					MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM,parseString,goodsInfos,GuideType.EQUIP.getValue());
					MessageUtil.notify_All_player(Notifys.CUTOUT_ROLE,parseString,goodsInfos,GuideType.EQUIP.getValue());
					//! end
					
				}
				
				//! add by xuliang
				ItemInfo itemInfo = equip.buildItemInfo();
				List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
				GoodsInfoRes goodsInfo = new GoodsInfoRes();
				goodsInfo.setItemInfo(itemInfo);
				goodsInfos.add(goodsInfo);
				MessageUtil.notify_player(player, Notifys.CHAT_IMPORTANT, ResManager
						.getInstance().getString("恭喜您成功将{$}强化至+{1}"), goodsInfos, 0, snum+"");
				
				if(snum >= 7) {
					for(int i=0; i<player.getEquips().length; i++) {
						Equip equip2 = player.getEquips()[i];
						if(equip2 != null && equip2 == equip) {
							//只发送身上已经穿上的装备
							ResStrengthEffectMessage effectMsg = new ResStrengthEffectMessage();
							effectMsg.setPersionId(player.getId());
							effectMsg.setEquipModelId(ydata.getQ_id());
							effectMsg.setStrengthLevel((byte) snum);
							effectMsg.setPos((byte) (i+1));
							MessageUtil.tell_round_message(player, effectMsg);
							//11 13
							int minStrength = EquipManager.getInstance().getMinStrength(player);
							ResStrengthLevelMessage msg = new ResStrengthLevelMessage();
							msg.setPersonId(player.getId());
							msg.setStrengthLevel((byte) minStrength);
							MessageUtil.tell_round_message(player, msg);
							break;
						}
					}
				}
//			}
			MessageUtil.notify_player(player, Notifys.EQST, ResManager
					.getInstance().getString("恭喜您，装备强化成功,目前为强化{1}级，战斗力提升。"),
					String.valueOf(snum));
			//主线强化任务触发
			TaskManager.getInstance().action(player, Task.ACTION_TYPE_SPECIAL, TaskEnum.QIANGHUA,snum,equip);

		} else { // 失败
			if (snum >= equip.getHighestgrade()) {
				equip.setHighestgrade((byte) snum);
				equip.setGradefailnum((short) (equip.getGradefailnum() + 1));
			}
			if (strengthBean.getQ_streng_fail_reduce() > 0) { // 降星
				/*
				 * 	luminghua hide
				 * // 9星装备保护符：使用后在12小时以内,7星以前,9星装备强化失败不掉星,VIP为24小时
				List<Buff> buffs = ManagerPool.buffManager.getBuffByModelId(
						player, 1406);
				// 5星装备保护符：使用后在12小时以内,7星以前,9星装备强化失败不掉星,VIP为24小时
				List<Buff> buffs2 = ManagerPool.buffManager.getBuffByModelId(
						player, 1413);
				// 完美强化卡：使用后在12小时以内,装备强化不掉星
				List<Buff> buffs3 = ManagerPool.buffManager.getBuffByModelId(
						player, 1414);

				if ((equip.getGradeNum() == 9 && buffs.size() > 0)
						|| (equip.getGradeNum() == 5 && buffs2.size() > 0)
						|| (buffs3.size() > 0)) {
					MessageUtil.notify_player(
							player,
							Notifys.EQST,
							ResManager.getInstance().getString(
									"很遗憾，强化操作失败了(装备保护符保护不掉星)，再接再厉哦。"));
				} else {*/
					int failNum = equip.getGradeNum();
					int xnum = equip.getGradeNum()
							- strengthBean.getQ_streng_fail_reduce();
					if (xnum < 0) {
						xnum = 0;
					}
					log.setBackwardslv(xnum);
					equip.setGradeNum(xnum);
					failNum -= xnum;
					String txt = randomGiveItem(player,
							strengthBean.getQ_streng_fail_item()); // 随机道具奖励
					if (txt != null && txt.length() > 0) {
						log.setFailgiveitem(txt);
						MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("很遗憾，强化失败爆了[{1}]颗星，但您获得了：{2}。"), String.valueOf(failNum), txt);
					} else {
						MessageUtil.notify_player(player, Notifys.EQST, ResManager.getInstance().getString("很遗憾，强化失败爆了[{1}]颗星。"), String.valueOf(failNum));
					}
					isreduce = true;
//				}
			} else {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很遗憾，强化操作失败了，再接再厉哦。"));
			}
			// 重新计算属性
			ManagerPool.playerAttributeManager.countPlayerAttribute(player,
					PlayerAttributeType.EQUIP, 0);
		}

		equip.calculateFightPower();
		if (result == 1 && isreduce == false) {
			// 检查并改变套装属性BUFF
			ManagerPool.equipManager.stTaoZhuang(player, 2);
			Q_itemBean model = getItemData(equip.getItemModelId());

			if (model.getQ_kind() == 1 && equip.getGradeNum() == 10) { // 同步武器外观和强化等级10
																		// 到世界服务器
				ManagerPool.playerManager.stSyncExterior(player);
				ResWeaponChangeMessage msg = new ResWeaponChangeMessage();
				msg.setPersonId(player.getId());
				msg.setWeaponId(model.getQ_id());
				msg.setWeaponStreng((byte) equip.getGradeNum());
				MessageUtil.tell_round_message(player, msg);
			}

		}
		log.setModelid(equip.getItemModelId());
		log.setResult(result);
		log.setTargetlv(snum);
		log.setConsumeitem(strengthBean.getQ_streng_item());
		log.setMoney(strengthBean.getQ_streng_money());
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
					.getInstance().getString("强化没有设定所需道具"));
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
						if (ManagerPool.backpackManager.removeItem(player, integers[0], integers[1], Reasons.STRENG_ITEM,
								action) == false) {
							result.removeResult = false;
							return result;
						}
					}else {
						if (ManagerPool.backpackManager.removeItem(player, integers[0], integers[1], useBind, Reasons.STRENG_ITEM,
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
					.getInstance().getString("强化没有设定所需道具"));
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
									.getInstance().getString("装备强化失败获得物品"),
									(byte) 1, 0, items);
							MessageUtil
									.notify_player(
											player,
											Notifys.EQST,
											ResManager
													.getInstance()
													.getString(
															"您的包裹已满，强化失败获得{1}个『{2}』已通过邮件发送给您。"),
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
	 * 装备强化立即完成消息
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqStrengClearCoolingMessage(Player player,
			ReqStrengClearCoolingMessage msg) {
		// 所需钻石数 = 强化数据库中的“立即完成功能消耗钻石数量 * （剩余的完成所需时间 / 装备强化耗时所需总时间）”
		EquipStreng esdata = player.getEquipStreng();
		Equip equip = ManagerPool.equipManager.getEquipById(player,
				esdata.getItemid());
		if (esdata.getItemid() > 0 && equip != null) {
			int snum = equip.getGradeNum() + 1; // 当前要加的强化等级
			Q_item_strengthBean strengthBean = getStrengItemData(equip
					.getItemModelId() + "_" + snum);
			if (strengthBean != null) {
				if (strengthBean.getQ_streng_time() > 0) {
					double t = (double) esdata.getStarttime()
							/ (double) strengthBean.getQ_streng_time();
					double yb = (double) strengthBean
							.getQ_fast_streng_yuanbao() * t;
					int ybint = (int) Math.ceil(yb);

					if (ManagerPool.backpackManager.checkGold(player, ybint)) {
						// 检测2级密码
						if (ManagerPool.protectManager
								.checkProtectStatus(player)) {
							return;
						}
						ManagerPool.backpackManager.changeGold(player, -ybint,
								Reasons.STRENG_QH_YUANBAO, Config.getId());
						esdata.setStarttime(0);
						atonceStrengthen(player);
					} else {
						MessageUtil
								.notify_player(
										player,
										Notifys.EQST,
										ResManager.getInstance().getString(
												"立即强化完成需要{1}钻石"),
										String.valueOf(ybint));
						stResErrorInfoToClient(player, (byte) 3, ybint, null);
					}
				}
			}
		}
	}

	/**
	 * 打开强化面板
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqStrengthenStateMessage(Player player,
			ReqStrengthenStateMessage msg) {
		atonceStrengthen(player);
	}

	/**
	 * 处理强化时间到期后返回前端
	 * 
	 * @param player
	 */
	public void atonceStrengthen(Player player) {
		EquipStreng esdata = player.getEquipStreng();
		Equip equip = ManagerPool.equipManager.getEquipById(player,
				esdata.getItemid());
		if (esdata.getItemid() > 0 && equip != null) {
			int snum = equip.getGradeNum() + 1; // 当前要加的强化等级
			Q_item_strengthBean strengthBean = getStrengItemData(equip
					.getItemModelId() + "_" + snum);
			if (strengthBean != null) {
				if (esdata.getStarttime() > 0) {
					ResStrengthenStateMessage stmsg = new ResStrengthenStateMessage();
					stmsg.setItemid(esdata.getItemid());
					stmsg.setTime(esdata.getStarttime());
					stmsg.setTimesum(strengthBean.getQ_streng_time());
					stmsg.setYuanbao(strengthBean.getQ_fast_streng_yuanbao());
					MessageUtil.tell_player_message(player, stmsg); // 发送给前端展示强化时间
					return;
				} else {
					changeEquip(player, equip, strengthBean, esdata.getResult(), snum, esdata.isUseBind()); // 改变装备
					ResStrengItemToClientMessage smsg = new ResStrengItemToClientMessage();
					smsg.setEquipInfo(ManagerPool.equipManager
							.getEquipInfo(equip));
					smsg.setIssuccess(esdata.getResult());
					smsg.setItemlevel((byte) equip.getGradeNum());
					MessageUtil.tell_player_message(player, smsg);
					esdata.setItemid(0);
					esdata.setResult((byte) 0);
					esdata.setStarttime(0);
					return;
				}
			}
		}
		ResStrengthenStateMessage stmsg = new ResStrengthenStateMessage();
		stmsg.setItemid(0);
		stmsg.setTime(0);
		MessageUtil.tell_player_message(player, stmsg); // 发送给前端展示强化时间
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
										"{1}『{2}』尚未强化完成，请稍后"),
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
			int lv = equip.getGradeNum(); // 当前的强化等级
			Q_item_strengthBean strengthBean = getStrengItemData(equip
					.getItemModelId() + "_" + lv);
			if (strengthBean == null) {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，该装备限制为：无法进阶"));
				return;
			}
			if (strengthBean.getQ_is_up_stage() == 0) {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，该装备限制为：无法进阶"));
				return;
			}

			int newitemid = strengthBean.getQ_stage_newitem();
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
			if (player.getMoney() < strengthBean.getQ_up_stage_money()) {
				MessageUtil.notify_player(player, Notifys.EQST, ResManager
						.getInstance().getString("很抱歉，所需金币不足。"));
				stResErrorInfoToClient(player, (byte) 1,
						strengthBean.getQ_up_stage_money(), null);
				return;
			}

			if (checkTakeMaterial(player, strengthBean.getQ_up_stage_item()) == false)
				return;
			if (ManagerPool.backpackManager.changeMoney(player,
					-strengthBean.getQ_up_stage_money(),
					Reasons.STRENG_SJ_GOLD, Config.getId())) {
				byte result = 0;// 进阶结果，1成功，0失败
				if (equip.getAdvfailnum() <= strengthBean.getQ_up_stage_min()) {
					result = 0; // 必定失败
				} else if (equip.getAdvfailnum() > strengthBean
						.getQ_up_stage_max()) {
					result = 1; // 必定成功
				} else {
					if (RandomUtils.isGenerate2(10000,
							strengthBean.getQ_stage_probability())) { // 进入随机
						result = 1;
					}
				}
				EquipStageLog log = new EquipStageLog(); // 日志结构
				log.setEquip(JSONserializable.toString(equip));
				if (result == 1) {
					equip.setItemModelId(newitemid);
					equip.setAdvfailnum((short) 0);
					equip.setGradeNum(strengthBean.getQ_newitem_level());
					MessageUtil.notify_player(player, Notifys.EQST, ResManager
							.getInstance().getString("恭喜您，装备进阶成功，战斗力提升"));
					if (strengthBean.getQ_stage_notice() == 1) {
						//! changed by xuliang
						String pname = getPosname(ydata.getQ_kind());
						
						ParseUtil parseUtil = new ParseUtil();
						String parseString = String.format(String.format(ResManager.getInstance().getString("恭喜 {@} 将%s[{%s}]成功进阶为{$}"), pname, 
								BackpackManager.getInstance().getName(oldmodelid)));
						parseUtil.setValue(parseString, new ParseUtil.PlayerParm(player.getId(), player.getName()));
						
						ItemInfo itemInfo = equip.buildItemInfo();
						List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
						GoodsInfoRes goodsInfo = new GoodsInfoRes();
						goodsInfo.setItemInfo(itemInfo);
						goodsInfos.add(goodsInfo);
						
						MessageUtil.notify_All_player(
								Notifys.CHAT_BULL,
								parseUtil.toString(),
								goodsInfos,0);

						// ParseUtil parseUtil = new ParseUtil();
						// parseUtil.setValue(String.format("恭喜 %s 将%s【%s】成功进阶为【%s】!{@}",
						// player.getName(),pname,BackpackManager.getInstance().getName(oldmodelid),BackpackManager.getInstance().getName(newitemid)),
						// new
						// ParseUtil.VipParm(VipManager.getInstance().getPlayerVipId(player),GuideType.EQUIP_UP.getValue()));
						// MessageUtil.notify_All_player(Notifys.CHAT_BULL,
						// parseUtil.toString(),new
						// ArrayList<GoodsInfoRes>(),GuideType.EQUIP.getValue());
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
				log.setConsumeitem(strengthBean.getQ_up_stage_item());
				log.setResult(result);
				log.setMoney(strengthBean.getQ_up_stage_money());
				log.setSid(player.getCreateServerId());
				LogService.getInstance().execute(log); // 写日志
			}
		}
	}

	/**
	 * GM测试强化
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
					ResManager.getInstance().getString("{1}强化等级变更为：{2}。"),
					BackpackManager.getInstance().getName(
							weared.getItemModelId()), num + "");
			//强化任务触发
			TaskManager.getInstance().action(player, Task.ACTION_TYPE_SPECIAL, TaskEnum.QIANGHUA,0);

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
	 * 清除强化记录
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

}
