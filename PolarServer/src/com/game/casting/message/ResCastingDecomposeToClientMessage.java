package com.game.casting.message;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 一键分解后发送到客户端的消息
 */
public class ResCastingDecomposeToClientMessage extends Message{

	//一键分解的物品格子编号列表
	private List<Integer> grididxList;
	
	//一键分解的物品对应的工艺度列表
	private List<Integer> technologyPointList;
	
	//玩家的工艺度
	private int technologyPoint;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//一键分解的物品格子编号列表
		writeShort(buf, grididxList.size());
		for (int i = 0; i < grididxList.size(); i++) {
			writeInt(buf, grididxList.get(i));
		}
		//一键分解的物品对应的工艺度列表
		writeShort(buf, technologyPointList.size());
		for (int i = 0; i < technologyPointList.size(); i++) {
			writeInt(buf, technologyPointList.get(i));
		}
		//玩家的工艺度
		writeInt(buf, this.technologyPoint);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//一键分解的物品对应的金币列表
		int grididexList_length = readShort(buf);
		for (int i = 0; i < grididexList_length; i++) {
			grididxList.add(readInt(buf));
		}
		//一键分解的物品对应的工艺度列表
		int technologyPointList_length = readShort(buf);
		for (int i = 0; i < technologyPointList_length; i++) {
			technologyPointList.add(readInt(buf));
		}
		//玩家的工艺度
		this.technologyPoint = readInt(buf);		
		return true;
	}

	/**
	 * get 一键分解的物品格子编号列表
	 * @return
	 */
	public List<Integer> getGrididxList() {
		return grididxList;
	}

	/**
	 * set 一键分解的物品格子编号列表
	 */
	public void setGrididxList(List<Integer> grididxList) {
		this.grididxList = grididxList;
	}

	/**
	 * get 一键分解的物品对应的工艺度列表
	 * @return
	 */
	public List<Integer> getTechnologyPointList() {
		return technologyPointList;
	}

	/**
	 * set 一键分解的物品对应的工艺度列表
	 */
	public void setTechnologyPointList(List<Integer> technologyPointList) {
		this.technologyPointList = technologyPointList;
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

	@Override
	public int getId() {
		return 529104;
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
		//一键分解的物品格子编号列表
		buf.append("grididxList:{");
		for (int i = 0; i < grididxList.size(); i++) {
			buf.append(grididxList.get(i).toString() +",");
		}
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		//一键分解的物品对应的工艺度列表
		buf.append("technologyPointList:{");
		for (int i = 0; i < technologyPointList.size(); i++) {
			buf.append(technologyPointList.get(i).toString() +",");
		}
		buf.append("},");
		//玩家的工艺度
		buf.append("technologyPoint:" + technologyPoint +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}