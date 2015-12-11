package com.game.lostskills.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import java.util.List;
import java.util.ArrayList;
import com.game.lostskills.bean.LostSkillInfo;
/**
  * 推送遗落技能信息
 */
public class ResLostSkillInfosMessage extends Message{

	//技能列表信息
	private List<LostSkillInfo> skillInfos = new ArrayList<LostSkillInfo>();
	/**
 	 *set 技能列表信息
	 *@return
	 */
	public void setSkillInfos(List<LostSkillInfo> skillInfos){
		this.skillInfos = skillInfos;
	}

	/**
 	 *get 技能列表信息
	 *@return
	 */
	public List<LostSkillInfo> getSkillInfos(){
		return this.skillInfos;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//技能列表信息
		writeShort(buf,skillInfos.size());
		for(int i = 0;i < skillInfos.size();i++){
			writeBean(buf,skillInfos.get(i));
		}
		return true;
	}

	@Override
	public int getId() {
		return 600007;
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
		//技能列表信息
		int skillInfos_length = readShort(buf);
		for(int i = 0;i < skillInfos_length;i++){
			skillInfos.add((LostSkillInfo)readBean(buf,LostSkillInfo.class));
		}
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//技能列表信息
		buf.append("skillInfos:{");
		for(int i=0;i<skillInfos.size();i++){
			buf.append(skillInfos.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}