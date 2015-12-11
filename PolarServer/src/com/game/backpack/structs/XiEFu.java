package com.game.backpack.structs;

import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.message.ResUseItemSuccessMessage;
import com.game.config.Config;
import com.game.languageres.manager.ResManager;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;

public class XiEFu extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = -247270966454058508L;
	
	private Logger logger = Logger.getLogger(XiEFu.class);

	@Override
	public void use(Player player, String... parameters) {
		if (player.getPkValue() > 0){
			player.setPkValue(player.getPkValue() - 1);
			PlayerManager.getInstance().onPkValueChange(player);
			
			if(!BackpackManager.getInstance().removeItem(player, this, 1, Reasons.GOODUSE, Config.getId())) {
				logger.error("扣除物品失败："+player.getId()+","+this.getItemModelId()+","+1);
			}
			
			//! xuliang 物品使用成功
			ResUseItemSuccessMessage successMsg = new ResUseItemSuccessMessage();
			successMsg.setItemId(this.getItemModelId());
			MessageUtil.tell_player_message(player, successMsg);
		}
	}

	@Override
	public void unuse(Player player, String... parameters) {
	}

}
