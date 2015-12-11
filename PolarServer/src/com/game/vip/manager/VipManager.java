package com.game.vip.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_shopBean;
import com.game.data.bean.Q_vipBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.languageres.manager.ResManager;
import com.game.liveness.manager.LivenessManager;
import com.game.mail.manager.MailServerManager;
import com.game.manager.ManagerPool;
import com.game.player.manager.PlayerAttributeManager;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttributeType;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;
import com.game.utils.ParseUtil;
import com.game.utils.TimeUtil;
import com.game.vip.bean.VipInfo;
import com.game.vip.log.RoleVipLog;
import com.game.vip.message.ResOpenVIPMessage;
import com.game.vip.message.ResPlayerVIPInfoMessage;
import com.game.vip.struts.GuideType;
import com.game.vip.struts.VipRight;
import com.game.zones.manager.ZonesTeamManager;

public class VipManager {

	private static Logger logger = Logger.getLogger(VipManager.class);

	private static Object obj = new Object();

	// 用于体验卡10分钟的过期检查
	private final Timer timer = new Timer();
	// 管理类实例
	private static VipManager manager;

	private VipManager() {
	}

	public static VipManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new VipManager();
			}
		}
		return manager;
	}

	/**********************************************************************************************************************/
	// notice:vip卡限制不可叠加，防止批量使用
	private enum Card {

		Experi("vip体验卡", 60, 1, 700069, 0, 1), Month("vip月卡", 30 * 24 * 60, 1, 700070, 60001, 2), Quarter("vip季度卡", 90 * 24 * 60, 2, 700071, 60001, 3), Year("vip半年卡",
				180 * 24 * 60, 4, 700072, 60001, 4);

		String name;
		int time;// 持续时间,分钟
		int level;// 使用后的vip等级
		int itemId;// 道具id
		int shopId;// 商店id
		int stage;// vip等阶： 0无vip，1体验卡，2月卡，3季度卡，4半年年卡

		Card(String name, int time, int level, int itemId, int shopId, int stage) {
			this.name = name;
			this.time = time;
			this.level = level;
			this.itemId = itemId;
			this.shopId = shopId;
			this.stage = stage;
		}

		static Card findByItemModelId(int itemModelId) {
			for (Card card : values()) {
				if (card.itemId == itemModelId) {
					return card;
				}
			}
			return null;
		}
	}
	/**********************************************************************************************************************/

	/**
	 * 直接使用钻石购买
	 * 
	 * @param player
	 * @param openType
	 *            开通类型，1 自己开通，2 帮好友开通
	 * @param vipType
	 *            购买类型 0=体验卡，1=月卡，2=季度卡，3=半年卡
	 * @param friendName
	 */
	public void buyVIP(Player player, byte openType, byte vipType, String friendName) {
		Card card = null;
		switch (vipType) {
		case 0:
			card = Card.Experi;
			break;
		case 1:
			card = Card.Month;
			break;
		case 2:
			card = Card.Quarter;
			break;
		case 3:
			card = Card.Year;
			break;
		default:
			return;
		}
		//返回开通结果
		ResOpenVIPMessage res = new ResOpenVIPMessage();
		Q_shopBean q_shopBean = DataManager.getInstance().q_shopContainer.getMap2().get(card.shopId + "_" + card.itemId);
		if (q_shopBean != null) {
			int q_gold = q_shopBean.getQ_gold();
			int gold = player.getGold() != null ? player.getGold().getGold() : 0;
			if (gold < q_gold) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，你的钻石不足"));
				return;
			}
			if (openType == 2) {
				// 检查有没有这个好友
				if (StringUtils.isBlank(friendName)) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("查无此人"));
					return;
				}
				long friendId = 0;
				Player friend = PlayerManager.getInstance().getOnlinePlayerByName(friendName);
				if (friend == null) {
					if(PlayerManager.getInstance().hasName(friendName)) {
						friendId = PlayerManager.getInstance().getOfflinePlayerIdByName(friendName);
					}
				} else {
					friendId = friend.getId();
				}
				if (friendId == 0) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("查无此人"));
					return;
				}
				if (BackpackManager.getInstance().changeGold(player, -q_gold, Reasons.VIP_CARD, Config.getId())) {
					List<Item> createItems = Item.createItems(card.itemId, 1, true, 0);
					if (createItems != null && createItems.size() == 1) {
						MailServerManager.getInstance().sendSystemMail(friendId, friendName, "朋友的馈赠！", "您的好友 " + player.getName() + " 慷慨的馈赠给您" + card.name + "！",
								(byte) 0, 0,createItems);
						if (friend != null) {
							MessageUtil.notify_player(friend, Notifys.STATE, ResManager.getInstance().getString("恭喜你，你的好友 {1} 送了你一张vip卡，请到邮箱领取"), player.getName());
						}
						MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("赠送成功！"));
					} else {
						logger.error("sb策划又配错vip卡了");
					}

				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，你的钻石不足"));
				}
			} else {
				if (BackpackManager.getInstance().changeGold(player, -q_gold, Reasons.VIP_CARD, Config.getId())) {
					openVIP(player, card);
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，你的钻石不足"));
				}
			}
			res.succeed();
		} else {
			logger.error("策划配置的vip id错误");
		}
		MessageUtil.tell_player_message(player, res);
	}

	/**
	 * 使用vip卡
	 * 
	 * @param player
	 * @param item
	 */
	public boolean useVIPCard(Player player, Item item) {
		Card card = Card.findByItemModelId(item.getItemModelId());
		if (card == null) {
			return false;
		}
		// 扣了物品再说
		if (!BackpackManager.getInstance().removeItem(player, item, 1, Reasons.VIP_CARD, Config.getId())) {
			return false;
		}
		return openVIP(player, card);
	}

	/**
	 * 获得vip权利
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	private boolean openVIP(final Player player, Card card) {
		if (player == null || card == null) {
			return false;
		}
		RoleVipLog vipLog = new RoleVipLog();
		vipLog.setPlayerid(player.getId());
		@SuppressWarnings("deprecation")
		VipRight vipright = player.getVipright();
		vipLog.setBefvipid(vipright.getVipLevel());
		// 1、第一次使用，开启vip；2、过期续卡；3、使用期续卡
		if (isVIP(player)) {
			// 使用期续卡
			// 叠加时间
			vipright.setEndTime(vipright.getEndTime() + card.time * 60);
			if (card == Card.Experi) {
				vipright.setHasTimer(true);
				// 检查体验卡是否过期
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// 可能中途使用其它vip卡,所以这里不能直接把等阶设为0
						zeroCheck(player);
					}
				}, new Date(vipright.getEndTime() * 1000));
			}
			//至尊提示
			if(card == Card.Year && vipright.getVipStage() < Card.Year.stage) {
				ParseUtil parseUtil = new ParseUtil();
				parseUtil.setValue(String.format(ResManager.getInstance().getString(String.format("恭喜{@}成为至尊VIP，生命值增加%d、攻击力增加%d、防御力增加%d！（{@}）", 250, 70, 40))),new ParseUtil.PlayerParm(player.getId(), player.getName()), new ParseUtil.VipParm(VipManager.getInstance().getVIPLevel(player), GuideType.VIP.getValue()));
				String notice = parseUtil.toString();
				MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM,notice);
				MessageUtil.notify_All_player(Notifys.CUTOUT,notice);
			}
			// 提升等阶
			if (vipright.getVipStage() < card.stage) {
				vipright.setVipStage(card.stage);
			}
			// 提升等级
			if (vipright.getVipLevel() < card.level) {
				vipright.setVipLevel(card.level);
			}
			fireListener(player);
			MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("恭喜你成功续费VIP，成为{1}VIP"), isHighVIP(player) ? "高级" : "至尊");
			vipLog.setType(2);
		} else {
			// 重置时间，除体验卡外统一设置结束时间为0点
			if (card == Card.Experi) {
				vipright.setHasTimer(true);
				vipright.setEndTime(System.currentTimeMillis() / 1000 + card.time * 60);
				// 检查体验卡是否过期
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// 可能中途使用其它vip卡,所以这里不能直接把等阶设为0
						zeroCheck(player);
					}
				}, new Date(vipright.getEndTime() * 1000));
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				vipright.setEndTime(calendar.getTimeInMillis() / 1000 + card.time * 60);
			}
			vipright.setPreCalculateExpTime(System.currentTimeMillis());
			// 重置等阶
			vipright.setVipStage(card.stage);
			// 提升等级
			if (vipright.getVipLevel() < card.level) {
				vipright.setVipLevel(card.level);
			}
			fireListener(player);
			//至尊提示
			if(card == Card.Year) {
				ParseUtil parseUtil = new ParseUtil();
				parseUtil.setValue(String.format(ResManager.getInstance().getString("恭喜{@}成为至尊VIP，生命值增加250、攻击力增加70、防御力增加40！（{@}）")),new ParseUtil.PlayerParm(player.getId(), player.getName()), new ParseUtil.VipParm(VipManager.getInstance().getVIPLevel(player), GuideType.VIP.getValue()));
				String notice = parseUtil.toString();
				MessageUtil.notify_All_player(Notifys.CHAT_SYSTEM,notice);
				MessageUtil.notify_All_player(Notifys.CUTOUT,notice);
			}
			long costGold = player.getGold() == null ? 0 : player.getGold().getCostGold();
			vipright.setCostGold(costGold);// 重置消费钻石数
			MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("恭喜你成为{1}VIP"), isHighVIP(player) ? "高级" : "至尊");
			LivenessManager.getInstance().changeVip(player);
			vipLog.setType(1);
		}
		// vip属性重置
		PlayerAttributeManager.getInstance().countPlayerAttribute(player, PlayerAttributeType.VIP);
		sendPlayerVipInfo(player);
		PlayerManager.getInstance().savePlayer(player);
		vipLog.setExp(vipright.getVipExp());
		vipLog.setAftvipid(vipright.getVipLevel());
		vipLog.setActionid(Config.getId());
		vipLog.setExpiretime((int) vipright.getEndTime());
		LogService.getInstance().execute(vipLog);
		logger.error("开通vip:"+player.getName()+",level:"+vipright.getVipLevel()+",stage:"+vipright.getVipStage());
		return true;
	}


	/**
	 * 登录检查，主要是体验卡的十分钟过期，有一种可能性是，玩家使用了体验卡后，服务器在这十分钟内重启了，导致定时器没执行，玩家在当天内都是vip
	 * 
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public void loginCheck(final Player player) {
		zeroCheck(player);
		sendPlayerVipInfo(player);
		VipRight vipright = player.getVipright();
		if (!vipright.isHasTimer() && vipright.getVipStage() ==  Card.Experi.stage) {
			// 检查体验卡是否过期
			vipright.setHasTimer(true);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					zeroCheck(player);
				}
			}, new Date(vipright.getEndTime() * 1000));
		}
		// ResVIPInfoWhenLoginMessage msg = new ResVIPInfoWhenLoginMessage();
		// msg.setVipLevel(this.getVIPLevel(player));
		// MessageUtil.tell_player_message(player, msg);
	}

	/**
	 * 在线玩家0点/当日首次登录触发 ：1、vip是否过期计算；2、vip用户增加vip经验点；3、过期vip用户衰减vip经验点
	 * 
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public void zeroCheck(Player player) {
		final int highPoint = 10;// 高级vip用户增加10点
		final int primePoint = 15;// 至尊vip用户增加15点
		VipRight vipright = player.getVipright();
		long endTime = vipright.getEndTime() * 1000;
		long currentTimeMillis = System.currentTimeMillis();
		if (endTime != 0) {
			long preCalculateExpTime = vipright.getPreCalculateExpTime();
			// vip是否过期
			if (currentTimeMillis >= endTime) {
				// 添加过期前的点数
				int betweenDays = TimeUtil.getBetweenDays(preCalculateExpTime, endTime);
				if (betweenDays > 0) {
					int addPoint = (isHighVIP(player) ? highPoint : primePoint) * betweenDays;
					alterVIPExp(player, addPoint);
				}
				// 重置为vip结束时间
				preCalculateExpTime = endTime;
				// 移除vip特权
				vipright.setVipStage(0);
				vipright.setEndTime(0);
				// 重新计算属性
				PlayerAttributeManager.getInstance().countPlayerAttribute(player, PlayerAttributeType.VIP);
				this.sendPlayerVipInfo(player);
				MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("很遗憾，你的VIP过期了"));
				fireListener(player);
				// 日志
				RoleVipLog vipLog = new RoleVipLog();
				vipLog.setPlayerid(player.getId());
				vipLog.setBefvipid(vipright.getVipLevel());
				vipLog.setType(4);
				vipLog.setAftvipid(vipright.getVipLevel());
				vipLog.setActionid(Config.getId());
				vipLog.setExpiretime((int) vipright.getEndTime());
				LogService.getInstance().execute(vipLog);
			}

			// 计算经验点
			if (isVIP(player)) {
				int betweenDays = TimeUtil.getBetweenDays(preCalculateExpTime, currentTimeMillis);
				if (betweenDays > 0) {
					// 最终要增加的点数
					int addPoint = (isHighVIP(player) ? highPoint : primePoint) * betweenDays;
					alterVIPExp(player, addPoint);
				}

			} else if (vipright.getVipLevel() > 0) {
				int betweenDays = TimeUtil.getBetweenDays(preCalculateExpTime, currentTimeMillis);
				if (betweenDays > 0) {
					int reducePoint = -5 * betweenDays;// 每天衰减点数
					alterVIPExp(player, reducePoint);
				}

			}
			vipright.setPreCalculateExpTime(currentTimeMillis);
			PlayerManager.getInstance().savePlayer(player);
		}
	}

	/**
	 * 修改vip经验点
	 * 
	 * @param player
	 * @param alter
	 */
	@SuppressWarnings("deprecation")
	private void alterVIPExp(Player player, int alter) {
		if (alter == 0) {
			return;
		}
		Q_vipBean q_vipBean = getQ_vipBeanNoStage(player);
		VipRight vipright = player.getVipright();
		RoleVipLog vipLog = new RoleVipLog();
		vipLog.setPlayerid(player.getId());
		vipLog.setBefvipid(vipright.getVipLevel());
		if (alter > 0) {
			while(vipright.getVipExp() + alter >= q_vipBean.getQ_exp()) {
				if (vipright.getVipLevel() + 1 > 9) {
					vipright.setVipExp(q_vipBean.getQ_exp());
					break;
				} else {
					int remain = alter - (q_vipBean.getQ_exp() - vipright.getVipExp());
					alter = remain;
					vipright.setVipExp(0);
					vipright.setVipLevel(vipright.getVipLevel() + 1);
					q_vipBean = getQ_vipBeanNoStage(player);
					vipLog.setType(3);
					if(isVIP(player)) {
						MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("恭喜你，你的VIP等级提升了"));
						// 重新计算属性
						PlayerAttributeManager.getInstance().countPlayerAttribute(player, PlayerAttributeType.VIP);
						fireListener(player);
					}
				}
			}
			//如果还没到9级满经验
			if(vipright.getVipExp() != q_vipBean.getQ_exp()) {
				vipright.setVipExp(vipright.getVipExp() + alter);
			}
		} else {
			if (vipright.getVipExp() + alter < 0) {
				// 掉级，最低1级
				if (vipright.getVipLevel() - 1 < 1) {
					vipright.setVipExp(0);
				} else {
					vipright.setVipLevel(vipright.getVipLevel() - 1);
					Q_vipBean q_vipBean2 = DataManager.getInstance().q_vipContainer.getMap().get(vipright.getVipLevel());
					vipright.setVipExp(q_vipBean2.getQ_exp() + (vipright.getVipExp() + alter));
					vipLog.setType(5);
					MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("很遗憾，你的VIP等级下降了"));
					// 重新计算属性
					PlayerAttributeManager.getInstance().countPlayerAttribute(player, PlayerAttributeType.VIP);
					fireListener(player);
				}
			} else {
				vipright.setVipExp(vipright.getVipExp() + alter);
			}
		}
		sendPlayerVipInfo(player);
		vipLog.setExp(vipright.getVipExp());
		vipLog.setAftvipid(vipright.getVipLevel());
		vipLog.setActionid(Config.getId());
		vipLog.setExpiretime((int) vipright.getEndTime());
		LogService.getInstance().execute(vipLog);
	}
	
	/**
	 * 等级变化的时候调用
	 * @param player
	 */
	private void fireListener(Player player) {
		try {
			ManagerPool.prayManager.stReqPrayInfo(player);
			ZonesTeamManager.getInstance().stReqZoneTeamOpenToGameMessage(player, 0);
		}catch(Exception e) {
			logger.error("vip升级报错：",e);
		}
	}
	public void addExpByGM(Player player, int alter) {
		this.alterVIPExp(player, alter);
	}

	private static final int COST_GOLD = 100;
	/**
	 * 统计离上次消费是否超过100钻石
	 * 
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public void costGold(Player player) {
//		if (!isVIP(player)) {
//			// 成为vip之前的不算
//			return;
//		}
		long nowCost = Math.abs(player.getGold() == null ? 0 : player.getGold().getCostGold());
		long preCost = Math.abs(player.getVipright().getCostGold());
		if (nowCost - preCost >= COST_GOLD) {
			long cost = nowCost - preCost;
			this.alterVIPExp(player, (int) (cost / COST_GOLD));
			player.getVipright().setCostGold(preCost + (cost / COST_GOLD * COST_GOLD));
			PlayerManager.getInstance().savePlayer(player);
		}
	}
	/**
	 * 获取当前vip等级 0-9
	 * 
	 * @param player
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public int getVIPLevel(Player player) {
		if (isVIP(player)) {
			return player.getVipright().getVipLevel();
		}
		return 0;
	}

	public int getVIPStage(Player player) {
		if (isVIP(player)) {
			return player.getVipright().getVipStage();
		}
		return 0;
	}
	/**
	 * 是否vip用户
	 * 
	 * @param player
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean isVIP(Player player) {
		return player.getVipright().getVipStage() != 0;
	}

	/**
	 * 是否高级vip用户
	 * 
	 * @param player
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private boolean isHighVIP(Player player) {
		int vipStage = player.getVipright().getVipStage();
		return vipStage >= 1 && vipStage < 4;
	}

	/**
	 * 是否至尊vip用户
	 * 
	 * @param player
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private boolean isPrimeVIP(Player player) {
		return player.getVipright().getVipStage() == 4;
	}

	@SuppressWarnings("deprecation")
	private Q_vipBean getQ_vipBean(Player player) {
		int vipLevel = 0;
		if (isVIP(player)) {
			vipLevel = player.getVipright().getVipLevel();
		}
		return DataManager.getInstance().q_vipContainer.getMap().get(vipLevel);
	}
	
	@SuppressWarnings("deprecation")
	private Q_vipBean getQ_vipBeanNoStage(Player player) {
		int vipLevel = player.getVipright().getVipLevel();
		return DataManager.getInstance().q_vipContainer.getMap().get(vipLevel);
	}
	/**
	 * 
	 * @param player
	 * @return vip属性加成
	 */
	public String getAttributeAddition(Player player) {
		return getQ_vipBean(player).getQ_attribute();
	}
	/**
	 * @param player
	 * @return 交易税比例,万分比(无vip是2000，其他是200)
	 */
	public int getTransactionTax(Player player) {
		return getQ_vipBean(player).getQ_transaction();
	}

	/**
	 * @param player
	 * @return 是否可以循环挂机
	 */
	public boolean canHangUp(Player player) {
		return getQ_vipBean(player).getQ_hangUp() == 1;
	}

	/**
	 * 
	 * @param player
	 * @return 使用远程商店需要消耗的钻石数(无vip是2，其他是0)
	 */
	public int getShopCost(Player player) {
		return getQ_vipBean(player).getQ_shop();
	}

	/**
	 * 
	 * @param player
	 * @return 使用远程仓库需要消耗的钻石数(无vip是2，其他是0)
	 */
	public int getStorageCost(Player player) {
		return getQ_vipBean(player).getQ_storage();
	}

	/**
	 * @param player
	 * @return 是否可以使用聊天表情
	 */
	public boolean canUseChatFaces(Player player) {
		return getQ_vipBean(player).getQ_chat() == 1;
	}

	/**
	 * 
	 * @param player
	 * @return 恶魔广场/血色城堡 可增加次数
	 */
	public int getDevilAddition(Player player) {
		return getQ_vipBean(player).getQ_devil();
	}

	/**
	 * @param player
	 * @return 是否可以补签
	 */
	public boolean canReSignIn(Player player) {
		return getQ_vipBean(player).getQ_signIn() == 1;
	}

	/**
	 * 
	 * @param player
	 * @return 每天增加活跃值点数
	 */
	public int getActiveAddition(Player player) {
		return getQ_vipBean(player).getQ_active();
	}

	/**
	 * @param player
	 * @return 副本翻牌免费次数
	 */
	public int getMissionLotteryAddition(Player player) {
		return getQ_vipBean(player).getQ_lottery();
	}

	/**
	 * 
	 * @param player
	 * @return 恶魔广场、血色城堡是否可以使用高级鼓舞功能
	 */
	public boolean canInspireAddition(Player player) {
		return getQ_vipBean(player).getQ_inspire() == 1;
	}

	/**
	 * 
	 * @param player
	 * @return 是否可以进入vip商城
	 */
	public boolean canOpenVIPStore(Player player) {
		return getQ_vipBean(player).getQ_store() == 1;
	}

	/**
	 * @param player
	 * @return 是否可以免费传送
	 */
	public boolean canFreeTransfer(Player player) {
		return getQ_vipBean(player).getQ_transfer() == 1;
	}

	/**
	 * 
	 * @param player
	 * @return 是否可以钻石提升坐骑
	 */
	public boolean canStrengthHorseByGold(Player player) {
		return getQ_vipBean(player).getQ_horse() == 1;
	}

	/**
	 * 
	 * @param player
	 * @return 是否可以使用高级强化
	 */
	public boolean canPrimeStrength(Player player) {
		return getQ_vipBean(player).getQ_strength() == 1;
	}

	/**
	 * 
	 * @param player
	 * @return 杀怪加成经验，万分比
	 */
	public int getMonsterExpAddition(Player player) {
		return getQ_vipBean(player).getQ_monsterExp();
	}

	/**
	 * 
	 * @param player
	 * @return 爬塔加成次数
	 */
	public int getPtAddition(Player player) {
		return getQ_vipBean(player).getQ_pt();
	}

	/**
	 * 
	 * @param player
	 * @return 额外增加祈愿次数
	 */
	public int getPrayAddition(Player player) {
		return getQ_vipBean(player).getQ_pray();
	}
	@SuppressWarnings("deprecation")
	public VipInfo sendPlayerVipInfo(Player player) {
		VipInfo vipInfo = new VipInfo();
		VipRight vipright = player.getVipright();
		vipInfo.setStage(vipright.getVipStage());
		vipInfo.setRealLevel(vipright.getVipLevel());
		vipInfo.setLevel(getVIPLevel(player));
		vipInfo.setExp(vipright.getVipExp());
		if (isVIP(player))
			vipInfo.setRemain((int) vipright.getEndTime());
		ResPlayerVIPInfoMessage resmsg = new ResPlayerVIPInfoMessage();
		resmsg.setVipinfo(vipInfo);
		MessageUtil.tell_player_message(player, resmsg);
		logger.error("send vipInfo:"+player.getName()+",level:"+vipright.getVipLevel()+",stage:"+vipright.getVipStage()+",time:"+vipright.getEndTime());
		return vipInfo;
	}

	/*********************************************************************************************************/
	/**
	 * 得到玩家VIP等级   0-无VIP  1-白金VIP 2-钻石VIP 3-至尊VIP
	 */
	// public int getPlayerVipId(Player player){
	// int vipid = 0;
	// List<Q_vipBean> viplist = DataManager.getInstance().q_vipContainer.getList();
	// for(Q_vipBean bean: viplist){
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, bean.getQ_buffid());
	// if(bufflist.size()>0){
	// vipid = bean.getQ_id();
	// break;
	// }
	// }
	//
	// if(vipid == 101) {//临时VIP白金卡
	// vipid=1;
	// }else if (vipid == 103) {//临时VIP至尊卡
	// vipid=3;
	// }
	// return vipid;
	// }
	//
	// /**
	// * 得到玩家VIP卡ID 0-无VIP 1-白金VIP 2-钻石VIP 3-至尊VIP 101-临时VIP
	// * 需要知道是 真正的白金VIP 还是 临时VIP 时调用此方法
	// */
	// public int getPlayerVipIdReal(Player player){
	// int vipid = 0;
	// List<Q_vipBean> viplist = DataManager.getInstance().q_vipContainer.getList();
	// for(Q_vipBean bean: viplist){
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, bean.getQ_buffid());
	// if(bufflist.size()>0){
	// vipid = bean.getQ_id();
	// break;
	// }
	// }
	// return vipid;
	// }
	//
	// //发送VIP信息给客户端
	// public void sendVipInfoToClient(Player player){
	// ResPlayerVIPInfoMessage resmsg = new ResPlayerVIPInfoMessage();
	// VipInfo playerVipInfo = getPlayerVipInfo(player);
	//
	// resmsg.setVipinfo(playerVipInfo);
	// MessageUtil.tell_player_message(player, resmsg);
	// }
	//
	// //得到玩家的 VIP 信息
	// public VipInfo getPlayerVipInfo(Player player){
	// VipInfo info = new VipInfo();
	// int vipid = getPlayerVipId(player);
	// if(vipid>0){
	// info.setVipId(vipid);
	// info.setStatus(canReceiveReward(player)? 1:0);
	// info.setRemain(getVipRemainTime(player));
	// }else{ //vipid<=0
	// info.setVipId(0);
	// info.setStatus(0);
	// info.setRemain(0);
	// }
	// if(player.getVipright().getReceivedTopReward()>0){
	// info.setShowad((byte)0); //领过 不显示
	// }else{
	// info.setShowad((byte)1);
	// }
	// return info;
	// }
	//
	// //领取至尊VIP奖励
	// public void receiveVipTopReward(Player player){
	// if(player.getVipright().getReceivedTopReward()>0){ //领过
	// return ;
	// }else{
	// int vipid = getPlayerVipId(player);
	// if(vipid==3){
	// //过期时间=当前时间+72小时
	// if(BackpackManager.getInstance().getEmptyGridNum(player)>=5){ //是否能装的下
	// long losttime = System.currentTimeMillis()+72*60*60*1000;
	// long actionid = Config.getId();
	// List<Item> items = Item.createItems(1023 , 1, true, losttime); //坐骑祝福丹
	// items.addAll(Item.createItems(8403, 1, true, losttime)); //龙元心法秘籍
	// items.addAll(Item.createItems(1018, 2, true, 0)); //连斩延时丹 *2 1018
	// items.addAll(Item.createItems(1100, 3, true, 0)); //红玫瑰*3 1100
	// items.addAll(Item.createItems(1015, 2, true, 0)); //双倍收益丹*2 1015
	// BackpackManager.getInstance().addItems(player, items, Reasons.ACTIVITY_GIFT, actionid);
	// //设置已领取标记
	// player.getVipright().setReceivedTopReward(1);
	// //通知客户端
	// sendVipInfoToClient(player);
	// LogService.getInstance().execute(new RoleVipLog(player, 6, vipid, vipid, actionid));
	// }else{
	// MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("包裹空间不足,至尊VIP奖励领取失败"));
	// }
	// }
	// }
	// }
	//
	// //得到VIP剩余时间
	// public int getVipRemainTime(Player player){
	// long remain = 0;
	// List<Q_vipBean> viplist = DataManager.getInstance().q_vipContainer.getList();
	// for(Q_vipBean bean: viplist){
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, bean.getQ_buffid());
	// if(bufflist.size()>0){
	// Buff vipbuff = bufflist.get(0);
	// remain = vipbuff.getStart()+vipbuff.getTotalTime()-System.currentTimeMillis();
	// break;
	// }
	// }
	// return (int)(remain/1000);
	// }
	//
	// //领取VIP礼包
	// public boolean receiveVIPReward(Player player){
	// int vipid = getPlayerVipIdReal(player);
	// HashMap<Integer,Q_vipBean> map = DataManager.getInstance().q_vipContainer.getMap();
	// Q_vipBean vipbean = map.get(vipid);
	// if(vipbean!=null){
	// int giftid = vipbean.getQ_dayreceivegiftid(); //礼包
	// int money = vipbean.getQ_dayreceive_money(); //金币
	// int bindgold = vipbean.getQ_dayreceive_lijin(); //绑钻
	// if(Global.BAG_MAX_COPPER-player.getMoney()<money){ //金币满了
	// MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("操作失败，背包金币数己达上限"));
	// return false;
	// }
	// if(Global.BAG_MAX_BINDGOLD-player.getBindGold()<bindgold){ //绑钻满了
	// MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("操作失败，绑钻数己达上限"));
	// return false;
	// }
	// if(BackpackManager.getInstance().getAbleAddNum(player, giftid, true, 0)<=0){
	// MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("操作失败，背包空间不足"));
	// return false;
	// }
	// long actionid = Config.getId();
	// List<Item> vipgift = Item.createItems(giftid, 1, true, 0);
	// if(BackpackManager.getInstance().addItems(player, vipgift, Reasons.VIP_DAILYGIFT, actionid)){ //领取成功
	// BackpackManager.getInstance().changeMoney(player, money, Reasons.VIP_DAILYMONEY, actionid);
	// BackpackManager.getInstance().changeBindGold(player, bindgold, Reasons.VIP_DAILYBINDGOLD, actionid);
	// RankManager.getInstance().vipGiveRank(player);
	// player.getVipright().setLastReceiveVipRewardTime(System.currentTimeMillis());
	// MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("领取成功。"));
	//
	// RoleVipLog viplog = new RoleVipLog(player, 5, vipid, vipid, actionid);
	// LogService.getInstance().execute(viplog); //执行保存日志
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// public Q_vipBean getVipModelByBuffid(int buffid){
	// Q_vipBean bean = null;
	// List<Q_vipBean> viplist = DataManager.getInstance().q_vipContainer.getList();
	// for(Q_vipBean b: viplist){
	// if(b.getQ_buffid()==buffid){
	// bean = b; break;
	// }
	// }
	// return bean;
	// }
	//
	// //是否可以领取VIP礼包 判断条件 上次领取时间与当前时间是否在同一天 前置条件是判断是否是VIP 是VIP才调用此方法
	// public boolean canReceiveReward(Player player){
	// long lastreceivetime = player.getVipright().getLastReceiveVipRewardTime();
	// long now = System.currentTimeMillis();
	// boolean issameday = TimeUtil.isSameDay(lastreceivetime, now);
	//
	// return !issameday;
	// }
	//
	// //vip状态移除事件
	// public void removeVipEvent(Player player){
	// //通知前端
	// ResVIPExpiredMessage resmsg = new ResVIPExpiredMessage();
	// MessageUtil.tell_player_message(player, resmsg);
	// //通知世界服
	// PlayerManager.getInstance().stSyncExterior(player);
	// //更新副本次数
	// ManagerPool.zonesManager.stResZoneSurplusSum(player);
	// //
	// RoleVipLog viplog = new RoleVipLog(player, 4, VipManager.getInstance().getPlayerVipId(player), 0, -1);
	// LogService.getInstance().execute(viplog); //执行保存日志
	// }
	//
	// //使用VIP卡
	// public void userVipCard(Player player, BuffGoods item, String buff){
	// if(player.getWebName().equals("qq3366") && player.getVipright().getWebVipLevel()<=0 ){ return; }//3366平台使用VIP的条件必须是平台VIP
	// int vipid = VipManager.getInstance().getPlayerVipIdReal(player); //玩家当前vipid
	// int curbuffid = 0;
	// Q_vipBean curvipBean = null;
	// if(vipid>0){
	// curvipBean = DataManager.getInstance().q_vipContainer.getMap().get(vipid);
	// curbuffid = curvipBean.getQ_buffid(); //当前buffid
	// }
	// if(!StringUtils.isNumeric(buff)){ log.error("错误！检查VIP配置表,BUFF字段不是数字"); return; }
	// int buffid = Integer.parseInt(buff); //要加的buffid
	// String tip = ResManager.getInstance().getString("成功使用{1},获得{2}特权时间{3}天");
	// Q_vipBean targetvipBean = VipManager.getInstance().getVipModelByBuffid(buffid);
	// Q_buffBean targetbuffBean = DataManager.getInstance().q_buffContainer.getMap().get(buffid);
	// Q_itemBean useitemBean = DataManager.getInstance().q_itemContainer.getMap().get(item.getItemModelId());
	// if(curbuffid==1411){ //当前是临时VIP 全部执行替换操作 并且重置VIP每日礼包领奖时间 应该排除临时 但是临时卡的使用是走useVipTmpCard方法不会进这里
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, curbuffid);
	// if(bufflist.size()>0){
	// long actionid = Config.getId();
	// Buff curbuff = bufflist.get(0);
	// long remainbufftime = curbuff.getTotalRemainTime();
	// ManagerPool.buffManager.removeBuff(player, curbuff);
	// ManagerPool.buffManager.addBuff(player, player, buffid, remainbufftime, 0, 0);//添加新VIPbuff加上剩余时间
	// BackpackManager.getInstance().removeItem(player, item, 1,Reasons.GOODUSE,actionid);
	// VipManager.getInstance().resetVipReward(player); //重置VIP每日礼包领奖时间
	// player.getVipright().resetVipRight(player); //重置vip权限 免费钻石传送次数
	// MessageUtil.notify_player(player, Notifys.SUCCESS, tip, useitemBean.getQ_name(), targetvipBean.getQ_name(), ""+(targetbuffBean.getQ_effect_time()/3600/24) );
	// RoleVipLog viplog = new RoleVipLog(player, 3, 101, VipManager.getInstance().getPlayerVipId(player), actionid);
	// LogService.getInstance().execute(viplog); //执行保存日志
	// PlayerManager.getInstance().stSyncExterior(player);//vip状态同步到世界服
	// }
	// }else if (curbuffid==1412) { //移除临时至尊VIP，并加正式VIP
	//
	// if (item.getItemModelId() != 1022) {
	// MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您已经使用至尊VIP体验卡，等到期后才能使用{1}"),item.acqItemModel().getQ_name() );
	// return ;
	// }
	//
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, curbuffid);
	// if(bufflist.size()>0){
	// long actionid = Config.getId();
	// Buff curbuff = bufflist.get(0);
	// long remainbufftime = curbuff.getTotalRemainTime();
	// ManagerPool.buffManager.removeBuff(player, curbuff);
	// BackpackManager.getInstance().removeItem(player, item, 1,Reasons.GOODUSE,actionid);
	// ManagerPool.buffManager.addBuff(player, player, buffid, remainbufftime, 0, 0);//添加新VIPbuff加上剩余时间
	// VipManager.getInstance().resetVipReward(player); //重置VIP每日礼包领奖时间
	// player.getVipright().resetVipRight(player); //重置vip权限 免费钻石传送次数
	// MessageUtil.notify_player(player, Notifys.SUCCESS, tip, useitemBean.getQ_name(), targetvipBean.getQ_name(), ""+(targetbuffBean.getQ_effect_time()/3600/24) );
	// RoleVipLog viplog = new RoleVipLog(player, 3, 103, VipManager.getInstance().getPlayerVipId(player), actionid);
	// LogService.getInstance().execute(viplog); //执行保存日志
	// PlayerManager.getInstance().stSyncExterior(player);//vip状态同步到世界服
	// }
	// }else{
	// if(vipid<=0){ //成为VIP
	// int result = ManagerPool.buffManager.addBuff(player, player, buffid, 1, 0, 0);//加buff
	// if (result > 0) { //使用vip卡成功
	// long actionid = Config.getId();
	// BackpackManager.getInstance().removeItem(player, item, 1,Reasons.GOODUSE, actionid);
	// ResVIPAnnounceMessage vipannmsg = new ResVIPAnnounceMessage(); //使用成功后 播放公告
	// vipannmsg.setPlayername(player.getName());
	// vipannmsg.setVipid(targetvipBean.getQ_id());
	// MessageUtil.tell_world_message(vipannmsg); //通知前端发送公告
	// VipManager.getInstance().resetVipReward(player); //重置VIP每日礼包领奖时间
	// player.getVipright().resetVipRight(player); //重置vip权限 免费钻石传送次数
	// PlayerManager.getInstance().stSyncExterior(player);//vip状态同步到世界服
	// //屏幕提示
	// MessageUtil.notify_player(player, Notifys.SUCCESS, tip, useitemBean.getQ_name(), targetvipBean.getQ_name(), ""+(targetbuffBean.getQ_effect_time()/3600/24) );
	// RoleVipLog viplog = new RoleVipLog(player, 1, vipid, VipManager.getInstance().getPlayerVipId(player), actionid);
	// LogService.getInstance().execute(viplog); //执行保存日志
	// }
	// }else if(buffid == curbuffid){ //是同一种VIP
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, curbuffid);
	// if(bufflist.size()>0){
	// long actionid = Config.getId();
	// ManagerPool.buffManager.addBuff(player, player, buffid, 0, 0, 0);//添加新VIPbuff加上剩余时间
	// BackpackManager.getInstance().removeItem(player, item, 1,Reasons.GOODUSE,actionid);
	// MessageUtil.notify_player(player, Notifys.SUCCESS, tip, useitemBean.getQ_name(), targetvipBean.getQ_name(), ""+(targetbuffBean.getQ_effect_time()/3600/24) );
	// RoleVipLog viplog = new RoleVipLog(player, 2, vipid, VipManager.getInstance().getPlayerVipId(player), actionid);
	// LogService.getInstance().execute(viplog); //执行保存日志
	// }
	// }else if(buffid > curbuffid) { //要加的vip>当前vip ->升级VIP ->清空领奖时间
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, curbuffid);
	// if(bufflist.size()>0){
	// long actionid = Config.getId();
	// Buff curbuff = bufflist.get(0);
	// long remainbufftime = curbuff.getTotalRemainTime();
	// ManagerPool.buffManager.removeBuff(player, curbuff);
	// ManagerPool.buffManager.addBuff(player, player, buffid, remainbufftime, 0, 0);//添加新VIPbuff加上剩余时间
	// BackpackManager.getInstance().removeItem(player, item, 1,Reasons.GOODUSE,actionid);
	// VipManager.getInstance().resetVipReward(player); //重置VIP每日礼包领奖时间
	// player.getVipright().resetVipRight(player); //重置vip权限 免费钻石传送次数
	// MessageUtil.notify_player(player, Notifys.SUCCESS, tip, useitemBean.getQ_name(), targetvipBean.getQ_name(), ""+(targetbuffBean.getQ_effect_time()/3600/24) );
	// RoleVipLog viplog = new RoleVipLog(player, 3, vipid, VipManager.getInstance().getPlayerVipId(player), actionid);
	// LogService.getInstance().execute(viplog); //执行保存日志
	// }
	// }else if(buffid < curbuffid){ //要加的vip<当前vip ->保持vip //在当前剩余时间基础上+要加的vip时间
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, curbuffid);
	// if(bufflist.size()>0){
	// long actionid = Config.getId();
	// ManagerPool.buffManager.addBuff(player, player, buffid, 0, 0, 0);//添加新VIPbuff加上剩余时间
	// BackpackManager.getInstance().removeItem(player, item, 1,Reasons.GOODUSE, actionid);
	// MessageUtil.notify_player(player, Notifys.SUCCESS, tip, useitemBean.getQ_name(), curvipBean.getQ_name(), ""+(targetbuffBean.getQ_effect_time()/3600/24) );
	// RoleVipLog viplog = new RoleVipLog(player, 2, vipid, VipManager.getInstance().getPlayerVipId(player), actionid);
	// LogService.getInstance().execute(viplog); //执行保存日志
	// }
	// }
	// }
	// //更新副本次数
	// ManagerPool.zonesManager.stResZoneSurplusSum(player);
	// //更新玩家VIP信息
	// ResPlayerVIPInfoMessage resmsg = new ResPlayerVIPInfoMessage();
	// resmsg.setVipinfo(ManagerPool.vipManager.getPlayerVipInfo(player));
	// MessageUtil.tell_player_message(player, resmsg);
	// }
	//
	// //使用临时VIP卡
	// public void useVipTmpCard(Player player, BuffGoods item, String buff){
	// if(player.getWebName().equals("qq3366") && player.getVipright().getWebVipLevel()<=0 ){ return; }//3366平台使用VIP的条件必须是平台VIP
	// int vipid = VipManager.getInstance().getPlayerVipId(player); //玩家当前vipid
	// if(!StringUtils.isNumeric(buff)){ log.error("错误！检查VIP配置表,BUFF字段不是数字"); return; }
	// int buffid = Integer.parseInt(buff);
	// String tip = ResManager.getInstance().getString("成功使用{1},获得{2}特权时间{3}小时");
	// RoleVipLog viplog = new RoleVipLog();
	// viplog.setSid(player.getCreateServerId());
	// Q_vipBean targetvipBean = VipManager.getInstance().getVipModelByBuffid(buffid);
	// Q_itemBean useitemBean = DataManager.getInstance().q_itemContainer.getMap().get(item.getItemModelId());
	// List<Buff> zzbufflist = ManagerPool.buffManager.getBuffByModelId(player, 1412);
	// if (zzbufflist.size() > 0) {
	// MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您已经使用至尊VIP体验卡，不能再使用{1}"),item.acqItemModel().getQ_name() );
	// return;
	// }
	//
	// if(vipid>0){ //已是VIP了再使用临时卡 则延长时间
	// Q_vipBean curvipBean = DataManager.getInstance().q_vipContainer.getMap().get(vipid);
	// int curbuffid = curvipBean.getQ_buffid(); //当前buffid
	// List<Buff> bufflist = ManagerPool.buffManager.getBuffByModelId(player, curbuffid);
	// if(bufflist.size()>0){
	// long actionid = Config.getId();
	// ManagerPool.buffManager.addBuff(player, player, buffid, 0, 0, 0);//添加新VIPbuff加上剩余时间
	// BackpackManager.getInstance().removeItem(player, item, 1,Reasons.GOODUSE, actionid);
	// MessageUtil.notify_player(player, Notifys.SUCCESS, tip, useitemBean.getQ_name(), curvipBean.getQ_name(), "1" );
	// viplog = new RoleVipLog(player, 2, 101, VipManager.getInstance().getPlayerVipId(player), actionid);
	// }
	// }else{ //不是VIP使用临时卡成为VIP
	// int result = ManagerPool.buffManager.addBuff(player, player, buffid, 1, 0, 0);//加buff
	// if (result > 0) { //使用vip卡成功
	// long actionid = Config.getId();
	// BackpackManager.getInstance().removeItem(player, item, 1,Reasons.GOODUSE, actionid);
	// VipManager.getInstance().resetVipReward(player); //重置VIP每日礼包领奖时间
	// player.getVipright().resetVipRight(player); //重置vip权限
	// PlayerManager.getInstance().stSyncExterior(player);//vip状态同步到世界服
	// //屏幕提示
	// MessageUtil.notify_player(player, Notifys.SUCCESS, tip, useitemBean.getQ_name(), targetvipBean.getQ_name(), "1" );
	// viplog = new RoleVipLog(player, 1, vipid, 101, actionid);
	// }
	// }
	// //保存日志
	// LogService.getInstance().execute(viplog);
	// //更新玩家VIP信息
	// ResPlayerVIPInfoMessage resmsg = new ResPlayerVIPInfoMessage();
	// resmsg.setVipinfo(ManagerPool.vipManager.getPlayerVipInfo(player));
	// MessageUtil.tell_player_message(player, resmsg);
	// }
	//
	// /**使用临时至尊VIP
	// *
	// * @param player
	// * @param item
	// * @param buff
	// */
	// public void useVipTmpZhiZunCard(Player player, BuffGoods item, String buff){
	// if(player.getWebName().equals("qq3366") && player.getVipright().getWebVipLevel()<=0 ){ return; }//3366平台使用VIP的条件必须是平台VIP
	// int vipid = getPlayerVipIdReal(player); //玩家当前vipid
	// String name = item.acqItemModel().getQ_name();
	// if (vipid > 0 ) {
	// MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您已经是VIP，不能再使用{1}"),name );
	// return ;
	// }
	//
	// if(ManagerPool.backpackManager.removeItem(player, item, 1,Reasons.GOODUSE, Config.getId())){
	// ManagerPool.buffManager.addBuff(player, player, Integer.valueOf(buff), 0, 0, 0);//添加新VIPbuff
	// VipManager.getInstance().resetVipReward(player); //重置VIP每日礼包领奖时间
	// PlayerManager.getInstance().stSyncExterior(player);//vip状态同步到世界服
	// //更新副本次数
	// ManagerPool.zonesManager.stResZoneSurplusSum(player);
	// //更新玩家VIP信息
	// ResPlayerVIPInfoMessage resmsg = new ResPlayerVIPInfoMessage();
	// resmsg.setVipinfo(ManagerPool.vipManager.getPlayerVipInfo(player));
	// MessageUtil.tell_player_message(player, resmsg);
	// MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功使用{1}，获得7天VIP时间") ,name);
	// }
	// }
	//
	//
	//
	// //GM命令用
	// public void removeVip(Player player){
	// int vipid = getPlayerVipId(player);
	// if(vipid>0){
	// if(vipid==1){
	// BuffManager.getInstance().removeByBuffId(player, 1401); //移除白金VIP
	// BuffManager.getInstance().removeByBuffId(player, 1411); //移除临时白金VIP
	// removeVipEvent(player);
	//
	// }else if(vipid==2){
	// BuffManager.getInstance().removeByBuffId(player, 1402);
	// removeVipEvent(player);
	//
	// }else if(vipid==3){
	// BuffManager.getInstance().removeByBuffId(player, 1403);
	// removeVipEvent(player);
	//
	//
	// }
	// }
	// }
	// public void resetFreeFly(Player player){
	// long now = System.currentTimeMillis();
	// long last = player.getVipright().getLastFreeFlyTime();
	// if(!TimeUtil.isSameDay(now, last)){ //上次与当前不是同一天 需要重设免费次数
	// player.getVipright().resetVipRight(player);
	// }
	// }
	// public void resetVipReward(Player player){
	// player.getVipright().setLastReceiveVipRewardTime(0);
	// }
//	
//	/**
//	 * 获取玩家vip等级
//	 * @param player
//	 */
//	@SuppressWarnings("unchecked")
//	public void setVipLevel(Player player){
//		if("".equals(WServer.getInstance().getServerWeb())){
//			try{
//				String result = HttpUtil.postr("http://openapi.tencentyun.com/v3/user/get_info", "openid=" + player.getUserName() + "&openkey=" + player.getAgentPlusdata() + "&appid=2&sig=VrN%2BTn5J%2Fg4IIo0egUdxq6%2B0otk%3D&pf=" + player.getAgentColdatas() + "&format=json&userip=" + player.getLoginIp() + "&charset=utf-8&flag=2");
//				HashMap<String, Object> objs = (HashMap<String, Object>)JSON.parse(result);
//				
//			}catch (Exception e) {
//				log.error(e, e);
//			}
//		}else{
//			player.getVipright().setWebVipLevel(1);
//		}
//	}

	public static void main(String[] s) {
		// System.out.println(TimeUtil.getBetweenDays(System.currentTimeMillis(), System.currentTimeMillis()));
		System.out.println(50 % 100);
		System.out.println(553 % 100);
		System.out.println(553 / 100);
	}
}
