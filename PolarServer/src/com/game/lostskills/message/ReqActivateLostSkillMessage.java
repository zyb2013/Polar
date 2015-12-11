package com.game.lostskills.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 请求升级技能
 */
public class ReqActivateLostSkillMessage extends Message{

	//技能类型
	private int skillType;
	/**
 	 *set 技能类型
	 *@return
	 */
	public void setSkillType(int skillType){
		this.skillType = skillType;
	}

	/**
 	 *get 技能类型
	 *@return
	 */
	public int getSkillType(){
		return this.skillType;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//技能类型
		writeInt(buf,this.skillType);
		return true;
	}

	@Override
	public int getId() {
		return 600206;
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
		//技能类型
		this.skillType = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//技能类型
		buf.append("skillType:"+skillType+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}