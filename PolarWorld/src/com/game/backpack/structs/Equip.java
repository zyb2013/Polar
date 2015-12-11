package com.game.backpack.structs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.data.bean.Q_equip_appendBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_item_add_attributeBean;
import com.game.data.bean.Q_item_strengthBean;
import com.game.data.manager.DataManager;
import com.game.manager.ManagerPool;
import com.game.structs.Attributes;
import com.game.utils.Global;

/**
 * 装备类
 * 
 * @author
 * 
 */
public class Equip extends Item {

	private Logger log = Logger.getLogger(Equip.class);

	private static final long serialVersionUID = -6709344489920034517L;
	public static final String BODYEQUIPLOST = "BODYEQUIPLOST";

	// 是否可用状态(用于已经穿戴上的装备，如果穿戴后玩家使用洗点丹，属性下降后不满足装备穿戴要求，那么装备变为不可用，并且不再有装备的属性加成)
	private boolean canUse;

	// 强化等级
	private int gradeNum;
	//追加等级
	private int addAttributeLevel;
	// 扩展属性(卓越)
	private List<Attribute> attributes = new ArrayList<Attribute>();
	// 已镶嵌宝石
	private List<Jewel> jewels = new ArrayList<Jewel>();
	
	private int attributeCount;
	
	// minghua hide
	// 攻击
	protected transient int attack;

	// // 防御
	// protected transient int defense;
	// // 暴击
	// protected transient int crit;
	// // 命中判定
	// private int hit;//
	// // 闪避
	// protected transient int dodge;
	// // 最大血量
	// protected transient int maxHp;
	// // 最大魔法
	// protected transient int maxMp;
	// // 最大体力
	// protected transient int maxSp;
	// // 攻击速度
	// protected transient int attackSpeed;
	// // 速度
	// protected transient int speed;
	// // 幸运
	// protected transient int luck;

	// 是否满附加
	protected transient boolean fullAppend;
	// 是否满镶嵌
	protected transient boolean fullEnchase;
	// 是否满强化
	protected transient boolean fullStrength;
	// 品质
	protected transient int quality;
	// 品质加成
	protected transient int qualityAdd;

	// 最高星数强化失败次数
	private short gradefailnum;
	// 曾进行强化的最高星数
	private byte highestgrade;
	// 进阶失败次数
	private short advfailnum;

