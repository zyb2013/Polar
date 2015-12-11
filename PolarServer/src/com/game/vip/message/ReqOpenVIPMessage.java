/**
 * 
 */
package com.game.vip.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 * 
 * @date 2013年12月28日 下午4:17:33
 * 
 *       请求开通vip
 */
public class ReqOpenVIPMessage extends Message {


	// 开通类型，1 自己开通，2 帮好友开通
	private byte openType;

	// 卡类型，0 体验卡，1 月卡，2 季度卡，3 半年卡
	private byte vipType;

	// 如果是帮好友开通，这里是好友的名称
	private String friendName;

	@Override
	public int getId() {
		return 147205;
	}


	@Override
	public boolean read(IoBuffer buff) {
		openType = readByte(buff);
		vipType = readByte(buff);
		friendName = readString(buff);
		return false;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeByte(buff, openType);
		writeByte(buff, vipType);
		writeString(buff, friendName);
		return false;
	}

	public byte getOpenType() {
		return openType;
	}

	public void setOpenType(byte openType) {
		this.openType = openType;
	}

	public byte getVipType() {
		return vipType;
	}

	public void setVipType(byte vipType) {
		this.vipType = vipType;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	@Override
	public String getQueue() {
		// TODO Auto-generated constructor stub
		return null;
	}


	@Override
	public String getServer() {
		// TODO Auto-generated constructor stub
		return null;
	}

}
