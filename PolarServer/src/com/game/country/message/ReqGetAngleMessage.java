package com.game.country.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 请求获得宝箱消息
 */
public class ReqGetAngleMessage extends Message{

	
	int count;
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		writeInt(buf, this.count);
		return true;
	}

	@Override
	public int getId() {
		return 550121;
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
		this.count =	readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}