	// 计算战斗力
	private int fightPower;

//	
//
//	/**
//	 * 穿着装备
//	 * 
//	 * @param roleId
//	 *            玩家id
//	 */
//	public void use(Player player, String... parameters) {
//		// 获得物品模型
//		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
//		if (model == null)
//			return;
//		if (model.getQ_bind() == 2) {
//			// 装备时绑定
//			this.setBind(true);
//		}
//
//		// if (!this.isBind()) {
//		// // 顶级强化装备佩戴时将装备设为绑定（0不生效，1生效）
//		// if (this.isFullStrength()
//		// && ManagerPool.dataManager.q_globalContainer
//		// .getMap()
//		// .get(CommonConfig.EQUIP_TOPLEVEL_INTENSIFY_USEBIND
//		// .getValue()).getQ_int_value() == 1) {
//		// this.setBind(true);
//		// }
//		// }
//
//		// if (!this.isBind()) {
//		// // 顶级镶嵌装备佩戴将装备设为绑定（0不生效，1生效）
//		// if (this.isFullEnchase()
//		// && ManagerPool.dataManager.q_globalContainer
//		// .getMap()
//		// .get(CommonConfig.EQUIP_TOPLEVEL_INLAY_USEBIND
//		// .getValue()).getQ_int_value() == 1) {
//		// this.setBind(true);
//		// }
//		// }
//
//		// if (!this.isBind()) {
//		// // 紫色装备佩戴时将装备设为绑定（0不生效，1生效）
//		// if (this.getQuality() == 4
//		// && ManagerPool.dataManager.q_globalContainer.getMap()
//		// .get(CommonConfig.EQUIP_PURPLE_USEBIND.getValue())
//		// .getQ_int_value() == 1) {
//		// this.setBind(true);
//		// }
//		// }
//		// long action = Config.getId();
//		// 获得对应装备栏装备
//		if (model.getQ_kind() == 1 || model.getQ_kind() == 2) {
//			// Q_kind 1是主手武器，2是副手武器盾，法杖固定放在主手栏，盾固定放在副手栏，其他单手武器(二级类型填1)没限制，双手武器(二级类型填2)占两栏
//			Equip weapon1 = player.getEquips()[0];
//			Equip weapon2 = player.getEquips()[1];
//			Q_itemBean weapon1Model = null;
//			if (weapon1 != null) {
//				weapon1Model = ManagerPool.dataManager.q_itemContainer.getMap().get(weapon1.getItemModelId());
//			}
//			// 首先判断有没有双手武器
//			if (weapon1 == null || weapon1Model.getQ_secondary_type() != 2) {
//				if (model.getQ_secondary_type() == 2) {
//					// 如果是双手武器，要把两件武器脱了
//					boolean unWearEquip = unWearEquip(player, 1);
//					boolean unWearEquip2 = unWearEquip(player, 2);
//					if (unWearEquip && unWearEquip2) {
//						wearEquip(player, model.getQ_kind());
//					}
//				} else {
//					// 如果是单手武器，找出一个空位放入,其中法杖固定放在主手栏/盾只能放着副手
//					if (model.getQ_third_type() == 1) {
//						if (unWearEquip(player, 1))
//							wearEquip(player, 1);
//					}else if (model.getQ_kind() == 2) {
//						if (unWearEquip(player, 2))
//							wearEquip(player, 2);
//					} else {
//						if (weapon1 == null) {
//							wearEquip(player, 1);
//						} else if (weapon2 == null) {
//							wearEquip(player, 2);
//						} else {
//							// 判断战斗力
//							if (weapon1.getFightPower() < weapon2.getFightPower()) {
//								if (unWearEquip(player, 1))
//									wearEquip(player, 1);
//							} else {
//								if (unWearEquip(player, 2))
//									wearEquip(player, 2);
//							}
//						}
//					}
//
//				}
//			} else {
//				// 左手已经放了个双手武器，脱下左手的武器，换上新武器
//				boolean unWearEquip = unWearEquip(player, 1);
//				if (unWearEquip) {
//					wearEquip(player, model.getQ_kind());
//				}
//			}
//			// if (weapon1 == null) {
//			// // 放入左手
//			// useEquipToEmptyCell(player, action, model.getQ_kind(),
//			// model.getQ_id());
//			// } else if (weapon1 != null && weapon2 == null) {
//			// // 放入右手
//			// Q_itemBean weapon1Model = ManagerPool.dataManager.q_itemContainer.getMap().get(weapon1.getItemModelId());
//			// if(weapon1Model.getQ_secondary_type()==2){
//			// //左手已经放了个双手武器，脱下左手的武器，换上新武器
//			// changeEquipToCell(player, model, action, weapon1,0);
//			// }else if(model.getQ_secondary_type()==2){
//			// //如果要装备的武器是双手武器，脱下左手的武器，换上新武器
//			// changeEquipToCell(player, model, action, weapon1,0);
//			//
//			// }else{
//			// useEquipToEmptyCell(player, action, 2, model.getQ_id());
//			// }
//			// } else {
//			// if(model.getQ_secondary_type()==2){
//			// //如果要装备的武器是双手武器，脱下左手和右手的武器，换上新武器
//			// if(BackpackManager.getInstance().hasAddSpace(player, weapon2)){
//			// boolean addResult=BackpackManager.getInstance().addItem(player, weapon2, Reasons.WEAREDORUNWEARED, action);
//			// if(addResult){
//			// player.getEquips()[1]=null;
//			// changeEquipToCell(player, model, action, weapon1,0);
//			// }
//			// }
//			//
//			// }else{
//			// // TODO 根据分值判断加那个
//			// int maxAttack1 = weapon1.getPhysicAttackUpper(player.getLevel()) + weapon1.getMagicAttackUpper(player.getLevel());
//			// int maxAttack2 = weapon2.getPhysicAttackUpper(player.getLevel()) + weapon2.getMagicAttackUpper(player.getLevel());
//			// if(maxAttack1>maxAttack2){
//			// changeEquipToCell(player, model, action, weapon2,2);
//			// }else {
//			// changeEquipToCell(player, model, action, weapon1,1);
//			// }
//			// }
//			//
//			//
//			// }
//
//		} else if (model.getQ_kind() == 9) {// 戒指
//			Equip ring1 = player.getEquips()[8];
//			Equip ring2 = player.getEquips()[9];
//			if (ring1 == null) {
//				// 放入左手
//				wearEquip(player, 9);
//			} else if (ring2 == null) {
//				// 放入右手
//				wearEquip(player, 10);
//			}else {
//				// TODO 根据分值判断加那个(先替换左边的)
//				if (unWearEquip(player, 9))
//					wearEquip(player, 9);
//			}
//		}else {
//			if (unWearEquip(player, model.getQ_kind()))
//				wearEquip(player, model.getQ_kind());
//		}
//		ManagerPool.playerManager.savePlayer(player);
//	}
//
//	/**
//	 * 卸下装备
//	 * 
//	 * @param roleId
//	 *            玩家id
//	 */
//	public void unuse(Player player, String... parameters) {
//		// 获得物品模型
//		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
//		if (model == null)
//			return;
//
//		// 获得对应装备栏装备
//		Equip[] equips = player.getEquips();
//
//		Equip weared = null;
//		int equipPositon = -1;
//		for (int i = 0; i < equips.length; i++) {
//			if (equips[i] != null && equips[i].getId() == this.getId()) {
//
//				weared = equips[i];
//				equipPositon = i;
//				break;
//			}
//		}
//		if (weared == null) {
//			return;
//		}
//		/*
//		 * int modelkind = model.getQ_kind(); Equip weared = null; if (modelkind == 1 || modelkind == 2) { weared = player.getEquips()[model.getQ_kind() - 1]; if (weared == null) {
//		 * weared = player.getEquips()[model.getQ_kind()]; } } else { weared = player.getEquips()[model.getQ_kind() - 1]; } if (weared == null || weared != this) { return; }
//		 */
//
//		EquipStreng esdata = player.getEquipStreng();
//		String name = BackpackManager.getInstance().getName(getItemModelId());
//		if (esdata.getItemid() == weared.getId()) {
//			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("『{1}』正在强化中，无法卸下，请等待强化完成。"), name);
//			return;
//		}
//
//		// 装备该装备
//		if (weared == this) {
//			if (parameters != null && parameters.length > 0 && parameters[0].equals(BODYEQUIPLOST)) {
//				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("您身上的{1}已经过期，自动卸下"), name);
//				MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("您身上的{1}已经过期，自动卸下"), name);
//				if (!(BackpackManager.getInstance().hasAddSpace(player, weared) && ManagerPool.backpackManager.addItem(player, weared, Reasons.WEAREDORUNWEARED, Config.getId()))) {
//					ArrayList<Item> arrayList = new ArrayList<Item>();
//					arrayList.add(this);
//					ManagerPool.mailServerManager.sendSystemMail(player.getId(), null, ResManager.getInstance().getString("系统邮件"),
//							ResManager.getInstance().getString("装备过期自动卸下,加入包裹失败"), (byte) 1, 0, arrayList);
//				}
//			} else {
//				if (!BackpackManager.getInstance().hasAddSpace(player, weared)) {
//					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("对不起，您的背包空格不足，请清理后再进行本操作"));
//					return;
//				}
//				if (!ManagerPool.backpackManager.addItem(player, weared, Reasons.WEAREDORUNWEARED, Config.getId())) {
//					// 加入包裹失败
//					return;
//				}
//			}
//
//			player.getEquips()[equipPositon] = null;
//			// player.getEquips()[model.getQ_kind() - 1] = null;
//
//			// 发送卸下装备信息
//			MessageUtil.tell_player_message(player, ManagerPool.equipManager.getUnwearEquipInfo(this, equipPositon));
//			if (model.getQ_kind() == 1 || model.getQ_kind() == 2) {
//				ResWeaponChangeMessage msg = new ResWeaponChangeMessage();
//				msg.setPersonId(player.getId());
//				msg.setWeaponId(0);
//				msg.setPos((byte) (equipPositon + 1));
//				MessageUtil.tell_round_message(player, msg);
//			} else if (model.getQ_kind() == 3) {
//				ResArmorChangeMessage msg = new ResArmorChangeMessage();
//				msg.setPersonId(player.getId());
//				msg.setArmorId(0);
//				MessageUtil.tell_round_message(player, msg);
//			} else if (model.getQ_kind() == 11) {
//				ResEquipPetMessage msg = new ResEquipPetMessage();
//				msg.setPlayerId(player.getId());
//				msg.setEquipPetId(0);
//				MessageUtil.tell_round_message(player, msg);
//			}
//			if (log.isDebugEnabled()) {
//				log.debug("UNWEAR " + this.getId() + " TO " + this.getGridId());
//			}
//			// 重新计算属性
//			ManagerPool.playerAttributeManager.countPlayerAttribute(player, PlayerAttributeType.EQUIP, this.getItemModelId());
//			ManagerPool.playerManager.savePlayer(player);
//			if (model.getQ_kind() == 1 || model.getQ_kind() == 2) { // 只有武器和衣服变化才需要发这个消息
//				ReqSyncPlayerAppearanceInfoMessage syncmsg = new ReqSyncPlayerAppearanceInfoMessage();
//				syncmsg.setPlayerId(player.getId());
//				syncmsg.setAppearanceInfo(ManagerPool.transactionsManager.setPlayerAppearanceInfo(player));
//				syncmsg.setVipid(VipManager.getInstance().getVIPLevel(player));
//				MessageUtil.send_to_world(syncmsg);
//			}
//			ManagerPool.equipManager.stTaoZhuang(player, 1);// 改变套装属性
//			if (model.getQ_attach_skill() != 0) {
//				SkillManager.getInstance().removeSkill(player, model.getQ_attach_skill());
//			}
//			if(!StringUtil.isBlank(model.getQ_buff())){
//				String[] split = model.getQ_buff().split(Symbol.FENHAO_REG);
//				int[] buffs = new int[split.length];
//				for(int i = 0; i < split.length; i++) {
//					buffs[i] = Integer.parseInt(split[i]);
//				}
//				BuffManager.getInstance().removeByBuffId(player,buffs);
//			}
//		}
//	}

