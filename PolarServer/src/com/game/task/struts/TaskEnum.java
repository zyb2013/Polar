package com.game.task.struts;

/**
 * 所有任务条件类型枚举,不一定只是军功，其他类型任务也会用到
 *
 * @author  
 * 
 * rootTaskEnum=8的小类型
 */
public class TaskEnum {

	/**
	 * //击杀怪物数量	1	击杀与玩家角色等级，相差5级范围的怪物，才能计数
	 */
	public static final int KILLNORMAL = 1;
	/**
	 * //击杀精英数量	2	击杀与玩家角色等级，相差10级范围的精英才能计数
	 */
	public static final int KILLELITE = 2;
	/**
	 * //击杀BOSS数量	3	击杀与玩家角色等级，相差10级范围的BOSS才能计数
	 */
	public static final int KILLBOSS = 3;
	/**
	 * //圣盟战中击杀非本盟玩家	4	仅在每周6才会出现任务，在战场中击杀玩家有效——若当前周6不开启圣盟争夺战，则隐藏任务
	 */
	public static final int KINGKILLNOSAMEGUILDPLAYER = 4;
	/**
	 * //神树浇水	5	给自己、战盟神树浇水均可完成任务，浇水数量根据配置
	 */
	public static final int SPIRITTREEWATERING = 5;
	/**
	 * //每日登陆	6	每日登陆后直接完成任务并获得奖励，每日1次
	 */
	public static final int EVERYDAYLOGIN = 6;
	/**
	 * //完成日常任务	7	完成当天的20环日常任务后方可领取，直接付费完成的也算
	 */
	public static final int COMPLETEDAYTASK = 7;
	/**
	 * //消费绑钻	8	消费绑钻达到多少，即可完成任务
	 */
	public static final int COSTBINDGOLD = 8;
	/**
	 * //消费钻石	9	消费钻石达到多少，即可完成任务
	 */
	public static final int COSTGOLD = 9;
	/**
	 * //通关经典战役	10	[10,副本id]
	 */
	public static final int COMPLETECLASSICBATTLE = 10;
	/**
	 * //使用道具类型 11 按照任务要求，使用某种物品成功，即可完成任务，格式：[11,物品id]
	 */
	public static final int USEITEM = 11;
	/**
	 * //点绛唇摇香唇	12	根据摇到的香唇数量来完成任务，逆天改运获得的香唇，也算
	 */
	public static final int INCENSELIP = 12;
	/**
	 * //击杀仇人	13	击杀与自己等级相差5级范围的仇人，可完成任务；同一名仇人只能计1次击杀
	 */
	public static final int KILLENEMY = 13;
	/**
	 * //完成讨伐任务	14	每日完成讨伐任务的数量达到多少，即可完成任务；吞噬的讨伐任务只算1个；
	 */
	public static final int COMPLETECONQUERTASK = 14;
	/**
	 * //打掉盟旗	15
	 * 在盟旗争夺战的当天，出现该任务；完成方式同击杀BOSS的判断：打掉20%的血或最后1刀，若盟旗死了算完成，盟旗不死就不能完成；
	 */
	public static final int KILLGUILDBANNER = 15;
	/**
	 * //获得物品	16	指定通过打怪、购买获得某种物品，并达到一定数量，即可完成任务，格式：[11,物品id，物品数量]
	 */
	public static final int GETITEM = 16;
	/**
	 * //地宫寻宝任务完成	17	在地宫寻宝中，触发任务并完成，完成的数量根据配置
	 */
	public static final int COMPLETETREASUREhUNTTASK = 17;
	/**
	 * //摘取神树果子	18	摘取其他玩家的神树果子，根据摘取的数量来完成任务
	 */
	public static final int HARVESTINGSPIRITTREEFRUIT = 18;
	/**
	 * //采集类	19	按照任务要求，在某张地图采集某种物品，即可完成任务
	 */
	public static final int COMPLETEGATHER = 19;
	/**
	 * //组队完成副本X次	20	在组队情况下，通关多人副本即可完成任务
	 */
	public static final int COMPLETETEAMZONE = 20;
	/**
	 * //获得真气xx	21	通过各种方式获得真气数量达到多少，即可完成
	 */
	public static final int GETZHENQI = 21;
	/**
	 * 任务完成条件检测强化序列（物品id_强化等级;物品id_强化等级;）
	 */
	public static final int QIANGHUA = 22;
	
	/**
	 * 坐骑升阶
	 */
	public static final int HORSE_STAGE_UP = 23;
	
	/**
	 * 道具合成
	 */
	public static final int ITEM_COMPOSE = 24;
	/**
	 * 坐骑骑乘
	 */
	public static final int HORSE_RIDE_UP = 25;
	
	/**
	 * 通关s级评价副本 [26,副本id]
	 * 
	 */
	public static final int S_ZONE = 26;
	
	/**
	 * 杀死任意几只boss怪，[27,数量]
	 */
	public static final int KILL_BOSS = 27;
	
	/**
	 * 进入副本 [29,副本id]
	 */
	public static final int ENTER_MISSION = 29;
	
	/**
	 * 穿戴装备，配置q_end_need_qianghua或q_end_need_wear字段
	 */
	public static final int WEAR_EQUIP = 30;
	
	/**
	 * 首充 [31,1]
	 */
	public static final int FIRST_PAY = 31;
	
	/**
	 * 通关ss级评价副本 [32,副本id]
	 * 
	 */
	public static final int SS_ZONE = 32;
	
	/**
	 * 通关sss级评价副本 [33,副本id]
	 * 
	 */
	public static final int SSS_ZONE = 33;
	
	/**
	 * 杀死任意几只精英怪，[34,数量]
	 */
	public static final int KILL_ELITE = 34;
	
	/**
	 * 脱装备
	 */
	public static final int UNWEAR_EQUIP = 35;
}
