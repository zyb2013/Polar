package com.game.drop.script;

import com.game.drop.structs.MapDropInfo;
import com.game.script.IScript;

/**
 * 掉落物品消失事件
 * @author  
 */
public interface IDropClearScript extends IScript{
	
	public boolean dropClear(MapDropInfo mapDropInfo);
}
