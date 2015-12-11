/**
 * 
 */
package com.game.equip.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 * 
 * @date 2013年12月24日 上午9:32:50
 * 
 *       萌宠穿戴
 */
public class ResEquipPetMessage extends Message {

	private long playerId;
	private int equipPetId;// 如果为0就代表是脱萌宠

	@Override
	public int getId() {
		return 103895;
	}


	@Override
	public boolean read(IoBuffer buf) {
		playerId = readLong(buf);
		equipPetId = readInt(buf);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) {
		writeLong(buf, playerId);
		writeInt(buf, equipPetId);
		return true;
	}
	
	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getEquipPetId() {
		return equipPetId;
	}

	public void setEquipPetId(int equipPetId) {
		this.equipPetId = equipPetId;
	}


	/*
	 * @see com.game.message.Message#getQueue()
	 */
	@Override
	public String getQueue() {
		// TODO Auto-generated constructor stub
		return null;
	}

	/*
	 * @see com.game.message.Message#getServer()
	 */
	@Override
	public String getServer() {
		// TODO Auto-generated constructor stub
		return null;
	}


}
