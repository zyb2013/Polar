package com.game.gradegift.bean;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Bean;
/**
  * 等级礼包信息
 */
public class GainGradeGiftInfo extends Bean{

	//礼包ID
	private int giftId;
	//礼包领取记录（领取的次数）
	private int isGain;
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
 	 *set 礼包领取记录（领取的次数）
	 *@return
	 */
	public void setIsGain(int isGain){
		this.isGain = isGain;
	}

	/**
 	 *get 礼包领取记录（领取的次数）
	 *@return
	 */
	public int getIsGain(){
		return this.isGain;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//礼包ID
		writeInt(buf,this.giftId);
		//礼包领取记录（领取的次数）
		writeInt(buf,this.isGain);
		return true;
	}

	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//礼包ID
		this.giftId = readInt(buf);
		//礼包领取记录（领取的次数）
		this.isGain = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//礼包ID
		buf.append("giftId:"+giftId+",");
		//礼包领取记录（领取的次数）
		buf.append("isGain:"+isGain+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}