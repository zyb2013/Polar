package com.game.chat.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.HorseEquip;
import com.game.backpack.structs.Item;
import com.game.backpack.structs.ItemConst;
import com.game.chat.bean.GoodsInfoReq;
import com.game.chat.bean.GoodsInfoRes;
import com.game.chat.bean.RoleChatInfo;
import com.game.chat.message.ChatResponseMessage;
import com.game.chat.message.ChatResponseSWMessage;
import com.game.config.Config;
import com.game.db.bean.BlackListBean;
import com.game.db.dao.BlackListDao;
import com.game.gem.bean.PosGemInfo;
import com.game.gem.manager.GemManager;
import com.game.horse.struts.Horse;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.manager.PlayerManager;
import com.game.player.script.IPlayerChatScript;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.script.structs.ScriptEnum;
import com.game.structs.Reasons;
import com.game.utils.Global;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;
import com.game.utils.TimeUtil;
import com.game.utils.WordFilter;
import com.game.vip.manager.VipManager;

/**
 * 聊天管理
 * 
 */
public class ChatManager {

	private Logger log = Logger.getLogger(ChatManager.class);
	private Logger chatLog = Logger.getLogger("CHATLOG");
	/**
	 * 用于同步的Object
	 */
	private static Object obj = new Object();
	/**
	 * 发言过滤器
	 */
	private static final WordFilter filter = WordFilter.getInstance();
	/**
	 * 分隔符
	 */
	private static String splittag = "\f";
	/**
	 * 聊天类型-世界
	 */
	private static final byte CHATTYPE_WORLD = 0;
	/**
	 * 聊天类型-场景
	 */
	private static final byte CHATTYPE_SCENE = 1;
	/**
	 * 聊天类型-私聊
	 */
	private static final byte CHATTYPE_ROLE = 2;
	/**
	 * 聊天类型-队伍
	 */
	private static final byte CHATTYPE_TEAM = 3;
	/**
	 * 聊天类型-战盟
	 */
	private static final byte CHATTYPE_GROUP = 4;
	/**
	 * 聊天类型-喇叭
	 */
	private static final byte CHATTYPE_LABA = 5;
	// private static final byte CHATTYPE_SYSTEM=6;
	/**
	 * 聊天类型-GM聊天
	 */
	private static final byte CHATTYPE_GM = 7;
	/**
	 * 时间检查跨度
	 */
	private static int autoProhibitCheckStep = 600;
	/**
	 * 自动禁言最高等级
	 */
	private static int autoProhibitCheckLevel = 30;
	/**
	 * 自动禁言需要的触发次数
	 */
	private static int autoProhibitCheckCount = 3;
	/**
	 * 自动禁言时间长度
	 */
	private static int autoProhibitTimeLong = 36000;
	/**
	 * 禁言Map key:username
	 */
	private Map<String, BlackListBean> chatBlackMap = new HashMap<String, BlackListBean>();
	/**
	 * 禁言Map key:ip
	 */
	private Map<String, BlackListBean> chatipBlackMap = new HashMap<String, BlackListBean>();
	/**
	 * 黑名单Dao
	 */
	private BlackListDao blacklistDao;

	/**
	 * 聊天管理类实例
	 */
	private static ChatManager manager;

	private ChatManager() {
		blacklistDao = new BlackListDao();
	}

