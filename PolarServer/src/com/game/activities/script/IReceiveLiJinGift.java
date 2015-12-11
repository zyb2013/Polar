package com.game.activities.script;

import com.game.player.structs.Player;
import com.game.script.IScript;

/**
 * 定时领取绑钻
 * 废弃 hongxiao.z 2014.2.11
 */
@Deprecated 
public interface IReceiveLiJinGift extends IScript {
	
	public void receive(Player player);
	
}
