/**
 * 
 */
package com.game.equipstreng.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2014年1月9日 下午7:27:25
 */
public class ResStrengthEffectMessage extends Message {
	
	private long persionId;	//玩家id
	private int equipModelId;	//装备模板id
	private byte strengthLevel;	//强化等级
	private byte pos;	//装备部位
	

	@Override
	public int getId() {
		return 130205;
	}

	
	@Override
	public boolean read(IoBuffer buff) {
		this.persionId =readLong(buff);
		this.equipModelId = readInt(buff);
		this.strengthLevel = readByte(buff);
		this.pos = readByte(buff);
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeLong(buff,persionId);
		writeInt(buff,equipModelId);
		writeByte(buff,strengthLevel);
		writeByte(buff,pos);
		return true;
	}
	
	
	
	public byte getPos() {
		return pos;
	}


	public void setPos(byte pos) {
		this.pos = pos;
	}


	public long getPersionId() {
		return persionId;
	}


	public void setPersionId(long persionId) {
		this.persionId = persionId;
	}


	public int getEquipModelId() {
		return equipModelId;
	}


	public void setEquipModelId(int equipModelId) {
		this.equipModelId = equipModelId;
	}


	public byte getStrengthLevel() {
		return strengthLevel;
	}


	public void setStrengthLevel(byte strengthLevel) {
		this.strengthLevel = strengthLevel;
	}


	@Override
	public String getQueue() {
		return null;
	}

	
	@Override
	public String getServer() {
		return null;
	}

	


}
