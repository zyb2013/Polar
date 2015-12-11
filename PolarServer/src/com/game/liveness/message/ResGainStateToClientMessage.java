package com.game.liveness.message;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.liveness.structs.StateInfo;
import com.game.message.Message;

/**
 * 请求宝箱领取状态返回
 * @author hongxiao.z
 * @date   2013-12-26  下午3:27:57
 */
public class ResGainStateToClientMessage extends Message
{
	/**
	 * 宝箱领取状态列表
	 */
	private List<StateInfo> gainStates;
	
	public ResGainStateToClientMessage(List<StateInfo> gainStates)
	{
		this.gainStates = gainStates;
	}
	
	

	public List<StateInfo> getGainStates() {
		return gainStates;
	}

	public void setGainStates(List<StateInfo> gainStates) {
		this.gainStates = gainStates;
	}

	@Override
	public int getId() 
	{
		return 600107;
	}

	@Override
	public String getQueue() 
	{
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	@Override
	public boolean read(IoBuffer arg0) 
	{
		int size = readShort(arg0);
		
		if(size == 0)
		{
			return true;
		}
		
		this.gainStates = new ArrayList<StateInfo>();
		
		for (int i = 0; i < size; i++) 
		{
			StateInfo state = new StateInfo();
			state.setBoxid(readShort(arg0));
			state.setGain(readShort(arg0));
			this.gainStates.add(state);
		}
		
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) 
	{
		//写入列表大小
		writeShort(buf, gainStates.size());
		
		for (StateInfo state : this.gainStates) 
		{
			writeShort(buf, state.getBoxid());
			writeShort(buf, state.getGain());
			//writeBean(buf, state);
		}
		
		return true;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}
