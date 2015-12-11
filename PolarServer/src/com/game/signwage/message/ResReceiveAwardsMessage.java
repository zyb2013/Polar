package com.game.signwage.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 领取签到奖励成功返回
 * @author hongxiao.z
 * @date   2013-12-30  下午4:27:24
 */
public class ResReceiveAwardsMessage extends Message
{
	//签到次数，奖励索引
	private byte num;
	
	public ResReceiveAwardsMessage()
	{
		
	}
	
	public ResReceiveAwardsMessage(byte num)
	{
		this.num = num;
	}
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf)
	{
		writeByte(buf, num);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf)
	{
		this.num = readByte(buf);
		return true;
	}
	
	
	@Override
	public int getId() {
		return 600012;
	}

	public byte getNum() {
		return num;
	}

	public void setNum(byte num) {
		this.num = num;
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
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}