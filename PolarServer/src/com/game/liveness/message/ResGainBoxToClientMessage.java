package com.game.liveness.message;
import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * 请求领取宝箱奖励返回
 * @author hongxiao.z
 * @date   2013-12-26  下午3:27:57
 */
public class ResGainBoxToClientMessage extends Message
{
	//宝箱ID
	private int boxid;
	
	/**
	 * 领取结果
	 */
	private byte result;
	
	public ResGainBoxToClientMessage(int boxid, byte result)
	{
		this.boxid = boxid;
		this.result = result;
	}

	public byte getResult()
	{
		return this.result;
	}

	@Override
	public int getId() 
	{
		return 600105;
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
		this.boxid = readInt(arg0);
		this.result = readByte(arg0);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) 
	{
		//将领取结果写入流
		writeInt(buf, this.boxid);
		writeByte(buf, this.result);
		return true;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}

	public int getBoxid() {
		return boxid;
	}

	public void setBoxid(int boxid) {
		this.boxid = boxid;
	}

	public void setResult(byte result) {
		this.result = result;
	}
}
