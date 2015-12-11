package com.game.player.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import java.util.List;
import java.util.ArrayList;
/**
  * 敌对列表改变
 */
public class ResChangePlayerEnemiesToClientMessage extends Message{

	//敌对列表
	private List<Long> enemys = new ArrayList<Long>();
	/**
 	 *set 敌对列表
	 *@return
	 */
	public void setEnemys(List<Long> enemys){
		this.enemys = enemys;
	}

	/**
 	 *get 敌对列表
	 *@return
	 */
	public List<Long> getEnemys(){
		return this.enemys;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//敌对列表
		writeShort(buf,enemys.size());
		for(int i = 0;i < enemys.size();i++){
			writeLong(buf,enemys.get(i));
		}
		return true;
	}

	@Override
	public int getId() {
		return 511401;
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
		//敌对列表
		int enemys_length = readShort(buf);
		for(int i = 0;i < enemys_length;i++){
			enemys.add(readLong(buf));
		}
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//敌对列表
		buf.append("enemys:{");
		for(int i=0;i<enemys.size();i++){
			buf.append(enemys.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}