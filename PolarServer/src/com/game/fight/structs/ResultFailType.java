package com.game.fight.structs;

/**
 * 战斗失败结果枚举
 * @author panic
 *
 */
public class ResultFailType {
	//技能冷却中
	public static long cooldown = 1;
	//玩家已经死亡
	public static long isDie = 2;
	//定身或睡眠中 DINGSHEN SHUIMIAN
	public static long DINGSHENSHUIMIAN = 3;
	// 游泳中 SWIM
	public static long SWIM = 4;
	//技能没用找到 model
	public static long modelnull = 5;
	//攻击者（玩家）技能不对
	public static long defaultstudyno = 6;
	//攻击者（玩家）非主动技能
	public static long triggertypeno = 7;
	//是否人物技能
	public static long skilluserno = 8;
	//monster.isDie
	public static long monsterisDie= 9;
	//script.check
	public static long scriptcheckno = 10;
	//目标检查 （单体目标且对象不为自己时） 请先选定一个释放目标
	//Q_area_shape() == 1 && model.getQ_target()
	public static long 	shapetargetno= 11;	
	//目标不符，无法释放
	public static long targetno = 12;
	//CHANGEMAP
	public static long CHANGEMAP = 13;
	//mapno
	public static long mapno = 14;
	//本地图不能使用此技能
	public static long mapskillno = 15;
	//超出攻击距离
	public static long rangelimit = 16;
	//Mpno //消耗检查 //魔法值不足
	public static long Mpno = 17;
	//公共冷却检查
	public static long SKILLPUBLICNO = 18;
	//跳跃状态不能攻击 JUMP
	public static long JUMPNO = 19;
	//是否范围技能
	public static long areashapesingle = 20;
	//是否范围技能无行走路径
	public static long noroads = 21;
	//攻击者（玩家）变身技能不对
	public static long defaultshapechangeno = 22;
}
