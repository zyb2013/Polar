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
	private List<BankLogInfo> bankLogs = new ArrayList<BankLogInfo>();
	//总记录数
	private int count;
	/**
 	 *set 玩家操作集合
	 *@return
	 */
	public void setBankLogs(List<BankLogInfo> bankLogs){
		this.bankLogs = bankLogs;
	}

	/**
 	 *get 玩家操作集合
	 *@return
	 */
	public List<BankLogInfo> getBankLogs(){
		return this.bankLogs;
	}

	/**
 	 *set 总记录数
	 *@return
	 */
	public void setCount(int count){
		this.count = count;
	}

	/**
 	 *get 总记录数
	 *@return
	 */
	public int getCount(){
		return this.count;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家操作集合
		writeShort(buf,bankLogs.size());
		for(int i = 0;i < bankLogs.size();i++){
			writeBean(buf,bankLogs.get(i));
		}
		//总记录数
		writeInt(buf,this.count);
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
		int bankLogs_length = readShort(buf);
		for(int i = 0;i < bankLogs_length;i++){
			bankLogs.add((BankLogInfo)readBean(buf,BankLogInfo.class));
		}
		//总记录数
		this.count = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家操作集合
		buf.append("bankLogs:{");
		for(int i=0;i<bankLogs.size();i++){
			buf.append(bankLogs.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//总记录数
		buf.append("count:"+count+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}