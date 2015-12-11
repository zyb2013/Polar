package com.game.csys.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import java.util.List;
import java.util.ArrayList;
import com.game.csys.bean.CsysTopInfo;
/**
  * 返回排行榜信息 
 */
public class ResCsysTopListMessage extends Message{

	//排行榜信息
	private List<CsysTopInfo> infos = new ArrayList<CsysTopInfo>();
	/**
 	 *set 排行榜信息
	 *@return
	 */
	public void setInfos(List<CsysTopInfo> infos){
		this.infos = infos;
	}

	/**
 	 *get 排行榜信息
	 *@return
	 */
	public List<CsysTopInfo> getInfos(){
		return this.infos;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//排行榜信息
		writeShort(buf,infos.size());
		for(int i = 0;i < infos.size();i++){
			writeBean(buf,infos.get(i));
		}
		return true;
	}

	@Override
	public int getId() {
		return 550101;
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
		//排行榜信息
		int infos_length = readShort(buf);
		for(int i = 0;i < infos_length;i++){
			infos.add((CsysTopInfo)readBean(buf,CsysTopInfo.class));
		}
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//排行榜信息
		buf.append("infos:{");
		for(int i=0;i<infos.size();i++){
			buf.append(infos.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}