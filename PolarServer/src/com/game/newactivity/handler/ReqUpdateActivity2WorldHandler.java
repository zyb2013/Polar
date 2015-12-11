package com.game.newactivity.handler;

import com.game.command.Handler;
import com.game.data.bean.Q_newActivityBean;
import com.game.newactivity.NewActivityContainer;
import com.game.newactivity.message.ReqUpdateActivity2WorldMessage;
import com.game.newactivity.message.ResUpdateActivity2WorldMessage;
import com.game.utils.MessageUtil;

/**
 * @author luminghua
 *
 * @date   2014年3月4日 下午9:52:52
 */
public class ReqUpdateActivity2WorldHandler extends Handler {

	@Override
	public void action() {
		ReqUpdateActivity2WorldMessage msg = (ReqUpdateActivity2WorldMessage)this.getMessage();
		Q_newActivityBean bean = msg.getBean();
		if(NewActivityContainer.getInstance().alterBean(bean)) {
			Q_newActivityBean q_newActivityBean = NewActivityContainer.getInstance().get(bean.getQ_id());
			ResUpdateActivity2WorldMessage res = new ResUpdateActivity2WorldMessage();
			res.setBean(q_newActivityBean);
			MessageUtil.send_to_world(res);
		}
	}

}
