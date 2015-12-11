package com.game.player.script;

import com.game.player.structs.Player;
import com.game.script.IScript;

public interface IPlayerLevelUPScript extends IScript {

	public void onLevelUP(Player player );
}
