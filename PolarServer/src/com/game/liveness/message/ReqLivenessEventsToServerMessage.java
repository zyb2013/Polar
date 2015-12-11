package com.game.liveness.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 请求活跃度事件进度列表
 * @author hongxiao.z
 * @date   2013-12-26  下午2:37:01
 */
public class ReqLivenessEventsToServerMessage extends Message{
	
	@Override
	public int getId() {
		return 600202;
	}
	
	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}

	@Override
	public boolean read(IoBuffer arg0) {
		return true;
	}

	@Override
	public boolean write(IoBuffer arg0) {
		return true;
	}
	
}