package com.game.casting.message;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 一键出售后发送到客户端的消息
 */
public class ResCastingSellToClientMessage extends Message{

	//一键出售的物品格子编号列表
	private List<Integer> grididxList;
	
	//一键出售的物品对应的金币列表
	private List<Integer> goldList;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//一键出售的物品格子编号列表
		writeShort(buf, grididxList.size());
		for (int i = 0; i < grididxList.size(); i++) {
			writeInt(buf, grididxList.get(i));
		}
		//一键出售的物品对应的金币列表
		writeShort(buf, goldList.size());
		for (int i = 0; i < goldList.size(); i++) {
			writeInt(buf, goldList.get(i));
		}
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//一键出售的物品对应的金币列表
		int grididexList_length = readShort(buf);
		for (int i = 0; i < grididexList_length; i++) {
			grididxList.add(readInt(buf));
		}
		//一键出售的物品对应的金币列表
		int goldList_length = readShort(buf);
		for (int i = 0; i < goldList_length; i++) {
			goldList.add(readInt(buf));
		}
		return true;
	}

	/**
	 * get 一键出售的物品格子编号列表
	 * @return
	 */
	public List<Integer> getGrididxList() {
		return grididxList;
	}

	/**
	 * set 一键出售的物品格子编号列表
	 */
	public void setGrididxList(List<Integer> grididxList) {
		this.grididxList = grididxList;
	}

	/**
	 * get 一键出售的物品对应的金币列表
	 * @return
	 */
	public List<Integer> getGoldList() {
		return goldList;
	}

	/**
	 * set 一键出售的物品对应的金币列表
	 */
	public void setGoldList(List<Integer> goldList) {
		this.goldList = goldList;
	}

	@Override
	public int getId() {
		return 529105;
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
		//一键出售的物品对应的金币列表
		buf.append("grididxList:{");
		for (int i = 0; i < grididxList.size(); i++) {
			buf.append(grididxList.get(i).toString() +",");
		}
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		//一键出售的物品对应的金币列表
		buf.append("goldPointList:{");
		for (int i = 0; i < goldList.size(); i++) {
			buf.append(goldList.get(i).toString() +",");
		}
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}