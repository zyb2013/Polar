/**
 * 
 */
package com.game.shortcut.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 * 
 * @date 2013年12月17日 上午10:49:14
 * 
 *       战斗小助手
 */
public class ReqSaveAssistantMessage extends Message {

	// 保存信息
	private String saveString;
	/* 
	 * @see com.game.message.Message#getId()
	 */
	@Override
	public int getId() {
		return 131210;
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
