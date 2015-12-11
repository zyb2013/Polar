/**
 * 
 */
package com.game.shortcut.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.shortcut.message.ReqSaveAssistantMessage;

/**
 * @author luminghua
 *
 * @date   2013年12月17日 上午10:52:47
 */
public class ReqSaveAssistantHandler extends Handler {

	Logger log = Logger.getLogger(ReqSaveAssistantHandler.class);

	/* 
	 * @see com.game.command.ICommand#action()
	 */
	@Override
	public void action() {
		try {
			ReqSaveAssistantMessage message = (ReqSaveAssistantMessage) this.getMessage();
			ManagerPool.shortCutManager.saveAssistantInfo((Player) this.getParameter(), message.getSaveString());
		} catch (ClassCastException e) {
			log.error("", e);
		}
	}

}
