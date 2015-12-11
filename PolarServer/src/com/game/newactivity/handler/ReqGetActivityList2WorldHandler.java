package com.game.newactivity.handler;

import java.util.Collection;

import com.game.command.Handler;
import com.game.data.bean.Q_newActivityBean;
import com.game.newactivity.NewActivityContainer;
import com.game.newactivity.message.ResGetActivityList2WorldMessage;
import com.game.utils.MessageUtil;

/**
 * @author luminghua
 *
 * @date   2014年3月4日 下午9:52:34
 */
public class ReqGetActivityList2WorldHandler extends Handler {

	@Override
	public void action() {
		Collection<Q_newActivityBean> allBeans = NewActivityContainer.getInstance().getAll();
		ResGetActivityList2WorldMessage msg = new ResGetActivityList2WorldMessage();
		msg.setBeanList(allBeans);
		MessageUtil.send_to_world(msg);
	}

}
