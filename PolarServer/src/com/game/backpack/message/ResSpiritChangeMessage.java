package com.game.backpack.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 精魄值通知
 */
public class ResSpiritChangeMessage extends Message{

	//当前精魄值
	private int spirit;
	/**
 	 *set 当前精魄值
	 *@return
	 */
	public void setSpirit(int spirit){
		this.spirit = spirit;
	}

	/**
 	 *get 当前精魄值
	 *@return
	 */
	public int getSpirit(){
		return this.spirit;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//当前精魄值
		writeInt(buf,this.spirit);
		return true;
	}

	@Override
	public int getId() {
		return 600011;
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
		//当前精魄值
		this.spirit = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//当前精魄值
		buf.append("spirit:"+spirit+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}