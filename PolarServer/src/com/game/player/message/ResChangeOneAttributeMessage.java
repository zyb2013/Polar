package com.game.player.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;


/**
 * 加点返回报文
 */
public class ResChangeOneAttributeMessage extends Message{
	//1：成功，0：失败
	private int endValue;
	
	//【力量，体力，敏捷，智力，预留属性】
	private int strength;
	private int vitality;
	private int agile;
	private int intelligence;
	private int restPlusPoint;
	

	public int getEndValue() {
		return endValue;
	}

	public void setEndValue(int endValue) {
		this.endValue = endValue;
	}
	

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	public int getAgile() {
		return agile;
	}

	public void setAgile(int agile) {
		this.agile = agile;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	public int getRestPlusPoint() {
		return restPlusPoint;
	}

	public void setRestPlusPoint(int restPlusPoint) {
		this.restPlusPoint = restPlusPoint;
	}

	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		writeInt(buf, this.endValue);
		writeInt(buf, this.strength);
		writeInt(buf, this.vitality);
		writeInt(buf, this.agile);
		writeInt(buf, this.intelligence);
		writeInt(buf, this.restPlusPoint);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		this.endValue=readInt(buf);
		this.strength=readInt(buf);
		this.vitality=readInt(buf);
		this.agile=readInt(buf);
		this.intelligence=readInt(buf);
		this.restPlusPoint=readInt(buf);
		return true;
	}
	
	
	@Override
	public int getId() {
		return 103891;
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
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}