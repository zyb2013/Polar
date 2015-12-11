package com.game.casting.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.casting.bean.CastingBoxInfo;
import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 打开铸造工厂界面发送到客户端消息
 */
public class ResCastingOpenToClientMessage extends Message{

	//玩家的工艺度
	private int technologyPoint;

	//铸造工厂奖励仓库信息
	private CastingBoxInfo castingBoxInfo;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家的工艺度
		writeInt(buf, this.technologyPoint);
		//铸造工厂奖励仓库信息
		writeBean(buf, this.castingBoxInfo);
		return true;
	}

	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家的工艺度
		this.technologyPoint = readInt(buf);
		//铸造工厂奖励仓库信息
		this.castingBoxInfo = (CastingBoxInfo)readBean(buf, CastingBoxInfo.class);
		return true;
	}

	/**
	 * get 玩家的工艺度
	 * @return
	 */
	public int getTechnologyPoint() {
		return technologyPoint;
	}

	/**
	 * set 玩家的工艺度
	 */
	public void setTechnologyPoint(int technologyPoint) {
		this.technologyPoint = technologyPoint;
	}

	/**
	 * get 铸造工厂奖励仓库信息
	 * @return
	 */
	public CastingBoxInfo getCastingBoxInfo() {
		return castingBoxInfo;
	}

	/**
	 * set 铸造工厂奖励仓库信息
	 */
	public void setCastingBoxInfo(CastingBoxInfo castingBoxInfo) {
		this.castingBoxInfo = castingBoxInfo;
	}

	@Override
	public int getId() {
		return 529101;
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
		//玩家的工艺度
		buf.append("technologyPoint:" + technologyPoint +",");
		//铸造工厂奖励仓库信息
		if(this.castingBoxInfo!=null) buf.append("castingBoxInfo:" + castingBoxInfo.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}