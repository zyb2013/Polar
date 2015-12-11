package com.game.zones.handler;

import org.apache.log4j.Logger;

import com.game.zones.message.ReqAddBuffMessage;
import com.game.zones.message.ReqClearZoneCDMessage;
import com.game.zones.message.ReqInventedZoneMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqInventedZoneHandler extends Handler {

	Logger log = Logger.getLogger(ReqInventedZoneHandler.class);

	public void action() {
		try {
			ReqInventedZoneMessage msg = (ReqInventedZoneMessage) this.getMessage();
			if (msg.getType() == 0) {
				ManagerPool.zonesManager.stReqInventedZoneInMessage((Player) this.getParameter(), msg);
			}
			if (msg.getType() == 1) {
				ManagerPool.zonesManager.stReqInventedZoneOutMessage((Player) this.getParameter(), msg);
			}
		} catch (ClassCastException e) {
			log.error(e);
		}
	}
}