package com.game.lostskills.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.lostskills.manager.LostSkillManager;
import com.game.lostskills.message.ReqActivateLostSkillMessage;
import com.game.player.structs.Player;

/**
 * 激活技能信息
 * @author hongxiao.z
 * @date   2013-12-26  下午3:03:54
 */
public class ReqActivateLostSkillHandler extends Handler
{

	Logger log = Logger.getLogger(ReqActivateLostSkillHandler.class);

	public void action()
	{
		try
		{
			ReqActivateLostSkillMessage msg = (ReqActivateLostSkillMessage)this.getMessage();
			LostSkillManager.updateSkill((Player) this.getParameter(), msg.getSkillType());
		}
		catch(ClassCastException e)
		{
			log.error(e,e);
		}
	}
}