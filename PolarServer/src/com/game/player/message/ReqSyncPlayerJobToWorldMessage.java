package com.game.player.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
  * 更新玩家职业信息
 */
public class ReqSyncPlayerJobToWorldMessage extends Message{

	//玩家的ID
	private long playerID;
	
	//角色新职业
	private byte job;

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家的ID
		writeLong(buf, this.playerID);
		//角色新职业
		writeByte(buf, this.job);
		return true;
	}
	
	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家的ID
		this.playerID = readLong(buf);
		//角色新职业
		this.job = readByte(buf);
		return true;
	}
	
	/**
	 * get 玩家的ID
	 * @return
	 */
	public long getPlayerID() {
		return playerID;
	}

	/**
	 * set 玩家的ID
	 */
	public void setPlayerID(long playerID) {
		this.playerID = playerID;
	}

	/**
	 * get 玩家的ID
	 * @return
	 */
	public byte getJob() {
		return job;
	}

	/**
	 * set 玩家的ID
	 */
	public void setJob(byte job) {
		this.job = job;
	}

	@Override
	public int getId() {
		return 520300;
	}
	
	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer(){
		return null;
	} 
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家的ID
		buf.append("playerID:"+playerID+",");
		//角色新职业
		buf.append("job:"+job+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}