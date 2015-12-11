package com.game.newactivity.handler;

import com.game.command.Handler;
import com.game.newactivity.NewActivityManager;
import com.game.newactivity.message.ResUpdateActivity2WorldMessage;

/**
 * @author luminghua
 *
 * @date   2014年3月5日 下午2:18:13
 */
public class ResUpdateActivity2WorldHandler extends Handler {

	@Override
	public void action() {
		ResUpdateActivity2WorldMessage msg = (ResUpdateActivity2WorldMessage)this.getMessage();
		NewActivityManager.getInstance().responseUpdateActivity(msg.getBean());
	}

}
