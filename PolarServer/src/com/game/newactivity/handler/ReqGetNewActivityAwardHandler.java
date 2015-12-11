package com.game.newactivity.handler;

import com.game.command.Handler;
import com.game.newactivity.NewActivityManager;
import com.game.newactivity.message.ReqGetNewActivityAward;
import com.game.player.structs.Player;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午2:47:43
 */
public class ReqGetNewActivityAwardHandler extends Handler {

	@Override
	public void action() {
		ReqGetNewActivityAward msg = (ReqGetNewActivityAward)this.getMessage();
		NewActivityManager.getInstance().getAward((Player)this.getParameter(), msg.getActivityId(), msg.getOrder());
	}

}
