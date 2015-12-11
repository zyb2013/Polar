/**
 * 
 */
package com.game.vip.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 * 
 * @date 2013年12月28日 下午3:37:03
 * 
 *       登录推送vip信息
 */
public class ResVIPInfoWhenLoginMessage extends Message {

	private int vipLevel;

	@Override
	public int getId() {
		return 147105;
	}


	@Override
	public boolean read(IoBuffer buff) {
		vipLevel = readInt(buff);
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeInt(buff, vipLevel);
		return true;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
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
