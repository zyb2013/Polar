package com.game.pray.manager;

import org.apache.log4j.Logger;
import com.game.config.Config;
import com.game.data.bean.Q_characterBean;
import com.game.data.bean.Q_prayBean;
import com.game.dblog.LogService;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.pray.bean.PrayInfo;
import com.game.pray.log.PrayLog;
import com.game.pray.message.ResPrayInfoMessage;
import com.game.pray.message.ResPrayResultToClientMessage;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.Global;
import com.game.utils.MessageUtil;

public class PrayManager {

	private static final Logger log = Logger.getLogger("PRAY");
	
	private static Object obj = new Object();
	
	//玩家管理类实例
	private static PrayManager manager;
	
	private PrayManager(){}
	
	public static PrayManager getInstance(){
		synchronized (obj) 
		{
			if(manager == null)
			{
				manager = new PrayManager();
			}
		}
		return manager;
	}
	
	/**
	 * 玩家祈愿
	 * @param player
	 * @param type 1-祈愿金币;2-祈愿经验
	 */
	public void stReqPray(Player player, byte type) {
		if(player == null) {
			return;
		}
		
		long action = Config.getId();
		PrayLog prayLog = new PrayLog();
		prayLog.setSid(player.getServerId());
		prayLog.setPlayerId(player.getId());
		prayLog.setLevel(player.getLevel());
		prayLog.setVip(ManagerPool.vipManager.getVIPLevel(player));
		
		int total = 1 + ManagerPool.vipManager.getPrayAddition(player);
		int gold = player.getGold() == null ? 0 : player.getGold().getGold();//身上钻石
		int prayGold = 0, prayExp = 0;
		ResPrayResultToClientMessage sendMessage = new ResPrayResultToClientMessage();
		sendMessage.setType(type);
		sendMessage.setIsSuccess((byte) 0);
		
		if(type == 1) {
			//检查剩余次数
			if(player.getPrayGoldCount() >= total) {
				MessageUtil.tell_player_message(player, sendMessage);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("对不起，今天您的祈愿次数已满。"));
				return;
			}
			
			//检查获得的金币数量
			prayGold = countPrayGold(player);
			int money = player.getMoney() + prayGold;
			if(prayGold <= 0 || money > Global.BAG_MAX_COPPER) {
				MessageUtil.tell_player_message(player, sendMessage);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("金币数量己达到上限。"));
				return;
			}
			
