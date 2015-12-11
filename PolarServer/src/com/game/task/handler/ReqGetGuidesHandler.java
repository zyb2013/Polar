/**
 * 
 */
package com.game.task.handler;

import java.util.ArrayList;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.task.message.ResGetGuidesMessage;
import com.game.utils.MessageUtil;

/**
 * @author luminghua
 *
 * @date   2013年12月30日 下午8:11:35
 */
public class ReqGetGuidesHandler extends Handler {

	@Override
	public void action() {
		Player player = (Player) this.getParameter();
		ArrayList<Integer> guides = player.getGuides();
		ResGetGuidesMessage msg = new ResGetGuidesMessage();
		msg.setGuides(guides);
		MessageUtil.tell_player_message(player, msg);
	}

}
