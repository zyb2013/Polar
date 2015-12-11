package com.game.player.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 同步充值排行榜
 */
public class ReqSyncPlayerRechargeMessage extends Message{

	//玩家ID
	private long playerId;
	//玩家充值额度
	private int rechargeGold;
	//玩家充值时间
	private long rechargeTime;
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
 	 *set 玩家充值额度
	 *@return
	 */
	public void setRechargeGold(int rechargeGold){
		this.rechargeGold = rechargeGold;
	}

	/**
 	 *get 玩家充值额度
	 *@return
	 */
	public int getRechargeGold(){
		return this.rechargeGold;
	}

	/**
 	 *set 玩家充值时间
	 *@return
	 */
	public void setRechargeTime(long rechargeTime){
		this.rechargeTime = rechargeTime;
	}

	/**
 	 *get 玩家充值时间
	 *@return
	 */
	public long getRechargeTime(){
		return this.rechargeTime;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家ID
		writeLong(buf,this.playerId);
		//玩家充值额度
		writeInt(buf,this.rechargeGold);
		//玩家充值时间
		writeLong(buf,this.rechargeTime);
		return true;
	}

	@Override
	public int getId() {
		return 542302;
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
		//玩家充值额度
		this.rechargeGold = readInt(buf);
		//玩家充值时间
		this.rechargeTime = readLong(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家ID
		buf.append("playerId:"+playerId+",");
		//玩家充值额度
		buf.append("rechargeGold:"+rechargeGold+",");
		//玩家充值时间
		buf.append("rechargeTime:"+rechargeTime+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}