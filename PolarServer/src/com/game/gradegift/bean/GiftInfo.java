package com.game.gradegift.bean;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Bean;
/**
  * 等级礼包信息
 */
public class GiftInfo extends Bean{

	//礼包ID
	private int giftId;
	//剩次数
	private int reamain;
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
 	 *set 剩次数
	 *@return
	 */
	public void setReamain(int reamain){
		this.reamain = reamain;
	}

	/**
 	 *get 剩次数
	 *@return
	 */
	public int getReamain(){
		return this.reamain;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//礼包ID
		writeInt(buf,this.giftId);
		//剩次数
		writeInt(buf,this.reamain);
		return true;
	}

	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//礼包ID
		this.giftId = readInt(buf);
		//剩次数
		this.reamain = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//礼包ID
		buf.append("giftId:"+giftId+",");
		//剩次数
		buf.append("reamain:"+reamain+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}