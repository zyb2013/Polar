package com.game.offline.manager;

import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.config.Config;
import com.game.data.bean.Q_characterBean;
import com.game.data.bean.Q_globalBean;
import com.game.dblog.LogService;
import com.game.manager.ManagerPool;
import com.game.offline.OfflineLog;
import com.game.offline.message.ResRetreatInfoMessage;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.CommonConfig;
import com.game.utils.MessageUtil;

/**
 * 离线系统
 * 
 * 　■离线时间的获取有两种方式：
　　		●玩家在主城待机
　　		●玩家下线
　　■通过离线时间转换成中间值——追回值
　　■转换形式为
　　		●玩家在主城待机时，每12秒获得1点追回值
　　		●玩家下线时，每24秒获得1点追回值
　　■设有追回值上限（40000点），不领取完不能继续累积
　　■可以将追回值换算为玩家的经验，有免费领取和双倍领取两种模式，双倍领取需要支付钻石，根据追回值的增加，需支付的费用亦增加
 * 
 * @author luminghua
 * 
 * @date 2013年12月20日 上午12:15:34
 */
public class OffLineManager {

	protected Logger log = Logger.getLogger(OffLineManager.class);
	//玩家管理类实例
	private static OffLineManager manager;
	private static Object obj = new Object();

	private OffLineManager() {
	}

	public static OffLineManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new OffLineManager();
			}
		}
		return manager;
	}

	// private int retreatItemId = 1009; //闭关符ID
	// 最大的离线追加值
	public static final int MAX_OFFLINE_COUNT = 40000;

	/**
	 * 登录计算离线追加值
	 * 
	 * @param player
	 */
	public void calculateOfflineCountWhenLogin(Player player) {
		if (player.getLogoutTime() != 0) {
			Q_globalBean limitLevel = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.OFFLINE_LEVEL.getValue());
			if(limitLevel != null && limitLevel.getQ_int_value() > player.getLevel()) {
				return;
			}
			if(player.getLogoutTime() == player.getLastOffLineTime()) {
				//服务器重启了
				return;
			}
			player.setLastOffLineTime(player.getLogoutTime());
			// 获取离线时间
			int offlineTime = (int) ((System.currentTimeMillis() - player.getLogoutTime()) / 1000);
			// 每24秒一点追加值
			alterOfflineCount(player, offlineTime / 24);
			sendRetreatInfoMessage(player, 0);
		}
	}

	/**
	 * 修改离线追加值
	 * 
	 * @param player
	 * @param alter
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public int alterOfflineCount(Player player, int alter) {
		Q_globalBean limitLevel = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.OFFLINE_LEVEL.getValue());
		if(limitLevel != null && limitLevel.getQ_int_value() > player.getLevel()) {
			return 0;
		}
		int offlineCount = player.getOfflineCount();
		if (offlineCount + alter > MAX_OFFLINE_COUNT) {
			player.setOfflineCount(MAX_OFFLINE_COUNT);
		} else {
			player.setOfflineCount(offlineCount + alter);
		}
		if (offlineCount < 0) {
			player.setOfflineCount(0);
		}
		PlayerManager.getInstance().savePlayer(player);
		return player.getOfflineCount();
	}

	/**
	 * 可领取经验（单倍）
	 * 
	 * @param player
	 * @param q_characterBean
	 * @param retreatTime
	 * @return
	 */
	private int getRetreatExp(Player player, int offlineCount) {
		if (player == null) {
			return 0;
		}
		int key = ManagerPool.dataManager.q_characterContainer.getKey(player.getJob(), player.getLevel());
		Q_characterBean q_characterBean = ManagerPool.dataManager.q_characterContainer.getMap().get(key);
		if (q_characterBean == null) {
			return 0;
		}
		return (int) (q_characterBean.getQ_rexp() * offlineCount * 0.1d);
	}

	/**
	 * 返回双倍领取需要消耗的钻石
	 * 
	 * @param player
	 * @return
	 */
	private int getCostGold(Player player, int offlineCount) {
		Double costGold = 0.014d * offlineCount;
		if (costGold.intValue() < 1) {
			return 1;
		} else {
			return costGold.intValue();
		}
	}



	/**
	 * 返回离线系统信息
	 * 
	 * @param player
	 *            玩家
	 * @param message
	 *            消息
	 */
	public void reqRetreatInfoMessageToServer(Player player) {
		if (player == null) {
			log.error("reqRetreatInfoMessageToServer没有玩家");
			return;
		}
		sendRetreatInfoMessage(player, 0);
	}


	/**
	 * 发送离线系统信息消息
	 * 
	 * @param player
	 * @param type
	 *            0普通1特效
	 */
	public void sendRetreatInfoMessage(Player player, int type) {
		if (player == null) {
			return;
		}
		ResRetreatInfoMessage sendMessage = new ResRetreatInfoMessage();
		int offlineCount = player.getOfflineCount();
		sendMessage.setOfflineCount(offlineCount);
		sendMessage.setCurExp(getRetreatExp(player, offlineCount));
		sendMessage.setCostGold(getCostGold(player, offlineCount));
		MessageUtil.tell_player_message(player, sendMessage);
	}

	/**
	 * 领取奖励
	 * 
	 * @param player
	 * @param type
	 *            0单倍，1双倍
	 */
	public void getAward(Player player, int type) {
		int offlineCount = player.getOfflineCount();
		if (offlineCount <= 0) {
			MessageUtil.notify_player(player, Notifys.ERROR, "你当前追回值为0，不可领取");
			return;
		}
		int exp = getRetreatExp(player, offlineCount);
		int costGold = getCostGold(player, offlineCount);
		int ownGold = player.getGold() == null ? 0 : player.getGold().getGold();
		if (type == 0) {
			this.alterOfflineCount(player, -player.getOfflineCount());
			PlayerManager.getInstance().addExp(player, exp, AttributeChangeReason.BIGUAN);
			this.sendRetreatInfoMessage(player, 0);
			MessageUtil.notify_player(player, Notifys.NORMAL, "恭喜您成功领取经验 " + exp);
		} else if (ownGold >= costGold) {
			// 扣了钱再说
			if (!BackpackManager.getInstance().changeGold(player, -costGold, Reasons.OFFLINE_COST_GOLD, Config.getId())) {
				MessageUtil.notify_player(player, Notifys.ERROR, "领取当前双倍经验需要钻石 " + costGold);
				return;
			}
			this.alterOfflineCount(player, -player.getOfflineCount());
			PlayerManager.getInstance().addExp(player, 2 * exp, AttributeChangeReason.BIGUAN);
			this.sendRetreatInfoMessage(player, 0);
			MessageUtil.notify_player(player, Notifys.NORMAL, "恭喜您花费 " + costGold + " 钻石，成功领取双倍经验 " + 2 * exp);
		} else {
			MessageUtil.notify_player(player, Notifys.ERROR, "领取当前双倍经验需要钻石 " + costGold);
			return;
		}
		PlayerManager.getInstance().savePlayer(player);
		OfflineLog log = new OfflineLog();
		log.setPlayerid(player.getId());
		log.setExp(type==0?exp:exp*2);
		log.setGold(costGold);
		log.setOfflinevalue(offlineCount);
		LogService.getInstance().execute(log);
	}
}
