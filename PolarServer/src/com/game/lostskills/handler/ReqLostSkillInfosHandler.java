package com.game.lostskills.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.lostskills.manager.LostSkillManager;
import com.game.player.structs.Player;

/**
 * 请求技能列表信息
 * @author hongxiao.z
 * @date   2014-2-18  下午5:45:52
 */
public class ReqLostSkillInfosHandler extends Handler
{

	Logger log = Logger.getLogger(ReqLostSkillInfosHandler.class);

	public void action()
	{
		try
		{
			LostSkillManager.pushSkillInfos((Player)this.getParameter());
		}
		catch(ClassCastException e)
		{
			log.error(e,e);
		}
	}
}