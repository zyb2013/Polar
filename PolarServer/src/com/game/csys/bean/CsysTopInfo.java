package com.game.csys.bean;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Bean;
/**
  * 玩家排行榜信息 
 */
public class CsysTopInfo  extends Bean{

	//玩家名字
	private String playername;
	//玩家ID
	private long playerid;
	//杀敌数量
	private int kill;
	//死亡次数
	private int death;
	//采集数量
	private int collectCount;
	//积分
	private int integral;
	//排名
	private int ranking;
	//经验（预留字段结算时使用）
	private long exp;
	/**
 	 *set 玩家名字
	 *@return
	 */
	public void setPlayername(String playername){
		this.playername = playername;
	}

	/**
 	 *get 玩家名字
	 *@return
	 */
	public String getPlayername(){
		return this.playername;
	}

	/**
 	 *set 玩家ID
	 *@return
	 */
	public void setPlayerid(long playerid){
		this.playerid = playerid;
	}

	/**
 	 *get 玩家ID
	 *@return
	 */
	public long getPlayerid(){
		return this.playerid;
	}

	/**
 	 *set 杀敌数量
	 *@return
	 */
	public void setKill(int kill){
		this.kill = kill;
	}

	/**
 	 *get 杀敌数量
	 *@return
	 */
	public int getKill(){
		return this.kill;
	}

	/**
 	 *set 死亡次数
	 *@return
	 */
	public void setDeath(int death){
		this.death = death;
	}

	/**
 	 *get 死亡次数
	 *@return
	 */
	public int getDeath(){
		return this.death;
	}

	/**
 	 *set 采集数量
	 *@return
	 */
	public void setCollectCount(int collectCount){
		this.collectCount = collectCount;
	}

	/**
 	 *get 采集数量
	 *@return
	 */
	public int getCollectCount(){
		return this.collectCount;
	}

	/**
 	 *set 积分
	 *@return
	 */
	public void setIntegral(int integral){
		this.integral = integral;
	}

	/**
 	 *get 积分
	 *@return
	 */
	public int getIntegral(){
		return this.integral;
	}

	/**
 	 *set 排名
	 *@return
	 */
	public void setRanking(int ranking){
		this.ranking = ranking;
	}

	/**
 	 *get 排名
	 *@return
	 */
	public int getRanking(){
		return this.ranking;
	}

	/**
 	 *set 经验（预留字段结算时使用）
	 *@return
	 */
	public void setExp(long exp){
		this.exp = exp;
	}

	/**
 	 *get 经验（预留字段结算时使用）
	 *@return
	 */
	public long getExp(){
		return this.exp;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家名字
		writeString(buf,this.playername);
		//玩家ID
		writeLong(buf,this.playerid);
		//杀敌数量
		writeInt(buf,this.kill);
		//死亡次数
		writeInt(buf,this.death);
		//采集数量
		writeInt(buf,this.collectCount);
		//积分
		writeInt(buf,this.integral);
		//排名
		writeInt(buf,this.ranking);
		//经验（预留字段结算时使用）
		writeLong(buf,this.exp);
		return true;
	}

	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家名字
		this.playername = readString(buf);
		//玩家ID
		this.playerid = readLong(buf);
		//杀敌数量
		this.kill = readInt(buf);
		//死亡次数
		this.death = readInt(buf);
		//采集数量
		this.collectCount = readInt(buf);
		//积分
		this.integral = readInt(buf);
		//排名
		this.ranking = readInt(buf);
		//经验（预留字段结算时使用）
		this.exp = readLong(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家名字
		buf.append("playername:"+playername+",");
		//玩家ID
		buf.append("playerid:"+playerid+",");
		//杀敌数量
		buf.append("kill:"+kill+",");
		//死亡次数
		buf.append("death:"+death+",");
		//采集数量
		buf.append("collectCount:"+collectCount+",");
		//积分
		buf.append("integral:"+integral+",");
		//排名
		buf.append("ranking:"+ranking+",");
		//经验（预留字段结算时使用）
		buf.append("exp:"+exp+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}