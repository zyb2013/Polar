package com.game.login.handler;

import org.apache.log4j.Logger;

import com.game.login.message.ReqLoginForPulbicMessage;
import com.game.command.Handler;

public class ReqLoginForPulbicHandler extends Handler{

	Logger log = Logger.getLogger(ReqLoginForPulbicHandler.class);

	public void action(){
		try{
			ReqLoginForPulbicMessage msg = (ReqLoginForPulbicMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}