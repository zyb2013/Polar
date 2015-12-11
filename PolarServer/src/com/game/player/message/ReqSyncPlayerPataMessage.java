package com.game.player.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 同步爬塔排行榜
 */
public class ReqSyncPlayerPataMessage extends Message{

	//玩家ID
	private long playerId;
	//玩家爬塔副本ID
	private int pataMapId;
	//玩家爬塔时间
	private long pataTime;
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
 	 *set 玩家爬塔副本ID
	 *@return
	 */
	public void setPataMapId(int pataMapId){
		this.pataMapId = pataMapId;
	}

	/**
 	 *get 玩家爬塔副本ID
	 *@return
	 */
	public int getPataMapId(){
		return this.pataMapId;
	}

	/**
 	 *set 玩家爬塔时间
	 *@return
	 */
	public void setPataTime(long pataTime){
		this.pataTime = pataTime;
	}

	/**
 	 *get 玩家爬塔时间
	 *@return
	 */
	public long getPataTime(){
		return this.pataTime;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家ID
		writeLong(buf,this.playerId);
		//玩家爬塔关卡
		writeInt(buf,this.pataMapId);
		//玩家爬塔时间
		writeLong(buf,this.pataTime);
		return true;
	}

	@Override
	public int getId() {
		return 542303;
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
		//玩家爬塔副本ID
		this.pataMapId = readInt(buf);
		//玩家爬塔时间
		this.pataTime = readLong(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家ID
		buf.append("playerId:"+playerId+",");
		//玩家爬塔副本ID
		buf.append("pataStage:"+pataMapId+",");
		//玩家爬塔时间
		buf.append("pataTime:"+pataTime+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}