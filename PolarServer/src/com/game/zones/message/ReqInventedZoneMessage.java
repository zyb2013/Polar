package com.game.zones.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 进入虚拟副本协议
 */
public class ReqInventedZoneMessage extends Message{

	//虚拟副本ID
	private int zoneid;
	//0 是进入  1是离开
	private byte type;
	/**
 	 *set 虚拟副本ID
	 *@return
	 */
	public void setZoneid(int zoneid){
		this.zoneid = zoneid;
	}

	/**
 	 *get 虚拟副本ID
	 *@return
	 */
	public int getZoneid(){
		return this.zoneid;
	}

	/**
 	 *set 0 是进入  1是离开
	 *@return
	 */
	public void setType(byte type){
		this.type = type;
	}

	/**
 	 *get 0 是进入  1是离开
	 *@return
	 */
	public byte getType(){
		return this.type;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//虚拟副本ID
		writeInt(buf,this.zoneid);
		//0 是进入  1是离开
		writeByte(buf,this.type);
		return true;
	}

	@Override
	public int getId() {
		return 528320;
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
		//虚拟副本ID
		this.zoneid = readInt(buf);
		//0 是进入  1是离开
		this.type = readByte(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//虚拟副本ID
		buf.append("zoneid:"+zoneid+",");
		//0 是进入  1是离开
		buf.append("type:"+type+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}