package com.game.player.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * PK值改变
 */
public class ResPlayerPKValueChangeMessage extends Message{

	//玩家ID
	private long personId;
	//PK值
	private int pkValue;
	/**
 	 *set 玩家ID
	 *@return
	 */
	public void setPersonId(long personId){
		this.personId = personId;
	}

	/**
 	 *get 玩家ID
	 *@return
	 */
	public long getPersonId(){
		return this.personId;
	}

	/**
 	 *set PK值
	 *@return
	 */
	public void setPkValue(int pkValue){
		this.pkValue = pkValue;
	}

	/**
 	 *get PK值
	 *@return
	 */
	public int getPkValue(){
		return this.pkValue;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家ID
		writeLong(buf,this.personId);
		//PK值
		writeInt(buf,this.pkValue);
		return true;
	}

	@Override
	public int getId() {
		return 511400;
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
		this.personId = readLong(buf);
		//PK值
		this.pkValue = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家ID
		buf.append("personId:"+personId+",");
		//PK值
		buf.append("pkValue:"+pkValue+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}