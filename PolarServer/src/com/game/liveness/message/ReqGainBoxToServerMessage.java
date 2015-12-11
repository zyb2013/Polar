package com.game.liveness.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 请求领取宝箱奖励
 * @author hongxiao.z
 * @date   2013-12-26  下午2:37:01
 */
public class ReqGainBoxToServerMessage extends Message{
	
	/**
	 * 宝箱ID
	 */
	private int boxid;
	
	@Override
	public int getId() 
	{
		return 600104;
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
	public boolean read(IoBuffer buf) 
	{
		this.boxid = readInt(buf);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) 
	{
		writeInt(buf, this.boxid);
		return true;
	}

	public int getBoxid() {
		return boxid;
	}
}