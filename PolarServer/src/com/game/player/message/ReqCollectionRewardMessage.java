package com.game.player.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 点击收藏 申请 道具奖励
 */
public class ReqCollectionRewardMessage extends Message{

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		return true;
	}

	@Override
	public int getId() {
		return 55002;
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