package com.game.newactivity.message;

import java.util.List;

/**
* @author luminghua,by python
*
* @date 2014-02-25 10:39
*
*/
public class ResGetNewActivityAward extends Message{


	@Override
	public int getId() {
		return 511004;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}

	private byte result;
	public byte getResult(){
		return result;
	}
	public void setResult(byte result){
		this.result=result;
	}

	@Override
	public boolean read(IoBuffer buff) {
		result = readByte(buff);
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeByte(buff,result);
	}
}