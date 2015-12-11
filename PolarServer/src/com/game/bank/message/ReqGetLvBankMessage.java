package com.game.bank.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 传入等级 领取升级钱庄
 */
public class ReqGetLvBankMessage extends Message{

	//领取的等级 
	private int lv;
	/**
 	 *set 领取的等级 
	 *@return
	 */
	public void setLv(int lv){
		this.lv = lv;
	}

	/**
 	 *get 领取的等级 
	 *@return
	 */
	public int getLv(){
		return this.lv;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//领取的等级 
		writeInt(buf,this.lv);
		return true;
	}

	@Override
	public int getId() {
		return 510004;
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
		//领取的等级 
		this.lv = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//领取的等级 
		buf.append("lv:"+lv+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}