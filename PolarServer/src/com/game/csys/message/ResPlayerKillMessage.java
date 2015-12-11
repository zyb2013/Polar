package com.game.csys.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 玩家击杀数消息
 */
public class ResPlayerKillMessage extends Message{

	//玩家ID
	private long playerId;
	//击杀数
	private int count;
	/**
 	 *set 玩家ID
	 *@return
	 */
	public void setPlayerId(long playerId){
		this.playerId = playerId;
	}

	/**
 	 *get 玩家ID
	 *@return
	 */
	public long getPlayerId(){
		return this.playerId;
	}

	/**
 	 *set 击杀数
	 *@return
	 */
	public void setCount(int count){
		this.count = count;
	}

	/**
 	 *get 击杀数
	 *@return
	 */
	public int getCount(){
		return this.count;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家ID
		writeLong(buf,this.playerId);
		//击杀数
		writeInt(buf,this.count);
		return true;
	}

	@Override
	public int getId() {
		return 550105;
	}
	@Override
	public String getQueue() {
		return null;
	}
	@Override
	public String getServer(){
		return null;
	} 
	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家ID
		this.playerId = readLong(buf);
		//击杀数
		this.count = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家ID
		buf.append("playerId:"+playerId+",");
		//击杀数
		buf.append("count:"+count+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}