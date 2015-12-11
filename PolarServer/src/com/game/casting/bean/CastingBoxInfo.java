package com.game.casting.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Bean;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 铸造工厂奖励仓库信息
 */
public class CastingBoxInfo extends Bean {
	
	//铸造工厂奖励仓库格子列表
	private List<CastingGridInfo> castingGridInfoList = new ArrayList<CastingGridInfo>();
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//铸造工厂奖励仓库格子列表
		writeShort(buf, castingGridInfoList.size());
		for (int i = 0; i < castingGridInfoList.size(); i++) {
			writeBean(buf, castingGridInfoList.get(i));
		}
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//铸造工厂奖励仓库格子列表
		int castingGridList_length = readShort(buf);
		for (int i = 0; i < castingGridList_length; i++) {
			castingGridInfoList.add((CastingGridInfo)readBean(buf, CastingGridInfo.class));
		}
		return true;
	}
	
	/**
	 * get 铸造工厂奖励仓库格子列表
	 * @return
	 */
	public List<CastingGridInfo> getCastingGridInfoList() {
		return castingGridInfoList;
	}

	/**
	 * set 铸造工厂奖励仓库格子列表
	 */
	public void setCastingGridInfoList(List<CastingGridInfo> castingGridInfoList) {
		this.castingGridInfoList = castingGridInfoList;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//铸造工厂奖励仓库格子列表
		buf.append("castingGridInfoList:{");
		for (int i = 0; i < castingGridInfoList.size(); i++) {
			buf.append(castingGridInfoList.get(i).toString() +",");
		}
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}