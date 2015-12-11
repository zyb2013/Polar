package com.game.backpack.structs;

import org.apache.log4j.Logger;

import com.game.player.structs.Player;

public class Medicines extends Item implements IAutoUseItem {
	/**
	 * Logger for this class
	 */
	protected static final Logger logger = Logger.getLogger(Medicines.class);

	private static final long serialVersionUID = 9167941278406610539L;

	/* 
	 * @see com.game.backpack.structs.IAutoUseItem#autoTakeUpUse(com.game.player.structs.Player, java.lang.String[])
	 */
	@Override
	public boolean autoTakeUpUse(Player player, String... parameters) {
		// TODO Auto-generated constructor stub
		return false;
	}
	
}
