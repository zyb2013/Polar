package com.game.prompt.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 添加到收藏消息
 */
public class AddToFavoriteMessage extends Message{

	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		return true;
	}
	
	
	@Override
	public int getId() {
		return 109110;
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