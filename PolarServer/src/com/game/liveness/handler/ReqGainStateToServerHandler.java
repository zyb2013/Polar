package com.game.liveness.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

/**
 * 宝箱领取状态请求处理类
 * @author hongxiao.z
 * @date   2013-12-26  下午3:03:54
 */
public class ReqGainStateToServerHandler extends Handler
{

	Logger log = Logger.getLogger(ReqGainStateToServerHandler.class);

	public void action()
	{
		try
		{
			ManagerPool.livenessManager.gainBoxState((Player) this.getParameter());
		}
		catch(ClassCastException e)
		{
			log.error(e,e);
		}
	}
}