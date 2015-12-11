package com.game.lostskills.bean;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Bean;
/**
  * 技能信息
 */
public class LostSkillInfo extends Bean{

	//技能ID
	private int skillId;
	//技能类型
	private int skillType;
	//技能等级
	private int skillLv;
	/**
 	 *set 技能ID
	 *@return
	 */
	public void setSkillId(int skillId){
		this.skillId = skillId;
	}

	/**
 	 *get 技能ID
	 *@return
	 */
	public int getSkillId(){
		return this.skillId;
	}

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
 	 *set 技能等级
	 *@return
	 */
	public void setSkillLv(int skillLv){
		this.skillLv = skillLv;
	}

	/**
 	 *get 技能等级
	 *@return
	 */
	public int getSkillLv(){
		return this.skillLv;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//技能ID
		writeInt(buf,this.skillId);
		//技能类型
		writeInt(buf,this.skillType);
		//技能等级
		writeInt(buf,this.skillLv);
		return true;
	}

	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//技能ID
		this.skillId = readInt(buf);
		//技能类型
		this.skillType = readInt(buf);
		//技能等级
		this.skillLv = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//技能ID
		buf.append("skillId:"+skillId+",");
		//技能类型
		buf.append("skillType:"+skillType+",");
		//技能等级
		buf.append("skillLv:"+skillLv+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}