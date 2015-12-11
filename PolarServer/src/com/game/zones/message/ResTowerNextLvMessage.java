package com.game.zones.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 请求切换下一层成功返回
 */
public class ResTowerNextLvMessage extends Message{

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		return true;
	}

	@Override
	public int getId() {
		return 600002;
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
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}