	/**
	 * 装备未装备装备的位置
	 * 
	 * @param player
	 * @param model
	 * @param action
	 */
	// private void useEquipToEmptyCell(Player player, long action, int kind,
	// int itemId) {
	// BackpackManager.getInstance().removeItemByCellId(player, getGridId(),
	// Reasons.WEAREDORUNWEARED, action);
	// this.setGridId(-1);
	// player.getEquips()[kind - 1] = this;
	// // 发送装备信息
	// int truePosite=kind-1;
	// MessageUtil.tell_player_message(player,
	// ManagerPool.equipManager.getWearEquipInfo(this,truePosite));
	// if (kind == 1 || kind == 2) {
	// ResWeaponChangeMessage msg = new ResWeaponChangeMessage();
	// msg.setPersonId(player.getId());
	// msg.setWeaponId(itemId);
	// msg.setWeaponStreng((byte) this.getGradeNum());
	// MessageUtil.tell_round_message(player, msg);
	// } else if (kind == 3) {
	// ResArmorChangeMessage msg = new ResArmorChangeMessage();
	// msg.setPersonId(player.getId());
	// msg.setArmorId(itemId);
	// MessageUtil.tell_round_message(player, msg);
	// } else if (kind == 11) {
	// ResEquipPetMessage msg = new ResEquipPetMessage();
	// msg.setPlayerId(player.getId());
	// msg.setItemId(this.getId());
	// MessageUtil.tell_round_message(player, msg);
	// }
	// if (kind == 1 || kind == 2 || kind == 3) { // 只有武器和衣服变化才需要发这个消息
	// ManagerPool.playerManager.stSyncExterior(player);
	// }
	// // 重新计算属性
	// ManagerPool.playerAttributeManager.countPlayerAttribute(player,PlayerAttributeType.EQUIP, this.getItemModelId());
	// if (this.getGradeNum() >= 7) {
	// ManagerPool.equipManager.stTaoZhuang(player, 1);// 改变套装属性
	// }
	// }

