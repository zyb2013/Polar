/**
 * 
 */
package com.game.task.handler;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.task.message.ReqSaveGuidesMessage;

/**
 * @author luminghua
 *
 * @date   2013年12月30日 下午8:08:11
 */
public class ReqSaveGuidesHandler extends Handler {

	@Override
	public void action() {
		ReqSaveGuidesMessage msg = (ReqSaveGuidesMessage) this.getMessage();
		Player player = (Player) this.getParameter();
		player.setGuides(msg.getGuides());
	}

}
