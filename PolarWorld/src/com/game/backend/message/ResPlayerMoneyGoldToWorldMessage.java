package com.game.backend.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 向世界服返回玩家实时金币钻石信息消息
 */
public class ResPlayerMoneyGoldToWorldMessage extends Message{

	//角色Id
	private long personId;
	
	//金币
	private int money;
	
	//钻石
	private int gold;
	
	//绑定钻石
	private int bindgold;
	
	//临时钻石
	private int tmpgold;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色Id
		writeLong(buf, this.personId);
		//金币
		writeInt(buf, this.money);
		//钻石
		writeInt(buf, this.gold);
		//绑定钻石
		writeInt(buf, this.bindgold);
		//临时钻石
		writeInt(buf, this.tmpgold);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//角色Id
		this.personId = readLong(buf);
		//金币
		this.money = readInt(buf);
		//钻石
		this.gold = readInt(buf);
		//绑定钻石
		this.bindgold = readInt(buf);
		//临时钻石
		this.tmpgold = readInt(buf);
		return true;
	}
	
	/**
	 * get 角色Id
	 * @return 
	 */
	public long getPersonId(){
		return personId;
	}
	
	/**
	 * set 角色Id
	 */
	public void setPersonId(long personId){
		this.personId = personId;
	}
	
	/**
	 * get 金币
	 * @return 
	 */
	public int getMoney(){
		return money;
	}
	
	/**
	 * set 金币
	 */
	public void setMoney(int money){
		this.money = money;
	}
	
	/**
	 * get 钻石
	 * @return 
	 */
	public int getGold(){
		return gold;
	}
	
	/**
	 * set 钻石
	 */
	public void setGold(int gold){
		this.gold = gold;
	}
	
	/**
	 * get 绑定钻石
	 * @return 
	 */
	public int getBindgold(){
		return bindgold;
	}
	
	/**
	 * set 绑定钻石
	 */
	public void setBindgold(int bindgold){
		this.bindgold = bindgold;
	}
	
	/**
	 * get 临时钻石
	 * @return 
	 */
	public int getTmpgold(){
		return tmpgold;
	}
	
	/**
	 * set 临时钻石
	 */
	public void setTmpgold(int tmpgold){
		this.tmpgold = tmpgold;
	}
	
	
	@Override
	public int getId() {
		return 135303;
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
		//角色Id
		buf.append("personId:" + personId +",");
		//金币
		buf.append("money:" + money +",");
		//钻石
		buf.append("gold:" + gold +",");
		//绑定钻石
		buf.append("bindgold:" + bindgold +",");
		//临时钻石
		buf.append("tmpgold:" + tmpgold +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}