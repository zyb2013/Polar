package com.game.gradegift.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 请求领取等级礼包
 */
public class ReqGainGradeGiftMessage extends Message{

	//礼包ID
	private int giftId;
	/**
 	 *set 礼包ID
	 *@return
	 */
	public void setGiftId(int giftId){
		this.giftId = giftId;
	}

	/**
 	 *get 礼包ID
	 *@return
	 */
	public int getGiftId(){
		return this.giftId;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//礼包ID
		writeInt(buf,this.giftId);
		return true;
	}

	@Override
	public int getId() {
		return 600204;
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
		//礼包ID
		this.giftId = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//礼包ID
		buf.append("giftId:"+giftId+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}