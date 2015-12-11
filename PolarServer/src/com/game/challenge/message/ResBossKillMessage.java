package com.game.challenge.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import java.util.List;
import java.util.ArrayList;
import com.game.challenge.bean.BossKillInfo;
/**
  * BOSS 掉落信息
 */
public class ResBossKillMessage extends Message{

	//Boss信息
	private List<BossKillInfo> bossInfos = new ArrayList<BossKillInfo>();
	//服务端时间
	private long serverTime;
	/**
 	 *set Boss信息
	 *@return
	 */
	public void setBossInfos(List<BossKillInfo> bossEvents){
		this.bossInfos = bossInfos;
	}

	/**
 	 *get Boss信息
	 *@return
	 */
	public List<BossKillInfo> getBossInfos(){
		return this.bossInfos;
	}

	/**
 	 *set 服务端时间
	 *@return
	 */
	public void setServerTime(long serverTime){
		this.serverTime = serverTime;
	}

	/**
 	 *get 服务端时间
	 *@return
	 */
	public long getServerTime(){
		return this.serverTime;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//Boss信息
		writeShort(buf,bossInfos.size());
		for(int i = 0;i < bossInfos.size();i++){
			writeBean(buf,bossInfos.get(i));
		}
		//服务端时间
		writeLong(buf,this.serverTime);
		return true;
	}

	@Override
	public int getId() {
		return 510021;
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
		//Boss信息
		int bossEvents_length = readShort(buf);
		for(int i = 0;i < bossEvents_length;i++){
			bossInfos.add((BossKillInfo)readBean(buf,BossKillInfo.class));
		}
		//服务端时间
		this.serverTime = readLong(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//Boss信息
		buf.append("bossEvents:{");
		for(int i=0;i<bossInfos.size();i++){
			buf.append(bossInfos.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//服务端时间
		buf.append("serverTime:"+serverTime+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}