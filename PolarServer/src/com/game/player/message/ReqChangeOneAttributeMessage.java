package com.game.player.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;


/**
 * 加点报文
 */
public class ReqChangeOneAttributeMessage extends Message{
	//【力量，体力，敏捷，智力，预留属性】
	private int strength;
	private int vitality;
	private int agile;
	private int intelligence;
	
	
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


	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//【力量，体力，敏捷，智力，预留属性】
		writeInt(buf, this.strength);
		writeInt(buf, this.vitality);
		writeInt(buf, this.agile);
		writeInt(buf, this.intelligence);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//【力量，体力，敏捷，智力，预留属性】
		this.strength=readInt(buf);
		this.vitality=readInt(buf);
		this.agile=readInt(buf);
		this.intelligence=readInt(buf);
		return true;
	}
	
	
	@Override
	public int getId() {
		return 103890;
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