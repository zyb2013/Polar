package com.game.csys.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 进入活动副本消息
 */
public class ResEnterActivitieMessage extends Message{

	//类型 0 活动结束   1活动开启
	private int type;
	/**
 	 *set 类型 0 活动结束   1活动开启
	 *@return
	 */
	public void setType(int type){
		this.type = type;
	}

	/**
 	 *get 类型 0 活动结束   1活动开启
	 *@return
	 */
	public int getType(){
		return this.type;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//类型 0 活动结束   1活动开启
		writeInt(buf,this.type);
		return true;
	}

	@Override
	public int getId() {
		return 550103;
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
		//类型 0 活动结束   1活动开启
		this.type = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//类型 0 活动结束   1活动开启
		buf.append("type:"+type+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}