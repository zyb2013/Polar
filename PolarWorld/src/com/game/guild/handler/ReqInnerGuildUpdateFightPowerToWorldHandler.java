package com.game.guild.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.guild.manager.GuildWorldManager;
import com.game.guild.message.ReqInnerGuildUpdateFightPowerToWorldMessage;

public class ReqInnerGuildUpdateFightPowerToWorldHandler extends Handler{

	Logger log = Logger.getLogger(ReqInnerGuildUpdateFightPowerToWorldHandler.class);

	public void action(){
		try{
			ReqInnerGuildUpdateFightPowerToWorldMessage msg = (ReqInnerGuildUpdateFightPowerToWorldMessage)this.getMessage();
			GuildWorldManager.getInstance().reqInnerGuildUpdateFightPower(msg);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}