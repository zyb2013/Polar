/**
 * 
 */
package com.game.task.message;

import java.util.ArrayList;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 * 
 * @date 2013年12月30日 下午8:01:38
 * 
 *       获取新手引导信息
 */
public class ResGetGuidesMessage extends Message {

	private ArrayList<Integer> guides = new ArrayList<Integer>();

	@Override
	public int getId() {
		return 120119;
	}

	@Override
	public boolean read(IoBuffer buff) {
		int size = readShort(buff);
		for (int i = 0; i < size; i++) {
			guides.add(readInt(buff));
		}
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		if (guides != null) {
			writeShort(buff, guides.size());
			for (int i = 0; i < guides.size(); i++) {
				writeInt(buff, guides.get(i));
			}
		} else {
			writeShort(buff, 0);
		}
		return true;
	}

	public ArrayList<Integer> getGuides() {
		return guides;
	}

	public void setGuides(ArrayList<Integer> guides) {
		this.guides = guides;
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
