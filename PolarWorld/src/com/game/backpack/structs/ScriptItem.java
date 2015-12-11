package com.game.backpack.structs;

import com.game.player.structs.Player;

public class ScriptItem extends Item implements IAutoUseItem {

	/* 
	 * @see com.game.backpack.structs.IAutoUseItem#autoTakeUpUse(com.game.player.structs.Player, java.lang.String[])
	 */
	@Override
	public boolean autoTakeUpUse(Player player, String... parameters) {
		// TODO Auto-generated constructor stub
		return false;
	}

}