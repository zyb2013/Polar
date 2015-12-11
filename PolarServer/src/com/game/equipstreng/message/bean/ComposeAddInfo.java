package com.game.equipstreng.message.bean;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Bean;
/**
  * 选择增加概率的材料列表
 */
public class ComposeAddInfo extends Bean{

	//材料的实体ID
	private long equip_id;
	
	public ComposeAddInfo(long equip_id)
	{
		this.equip_id = equip_id;
	}
	
	public ComposeAddInfo(){}
	
	/**
 	 *set 材料的实体ID
	 *@return
	 */
	public void setEquip_id(long equip_id){
		this.equip_id = equip_id;
	}

	/**
 	 *get 材料的实体ID
	 *@return
	 */
	public long getEquip_id(){
		return this.equip_id;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//材料的实体ID
		writeLong(buf,this.equip_id);
		return true;
	}

	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//材料的实体ID
		this.equip_id = readLong(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//材料的实体ID
		buf.append("equip_id:"+equip_id+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}