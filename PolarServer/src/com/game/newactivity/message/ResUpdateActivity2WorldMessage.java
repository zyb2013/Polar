package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.data.bean.Q_newActivityBean;
import com.game.message.Message;
import com.game.message.pool.ServerProtocol;

/**
 * @author luminghua
 *
 * @date   2014年3月4日 下午9:42:43
 */
public class ResUpdateActivity2WorldMessage extends Message {

	@Override
	public int getId() {
		return ServerProtocol.ResUpdateActivity2WorldMessage;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	private Q_newActivityBean bean;

	@Override
	public boolean read(IoBuffer buff) {
		bean = new Q_newActivityBean();
		bean.setQ_id(readInt(buff));
		bean.setQ_desc(readString(buff));
		bean.setQ_notice(readString(buff));
		bean.setQ_startAndEnd(readString(buff));
		bean.setQ_stime(readString(buff));
		bean.setQ_logic(readString(buff));
		bean.setQ_condDesc(readString(buff));
		bean.setQ_expend(readString(buff));
		bean.setQ_onof(readByte(buff)==1);
		bean.setQ_group(readString(buff));
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeInt(buff,bean.getQ_id());
		writeString(buff,bean.getQ_desc());
		writeString(buff,bean.getQ_notice());
		writeString(buff,bean.getQ_startAndEnd());
		writeString(buff,bean.getQ_stime());
		writeString(buff,bean.getQ_logic());
		writeString(buff,bean.getQ_condDesc());
		writeString(buff,bean.getQ_expend());
		writeByte(buff,(byte) (bean.isQ_onof()?1:0));
		writeString(buff,bean.getQ_group());
		return true;
	}

	public Q_newActivityBean getBean() {
		return bean;
	}

	public void setBean(Q_newActivityBean bean) {
		this.bean = bean;
	}
}
