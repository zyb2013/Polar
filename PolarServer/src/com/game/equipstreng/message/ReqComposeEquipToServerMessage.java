package com.game.equipstreng.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import java.util.List;
import java.util.ArrayList;
import com.game.equipstreng.message.bean.ComposeAddInfo;
/**
  * 爬塔领奖次数信息推送
 */
public class ReqComposeEquipToServerMessage extends Message{

	//将要合成的道具唯一ID
	private int compose_id;
	//是否绑定，默认0使用 1不使用
	private byte type;
	//选择作为合成材料的装备id
	private long equip_id;
	//选择增加概率的材料列表
	private List<ComposeAddInfo> materials = new ArrayList<ComposeAddInfo>();
	/**
 	 *set 将要合成的道具唯一ID
	 *@return
	 */
	public void setCompose_id(int compose_id){
		this.compose_id = compose_id;
	}

	/**
 	 *get 将要合成的道具唯一ID
	 *@return
	 */
	public int getCompose_id(){
		return this.compose_id;
	}

	/**
 	 *set 是否绑定，默认0使用 1不使用
	 *@return
	 */
	public void setType(byte type){
		this.type = type;
	}

	/**
 	 *get 是否绑定，默认0使用 1不使用
	 *@return
	 */
	public byte getType(){
		return this.type;
	}

	/**
 	 *set 选择作为合成材料的装备id
	 *@return
	 */
	public void setEquip_id(long equip_id){
		this.equip_id = equip_id;
	}

	/**
 	 *get 选择作为合成材料的装备id
	 *@return
	 */
	public long getEquip_id(){
		return this.equip_id;
	}

	/**
 	 *set 选择增加概率的材料列表
	 *@return
	 */
	public void setMaterials(List<ComposeAddInfo> materials){
		this.materials = materials;
	}

	/**
 	 *get 选择增加概率的材料列表
	 *@return
	 */
	public List<ComposeAddInfo> getMaterials(){
		return this.materials;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//将要合成的道具唯一ID
		writeInt(buf,this.compose_id);
		//是否绑定，默认0使用 1不使用
		writeByte(buf,this.type);
		//选择作为合成材料的装备id
		writeLong(buf,this.equip_id);
		//选择增加概率的材料列表
		writeShort(buf,materials.size());
		for(int i = 0;i < materials.size();i++){
			writeBean(buf,materials.get(i));
		}
		return true;
	}

	@Override
	public int getId() {
		return 103893;
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
		//将要合成的道具唯一ID
		this.compose_id = readInt(buf);
		//是否绑定，默认0使用 1不使用
		this.type = readByte(buf);
		//选择作为合成材料的装备id
		this.equip_id = readLong(buf);
		//选择增加概率的材料列表
		int materials_length = readShort(buf);
		for(int i = 0;i < materials_length;i++){
//			long id = readLong(buf);
//			materials.add(new ComposeAddInfo(id));
			materials.add((ComposeAddInfo)readBean(buf,ComposeAddInfo.class));
		}
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//将要合成的道具唯一ID
		buf.append("compose_id:"+compose_id+",");
		//是否绑定，默认0使用 1不使用
		buf.append("type:"+type+",");
		//选择作为合成材料的装备id
		buf.append("equip_id:"+equip_id+",");
		//选择增加概率的材料列表
		buf.append("materials:{");
		for(int i=0;i<materials.size();i++){
			buf.append(materials.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}