package com.game.backpack.structs;

//import com.game.languageres.manager.ResManager;
import com.game.player.structs.Player;
//import com.game.prompt.structs.Notifys;
//import com.game.utils.MessageUtil;

/**
 * 普通物品
 * @author  
 *
 */
public class horse extends Item {

	private static final long serialVersionUID = -2579158237907732200L;

	@Override
	public void use(Player player, String... parameters) {
		/*xiaozhuoming: 暂时没有用到
		MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString(""));
		*/
	}

	@Override
	public void unuse(Player player, String... parameters) {
		
	}
}
