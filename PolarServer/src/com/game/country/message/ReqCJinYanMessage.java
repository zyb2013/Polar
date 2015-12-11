package com.game.country.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 禁言某个玩家说话1小时
 */
public class ReqCJinYanMessage extends Message{

	//被禁言玩家名字  带 区号  例如 [1区]as
	private String playername;
	/**
 	 *set 被禁言玩家名字  带 区号  例如 [1区]as
	 *@return
	 */
	public void setPlayername(String playername){
		this.playername = playername;
	}

	/**
 	 *get 被禁言玩家名字  带 区号  例如 [1区]as
	 *@return
	 */
	public String getPlayername(){
		return this.playername;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//被禁言玩家名字  带 区号  例如 [1区]as
		writeString(buf,this.playername);
		return true;
	}

	@Override
	public int getId() {
		return 550122;
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
		//被禁言玩家名字  带 区号  例如 [1区]as
		this.playername = readString(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//被禁言玩家名字  带 区号  例如 [1区]as
		buf.append("playername:"+playername+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}