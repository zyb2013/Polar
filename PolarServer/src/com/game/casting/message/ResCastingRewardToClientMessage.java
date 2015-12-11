package com.game.casting.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.casting.bean.CastingGridInfo;
import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 铸造和一键锻造后返回客户端消息
 */
public class ResCastingRewardToClientMessage extends Message{

	//添加到铸造奖励仓库的物品
	private CastingGridInfo castingGridInfo;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//添加到铸造奖励仓库的物品
		writeBean(buf, this.castingGridInfo);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//添加到铸造奖励仓库的物品
		this.castingGridInfo = (CastingGridInfo)readBean(buf, CastingGridInfo.class);
		return true;
	}
	
	/**
	 * get 添加到铸造奖励仓库的物品
	 * @return
	 */
	public CastingGridInfo getCastingGridInfo() {
		return castingGridInfo;
	}

	/**
	 * set 添加到铸造奖励仓库的物品
	 */
	public void setCastingGridInfo(CastingGridInfo castingGridInfo) {
		this.castingGridInfo = castingGridInfo;
	}

	@Override
	public int getId() {
		return 529102;
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
		//添加到铸造奖励仓库的物品
		buf.append("castingGridInfo:" + castingGridInfo +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}