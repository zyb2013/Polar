package com.game.zones.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 爬塔领奖次数信息推送
 */
public class ResTowerRewardSurplusMessage extends Message{

	//剩余次数
	private int surplus;
	//领取需花费的钻石
	private int golds;
	/**
 	 *set 剩余次数
	 *@return
	 */
	public void setSurplus(int surplus){
		this.surplus = surplus;
	}

	/**
 	 *get 剩余次数
	 *@return
	 */
	public int getSurplus(){
		return this.surplus;
	}

	/**
 	 *set 领取需花费的钻石
	 *@return
	 */
	public void setGolds(int golds){
		this.golds = golds;
	}

	/**
 	 *get 领取需花费的钻石
	 *@return
	 */
	public int getGolds(){
		return this.golds;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//剩余次数
		writeInt(buf,this.surplus);
		//领取需花费的钻石
		writeInt(buf,this.golds);
		return true;
	}

	@Override
	public int getId() {
		return 600000;
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
		//剩余次数
		this.surplus = readInt(buf);
		//领取需花费的钻石
		this.golds = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//剩余次数
		buf.append("surplus:"+surplus+",");
		//领取需花费的钻石
		buf.append("golds:"+golds+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}