package com.game.login.handler;

import org.apache.log4j.Logger;

import com.game.login.message.ReqCreateCharacterMessage;
import com.game.manager.ManagerPool;
import com.game.command.Handler;

public class ReqCreateCharacterHandler extends Handler{

	Logger log = Logger.getLogger(ReqCreateCharacterHandler.class);

	public void action(){
		try{
			ReqCreateCharacterMessage msg = (ReqCreateCharacterMessage)this.getMessage();
			//创建角色
			//ManagerPool.playerManager.createCharacter(msg.getSession(), msg.getName(), msg.getIcon(), msg.getSex(), msg.getJob(), (byte)0, msg.getAuto());
			//没有性别，强制为0
			ManagerPool.playerManager.createCharacter(msg.getSession(), msg.getName(), msg.getIcon(), (byte) 0, msg.getJob(), (byte)0, msg.getAuto());
		}catch(ClassCastException e){
			log.error(e, e);
		}
	}
}