package com.game.country.message;

import com.game.toplist.bean.TopInfo;

import java.util.List;
import java.util.ArrayList;

import com.game.guild.bean.GuildInfo;
import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 发送给客户端获取排行榜信息消息
 */
public class ResGetTopGuildListToGameMessage extends Message{

	//前5排行榜信息列表
	private List<GuildInfo> top5infolist = new ArrayList<GuildInfo>();
	
	public List<GuildInfo> getTop5infolist() {
		return top5infolist;
	}

	public void setTop5infolist(List<GuildInfo> top5infolist) {
		this.top5infolist = top5infolist;
	}

	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//前5排行榜信息列表
		writeShort(buf, top5infolist.size());
		for (int i = 0; i < top5infolist.size(); i++) {
			writeBean(buf, top5infolist.get(i));
		}
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//前5排行榜信息列表
		int top5infolist_length = readShort(buf);
		for (int i = 0; i < top5infolist_length; i++) {
			top5infolist.add((GuildInfo)readBean(buf, GuildInfo.class));
		}
		return true;
	}
	
	
	@Override
	public int getId() {
		return 542331;
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
		//前5排行榜信息列表
		buf.append("top5infolist:{");
		for (int i = 0; i < top5infolist.size(); i++) {
			buf.append(top5infolist.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}