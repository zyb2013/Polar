package com.game.activities.script;

import org.apache.log4j.Logger;

import com.game.player.structs.Player;
import com.game.script.IScript;
/**
 * 活动接口
 * @author hongxiao.z
 * @date   2014-2-11  上午11:41:01
 */
public abstract class AbstractActivityScript implements IScript
{
	protected Logger log = Logger.getLogger(AbstractActivityScript.class);
	
	/*
	 * 活动是否对玩家开放
	 * true		开放
	 * false	未开放
	 */
	private volatile Boolean isOpen;
	
	/**
	 * 系统运行状态(用于系统监控)
	 * true    系统可监控
	 * false   系统不监控
	 */
	private volatile Boolean state;
	
	/**
	 * 对玩家开放活动 
	 * @create	hongxiao.z      2014-2-11 下午3:05:20
	 */
	protected void open()
	{
		this.isOpen = Boolean.TRUE;
	}
	
	/**
	 * 对玩家关闭活动
	 * @create	hongxiao.z      2014-2-11 下午3:06:04
	 */
	protected void close()
	{
		this.isOpen = Boolean.FALSE;
	}
	
	/**
	 * 系统初始化活动 
	 * @create	hongxiao.z      2014-2-11 下午3:10:13
	 */
	public final void init()
	{
		try
		{
			if(!initImpl())
			{
				return;
			}
			
			this.state = Boolean.TRUE;
		}
		catch(Exception e)
		{
			log.error(e, e);
		}
	}
	
	/**
	 * 初始化活动
	 * @create	hongxiao.z      2014-2-11 上午11:41:22
	 */
	protected abstract boolean initImpl();
	
	/**
	 * 停止系统监控 
	 * @create	hongxiao.z      2014-2-11 下午3:10:02
	 */
	public final void stop()
	{
		try
		{
			if(!stopImpl())
			{
				return;
			}
			
			this.state = Boolean.FALSE;
			log.warn("活动关闭...ID[" + getActivityId() + "]");
		}
		catch(Exception e)
		{
			log.error(e, e);
		}
	}
	
	/**
	 * 终止活动 
	 * @create	hongxiao.z      2014-2-11 上午11:41:35
	 */
	protected abstract boolean stopImpl();
	
	/**
	 * 活动监控 
	 * @create	hongxiao.z      2014-2-11 下午3:11:55
	 */
	public final void action()
	{
		if(!this.state)
		{
			return;
		}
		
		try
		{
			actionImpl();
		}
		catch(Exception e)
		{
			log.error(e, e);
		}
	}
	
	/**
	 * 活动运行监控
	 * @create	hongxiao.z      2014-2-11 上午11:41:49
	 */
	protected abstract void actionImpl();
	
	/**
	 * 角色的主动意向(由角色主动调用)
	 * @param player			角色实体
	 * @param intention			意向类型(自定义)
	 * @param objs				参数列表
	 * @create	hongxiao.z      2014-2-11 下午2:47:10
	 */
	public abstract void initiative(Player player, Object intention, Object... objs);
	
	/**
	 * 角色的被动意向(由系统自动调用)
	 * @param player			角色实体
	 * @param intention			意向类型(自定义)
	 * @param objs				参数列表
	 * @create	hongxiao.z      2014-2-11 下午2:47:10
	 */
	public abstract void passivity(Player player, Object intention, Object... objs);

	public Boolean isOpen() 
	{
		return isOpen;
	}
	protected AbstractActivityScript()
	
	{
		init();
	}
	
	public abstract int getActivityId();
	
	/**
	 * 获取主类别标识
	 * @return
	 * @create	hongxiao.z      2014-2-11 下午5:49:43
	 */
//	public abstract int getMainType();

	/**
	 * 获取类别中的标识
	 * @return
	 * @create	hongxiao.z      2014-2-11 下午5:49:59
	 */
//	public abstract int getType();

	// =====================活动调用意向=======================
	public static final int GAIN_REWARD 		= 1;	//领取活动奖励
	public static final int SEND_GRADE_GIFT_INFO = 2;	//等级礼包数量信息推送
}
