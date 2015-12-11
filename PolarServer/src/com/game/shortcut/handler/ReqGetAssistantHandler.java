/**
 * 
 */
package com.game.shortcut.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.shortcut.message.ReqGetAssistantMessage;

/**
 * @author luminghua
 *
 * @date   2013年12月17日 上午10:56:19
 */
public class ReqGetAssistantHandler extends Handler {

	Logger log = Logger.getLogger(ReqGetAssistantHandler.class);

	/* 
	 * @see com.game.command.ICommand#action()
	 */
	@Override
	public void action() {
		try {
			ReqGetAssistantMessage msg = (ReqGetAssistantMessage) this.getMessage();
			ManagerPool.shortCutManager.getAssistantInfo((Player) this.getParameter());
		} catch (ClassCastException e) {
			log.error("", e);
		}
	}

}
