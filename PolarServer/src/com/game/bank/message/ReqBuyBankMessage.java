package com.game.bank.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 购买钱庄
 */
public class ReqBuyBankMessage extends Message{

	//类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次 
	private int buyLv;
	/**
 	 *set 类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次 
	 *@return
	 */
	public void setBuyLv(int buyLv){
		this.buyLv = buyLv;
	}

	/**
 	 *get 类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次 
	 *@return
	 */
	public int getBuyLv(){
		return this.buyLv;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次 
		writeInt(buf,this.buyLv);
		return true;
	}

	@Override
	public int getId() {
		return 510003;
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
		//类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次 
		this.buyLv = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次 
		buf.append("buyLv:"+buyLv+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}