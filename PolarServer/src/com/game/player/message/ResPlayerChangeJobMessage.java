package com.game.player.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 转职成功信息
 */
public class ResPlayerChangeJobMessage extends Message{

	//角色新职业
	private byte job;
	//额外附加 属性点
	private int addbase;
	//额外奖励   内容 或参数  Json 格式   内容待定   暂空 
	private String otherParms;
	/**
 	 *set 角色新职业
	 *@return
	 */
	public void setJob(byte job){
		this.job = job;
	}

	/**
 	 *get 角色新职业
	 *@return
	 */
	public byte getJob(){
		return this.job;
	}

	/**
 	 *set 额外附加 属性点
	 *@return
	 */
	public void setAddbase(int addbase){
		this.addbase = addbase;
	}

	/**
 	 *get 额外附加 属性点
	 *@return
	 */
	public int getAddbase(){
		return this.addbase;
	}

	/**
 	 *set 额外奖励   内容 或参数  Json 格式   内容待定   暂空 
	 *@return
	 */
	public void setOtherParms(String otherParms){
		this.otherParms = otherParms;
	}

	/**
 	 *get 额外奖励   内容 或参数  Json 格式   内容待定   暂空 
	 *@return
	 */
	public String getOtherParms(){
		return this.otherParms;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色新职业
		writeByte(buf,this.job);
		//额外附加 属性点
		writeInt(buf,this.addbase);
		//额外奖励   内容 或参数  Json 格式   内容待定   暂空 
		writeString(buf,this.otherParms);
		return true;
	}

	@Override
	public int getId() {
		return 510001;
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
		//角色新职业
		this.job = readByte(buf);
		//额外附加 属性点
		this.addbase = readInt(buf);
		//额外奖励   内容 或参数  Json 格式   内容待定   暂空 
		this.otherParms = readString(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//角色新职业
		buf.append("job:"+job+",");
		//额外附加 属性点
		buf.append("addbase:"+addbase+",");
		//额外奖励   内容 或参数  Json 格式   内容待定   暂空 
		buf.append("otherParms:"+otherParms+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}