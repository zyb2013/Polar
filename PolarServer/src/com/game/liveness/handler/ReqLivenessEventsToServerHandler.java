package com.game.liveness.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

/**
 * 活跃事件列表请求处理类
 * @author hongxiao.z
 * @date   2013-12-26  下午3:03:54
 */
public class ReqLivenessEventsToServerHandler extends Handler
{

	Logger log = Logger.getLogger(ReqLivenessEventsToServerHandler.class);

	public void action()
	{
		try
		{
			ManagerPool.livenessManager.gainEvents((Player) this.getParameter());
		}
		catch(ClassCastException e)
		{
			log.error(e,e);
		}
	}
}