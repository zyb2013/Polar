/**
 * 
 */
package com.game.vip.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.vip.manager.VipManager;
import com.game.vip.message.ReqOpenVIPMessage;

/**
 * @author luminghua
 * 
 * @date 2013年12月28日 下午4:15:20
 * 
 *       开通vip
 */
public class ReqOpenVIPHandler extends Handler {

	Logger log = Logger.getLogger(ReqOpenVIPHandler.class);

	@Override
	public void action() {
		try {
			ReqOpenVIPMessage msg = (ReqOpenVIPMessage) this.getMessage();
			VipManager.getInstance().buyVIP((Player) this.getParameter(), msg.getOpenType(), msg.getVipType(), msg.getFriendName());
		} catch (ClassCastException e) {
			log.error("", e);
		}
	}

}
