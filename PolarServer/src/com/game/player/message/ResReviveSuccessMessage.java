package com.game.player.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 复活成功消息
 */
public class ResReviveSuccessMessage extends Message{

	//角色ID
	private long personId;
	//复活类型
	// 复活方式 1-原地复活 2-回城复活 3-自动复活
	private byte reason;
	/**
 	 *set 角色ID
	 *@return
	 */
	public void setPersonId(long personId){
		this.personId = personId;
	}

	/**
 	 *get 角色ID
	 *@return
	 */
	public long getPersonId(){
		return this.personId;
	}

	/**
 	 *set 复活类型
	 *@return
	 */
	public void setReason(byte reason){
		this.reason = reason;
	}

	/**
 	 *get 复活类型
	 *@return
	 */
	public byte getReason(){
		return this.reason;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色ID
		writeLong(buf,this.personId);
		//复活类型
		writeByte(buf,this.reason);
		return true;
	}

	@Override
	public int getId() {
		return 103102;
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
		//角色ID
		this.personId = readLong(buf);
		//复活类型
		this.reason = readByte(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//角色ID
		buf.append("personId:"+personId+",");
		//复活类型
		buf.append("reason:"+reason+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}