			Q_prayBean q_prayBean = ManagerPool.dataManager.q_prayContainer.getQ_prayBeanByPrayTimes(player.getPrayGoldCount() + 1);
			if(q_prayBean == null || q_prayBean.getQ_pray_gold_cost() <= 0) {
				MessageUtil.tell_player_message(player, sendMessage);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("祈愿信息出错。"));
				return;
			}
			
			if(player.getFirstPray() == (byte)1) {
				//检查钻石数量
				if(gold < q_prayBean.getQ_pray_gold_cost()) {
					MessageUtil.tell_player_message(player, sendMessage);
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("祈愿需要钻石：{1}。"), 
							String.valueOf(q_prayBean.getQ_pray_gold_cost()));	
					return;
				}
				
				//扣除钻石
				if(ManagerPool.backpackManager.changeGold(player, -q_prayBean.getQ_pray_gold_cost(), Reasons.PRAY, action) == false) {
					MessageUtil.tell_player_message(player, sendMessage);
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("扣除钻石失败。"));	
					return;
				}
			} else {
				//第一次祈愿金币不用扣除钻石
				player.setFirstPray((byte)1);
			}
			
			//扣除次数
			player.setPrayGoldCount(player.getPrayGoldCount() + 1);

			//增加金币
			ManagerPool.backpackManager.changeMoney(player, prayGold, Reasons.PRAY, action);
			MessageUtil.notify_player(player, Notifys.NORMAL, String.format(ResManager.getInstance().getString("祈祷获得金币%d。"), prayGold));
			
			//记录日志
			prayLog.setType(type);
			prayLog.setPrayTimes(player.getPrayGoldCount());
			prayLog.setCost(q_prayBean.getQ_pray_gold_cost());
			prayLog.setCount(prayGold);
			
			ManagerPool.livenessManager.raffle(player);
		}else if(type == 2) {
			//检查剩余次数
			if(player.getPrayExpCount() >= total) {
				MessageUtil.tell_player_message(player, sendMessage);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("对不起，今天您的祈愿次数已满。"));
				return;
			}
			
			//检查是否满级
			if (player.getLevel() >= Global.MAX_LEVEL) {
				MessageUtil.tell_player_message(player, sendMessage);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("玩家等级已满。"));
				return;
			}
			
			//检查钻石数量
			Q_prayBean q_prayBean = ManagerPool.dataManager.q_prayContainer.getQ_prayBeanByPrayTimes(player.getPrayExpCount() + 1);
			if(q_prayBean == null || q_prayBean.getQ_pray_exp_cost() <= 0) {
				MessageUtil.tell_player_message(player, sendMessage);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("祈愿信息出错。"));
				return;
			}
			
			if(gold < q_prayBean.getQ_pray_exp_cost()) {
				MessageUtil.tell_player_message(player, sendMessage);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("祈愿需要钻石：{1}。"), 
						String.valueOf(q_prayBean.getQ_pray_exp_cost()));	
				return;
			}
			
			//扣除钻石
			if(ManagerPool.backpackManager.changeGold(player, -q_prayBean.getQ_pray_exp_cost(), Reasons.PRAY, action) == false) {
				MessageUtil.tell_player_message(player, sendMessage);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("扣除钻石失败。"));	
				return;
			}
			
			//扣除次数
			player.setPrayExpCount(player.getPrayExpCount() + 1);
			
			//增加经验
			prayExp = countPrayExp(player);
			ManagerPool.playerManager.addExp(player, prayExp, AttributeChangeReason.PRAY);
			MessageUtil.notify_player(player, Notifys.NORMAL, String.format(ResManager.getInstance().getString("祈祷获得经验%d。"), prayExp));
			
			//记录日志
			prayLog.setType(type);
			prayLog.setPrayTimes(player.getPrayExpCount());
			prayLog.setCost(q_prayBean.getQ_pray_exp_cost());
			prayLog.setCount(prayExp);
		}else {
			log.error("stReqPray type error! type = " + type);
			return;
		}
		
		//返回祈愿界面信息
		this.stReqPrayInfo(player);
		sendMessage.setIsSuccess((byte) 1);
		MessageUtil.tell_player_message(player, sendMessage);
		
		try {
			LogService.getInstance().execute(prayLog);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 打开祈愿界面
	 * @param player
	 */
	public void stReqPrayInfo(Player player) {
		if(player == null) {
			return;
		}
		
		PrayInfo prayInfo = new PrayInfo();
		int total = 1 + ManagerPool.vipManager.getPrayAddition(player);
		
		prayInfo.setPrayGold(countPrayGold(player));
		Q_prayBean gold_Q_prayBean = ManagerPool.dataManager.q_prayContainer.getQ_prayBeanByPrayTimes(player.getPrayGoldCount() + 1);
		if(gold_Q_prayBean == null) gold_Q_prayBean = ManagerPool.dataManager.q_prayContainer.getQ_prayBeanByPrayTimes(player.getPrayGoldCount()); 
		prayInfo.setPrayGoldCost(gold_Q_prayBean == null ? 0 : gold_Q_prayBean.getQ_pray_gold_cost());
		prayInfo.setPrayGoldTimes(total - player.getPrayGoldCount() < 0 ? 0 : total - player.getPrayGoldCount());
		
		prayInfo.setPrayExp(countPrayExp(player));
		Q_prayBean exp_Q_prayBean = ManagerPool.dataManager.q_prayContainer.getQ_prayBeanByPrayTimes(player.getPrayExpCount() + 1);
		if(exp_Q_prayBean == null) exp_Q_prayBean = ManagerPool.dataManager.q_prayContainer.getQ_prayBeanByPrayTimes(player.getPrayExpCount());
		prayInfo.setPrayExpCost(exp_Q_prayBean == null ? 0 : exp_Q_prayBean.getQ_pray_exp_cost());
		prayInfo.setPrayExpTimes(total - player.getPrayExpCount() < 0 ? 0 : total - player.getPrayExpCount());
		
		prayInfo.setFirstPray(player.getFirstPray());
		ResPrayInfoMessage resPrayInfoMessage = new ResPrayInfoMessage();
		resPrayInfoMessage.setPrayInfo(prayInfo);
		MessageUtil.tell_player_message(player, resPrayInfoMessage);
	}
	
	/**
	 * 祈愿增加的金币
	 * @param player
	 * @return
	 */
	private int countPrayGold(Player player) {
		if(player != null) {
			int key = ManagerPool.dataManager.q_characterContainer.getKey(player.getJob(), player.getLevel());
			Q_characterBean q_characterBean = ManagerPool.dataManager.q_characterContainer.getMap().get(key);
			if(q_characterBean != null) {
				//祈愿金币 = q_characterBean.q_remoney * 18000
				return q_characterBean.getQ_remoney() * 18000;
			}
		}
		return 0;
	}
	
	/**
	 * 祈愿增加的经验
	 * @param player
	 * @return
	 */
	private int countPrayExp(Player player) {
		if(player != null) {
			int key = ManagerPool.dataManager.q_characterContainer.getKey(player.getJob(), player.getLevel());
			Q_characterBean q_characterBean = ManagerPool.dataManager.q_characterContainer.getMap().get(key);
			if(q_characterBean != null) {
				//祈愿经验 = q_characterBean.q_rexp * 240
				return q_characterBean.getQ_rexp() * 240;
			}
		}
		return 0;
	}
	
}