	public static ChatManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new ChatManager();
			}
		}
		return manager;
	}

	/**
	 * 获取玩家简要信息（用于聊天）
	 * 
	 * @param player
	 * @return
	 */
	public static RoleChatInfo getRoleChatInfo(Player player) {
		RoleChatInfo info = new RoleChatInfo();
		info.setId(player.getId());
		info.setLevel(player.getLevel());
		info.setName(player.getName());
		info.setSex(player.getSex());
		info.setJob(player.getJob());
		return info;
	}

	/**
	 * 聊天
	 * 
	 * @param player
	 *            玩家
	 * @param targetName
	 *            目标玩家名
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param content
	 *            聊天内容
	 */
	public void chat(Player player, String targetName, int chattype,
			List<GoodsInfoReq> other, String content) {
		if (player == null || chattype < 0 || chattype > 5) {
			log.error("参数问题 player:" + player + " targetname:" + targetName
					+ " chattype:" + chattype + " condition:" + content
					+ " other:" + JSONserializable.toString(other));
			return;
		}
		int count = StringUtil.findStrIndexOfCountIgnoreCase(content, splittag);
		if (other != null && other.size() > 0 && count != other.size()) {
			log.error("消息附加有问题 共用 " + count + "个参数,但附加列表是" + other.size() + "个");
			return;
		}
		chatLog.info(isProhibitChat(player) + "\t" + player.getName() + "\t"
				+ targetName + "\t" + getChatTypeName(chattype) + "\t"
				+ content + "\t" + JSONserializable.toString(other));
		content = filter.badWordstFilterStr(content);// 敏感词过滤
		int count2 = StringUtil
				.findStrIndexOfCountIgnoreCase(content, splittag);
		if (count != count2) {
			log.error("分隔符被敏感词滤掉了");
			return;
		}

		IPlayerChatScript script = (IPlayerChatScript) ManagerPool.scriptManager
				.getScript(ScriptEnum.PLAYER_CHAT);
		if (script != null) {
			try {
				script.onChat(player, chattype, content);
			} catch (Exception e) {
				log.error(e, e);
			}
		} else {
			log.error("玩家说话检测脚本不存在！");
		}

		switch (chattype) {
		case CHATTYPE_WORLD:// 世界聊天
			chatToWorld(player, chattype, other, content);
			break;
		case CHATTYPE_SCENE:// 场景聊天
			chatToScene(player, chattype, other, content);
			break;
		case CHATTYPE_ROLE:// 私聊
			chatToRole(player, targetName, chattype, other, content);
			break;
		case CHATTYPE_TEAM:// 队伍
			chatToTeam(player, chattype, other, content);
			break;
		case CHATTYPE_GROUP:// 战盟
			chatToGroup(player, chattype, other, content);
			break;
		case CHATTYPE_LABA:// 喇叭
			chatToLaBa(player, chattype, other, content);
			break;
		case CHATTYPE_GM:
			chatToGm(player, chattype, content);
		default:
			break;
		}
	}

	/**
	 * 处理世界聊天消息
	 * 
	 * @param source
	 *            玩家
	 * @param accepterName
	 *            目标名称
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品
	 * @param content
	 *            聊天内容
	 */
	private void dealChatMsgToWorld(Player source, String accepterName,
			int chattype, List<GoodsInfoReq> other, String content) {
//		Player player = PlayerManager.getInstance().getOnlinePlayerByName(
//				accepterName);

		ChatResponseSWMessage sw = new ChatResponseSWMessage();
		sw.setChater(source.getId());
		sw.setChatername(source.getName());
		sw.setChaterkinglv(source.gKingLevel());
		sw.setCountry(source.getCountry());
		sw.setChattype(chattype);
		sw.setCondition(content);
		sw.setReceivername(accepterName);
		List<GoodsInfoRes> buildGoodsResInfo = buildGoodsResInfo(source, other);
		if(other != null && other.size() > 0 && buildGoodsResInfo != null && buildGoodsResInfo.size() != other.size()) {
			MessageUtil.notify_player(source, Notifys.ERROR, ResManager.getInstance().getString("发送错误,物品不存在。"));
			return;
		}
		sw.setOther(buildGoodsResInfo);
		sw.setIsprohibit((byte) (isProhibitChat(source) ? 1 : 0));
		sw.setIsgm((byte) (isGm(source) ? 1 : 0));
//		 if (VipManager.getInstance().getPlayerVipIdReal(source) == 101) {
//		 sw.setSenderviptype((short) 0);
//		 } else {
		 sw.setSenderviptype((short) VipManager.getInstance().getVIPStage(source));
		 sw.setSenderviplevel((short) ManagerPool.vipManager.getVIPLevel(source));
//		 }
		// sw.setWebvip(source.getVipright().getWebVipLevel());
		// if (player != null) {
		// if (VipManager.getInstance().getPlayerVipIdReal(player) == 101) {
		// sw.setReceiverviptype((short) 0);
		// } else {
		// sw.setReceiverviptype((short) (short) VipManager.getInstance()
		// .getPlayerVipId(player));
		// }
		// sw.setReceiverwebvip(player.getVipright().getWebVipLevel());
		// }

		 if (isProhibitChat(source) && chattype != CHATTYPE_LABA) {
//	         MessageUtil.tell_player_message(source, sc);
			 MessageUtil.notify_player(source,Notifys.STATE, "您已被禁言，只能发送大喇叭", source.getName());	
		 } else {
			 // 发送到世界服处理
			 MessageUtil.send_to_world(sw);
		 }
	}

	/**
	 * 处理发向全部服务器聊天信息
	 * 
	 * @param source
	 *            发起玩家
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param condition
	 *            条件
	 */
	private int dealChatMsgToAllServer(Player source, int chattype,
			List<GoodsInfoReq> other, String condition) {
		ChatResponseMessage sc = new ChatResponseMessage();
		sc.setChater(source.getId());
		sc.setChatername(source.getName());
		sc.setChattype(chattype);
		sc.setViptype((short) VipManager.getInstance().getVIPStage(source));
		sc.setSenderviplevel((short) ManagerPool.vipManager.getVIPLevel(source));
		sc.setChaterlevel(source.getLevel());
		sc.setChaterkinglv(source.gKingLevel());
		sc.setCountry(source.getCountry());
		sc.setCondition(condition);
		List<GoodsInfoRes> buildGoodsResInfo = buildGoodsResInfo(source, other);
		if(other != null && other.size() > 0 && buildGoodsResInfo != null && buildGoodsResInfo.size() != other.size()) {
			MessageUtil.notify_player(source, Notifys.ERROR, ResManager.getInstance().getString("发送错误,物品不存在。"));
			return -1;
		}
		sc.setOther(buildGoodsResInfo);
		// if (VipManager.getInstance().getPlayerVipIdReal(source) == 101) {
		// sc.setViptype((short) 0);
		// } else {
		// sc.setViptype((short) VipManager.getInstance().getPlayerVipId(
		// source));
		// }
		// sc.setWebvip(source.getVipright().getWebVipLevel());
		// sc.setViptype((short)
		// VipManager.getInstance().getPlayerVipId(source));
		// source.getGmlevel()>0;
		sc.setIsgm((byte) (isGm(source) ? 1 : 0));
		if (isProhibitChat(source) && chattype != CHATTYPE_LABA) {
//			MessageUtil.tell_player_message(source, sc);
			MessageUtil.notify_player(source,Notifys.STATE, "您已被禁言，只能发送大喇叭", source.getName());	
			return -1;
		} else {
			MessageUtil.send_to_gate(sc);
		}
		return 0;
	}

	/**
	 * 处理场景聊天信息
	 * 
	 * @param source
	 *            发起者玩家
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param condition
	 *            发言内容
	 */
	private void dealChatMsgToScene(Player source, int chattype,
			List<GoodsInfoReq> other, String condition) {
		ChatResponseMessage sc = new ChatResponseMessage();
		sc.setChater(source.getId());
		sc.setChatername(source.getName());
		sc.setChattype(chattype);
		sc.setViptype((short) VipManager.getInstance().getVIPStage(source));
		sc.setSenderviplevel((short) ManagerPool.vipManager.getVIPLevel(source));
		sc.setChaterlevel(source.getLevel());
		sc.setChaterkinglv(source.gKingLevel());
		sc.setCountry(source.getCountry());
		sc.setCondition(condition);
		List<GoodsInfoRes> buildGoodsResInfo = buildGoodsResInfo(source, other);
		if(other != null && other.size() > 0 && buildGoodsResInfo != null && buildGoodsResInfo.size() != other.size()) {
			MessageUtil.notify_player(source, Notifys.ERROR, ResManager.getInstance().getString("发送错误,物品不存在。"));
			return;
		}
		sc.setOther(buildGoodsResInfo);
		// if (VipManager.getInstance().getPlayerVipIdReal(source) == 101) {
		// sc.setViptype((short) 0);
		// } else {
		// sc.setViptype((short) VipManager.getInstance().getPlayerVipId(
		// source));
		// }
		// sc.setWebvip(source.getVipright().getWebVipLevel());
		sc.setIsgm((byte) (isGm(source) ? 1 : 0));
		if (isProhibitChat(source)) {
//			MessageUtil.tell_player_message(source, sc);
			MessageUtil.notify_player(source,Notifys.STATE, "您已被禁言，只能发送大喇叭", source.getName());
		} else {
			MessageUtil.tell_round_message(source, sc);
		}
	}

	/**
	 * 创建物品信息
	 * 
	 * @param source
	 *            玩家
	 * @param other
	 *            附加物品信息
	 * @return
	 */
	private List<GoodsInfoRes> buildGoodsResInfo(Player source,
			List<GoodsInfoReq> other) {
		List<GoodsInfoRes> otherres = new ArrayList<GoodsInfoRes>();
		if (other != null && other.size() > 0) {
			for (GoodsInfoReq req : other) {
				// 包裹中
				Item itemById = ManagerPool.backpackManager.getItemById(source,
						req.getGoodId());
				GoodsInfoRes res = new GoodsInfoRes();
				if (itemById == null) {
					// 身上
					Equip[] equips = source.getEquips();
					for (int i = 0; i < equips.length; i++) {
						Equip equip = equips[i];
						if (equip != null && req.getGoodId() == equip.getId()) {
							itemById = equip;
							PosGemInfo posGemInfo = GemManager.getInstance()
									.getPosGemInfo(source, i);
							if (posGemInfo != null) {
								res.setGeminfo(posGemInfo.getGeminfo());
							}
						}
					}
				}
				if (itemById == null) {
					// 坐骑
					Horse horse = ManagerPool.horseManager.getHorse(source);
					for (HorseEquip equip : horse.getHorseequips()) {
						if (equip != null && req.getGoodId() == equip.getId()) {
							itemById = equip;
						}
					}
				}
				if(itemById == null) {
					//铸造工厂奖励仓库
					itemById = ManagerPool.castingManager.getCastingBoxItemByID(source, req.getGoodId());
				}

				if (itemById != null) {
					res.setQueryType(0);
					res.setIndex(req.getIndex());
					res.setItemInfo(itemById.buildItemInfo());
					otherres.add(res);
				} else {
					// 找不到物品
				}
			}
		}
		return otherres;
	}

	/**
	 * 私聊
	 * 
	 * @param source
	 *            发起玩家
	 * @param targetName
	 *            目标玩家名称
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param content
	 *            聊天内容
	 */
	private void chatToRole(Player source, String targetName, int chattype,
			List<GoodsInfoReq> other, String content) {
		// 角色id
		// Player targetPlayer =
		// PlayerManager.getInstance().getOnlinePlayerByName(targetName);
		// if(targetPlayer==null){
		// MessageUtil.notify_player(source, Notifys.NORMAL,"找不到该角色,或者对方已经离线");
		// return;
		// }
		if (targetName == null || targetName.length() <= 0) {
			MessageUtil.notify_player(source, Notifys.CHAT_ROLE, ResManager
					.getInstance().getString("很抱歉，私聊聊天对象必填"));
			return;
		}
		if (targetName.length() > 20) {
			log.error("非法请求参数targetName" + targetName);
			return;
		}

		if (source.getName().equals(targetName)) {
			MessageUtil.notify_player(source, Notifys.CHAT_ROLE, ResManager
					.getInstance().getString("很抱歉，自己不可以跟自己私聊天"));
			return;
		}
		int duration = TimeUtil.getDurationToNowSec(source
				.getLastPrivateChatTime());
		if (duration < Global.CHAT_PRIVATE_MIN_TIME) {
			MessageUtil.notify_player(source, Notifys.CHAT_ROLE, ResManager
					.getInstance().getString("请不要发言过快"));
			return;
		}
		source.setLastPrivateChatTime(System.currentTimeMillis()); // 设置私聊最后聊天时间
		dealChatMsgToWorld(source, targetName, chattype, other, content);
	}

	/**
	 * 组队聊
	 * 
	 * @param source
	 *            发起玩家
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param content
	 *            聊天内容
	 */
	private void chatToTeam(Player source, int chattype,
			List<GoodsInfoReq> other, String content) {
		int duration = TimeUtil.getDurationToNowSec(source
				.getLastTeamChatTime());
		if (duration < Global.CHAT_TEAM_MIN_TIME) {
			MessageUtil.notify_player(source, Notifys.CHAT_TEAM, ResManager
					.getInstance().getString("请不要发言过快"));
			return;
		}
		if (source.getTeamid() == 0) {
			MessageUtil.notify_player(source, Notifys.CHAT_TEAM, ResManager
					.getInstance().getString("只有在加入队伍后才能使用队伍频道发言"));
			return;
		}
		source.setLastTeamChatTime(System.currentTimeMillis()); // 设置组队聊最后聊天时间
		dealChatMsgToWorld(source, null, chattype, other, content);
	}

	/**
	 * 战盟聊天
	 * 
	 * @param source
	 *            发起玩家
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param content
	 *            聊天内容
	 */
	private void chatToGroup(Player source, int chattype,
			List<GoodsInfoReq> other, String content) {
		int duration = TimeUtil.getDurationToNowSec(source
				.getLastGroupChatTime());
		if (duration < Global.CHAT_GROUP_MIN_TIME) {
			MessageUtil.notify_player(source, Notifys.CHAT_GROUP, ResManager
					.getInstance().getString("请不要发言过快"));
			return;
		}
		if (source.getGuildId() == 0) {
			MessageUtil.notify_player(source, Notifys.CHAT_GROUP, ResManager
					.getInstance().getString("只有加入了战盟后才能使用战盟频道发言"));
			return;
		}
		source.setLastGroupChatTime(System.currentTimeMillis());// 设置战盟聊最后聊天时间
		dealChatMsgToWorld(source, null, chattype, other, content);
	}

	/**
	 * 场景聊
	 * 
	 * @param source
	 *            发起玩家
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param condition
	 *            发言内容
	 */
	private void chatToScene(Player source, int chattype,
			List<GoodsInfoReq> other, String condition) {
		int duration = TimeUtil.getDurationToNowSec(source
				.getLastSceneChatTime());
		if (duration < Global.CHAT_SCENE_MIN_TIME) {
			MessageUtil.notify_player(source, Notifys.CHAT_COMMON, ResManager
					.getInstance().getString("请不要发言过快"));
			return;
		}
		source.setLastSceneChatTime(System.currentTimeMillis());// 设置场景聊天最后聊天时间
		dealChatMsgToScene(source, chattype, other, condition);
	}

	/**
	 * 喇叭聊
	 * 
	 * @param source
	 *            发起玩家
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param condition
	 *            发言内容
	 */
	private void chatToLaBa(Player source, int chattype,
			List<GoodsInfoReq> other, String condition) {
		int duration = TimeUtil.getDurationToNowSec(source
				.getLastLabaChatTime());
		if (duration < Global.CHAT_LABA_MIN_TIME) {
			MessageUtil.notify_player(source, Notifys.CHAT_COMMON, ResManager
					.getInstance().getString("请不要发言过快"));
			return;
		}
		int itemNum = BackpackManager.getInstance().getItemNum(source,
				ItemConst.LABA);
		if (itemNum <= 0) {
			MessageUtil.notify_player(source, Notifys.CHAT_COMMON, ResManager
					.getInstance().getString("在该频道发言需要背包中有“小喇叭”道具"));
			return;
		}
		source.setLastLabaChatTime(System.currentTimeMillis());// 设置喇叭聊天最后聊天时间
		if(dealChatMsgToAllServer(source, chattype, other, condition) == 0) {
			BackpackManager.getInstance().removeItem(source, ItemConst.LABA, 1,
					Reasons.CHAT_RESUME, Config.getId());
		}
	}

	/**
	 * 世界聊
	 * 
	 * @param source
	 *            发起玩家
	 * @param chattype
	 *            聊天类型
	 * @param other
	 *            附加物品信息
	 * @param condition
	 *            发言内容
	 */
	private void chatToWorld(Player source, int chattype,
			List<GoodsInfoReq> other, String condition) {

		if (source.getLevel() < Global.CHAT_WORLD_MIN_GRADE) {
			MessageUtil.notify_player(source, Notifys.CHAT_WORLD, ResManager
					.getInstance().getString("只有等级达到了{1}级才能使用世界频道发言"), String
					.valueOf(Global.CHAT_WORLD_MIN_GRADE));
			return;
		}
		int duration = TimeUtil.getDurationToNowSec(source
				.getLastLabaChatTime());
		if (duration < Global.CHAT_WORLD_MIN_TIME) {
			MessageUtil.notify_player(source, Notifys.CHAT_WORLD, ResManager
					.getInstance().getString("请不要发言过快"));
			return;
		}
		source.setLastWorldChatTime(System.currentTimeMillis());// 设置玩家最后世界聊时间
		dealChatMsgToAllServer(source, chattype, other, condition);
	}

	/**
	 * GM聊天
	 * 
	 * @param source
	 *            玩家
	 * @param chattype
	 *            聊天类型
	 * @param content
	 *            聊天内容
	 */
	private void chatToGm(Player source, int chattype, String content) {
		// 条件检查
		// TODO 检查是否符合

		long durationToNow = TimeUtil.getDurationToNow(source
				.getLastGmChatTime());
		if (durationToNow < Global.CHAT_GM_MIN_TIME) {
			MessageUtil.notify_player(source, Notifys.CHAT_GM, ResManager
					.getInstance().getString("请不要发言过快"));
		}
		source.setLastGmChatTime(System.currentTimeMillis());// 设置GM聊天最后聊天时间
		dealChatMsgToWorld(source, "GM", CHATTYPE_GM, null, content);
	}

	/**
	 * 判断玩家是否禁言
	 * 
	 * @param player
	 * @return
	 */
	public boolean isProhibitChat(Player player) {
		long endTime = player.getProhibitChatTime()
				+ player.getStartProhibitChatTime();
		if (endTime > 0) {
			if (endTime > System.currentTimeMillis()) {
				return true;
			}
		}
		// 是否在禁言黑名单中且 是否在禁言时间内
		String username = player.getUserName();
		if (!StringUtils.isBlank(username)) {
			if (chatBlackMap.containsKey(username)) {
				long now = System.currentTimeMillis();
				long endtime = chatBlackMap.get(username).getEndtime();
				if (now < endtime)
					return true;
			}
		}
		String ip = player.getLoginIp();
		if (!StringUtils.isBlank(ip)) {
			if (chatipBlackMap.containsKey(ip)) {
				long now = System.currentTimeMillis();
				long endtime = chatipBlackMap.get(ip).getEndtime();
				if (now < endtime)
					return true;
			}
		}
		return false;
	}

	/**
	 * 判断玩家是否是GM
	 * 
	 * @param player
	 * @return
	 */
	public boolean isGm(Player player) {
		return PlayerManager.getInstance().getPlayerGmlevel(player) > 0;
	}

	/**
	 * 以聊天类型ID获得聊天类型名称
	 * 
	 * @param chattype
	 *            聊天类型ID
	 * @return
	 */
	public String getChatTypeName(int chattype) {
		String result = ResManager.getInstance().getString("未知");
		switch (chattype) {
		case CHATTYPE_WORLD:
			result = ResManager.getInstance().getString("世界");
			break;
		case CHATTYPE_SCENE:
			result = ResManager.getInstance().getString("场景");
			break;
		case CHATTYPE_ROLE:
			result = ResManager.getInstance().getString("私聊");
			break;
		case CHATTYPE_TEAM:
			result = ResManager.getInstance().getString("组队");
			break;
		case CHATTYPE_GROUP:
			result = ResManager.getInstance().getString("战盟");
			break;
		case CHATTYPE_LABA:
			result = ResManager.getInstance().getString("喇叭");
			break;
		case CHATTYPE_GM:
			result = "GM";
			break;
		default:
			result += "-" + chattype;
			break;
		}
		return result;
	}

	/**
	 * 检查黑名单列表
	 * 
	 * @param player
	 */
	public void checkBlackList(Player player) {
		if (PlayerManager.getInstance().getPlayerGmlevel(player) > 0) { // GM排除
			return;
		}
		if (player.getLevel() <= autoProhibitCheckLevel) {
			int durationToNowSec = TimeUtil.getDurationToNowSec(player
					.getAddBlackTime());
			if (durationToNowSec <= autoProhibitCheckStep
					|| player.getAddBlackTime() == 0
					|| player.getAddBlackCount() == 0) {
				if (player.getAddBlackTime() == 0) {
					player.setAddBlackTime(System.currentTimeMillis());
				}
				player.setAddBlackCount(player.getAddBlackCount() + 1);
				if (player.getAddBlackCount() >= autoProhibitCheckCount) {
					long nowend = player.getStartProhibitChatTime()
							+ player.getProhibitChatTime(); // 角色当前禁言结束时间
					long now = System.currentTimeMillis();
					long targetend = now + autoProhibitTimeLong * 1000; // 角色本次禁言结束时间
					if (targetend > nowend) { // 结束时间会延长的情况下 才改变禁言时间
						player.setProhibitChatTime(autoProhibitTimeLong * 1000);
						player.setStartProhibitChatTime(now);
					}
					log.info(player.getId() + "_" + player.getName()
							+ "在一定时间内被加入黑名单次数过多触发自动禁言");
				}
			} else {
				player.setAddBlackCount(0);
				player.setAddBlackTime(0);
			}
		}
	}

	/**
	 * 重加载黑名单列表
	 * 
	 * @param type
	 *            1 用户名黑名单 2 IP黑名单
	 */
	public void reloadChatBlackList(int type) {
		if (type == 1) {
			chatBlackMap = blacklistDao.selectBlackMapByTypeState(type, 0);
		} else if (type == 2) {
			chatipBlackMap = blacklistDao.selectBlackMapByTypeState(type, 0);
		} else if (type == 3) {
			Map<String, BlackListBean> testgmipmap = blacklistDao
					.selectBlackMapByTypeState(3, 0); // 开服前测试GM
			if (testgmipmap != null && testgmipmap.size() > 0) {
				PlayerManager.getInstance().setTestGmIps(testgmipmap.keySet());
			}
		}

	}

	/**
	 * 启动时加载
	 */
	public void loadChatBlackList() {
		chatBlackMap = blacklistDao.selectBlackMapByTypeState(1, 0); // 账号禁言
		chatipBlackMap = blacklistDao.selectBlackMapByTypeState(2, 0); // IP禁言
		Map<String, BlackListBean> testgmipmap = blacklistDao
				.selectBlackMapByTypeState(3, 0); // 开服前测试GM
		if (testgmipmap != null && testgmipmap.size() > 0) {
			PlayerManager.getInstance().setTestGmIps(testgmipmap.keySet());
		}
	}

}
