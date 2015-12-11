package com.game.newactivity.handler;

import com.game.command.Handler;
import com.game.newactivity.NewActivityManager;
import com.game.newactivity.message.ReqGetNewActivityInfo;
import com.game.player.structs.Player;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午2:49:17
 */
public class ReqGetNewActivityInfoHandler extends Handler {

	@Override
	public void action() {
		ReqGetNewActivityInfo msg = (ReqGetNewActivityInfo)this.getMessage();
		NewActivityManager.getInstance().sendDetailActivityInfo((Player)this.getParameter(), msg.getActivityId());
	}

}
