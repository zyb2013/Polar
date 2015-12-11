/**
 * 
 */
package com.game.map.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2014年1月13日 上午9:38:40
 * 
 * 全身加11加13
 */
public class ResStrengthLevelMessage extends Message {

	//角色id
	private long personId;
	//全身强化等级，暂时只有0,11,13
	private byte strengthLevel;
	
	@Override
	public int getId() {
		return 542103;
	}
	
	@Override
	public boolean read(IoBuffer buf) {
		personId = readLong(buf);
		strengthLevel = readByte(buf);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) {
		writeLong(buf,personId);
		writeByte(buf,strengthLevel);
		return true;
	}


	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
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
