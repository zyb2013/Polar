package com.game.equip.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 装备附加属性类
 */
public class EquipAttribute extends Bean {

	//附加属性类型
	private byte attributeType;
	
	//附加属性值
	private int attributeValue;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//附加属性类型
		writeByte(buf, this.attributeType);
		//附加属性值
		writeInt(buf, this.attributeValue);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//附加属性类型
		this.attributeType = readByte(buf);
		//附加属性值
		this.attributeValue = readInt(buf);
		return true;
	}
	

	/**
	 * @return the attributeType
	 */
	public byte getAttributeType() {
		return attributeType;
	}

	/**
	 * @param attributeType
	 *            the attributeType to set
	 */
	public void setAttributeType(byte attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return the attributeValue
	 */
	public int getAttributeValue() {
		return attributeValue;
	}

	/**
	 * @param attributeValue
	 *            the attributeValue to set
	 */
	public void setAttributeValue(int attributeValue) {
		this.attributeValue = attributeValue;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//附加属性类型
		buf.append("attributeType:" + attributeType +",");
		//附加属性值
		buf.append("attributeValue:" + attributeValue +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}