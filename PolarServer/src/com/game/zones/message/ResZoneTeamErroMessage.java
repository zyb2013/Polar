package com.game.zones.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 不符合进入 副本  条件的 玩家ID
 */
public class ResZoneTeamErroMessage extends Message{

	//不满足进入副本的条件类型  暂定 是 0
	private byte type;
	//队员ID
	private long memberid;
	//玩家名
	private String name;
	//副本ID
	private int zoneid;
	/**
 	 *set 不满足进入副本的条件类型  暂定 是 0
	 *@return
	 */
	public void setType(byte type){
		this.type = type;
	}

	/**
 	 *get 不满足进入副本的条件类型  暂定 是 0
	 *@return
	 */
	public byte getType(){
		return this.type;
	}

	/**
 	 *set 队员ID
	 *@return
	 */
	public void setMemberid(long memberid){
		this.memberid = memberid;
	}

	/**
 	 *get 队员ID
	 *@return
	 */
	public long getMemberid(){
		return this.memberid;
	}

	/**
 	 *set 玩家名
	 *@return
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
 	 *get 玩家名
	 *@return
	 */
	public String getName(){
		return this.name;
	}

	/**
 	 *set 副本ID
	 *@return
	 */
	public void setZoneid(int zoneid){
		this.zoneid = zoneid;
	}

	/**
 	 *get 副本ID
	 *@return
	 */
	public int getZoneid(){
		return this.zoneid;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//不满足进入副本的条件类型  暂定 是 0
		writeByte(buf,this.type);
		//队员ID
		writeLong(buf,this.memberid);
		//玩家名
		writeString(buf,this.name);
		//副本ID
		writeInt(buf,this.zoneid);
		return true;
	}

	@Override
	public int getId() {
		return 528118;
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
		//不满足进入副本的条件类型  暂定 是 0
		this.type = readByte(buf);
		//队员ID
		this.memberid = readLong(buf);
		//玩家名
		this.name = readString(buf);
		//副本ID
		this.zoneid = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//不满足进入副本的条件类型  暂定 是 0
		buf.append("type:"+type+",");
		//队员ID
		buf.append("memberid:"+memberid+",");
		//玩家名
		buf.append("name:"+name+",");
		//副本ID
		buf.append("zoneid:"+zoneid+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}