package com.game.liveness.manager;



/**
 * 事件枚举 
 * @author hongxiao.z
 * @date   2013-12-27  上午9:53:28
 */
enum EventType 
{
	/**
	 * 微端登陆
	 */
	WDDL((short)1),
	
	/**
	 * 成为VIP
	 */
	CWVIP((short)2),
	
	/**
	 * 击杀精英怪物
	 */
	JSJYGW((short)3),
	
	/**
	 * 通关恶魔广场
	 */
	ZDEMGC((short)4),
	
	/**
	 * 通关血色城堡
	 */
	ZDTGXSCB((short)5),
	
	/**
	 * 击杀怪物
	 */
	JSGW((short)6),
	
	/**
	 * 完成日常任务
	 */
	WCRCRW((short)7),
	
	/**
	 * 铸造工厂
	 */
	ZZGC((short)8),
	
	/**
	 * 消费绑钻
	 */
	XFBZ((short)9),
	
	/**
	 * 钻石寻宝
	 */
	ZSXB((short)10),
	
	/**
	 * 祈愿金币
	 */
	JBCJ((short)11),
	
	/**
	 * 强化
	 */
	QH((short)12),
	
	/**
	 * 赤色要塞
	 */
	CSYX((short)13),
	
	/**
	 * 通关爬塔
	 */
	PT((short)14),
	;

	/**
	 * ID编号
	 */
	private short code;
	
	public short getCode()
	{
		return this.code;
	}
	
	private EventType(short code)
	{
		this.code = code;
	}
	
	public static EventType getType(short code)
	{
		for (EventType type : values()) 
		{
			if(type.getCode() == code)
			{
				return type;
			}
		}
		
		return null;
	}
}
