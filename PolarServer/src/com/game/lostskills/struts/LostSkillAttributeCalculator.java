package com.game.lostskills.struts;

import com.game.lostskills.manager.LostSkillManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.player.structs.PlayerAttributeCalculator;
import com.game.player.structs.PlayerAttributeType;

public class LostSkillAttributeCalculator implements PlayerAttributeCalculator
{
	@Override
	public int getType() 
	{
		return PlayerAttributeType.LOST_SKILL;
	}

	@Override
	public PlayerAttribute getPlayerAttribute(Player player) 
	{
		return LostSkillManager.getAttrs(player);
	}
}
