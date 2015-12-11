package com.game.newactivity.message;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.data.bean.Q_newActivityBean;
import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2014年3月4日 下午9:23:38
 */
public class ResGetActivityList2WorldMessage extends Message {

	@Override
	public int getId() {
		return 511311;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}
	
	private Collection<Q_newActivityBean> beanList;

	@Override
	public boolean read(IoBuffer buff) {
		int size = readShort(buff);
		if(size != 0) {
			beanList = new LinkedList<Q_newActivityBean>();
			for(int i=0; i<size; i++) {
				Q_newActivityBean bean = new Q_newActivityBean();
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
				beanList.add(bean);
			}
		}
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		if(beanList == null) {
			writeShort(buff,0);
		}else {
			writeShort(buff,beanList.size());
			for(Q_newActivityBean bean:beanList) {
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
			}
		}
		return true;
	}

	public Collection<Q_newActivityBean> getBeanList() {
		return beanList;
	}

	public void setBeanList(Collection<Q_newActivityBean> beanList) {
		this.beanList = beanList;
	}
}
