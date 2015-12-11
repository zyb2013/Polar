package com.game.csys.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 积分变更消息 
 */
public class ResChangeIntegralMessage extends Message{

	//改变的原因     1 杀怪    3击杀玩家   4 采集
	private int changeType;
	//玩家原本的积分
	private int playerIntegral;
	//获得的基础积分
	private int Integral;
	//额外追加的 积分
	private int addedIntegral;
	/**
 	 *set 改变的原因     1 杀怪    3击杀玩家   4 采集
	 *@return
	 */
	public void setChangeType(int changeType){
		this.changeType = changeType;
	}

	/**
 	 *get 改变的原因     1 杀怪    3击杀玩家   4 采集
	 *@return
	 */
	public int getChangeType(){
		return this.changeType;
	}

	/**
 	 *set 玩家原本的积分
	 *@return
	 */
	public void setPlayerIntegral(int playerIntegral){
		this.playerIntegral = playerIntegral;
	}

	/**
 	 *get 玩家原本的积分
	 *@return
	 */
	public int getPlayerIntegral(){
		return this.playerIntegral;
	}

	/**
 	 *set 获得的基础积分
	 *@return
	 */
	public void setIntegral(int Integral){
		this.Integral = Integral;
	}

	/**
 	 *get 获得的基础积分
	 *@return
	 */
	public int getIntegral(){
		return this.Integral;
	}

	/**
 	 *set 额外追加的 积分
	 *@return
	 */
	public void setAddedIntegral(int addedIntegral){
		this.addedIntegral = addedIntegral;
	}

	/**
 	 *get 额外追加的 积分
	 *@return
	 */
	public int getAddedIntegral(){
		return this.addedIntegral;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//改变的原因     1 杀怪    3击杀玩家   4 采集
		writeInt(buf,this.changeType);
		//玩家原本的积分
		writeInt(buf,this.playerIntegral);
		//获得的基础积分
		writeInt(buf,this.Integral);
		//额外追加的 积分
		writeInt(buf,this.addedIntegral);
		return true;
	}

	@Override
	public int getId() {
		return 550102;
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
		//改变的原因     1 杀怪    3击杀玩家   4 采集
		this.changeType = readInt(buf);
		//玩家原本的积分
		this.playerIntegral = readInt(buf);
		//获得的基础积分
		this.Integral = readInt(buf);
		//额外追加的 积分
		this.addedIntegral = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//改变的原因     1 杀怪    3击杀玩家   4 采集
		buf.append("changeType:"+changeType+",");
		//玩家原本的积分
		buf.append("playerIntegral:"+playerIntegral+",");
		//获得的基础积分
		buf.append("Integral:"+Integral+",");
		//额外追加的 积分
		buf.append("addedIntegral:"+addedIntegral+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}