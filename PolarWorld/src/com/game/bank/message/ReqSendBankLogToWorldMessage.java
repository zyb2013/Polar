package com.game.bank.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 发送 玩家操作记录到世界服务器
 */
public class ReqSendBankLogToWorldMessage extends Message{

	//玩家名
	private String playerName;
	//操作  0 月卡购买  1月卡提取  2升级购买 3升级提取
	private int option;
	//数量 
	private int count;
	/**
 	 *set 玩家名
	 *@return
	 */
	public void setPlayerName(String playerName){
		this.playerName = playerName;
	}

	/**
 	 *get 玩家名
	 *@return
	 */
	public String getPlayerName(){
		return this.playerName;
	}

	/**
 	 *set 操作  0 月卡购买  1月卡提取  2升级购买 3升级提取
	 *@return
	 */
	public void setOption(int option){
		this.option = option;
	}

	/**
 	 *get 操作  0 月卡购买  1月卡提取  2升级购买 3升级提取
	 *@return
	 */
	public int getOption(){
		return this.option;
	}

	/**
 	 *set 数量 
	 *@return
	 */
	public void setCount(int count){
		this.count = count;
	}

	/**
 	 *get 数量 
	 *@return
	 */
	public int getCount(){
		return this.count;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家名
		writeString(buf,this.playerName);
		//操作  0 月卡购买  1月卡提取  2升级购买 3升级提取
		writeInt(buf,this.option);
		//数量 
		writeInt(buf,this.count);
		return true;
	}

	@Override
	public int getId() {
		return 510308;
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
		//玩家名
		this.playerName = readString(buf);
		//操作  0 月卡购买  1月卡提取  2升级购买 3升级提取
		this.option = readInt(buf);
		//数量 
		this.count = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家名
		buf.append("playerName:"+playerName+",");
		//操作  0 月卡购买  1月卡提取  2升级购买 3升级提取
		buf.append("option:"+option+",");
		//数量 
		buf.append("count:"+count+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}