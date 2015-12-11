package com.game.newactivity.handler;

import com.game.command.Handler;
import com.game.newactivity.ActivityRankManager;
import com.game.newactivity.message.ReqGetRankInfoMessage;
import com.game.player.structs.Player;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午5:13:27
 */
public class ReqGetRankInfoHandler extends Handler {

	@Override
	public void action() {
		ReqGetRankInfoMessage msg = (ReqGetRankInfoMessage)this.getMessage();
		ActivityRankManager.getInstance().playerAskRank(((Player)this.getParameter()), msg.getActivityId());
	}

}
