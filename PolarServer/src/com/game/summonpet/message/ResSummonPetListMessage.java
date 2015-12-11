package com.game.summonpet.message;


import com.game.summonpet.bean.SummonPetDetailInfo;
import java.util.List;
import java.util.ArrayList;
import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 召唤怪列表消息
 */
public class ResSummonPetListMessage extends Message{

	//召唤怪列表
	private List<SummonPetDetailInfo> summonpets = new ArrayList<SummonPetDetailInfo>();
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//召唤怪列表
		writeShort(buf, summonpets.size());
		for (int i = 0; i < summonpets.size(); i++) {
			writeBean(buf, summonpets.get(i));
		}
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//召唤怪列表
		int pets_length = readShort(buf);
		for (int i = 0; i < pets_length; i++) {
			summonpets.add((SummonPetDetailInfo)readBean(buf, SummonPetDetailInfo.class));
		}
		return true;
	}
	
	/**
	 * get 召唤怪列表
	 * @return 
	 */
	public List<SummonPetDetailInfo> getPets(){
		return summonpets;
	}
	
	/**
	 * set 召唤怪列表
	 */
	public void setPets(List<SummonPetDetailInfo> pets){
		this.summonpets = pets;
	}
	
	
	@Override
	public int getId() {
		return 510101;
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
		//召唤怪列表
		buf.append("pets:{");
		for (int i = 0; i < summonpets.size(); i++) {
			buf.append(summonpets.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}