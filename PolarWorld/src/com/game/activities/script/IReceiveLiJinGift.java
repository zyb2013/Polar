package com.game.activities.script;

import com.game.player.structs.Player;
import com.game.script.IScript;

/**
 * 定时领取绑钻
 * @author  
 * @2012-9-12 下午8:23:33
 */
public interface IReceiveLiJinGift extends IScript {
	
	public void receive(Player player);
	
}
