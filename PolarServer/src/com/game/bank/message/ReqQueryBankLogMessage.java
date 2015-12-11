package com.game.bank.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 查询钱庄记录消息
 */
public class ReqQueryBankLogMessage extends Message{

	//类型  0 全部  1提取  2存入 
	private int option;
	//索引起点
	private int indexlittle;
	//索引终点
	private int indexLarge;
	/**
 	 *set 类型  0 全部  1提取  2存入 
	 *@return
	 */
	public void setOption(int option){
		this.option = option;
	}

	/**
 	 *get 类型  0 全部  1提取  2存入 
	 *@return
	 */
	public int getOption(){
		return this.option;
	}

	/**
 	 *set 索引起点
	 *@return
	 */
	public void setIndexlittle(int indexlittle){
		this.indexlittle = indexlittle;
	}

	/**
 	 *get 索引起点
	 *@return
	 */
	public int getIndexlittle(){
		return this.indexlittle;
	}

	/**
 	 *set 索引终点
	 *@return
	 */
	public void setIndexLarge(int indexLarge){
		this.indexLarge = indexLarge;
	}

	/**
 	 *get 索引终点
	 *@return
	 */
	public int getIndexLarge(){
		return this.indexLarge;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//类型  0 全部  1提取  2存入 
		writeInt(buf,this.option);
		//索引起点
		writeInt(buf,this.indexlittle);
		//索引终点
		writeInt(buf,this.indexLarge);
		return true;
	}

	@Override
	public int getId() {
		return 510005;
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
		//类型  0 全部  1提取  2存入 
		this.option = readInt(buf);
		//索引起点
		this.indexlittle = readInt(buf);
		//索引终点
		this.indexLarge = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//类型  0 全部  1提取  2存入 
		buf.append("option:"+option+",");
		//索引起点
		buf.append("indexlittle:"+indexlittle+",");
		//索引终点
		buf.append("indexLarge:"+indexLarge+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}