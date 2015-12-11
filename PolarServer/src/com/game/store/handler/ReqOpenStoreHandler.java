/**
 * 
 */
package com.game.store.handler;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.store.manager.StoreManager;
import com.game.store.message.ResOpenStoreMessage;
import com.game.utils.MessageUtil;

/**
 * @author luminghua
 * 
 * @date 2013年12月30日 上午9:47:05
 * 
 *       打开远程仓库扣钻石
 */
public class ReqOpenStoreHandler extends Handler {

	@Override
	public void action() {
		Player player = (Player) this.getParameter();
		boolean openStore = StoreManager.getInstance().openStore(player);
		ResOpenStoreMessage msg = new ResOpenStoreMessage();
		if (openStore)
			msg.succeed();
		MessageUtil.tell_player_message(player, msg);
	}

}
