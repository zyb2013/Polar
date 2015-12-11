package com.game.newactivity.handler;

import com.game.command.Handler;
import com.game.newactivity.NewActivityManager;
import com.game.newactivity.message.ReqGetRankInfo2WorldMessage;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午5:13:27
 */
public class ReqGetRankInfo2WorldHandler extends Handler {

	@Override
	public void action() {
		ReqGetRankInfo2WorldMessage msg = (ReqGetRankInfo2WorldMessage)this.getMessage();
		NewActivityManager.getInstance().serverAskRank(msg.getServerId(),msg.getPlayerId(),msg.getType(),msg.getCount());
	}

}
