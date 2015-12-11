package com.game.bank.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import java.util.List;
import java.util.ArrayList;
import com.game.bank.bean.BankLogInfo;
/**
  * 返回 钱庄提取记录 
 */
public class ResQueryBankLogMessage extends Message{

	//玩家操作集合
	private List<BankLogInfo> playerName = new ArrayList<BankLogInfo>();
	/**
 	 *set 玩家操作集合
	 *@return
	 */
	public void setPlayerName(List<BankLogInfo> playerName){
		this.playerName = playerName;
	}

	/**
 	 *get 玩家操作集合
	 *@return
	 */
	public List<BankLogInfo> getPlayerName(){
		return this.playerName;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家操作集合
		writeShort(buf,playerName.size());
		for(int i = 0;i < playerName.size();i++){
			writeBean(buf,playerName.get(i));
		}
		return true;
	}

	@Override
	public int getId() {
		return 510202;
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
		//玩家操作集合
		int playerName_length = readShort(buf);
		for(int i = 0;i < playerName_length;i++){
			playerName.add((BankLogInfo)readBean(buf,BankLogInfo.class));
		}
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家操作集合
		buf.append("playerName:{");
		for(int i=0;i<playerName.size();i++){
			buf.append(playerName.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}