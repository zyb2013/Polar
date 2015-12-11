package com.game.zones.message;

import com.game.zones.bean.BfStructsInfo;
import java.util.List;
import java.util.ArrayList;
import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 发送4v4副本战场战况列表消息
 */
public class ResBf4v4infoToClientMessage extends Message{

	//战场战况列表
	private List<BfStructsInfo> BfStructsInfolist = new ArrayList<BfStructsInfo>();
	//倒计时（秒）
	private int time;
	
	//阵营A当前占旗数量
	private int seizeflag_a;
	
	//阵营B当前占旗数量
	private int seizeflag_b;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//战场战况列表
		writeShort(buf, BfStructsInfolist.size());
		for (int i = 0; i < BfStructsInfolist.size(); i++) {
			writeBean(buf, BfStructsInfolist.get(i));
		}
		//倒计时（秒）
		writeInt(buf, this.time);
		//阵营A当前占旗数量
		writeInt(buf, this.seizeflag_a);
		//阵营B当前占旗数量
		writeInt(buf, this.seizeflag_b);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//战场战况列表
		int BfStructsInfolist_length = readShort(buf);
		for (int i = 0; i < BfStructsInfolist_length; i++) {
			BfStructsInfolist.add((BfStructsInfo)readBean(buf, BfStructsInfo.class));
		}
		//倒计时（秒）
		this.time = readInt(buf);
		//阵营A当前占旗数量
		this.seizeflag_a = readInt(buf);
		//阵营B当前占旗数量
		this.seizeflag_b = readInt(buf);
		return true;
	}
	
	/**
	 * get 战场战况列表
	 * @return 
	 */
	public List<BfStructsInfo> getBfStructsInfolist(){
		return BfStructsInfolist;
	}
	
	/**
	 * set 战场战况列表
	 */
	public void setBfStructsInfolist(List<BfStructsInfo> BfStructsInfolist){
		this.BfStructsInfolist = BfStructsInfolist;
	}
	
	/**
	 * get 倒计时（秒）
	 * @return 
	 */
	public int getTime(){
		return time;
	}
	
	/**
	 * set 倒计时（秒）
	 */
	public void setTime(int time){
		this.time = time;
	}
	
	/**
	 * get 阵营A当前占旗数量
	 * @return 
	 */
	public int getSeizeflag_a(){
		return seizeflag_a;
	}
	
	/**
	 * set 阵营A当前占旗数量
	 */
	public void setSeizeflag_a(int seizeflag_a){
		this.seizeflag_a = seizeflag_a;
	}
	
	/**
	 * get 阵营B当前占旗数量
	 * @return 
	 */
	public int getSeizeflag_b(){
		return seizeflag_b;
	}
	
	/**
	 * set 阵营B当前占旗数量
	 */
	public void setSeizeflag_b(int seizeflag_b){
		this.seizeflag_b = seizeflag_b;
	}
	
	
	@Override
	public int getId() {
		return 128121;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//战场战况列表
		buf.append("BfStructsInfolist:{");
		for (int i = 0; i < BfStructsInfolist.size(); i++) {
			buf.append(BfStructsInfolist.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//倒计时（秒）
		buf.append("time:" + time +",");
		//阵营A当前占旗数量
		buf.append("seizeflag_a:" + seizeflag_a +",");
		//阵营B当前占旗数量
		buf.append("seizeflag_b:" + seizeflag_b +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}