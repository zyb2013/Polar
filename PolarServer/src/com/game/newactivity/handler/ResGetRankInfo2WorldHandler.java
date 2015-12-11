package com.game.newactivity.handler;

import com.game.command.Handler;
import com.game.newactivity.ActivityRankManager;
import com.game.newactivity.message.ResGetRankInfo2WorldMessage;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午5:13:27
 */
public class ResGetRankInfo2WorldHandler extends Handler {

	@Override
	public void action() {
		ResGetRankInfo2WorldMessage msg = (ResGetRankInfo2WorldMessage)this.getMessage();
		ActivityRankManager.getInstance().worldResRank(msg.getPlayerId(), msg.getType(), msg.getInfoList());
	}

}
