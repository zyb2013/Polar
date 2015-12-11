package com.game.pray.message;

import com.game.message.Message;
import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 请求祈愿消息
 */
public class ReqPrayMessage extends Message{

	//1-祈愿金币;2-祈愿经验
	private byte type;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//1-祈愿金币;2-祈愿经验
		writeByte(buf, this.type);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//1-祈愿金币;2-祈愿经验
		this.type = readByte(buf);
		return true;
	}
	
	/**
	 * get 1-祈愿金币;2-祈愿经验
	 */
	public byte getType() {
		return type;
	}

	/**
	 * set 1-祈愿金币;2-祈愿经验
	 * @param type
	 */
	public void setType(byte type) {
		this.type = type;
	}
	

	@Override
	public int getId() {
		return 528331;
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
		//1-祈愿金币;2-祈愿经验
		buf.append("type:" + type +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}