package com.game.chat.handler;

import org.apache.log4j.Logger;

import com.game.chat.message.RoleQueryMessage;
import com.game.chat.message.RoleQueryResultMessage;
import com.game.command.Handler;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.utils.MessageUtil;

public class RoleQueryHandler extends Handler{

	Logger log = Logger.getLogger(RoleQueryHandler.class);

	public void action(){
		try{
			RoleQueryMessage msg = (RoleQueryMessage)this.getMessage();
			RoleQueryResultMessage queryPlayer = PlayerManager.getInstance().queryChatPlayer((Player) getParameter(),msg.getRoleLikeName(),msg.getPage(),msg.getType());
			MessageUtil.tell_player_message((Player) getParameter(), queryPlayer);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}