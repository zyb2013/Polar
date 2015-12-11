package com.game.vip.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.vip.message.ReqReceiveVIPRewardMessage;

/**
 * 
 * @author luminghua
 * 
 * @date 2013年12月28日 下午4:15:46 暂时不用
 */
public class ReqReceiveVIPRewardHandler extends Handler{

	Logger log = Logger.getLogger(ReqReceiveVIPRewardHandler.class);

	public void action(){
		try{
			Player player = (Player)this.getParameter();
			ReqReceiveVIPRewardMessage msg = (ReqReceiveVIPRewardMessage)this.getMessage();
			// int vipid = ManagerPool.vipManager.getPlayerVipId(player);
			// if(vipid>0){
			// if(VipManager.getInstance().canReceiveReward(player) &&
			// VipManager.getInstance().receiveVIPReward(player)){ //领取成功 领取失败是否发送？
			// VipManager.getInstance().sendVipInfoToClient(player); //发送VIP信息
			// }
			// }
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}