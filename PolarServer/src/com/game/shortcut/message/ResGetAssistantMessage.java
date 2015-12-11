/**
 * 
 */
package com.game.shortcut.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2013年12月17日 上午10:55:58
 */
public class ResGetAssistantMessage extends Message {

	private String saveString;
	/* 
	 * @see com.game.message.Message#getId()
	 */
	@Override
	public int getId() {
		return 131209;
	}

	/*
	 * @see com.game.message.Bean#read(org.apache.mina.core.buffer.IoBuffer)
	 */
	@Override
	public boolean read(IoBuffer arg0) {
		this.saveString = readString(arg0);
		return true;
	}

	/*
	 * @see com.game.message.Bean#write(org.apache.mina.core.buffer.IoBuffer)
	 */
	@Override
	public boolean write(IoBuffer arg0) {
		writeString(arg0, saveString);
		return true;
	}

	/**
	 * @return the saveString
	 */
	public String getSaveString() {
		return saveString;
	}

	/**
	 * @param saveString
	 *            the saveString to set
	 */
	public void setSaveString(String saveString) {
		this.saveString = saveString;
	}

	/*
	 * @see com.game.message.Message#getQueue()
	 */
	@Override
	public String getQueue() {
		return null;
	}

	/*
	 * @see com.game.message.Message#getServer()
	 */
	@Override
	public String getServer() {
		return null;
	}

}
