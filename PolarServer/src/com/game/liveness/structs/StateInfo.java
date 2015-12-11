package com.game.liveness.structs;

import com.game.object.GameObject;

/**
 * 宝箱领取状态信息
 * @author hongxiao.z
 * @date   2013-12-27  下午7:26:06
 */
public class StateInfo extends GameObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3093167128632620739L;

	//宝箱ID
	private short boxid;
	
	//0未领取，  1领取
	private short gain;

//	@Override
//	public boolean read(IoBuffer buf) 
//	{
//		this.boxid = readInt(buf);
//		this.gain   = readInt(buf);
//		return false;
//	}
//
//	@Override
	
	
//	public boolean write(IoBuffer buf) 
//	{
//		writeInt(buf, this.boxid);
//		writeInt(buf, this.gain);
//		return false;
//	}

	public short getBoxid() {
		return boxid;
	}

	public void setBoxid(short boxid) {
		this.boxid = boxid;
	}

	public short getGain() {
		return gain;
	}

	public void setGain(short gain) {
		this.gain = gain;
	}
}