	/**
	 * 装备已经装备装备的位置
	 * 
	 * @param player
	 * @param model
	 * @param action
	 * @param weared
	 * @param tureKind 真实位置 为0时使用配制位置
	 */
	// private void changeEquipToCell(Player player, Q_itemBean model,
	// long action, Equip weared,int tureKind) {
	// EquipStreng esdata = player.getEquipStreng();
	// if (esdata.getItemid() == weared.getId()) {
	// // 获得物品模型
	// Q_itemBean oldmodel = ManagerPool.dataManager.q_itemContainer
	// .getMap().get(weared.getItemModelId());
	//
	// if (oldmodel == null)
	// return;
	// String name = BackpackManager.getInstance().getName(
	// weared.getItemModelId());
	// MessageUtil.notify_player(player, Notifys.ERROR, ResManager
	// .getInstance().getString("『{1}』正在强化中，无法卸下，请等待强化完成。"), name);
	// return;
	// }
	// weared.setGridId(this.getGridId());
	// // player.getBackpackItems().put(String.valueOf(weared.getGridId()),
	// // weared);
	// // log.info("UNWEAR " + weared.getId() + " TO " + weared.getGridId());
	// // 发送卸下装备信息
	// MessageUtil.tell_player_message(player,
	// ManagerPool.equipManager.getUnwearEquipInfo(weared));
	// if (model.getQ_kind() == 11) {
	// ResEquipPetMessage msg = new ResEquipPetMessage();
	// msg.setPlayerId(player.getId());
	// msg.setItemId(0);
	// MessageUtil.tell_round_message(player, msg);
	// }
	// // Q_itemBean q_itemBean =
	// // DataManager.getInstance().q_itemContainer.getMap().get(weared.getId());
	//
	// if (model.getQ_kind() == 1 || model.getQ_kind() == 2) {
	// ResWeaponChangeMessage msg = new ResWeaponChangeMessage();
	// msg.setPersonId(player.getId());
	// msg.setWeaponId(model.getQ_id());
	// msg.setWeaponStreng((byte) this.getGradeNum());
	// MessageUtil.tell_round_message(player, msg);
	// } else if (model.getQ_kind() == 3) {
	// ResArmorChangeMessage msg = new ResArmorChangeMessage();
	// msg.setPersonId(player.getId());
	// msg.setArmorId(model.getQ_id());
	// MessageUtil.tell_round_message(player, msg);
	// }
	//
	// ManagerPool.backpackManager.removeItem(player, getId(),
	// Reasons.WEAREDORUNWEARED, action);
	// ManagerPool.backpackManager.addItem(player, weared,
	// Reasons.WEAREDORUNWEARED, action);
	// this.setGridId(-1);
	// int truePosite=0;
	// if(tureKind==0){
	// player.getEquips()[model.getQ_kind() - 1] = this;
	// truePosite=model.getQ_kind()-1;
	// }else {
	// player.getEquips()[tureKind-1] = this;
	// truePosite=tureKind-1;
	// }
	// // 发送装备信息
	// MessageUtil.tell_player_message(player,ManagerPool.equipManager.getWearEquipInfo(this,truePosite));
	// if (model.getQ_kind() == 11) {
	// ResEquipPetMessage msg = new ResEquipPetMessage();
	// msg.setPlayerId(player.getId());
	// msg.setItemId(this.getId());
	// MessageUtil.tell_round_message(player, msg);
	// }
	// // 重新计算属性
	// ManagerPool.playerAttributeManager.countPlayerAttribute(player,PlayerAttributeType.EQUIP, this.getItemModelId());
	//
	// if (model.getQ_kind() == 1 || model.getQ_kind() == 2
	// || model.getQ_kind() == 3) {
	// ManagerPool.playerManager.stSyncExterior(player);
	// }
	// if (weared.getGradeNum() >= 7 || this.getGradeNum() >= 7) {
	// ManagerPool.equipManager.stTaoZhuang(player, 1);// 改变套装属性
	// }
	// }
//
//	private boolean unWearEquip(Player player, int kind) {
//		Equip weared = player.getEquips()[kind - 1];
//		if (weared != null) {
//			weared.setGridId(-1);
//			boolean addItem = ManagerPool.backpackManager.addItem(player, weared, Reasons.WEAREDORUNWEARED, Config.getId());
//			if (!addItem) {
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("你的背包空间不足"));
//				return false;
//			}
//			player.getEquips()[kind - 1] = null;
//			MessageUtil.tell_player_message(player, ManagerPool.equipManager.getUnwearEquipInfo(weared, kind - 1));
//			Q_itemBean wearedModel = ManagerPool.dataManager.q_itemContainer.getMap().get(weared.getItemModelId());
//			if (wearedModel.getQ_kind() == 11) {
//				ResEquipPetMessage msg = new ResEquipPetMessage();
//				msg.setPlayerId(player.getId());
//				msg.setEquipPetId(0);
//				MessageUtil.tell_round_message(player, msg);
//			}
//			if (wearedModel.getQ_attach_skill() != 0) {
//				SkillManager.getInstance().removeSkill(player, wearedModel.getQ_attach_skill());
//			}
//			if(!StringUtil.isBlank(wearedModel.getQ_buff())){
//				String[] split = wearedModel.getQ_buff().split(Symbol.FENHAO_REG);
//				int[] buffs = new int[split.length];
//				for(int i = 0; i < split.length; i++) {
//					buffs[i] = Integer.parseInt(split[i]);
//				}
//				BuffManager.getInstance().removeByBuffId(player,buffs);
//			}
//			// // 重新计算属性
//			// ManagerPool.playerAttributeManager.countPlayerAttribute(player, PlayerAttributeType.EQUIP, weared.getItemModelId());
//			// if (weared.getGradeNum() >= 7) {
//			// ManagerPool.equipManager.stTaoZhuang(player, 1);// 改变套装属性
//			// }
//		}
//		return true;
//	}
//
//	private boolean wearEquip(Player player, int kind) {
//		Equip weared = player.getEquips()[kind - 1];
//		EquipStreng esdata = player.getEquipStreng();
//		if (weared != null && esdata != null && esdata.getItemid() == weared.getId()) {
//			// 获得物品模型
//			Q_itemBean oldmodel = ManagerPool.dataManager.q_itemContainer.getMap().get(weared.getItemModelId());
//
//			if (oldmodel == null)
//				return false;
//			String name = BackpackManager.getInstance().getName(weared.getItemModelId());
//			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("『{1}』正在强化中，无法卸下，请等待强化完成。"), name);
//			return false;
//		}
//		if (weared != null) {
//			weared.setGridId(-1);
//			boolean addItem = ManagerPool.backpackManager.addItem(player, weared, Reasons.WEAREDORUNWEARED, Config.getId());
//			if (!addItem) {
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("你的背包空间不足"));
//				return false;
//			}
//			player.getEquips()[kind - 1] = null;
//			MessageUtil.tell_player_message(player, ManagerPool.equipManager.getUnwearEquipInfo(weared, kind - 1));
//			Q_itemBean wearedModel = ManagerPool.dataManager.q_itemContainer.getMap().get(weared.getItemModelId());
//			if (wearedModel.getQ_kind() == 11) {
//				ResEquipPetMessage msg = new ResEquipPetMessage();
//				msg.setPlayerId(player.getId());
//				msg.setEquipPetId(0);
//				MessageUtil.tell_round_message(player, msg);
//			}
//		}
//		Q_itemBean newWearModel = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
//		boolean removeItem = ManagerPool.backpackManager.removeItem(player, getId(), Reasons.WEAREDORUNWEARED, Config.getId());
//		if (!removeItem) {
//			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品不能装备"));
//			return false;
//		}
//		this.setGridId(-1);
//		player.getEquips()[kind - 1] = this;
//		if (newWearModel.getQ_kind() == 1 || newWearModel.getQ_kind() == 2) {
//			ResWeaponChangeMessage msg = new ResWeaponChangeMessage();
//			msg.setPersonId(player.getId());
//			msg.setWeaponId(newWearModel.getQ_id());
//			msg.setWeaponStreng((byte) this.getGradeNum());
//			msg.setPos((byte) (kind));
//			MessageUtil.tell_round_message(player, msg);
//		} else if (newWearModel.getQ_kind() == 3) {
//			ResArmorChangeMessage msg = new ResArmorChangeMessage();
//			msg.setPersonId(player.getId());
//			msg.setArmorId(newWearModel.getQ_id());
//			MessageUtil.tell_round_message(player, msg);
//		}
//		// 发送装备信息
//		MessageUtil.tell_player_message(player, ManagerPool.equipManager.getWearEquipInfo(this, kind - 1));
//		if (newWearModel.getQ_kind() == 11) {
//			ResEquipPetMessage msg = new ResEquipPetMessage();
//			msg.setPlayerId(player.getId());
//			msg.setEquipPetId(this.getItemModelId());
//			MessageUtil.tell_round_message(player, msg);
//		}
//		// 重新计算属性
//		ManagerPool.playerAttributeManager.countPlayerAttribute(player, PlayerAttributeType.EQUIP, this.getItemModelId());
//		if (newWearModel.getQ_kind() == 1 || newWearModel.getQ_kind() == 2 || newWearModel.getQ_kind() == 3) {
//			ManagerPool.playerManager.stSyncExterior(player);
//		}
//		if (weared != null && weared.getGradeNum() >= 7 || this.getGradeNum() >= 7) {
//			ManagerPool.equipManager.stTaoZhuang(player, 1);// 改变套装属性
//		}
//		if (newWearModel.getQ_attach_skill() != 0) {
//			SkillManager.getInstance().addSkill(player, newWearModel.getQ_attach_skill());
//		}
//		if(!StringUtil.isBlank(newWearModel.getQ_buff())){
//			String[] split = newWearModel.getQ_buff().split(Symbol.FENHAO_REG);
//			for(String buff:split) {
//				BuffManager.getInstance().addBuff(player, player, Integer.parseInt(buff), 0, 0, 0);
//			}
//		}
//		return true;
//	}

	public int calculateFightPower() {
		// 战斗力=四舍五入取整(生命+攻击*12+防御*12+0.95*防率/(防率+385)*4630+攻速*0.01*962*12)
//		Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(this.getItemModelId());
//		int equipLevel = q_itemBean.getQ_equip_level();
//		double attack = 0;
//		if (q_itemBean.getQ_max_magicattack() != 0) {
//			attack = (this.getMagicAttackUpper(equipLevel) + this.getMagicAttackLower(equipLevel)) / 2.0d;
//		} else {
//			attack = (this.getPhysicAttackUpper(equipLevel) + this.getPhysicAttackLower(equipLevel)) / 2.0d;
//		}
//		double fightPower = this.getMaxHp() + attack * 12 + this.getDefense() * 12 + 0.95d * this.getDodge() / (this.getDodge() + 385) * 4630;
//		// + this.getAttackSpeed()* 0.01 * 962 * 12;//攻速计算暂时不用
//		int result = new BigDecimal(fightPower).intValue();// 四舍五入
//		this.fightPower = result;
		Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(this.getItemModelId());
		int equipLevel = q_itemBean.getQ_equip_level();
		double attack = 0;
		if (q_itemBean.getQ_max_magicattack() != 0) {
			attack = (this.getMagicAttackUpper(equipLevel) + this.getMagicAttackLower(equipLevel)) / 2.0d;
		} else {
			attack = (this.getPhysicAttackUpper(equipLevel) + this.getPhysicAttackLower(equipLevel)) / 2.0d;
		}
		double fightPower = this.getMaxHp() + attack * 12 + this.getDefense() * 12 + this.getMaxMp() * 0.1d + 0.95d * this.getDodge() / (this.getDodge() + 1662) * 584
				+ this.getAttackSpeed() * 5.84d + (this.getIceDefence() + this.getPoisonDefence() + this.getRayDefence()) * 10
				+ ((double)this.getAddInjureValue() / Global.MAX_PROBABILITY + (double)this.getReduceInjureValue() / Global.MAX_PROBABILITY) * 584
				+ ((double)(this.getAttributeByType(Attributes.Q_REDUCE_DAMAGE.getValue()) + this.getAttributeByType(Attributes.Q_REBOUND_DAMAGE.getValue())))/ Global.MAX_PROBABILITY * 584;
		fightPower += this.attributeCount * 256;
		this.fightPower =(int) Math.ceil(fightPower);//零舍一入
		return this.fightPower;
	}
	/**
	 * 获取装备最大增加攻击力
	 * @return
	 */
	public int getPhysicAttackUpper(int playerLevel) {
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value=model.getQ_max_physicattack();
		
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.Q_PHYSICATTACK_BYLEVEL.getValue()) {
					value += (int) (playerLevel / attribute.getValue());
				}
				// if (attribute.getType() == Attributes.Q_PHYSICATTACK_PERCENT.getValue()) {
				// value += (int) (value * attribute.getValue() / Global.MAX_PROBABILITY);
				// }
			}
		}
		
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				value += strengthBean.getQ_max_physicattack();
				
			}
		}
		//计算追加属性
		if(addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if(q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_max_physicattack();
			}
		}
		return value;
	}
	/**
	 * 获取装备最大增加攻击力
	 * @return
	 */
	public int getPhysicAttackLower(int playerLevel) {
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value=model.getQ_min_physicattack();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.Q_PHYSICATTACK_BYLEVEL.getValue()) {
					value += (int) (playerLevel / attribute.getValue());
				}
				// if (attribute.getType() == Attributes.Q_PHYSICATTACK_PERCENT.getValue()) {
				// value += (int) (value * attribute.getValue() / Global.MAX_PROBABILITY);
				// }
			}
		}
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				value += strengthBean.getQ_min_physicattack();
			}
		}
		//计算追加属性
		if(addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if(q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_min_physicattack();
			}
		}
		return value;
	}
	
	/**
	 * 获取最大魔法攻击
	 * @return
	 */
	public int getMagicAttackUpper(int playerLevel) {
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value=model.getQ_max_magicattack();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.Q_MAGICATTACK_BYLEVEL.getValue()) {
					value += (int) (playerLevel / attribute.getValue());
				}
				// else if (attribute.getType() == Attributes.Q_MAGICATTACK_PERCENT.getValue()) {
				// value += (int) (value * attribute.getValue() / Global.MAX_PROBABILITY);
				// }
			}
		}
		
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				value += strengthBean.getQ_max_magicattack();
			}
		}
		// 计算追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_max_magicattack();
			}
		}
		return value;
	}
	/**
	 * 获取最小魔法攻击
	 * @return
	 */
	public int getMagicAttackLower(int playerLevel) {
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value=model.getQ_min_magicattack();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.Q_MAGICATTACK_BYLEVEL.getValue()) {
					value += (int) (playerLevel / attribute.getValue());
				}
				// else if (attribute.getType() == Attributes.Q_MAGICATTACK_PERCENT.getValue()) {
				// value += (int) (value * attribute.getValue() / Global.MAX_PROBABILITY);
				// }
			}
		}
		
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				value += strengthBean.getQ_min_magicattack();
			}
		}
		// 计算追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_min_magicattack();
			}
		}
		return value;
	}

	/**
	 * 获得毒抗
	 * @return
	 */
	public int getPoisonDefence(){
		int value=parseElementDefence(1);
		// Q_equip_appendBean append =
		// ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		// if (append != null) {
		// // 计算附加属性
		// for (int i = 0; i < attributes.size(); i++) {
		// Attribute attribute = attributes.get(i);
		// if (attribute.getType() == Attributes.POISON_DEF.getValue()) {
		// }
		// }
		// }
		
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				int addValue=parseElementDefenceStrenght(strengthBean,1);
				value += addValue;
			}
		}
		// 计算追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				int addValue = parseElementDefenceAddAttribute(q_item_add_attributeBean, 1);
				value += addValue;
			}
		}
		return value;
	}
	/**
	 * 获得冰抗
	 * @return
	 */
	public int getIceDefence(){
		int value= parseElementDefence(2);
		// Q_equip_appendBean append =
		// ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		// if (append != null) {
		// // 计算附加属性
		// for (int i = 0; i < attributes.size(); i++) {
		// Attribute attribute = attributes.get(i);
		// if (attribute.getType() == Attributes.ICE_DEF.getValue()) {
		// }
		// }
		// }
		
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				int addValue = parseElementDefenceStrenght(strengthBean, 2);
				value += addValue;
			}
		}
		// 计算追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				int addValue = parseElementDefenceAddAttribute(q_item_add_attributeBean, 2);
				value += addValue;
			}
		}
		return value;
		
	}
	/**
	 * 获得电抗
	 * @return
	 */
	public int getRayDefence(){
		int value= parseElementDefence(3);
		// Q_equip_appendBean append =
		// ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		// if (append != null) {
		// // 计算附加属性
		// for (int i = 0; i < attributes.size(); i++) {
		// Attribute attribute = attributes.get(i);
		// if (attribute.getType() == Attributes.RAY_DEF.getValue()) {
		//
		// }
		// }
		// }
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				int addValue = parseElementDefenceStrenght(strengthBean, 3);
				value += addValue;
			}
		}
		// 计算追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				int addValue = parseElementDefenceAddAttribute(q_item_add_attributeBean, 3);
				value += addValue;
			}
		}
		return value;
		
	}
	/**
	 * 解析强化后的毒、冰、电（type分别为：1，2，3）抗的值。格式 type,value;type,value;...。
	 * @param strengthBean
	 * @return
	 */
	public int parseElementDefenceStrenght(Q_item_strengthBean strengthBean,int type){
		String elementDefence=strengthBean.getQ_element_defence();
		if(elementDefence==null||"".equals(elementDefence)||"0".equals(elementDefence)){
			return 0;
		}
		String[] elementDefenceList=elementDefence.split(";");
		for(String elementDefenceListItem:elementDefenceList){
			String[] defenceValue=elementDefenceListItem.split(",");
			int defenceType=Integer.parseInt(defenceValue[0]);
			int value=Integer.parseInt(defenceValue[1]);
			if(defenceType==type){
				return value;
			}
		}
		return 0;
	}

	public int parseElementDefenceAddAttribute(Q_item_add_attributeBean addAttributeBean, int type) {
		String elementDefence = addAttributeBean.getQ_element_defence();
		if (elementDefence == null || "".equals(elementDefence) || "0".equals(elementDefence)) {
			return 0;
		}
		String[] elementDefenceList = elementDefence.split(";");
		for (String elementDefenceListItem : elementDefenceList) {
			String[] defenceValue = elementDefenceListItem.split(",");
			int defenceType = Integer.parseInt(defenceValue[0]);
			int value = Integer.parseInt(defenceValue[1]);
			if (defenceType == type) {
				return value;
			}
		}
		return 0;
	}
	/**
	 * 解析毒、冰、电（type分别为：1，2，3）抗的值。格式 type,value;type,value;...。
	 */
	public int parseElementDefence(int type){
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		String elementDefence=model.getQ_element_defence();
		if(elementDefence==null||"".equals(elementDefence)||"0".equals(elementDefence)){
			return 0;
		}
		String[] elementDefenceList=elementDefence.split(";");
		for(String elementDefenceListItem:elementDefenceList){
			String[] defenceValue=elementDefenceListItem.split(",");
			int defenceType=Integer.parseInt(defenceValue[0]);
			int value=Integer.parseInt(defenceValue[1]);
			if(defenceType==type){
				return value;
			}
		}
		return 0;
		
	}
	
	/**
	 * 获得毒攻击
	 * @return
	 */
	public int getPoisonAttack(){
		int value=parseElementAttack(1);
		
		// Q_equip_appendBean append =
		// ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		// if (append != null) {
		// // 计算附加属性
		// for (int i = 0; i < attributes.size(); i++) {
		// Attribute attribute = attributes.get(i);
		// if (attribute.getType() == Attributes.POISON_ATTACK.getValue()) {
		//
		// }
		// }
		// }
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				int addValue=parseElementAttackStrenght(strengthBean,1);
				value += addValue;
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				int addValue = parseElementAttackAddAttribute(q_item_add_attributeBean, 1);
				value += addValue;
			}
		}
		return value;
	}
	/**
	 * 获得冰攻击
	 * @return
	 */
	public int getIceAttack(){
		int value=parseElementAttack(2);
		
		// Q_equip_appendBean append =
		// ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		// if (append != null) {
		// // 计算附加属性
		// for (int i = 0; i < attributes.size(); i++) {
		// Attribute attribute = attributes.get(i);
		// if (attribute.getType() == Attributes.ICE_ATTACK.getValue()) {
		//
		// }
		// }
		// }
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				int addValue = parseElementAttackStrenght(strengthBean, 2);
				value += addValue;
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				int addValue = parseElementAttackAddAttribute(q_item_add_attributeBean, 2);
				value += addValue;
			}
		}
		return value;
	}
	/**
	 * 获得电攻击
	 * @return
	 */
	public int getRayAttack(){
		int value=parseElementAttack(3);
		// Q_equip_appendBean append =
		// ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		// if (append != null) {
		// // 计算附加属性
		// for (int i = 0; i < attributes.size(); i++) {
		// Attribute attribute = attributes.get(i);
		// if (attribute.getType() == Attributes.RAY_ATTACK.getValue()) {
		//
		// }
		// }
		// }
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean strengthBean = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (strengthBean != null) {
				int addValue = parseElementAttackStrenght(strengthBean, 3);
				value += addValue;
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				int addValue = parseElementAttackAddAttribute(q_item_add_attributeBean, 3);
				value += addValue;
			}
		}
		return value;
	}
	/**
	 * 解析强化后的毒、冰、电（type分别为：1，2，3）攻击的值。格式 type,value;type,value;...。
	 * @param strengthBean
	 * @return
	 */
	public int parseElementAttackStrenght(Q_item_strengthBean strengthBean,int type){
		String elementAttack=strengthBean.getQ_element_attack();
		if(elementAttack==null||"".equals(elementAttack)||"0".equals(elementAttack)){
			return 0;
		}
		String[] elementAttackList=elementAttack.split(";");
		for(String elementAttackListItem:elementAttackList){
			String[] attackValue=elementAttackListItem.split(",");
			int attackType=Integer.parseInt(attackValue[0]);
			int value=Integer.parseInt(attackValue[1]);
			if(attackType==type){
				return value;
			}
		}
		return 0;
	}

	public int parseElementAttackAddAttribute(Q_item_add_attributeBean addAttributeBean, int type) {
		String elementAttack = addAttributeBean.getQ_element_attack();
		if (elementAttack == null || "".equals(elementAttack) || "0".equals(elementAttack)) {
			return 0;
		}
		String[] elementAttackList = elementAttack.split(";");
		for (String elementAttackListItem : elementAttackList) {
			String[] attackValue = elementAttackListItem.split(",");
			int attackType = Integer.parseInt(attackValue[0]);
			int value = Integer.parseInt(attackValue[1]);
			if (attackType == type) {
				return value;
			}
		}
		return 0;
	}
	/**
	 * 解析毒、冰、电（type分别为：1，2，3）攻击的值。格式 type,value;type,value;...。
	 */
	public int parseElementAttack(int type){
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		String elementAttack=model.getQ_element_attack();
		if(elementAttack==null||"".equals(elementAttack)||"0".equals(elementAttack)){
			return 0;
		}
		String[] elementAttackList=elementAttack.split(";");
		for(String elementAttackListItem:elementAttackList){
			String[] attackValue=elementAttackListItem.split(",");
			int attackType=Integer.parseInt(attackValue[0]);
			int value=Integer.parseInt(attackValue[1]);
			if(attackType==type){
				return value;
			}
		}
		return 0;
		
	}
	/**
	 * 获取基础攻击命中
	 * @return
	 */
	public int getHitValue(){
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_hit();
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_hit();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_hit();
			}
		}
		return value;
	}
	/**
	 * 获取会心一击率
	 * @return
	 */
	public int getKnowingAttackValue() {
		// Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		// if (model == null) {
		// log.error("Item model " + this.getItemModelId() + " not found!");
		// return 0;
		// }
		// return model.getQ_max_damage_hit();
		int value = 0;
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			if (attribute.getType() == Attributes.KNOWING_ATTACKPERCENT.getValue()) {
				value += attribute.getValue();
			}
		}
		return value;
	}
	
	/**
	 * 获取无视一击率
	 * 
	 * @return
	 */
	public int getIgnoreAttackValue() {
		int value = 0;
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			if (attribute.getType() == Attributes.IGNORE_ATTACKPERCENT.getValue()) {
				value += attribute.getValue();
			}
		}
		return value;
	}

	/**
	 * int perfectAttackpercent=0;//卓越一击
	 * 
	 * @return
	 */
	public int getperfectAttackpercent(){
		return getAttributeByType(Attributes.PERFECT_ATTACKPERCENT.getValue());
	}

	/**
	 * 获取增加伤害属性
	 * 
	 * @return
	 */
	public int getAddInjureValue() {
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		return model.getQ_add_injure();
	}
	
	/**
	 * 获取吸收伤害属性
	 * 
	 * @return
	 */
	public int getReduceInjureValue() {
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		return model.getQ_reduce_injure();
	}

	/**
	 * 通过类型id获得对应的附加属性
	 * 
	 * @param type
	 * @return
	 */
	public int getAttributeByType(int type){
		List<Attribute> attributes=getAttributes();
		for(Attribute attribute:attributes){
			if(type==attribute.getType()){
				return attribute.getValue();
			}
		}
		return 0;
	}
	
	/**
	 * 获取装备增加攻击力
	 * 
	 * @return
	 */
	public int getAttack() {
		// 获取基本攻击
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_attack();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.ATTACK.getValue()) {
					// value += (int) ((append.getQ_attack_min() + (double) (append.getQ_attack_max() - append.getQ_attack_min())* attribute.getValue() / 100) * getQualityAdd() /
					// Global.MAX_PROBABILITY);
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_attack();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_attack();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加防御力
	 * 
	 * @return
	 */
	public int getDefense() {
		// 获取基本防御
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_defence();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.DEFENSE.getValue()) {
					// value += (int) ((append.getQ_defence_min() + (double) (append
					// .getQ_defence_max() - append.getQ_defence_min())
					// * attribute.getValue() / 100) * getQualityAdd() / Global.MAX_PROBABILITY);
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_defence();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_defence();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加暴击
	 * 
	 * @return
	 */
	public int getCrit() {
		// 获取基本暴击
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_crit();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if(attribute.getType() == Attributes.PERFECT_ATTACKPERCENT.getValue()){
					// value += (int)((append.getQ_crit_min() + (double)(append.getQ_crit_max() - append.getQ_crit_min()) * attribute.getValue() / 100) * getQualityAdd() /
					// Global.MAX_PROBABILITY);
					//panic god add  value += (int)((append.getQ_crit_min() + (double)(append.getQ_crit_max() - append.getQ_crit_min()) * attribute.getValue() / 100) * getQualityAdd() / Global.MAX_PROBABILITY);
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_crit();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_crit();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加闪避
	 * 
	 * @return
	 */
	public int getDodge() {
		// 获取基本闪避
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_dodge();
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_dodge();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_dodge();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加最大生命
	 * 
	 * @return
	 */
	public int getMaxHp() {
		// 获取基本最大生命
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_max_hp();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer
				.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.MAXHP.getValue()) {
					// value += (int) ((append.getQ_hp_min() + (double) (append.getQ_hp_max() - append.getQ_hp_min())* attribute.getValue() / 100) * getQualityAdd() /
					// Global.MAX_PROBABILITY);
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer
					.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_max_hp();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_max_hp();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加最大魔法值
	 * 
	 * @return
	 */
	public int getMaxMp() {
		// 获取基本最大魔法值
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_max_mp();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer
				.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.MAXMP.getValue()) {
					// value += (int) ((append.getQ_mp_min() + (double) (append.getQ_mp_max() - append.getQ_mp_min())* attribute.getValue() / 100) * getQualityAdd() /
					// Global.MAX_PROBABILITY);
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer
					.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_max_mp();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_max_mp();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加最大体力
	 * 
	 * @return
	 */
	public int getMaxSp() {
		// 获取基本最大体力
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_max_sp();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer
				.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.MAXSP.getValue()) {
					// value += (int) ((append.getQ_sp_min() + (double) (append
					// .getQ_sp_max() - append.getQ_sp_min())
					// * attribute.getValue() / 100) * getQualityAdd() / Global.MAX_PROBABILITY);
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer
					.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_maxsp();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_maxsp();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加攻击速度
	 * 
	 * @return
	 */
	public int getAttackSpeed() {
		// 获取基本攻击速度
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_attackspeed();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer
				.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				// 两条属性都是加值的，不是百分比
				if (attribute.getType() == Attributes.Q_ATTACKSPEED.getValue() || attribute.getType() == Attributes.ATTACKSPEED.getValue()) {
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer
					.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_attackspeed();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_attackspeed();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加速度
	 * 
	 * @return
	 */
	public int getSpeed() {
		// 获取基本速度
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_speed();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer
				.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.SPEED.getValue()) {
					// value += (int) ((append.getQ_speed_min() + (double) (append.getQ_speed_max() - append.getQ_speed_min())* attribute.getValue() / 100) * getQualityAdd() /
					// Global.MAX_PROBABILITY);
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 天生属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer
					.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_speed();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_speed();
			}
		}
		return value;
	}

	/**
	 * 获取装备增加幸运
	 * 
	 * @return
	 */
	public int getLuck() {
		// 获取基本幸运
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return 0;
		}
		int value = model.getQ_luck();
		Q_equip_appendBean append = ManagerPool.dataManager.q_equip_appendContainer
				.getMap().get(this.getItemModelId());
		if (append != null) {
			// 计算附加属性
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getType() == Attributes.LUCK.getValue()) {
					// value += (int) ((append.getQ_luck_min() + (double) (append
					// .getQ_luck_max() - append.getQ_luck_min())
					// * attribute.getValue() / 100) * getQualityAdd() / Global.MAX_PROBABILITY);
					value += attribute.getValue();
				}
			}
		}
		if (gradeNum > 0) {
			// 强化属性
			Q_item_strengthBean born = DataManager.getInstance().q_item_strengthContainer.getMap().get(getItemModelId() + "_" + gradeNum);
			if (born != null) {
				value += born.getQ_luck();
			}
		}
		// 追加属性
		if (addAttributeLevel > 0) {
			Q_item_add_attributeBean q_item_add_attributeBean = DataManager.getInstance().q_item_add_attributeContainer.getMap().get(getItemModelId() + "_" + addAttributeLevel);
			if (q_item_add_attributeBean != null) {
				value += q_item_add_attributeBean.getQ_luck();
			}
		}
		return value;
	}

	/**
	 * 是否顶级强化
	 * 
	 * @return
	 */
	public boolean isFullStrength() {
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap()
				.get(this.getItemModelId());
		if (model == null) {
			log.error("Item model " + this.getItemModelId() + " not found!");
			return false;
		}
		if (this.getGradeNum() == model.getQ_max_strengthen()) {
			return true;
		}
		return false;
	}

	/**
	 * 是否顶级附加
	 * 
	 * @return
	 */
	public boolean isFullAppend() {
		if (attributes != null && attributes.size() == 6) {
			for (int i = 0; i < 6; i++) {
				Attribute attribute = attributes.get(i);
				if (attribute.getValue() < 100)
					return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 获得品质 0-白色 1-黄色 2-绿色 3-蓝色 4-紫色
	 * 
	 * @return
	 */
	public int getQuality() {
		if (attributes != null && attributes.size() > 0) {
			int num = attributes.size();
			if (num < 2) {
				// 白色装备
				return 0;
			} else if (num < 4) {
				// 黄色装备
				return 1;
			} else if (num == 4) {
				// 绿色装备
				return 2;
			} else if (num == 5) {
				// 蓝色装备
				return 3;
			} else if (num == 6) {
				// 紫色装备
				return 4;
			}
		}
		return 0;
	}

	/**
	 * 获得品质加成数值
	 * 
	 * @return
	 */
	public int getQualityAdd() {
		return 1;
//		if (attributes != null && attributes.size() > 0) {
//			int num = attributes.size();
//			if (num < 2) {
//				// 白色装备
//				return Global.MAX_PROBABILITY;
//			} else if (num < 4) {
//				// 黄色装备
//				return Global.YELLOW_EQUIP_ADD;
//			} else if (num == 4) {
//				// 绿色装备
//				return Global.GREEN_EQUIP_ADD;
//			} else if (num == 5) {
//				// 蓝色装备
//				return Global.BLUE_EQUIP_ADD;
//			} else if (num == 6) {
//				// 紫色装备
//				return Global.PURPLE_EQUIP_ADD;
//			}
//		}
//		return Global.MAX_PROBABILITY;
	}

	/**
	 * 是否顶级镶嵌
	 * 
	 * @return
	 */
	public boolean isFullEnchase() {
		return false;
	}

	/**
	 * 最高星数强化失败次数
	 * 
	 * @return
	 */
	public short getGradefailnum() {
		return gradefailnum;
	}

	/**
	 * 最高星数强化失败次数
	 * 
	 * @return
	 */
	public void setGradefailnum(short gradefailnum) {
		this.gradefailnum = gradefailnum;
	}

	/**
	 * 曾进行强化的最高星数
	 * 
	 * @return
	 */
	public byte getHighestgrade() {
		return highestgrade;
	}

	/**
	 * 曾进行强化的最高星数
	 * 
	 * @param highestgrade
	 */
	public void setHighestgrade(byte highestgrade) {
		this.highestgrade = highestgrade;
	}

	/**
	 * 进阶失败次数
	 * 
	 * @return
	 */
	public short getAdvfailnum() {
		return advfailnum;
	}

	/**
	 * 进阶失败次数
	 * 
	 * @return
	 */
	public void setAdvfailnum(short advfailnum) {
		this.advfailnum = advfailnum;
	}

	public int getFightPower() {
		return fightPower;
	}

	public void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}

	public int getAddAttributeLevel() {
		return addAttributeLevel;
	}

	public void setAddAttributeLevel(int addAttributeLevel) {
		this.addAttributeLevel = addAttributeLevel;
	}

	/**
	 * 强化等级
	 * 
	 * @return
	 */
	public int getGradeNum() {
		return gradeNum;
	}

	/**
	 * 强化等级
	 * 
	 * @return
	 */
	public void setGradeNum(int gradeNum) {
		this.gradeNum = gradeNum;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Jewel> getJewels() {
		return jewels;
	}

	public void setJewels(List<Jewel> jewels) {
		this.jewels = jewels;
	}

	public boolean isCanUse() {
		return canUse;
	}

	public void setCanUse(boolean canUse) {
		this.canUse = canUse;
	}
	public int getAttributeCount() {
		return attributeCount;
	}
	public void setAttributeCount(int attributeCount) {
		this.attributeCount = attributeCount;
	}

}
