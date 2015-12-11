package com.game.liveness.message;
import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * 请求活跃度返回
 * @author hongxiao.z
 * @date   2013-12-26  下午3:27:57
 */
public class ResLivenessToClientMessage extends Message
{
	/**
	 * 当前活跃度
	 */
	private short liveness;
	
	public ResLivenessToClientMessage(short liveness)
	{
		this.liveness = liveness;
	}
	
	public int getLiveness() 
	{
		return liveness;
	}

	@Override
	public int getId() 
	{
		return 600101;
	}

	@Override
	public String getQueue() 
	{
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	@Override
	public boolean read(IoBuffer arg0) 
	{
		this.liveness = readShort(arg0);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) 
	{
		//将活跃度写入流
		writeShort(buf, this.liveness);
		return true;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}
