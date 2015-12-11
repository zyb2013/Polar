package com.game.bank.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 领取当日月卡钱庄奖励
 */
public class ReqGetMonthBankMessage extends Message{

	//类型  0 代表 领取即时奖励  1代表今日奖励 
	private int type;
	/**
 	 *set 类型  0 代表 领取即时奖励  1代表今日奖励 
	 *@return
	 */
	public void setType(int type){
		this.type = type;
	}

	/**
 	 *get 类型  0 代表 领取即时奖励  1代表今日奖励 
	 *@return
	 */
	public int getType(){
		return this.type;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//类型  0 代表 领取即时奖励  1代表今日奖励 
		writeInt(buf,this.type);
		return true;
	}

	@Override
	public int getId() {
		return 510006;
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
		//类型  0 代表 领取即时奖励  1代表今日奖励 
		this.type = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//类型  0 代表 领取即时奖励  1代表今日奖励 
		buf.append("type:"+type+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}