package com.game.vip.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.vip.message.ReqReceiveVIPTopRewardMessage;

/**
 * 
 * @author luminghua
 * 
 * @date 2013年12月28日 下午4:16:02
 * 
 *       暂时不用
 */
public class ReqReceiveVIPTopRewardHandler extends Handler{

	Logger log = Logger.getLogger(ReqReceiveVIPTopRewardHandler.class);
	//领取至尊VIP奖励
	public void action(){
		try{
			Player player = (Player)this.getParameter();
			ReqReceiveVIPTopRewardMessage msg = (ReqReceiveVIPTopRewardMessage)this.getMessage();
			// VipManager.getInstance().receiveVipTopReward(player);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}