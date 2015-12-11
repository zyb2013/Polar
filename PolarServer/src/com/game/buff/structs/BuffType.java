package com.game.buff.structs;

/**
 * buff类型
 * @author heyang
 *
 */
public class BuffType {

	/**
	 * 1	增加等级
	 */
	public final static int LEVEL = 1;
	/**
	 * 2	增加人物经验
	 */
	public final static int EXP = 2;
	/**
	 * 3	增加人物真气
	 */
	public final static int ZHENQI = 3;
	/**
	 * 4	增加战场声望
	 */
	public final static int BATTLEEXP = 4;
	/**
	 * 5	增加或减少当前生命值
	 */
	public final static int HP = 5;
	/**
	 * 6	增加或减少当前魔法值值
	 */
	public final static int MP = 6;
	/**
	 * 7	增加或减少当前体力值
	 */
	public final static int SP = 7;
	/**
	 * 8	增加或减少生命值上限
	 */
	public final static int MAXHP = 8;
	/**
	 * 9	增加或减少魔法值值上限
	 */
	public final static int MAXMP = 9;
	/**
	 * 10	增加或减少体力值上限
	 */
	public final static int MAXSP = 10;
	/**
	 * 11	增加或减少攻击力
	 */
	public final static int ATTACK = 11;
	/**
	 * 12	增加或减少防御力
	 */
	public final static int DEFENSE = 12;
	/**
	 * 13	增加或减少闪避值
	 */
	public final static int DODGE = 13;
	/**
	 * 14	增加或减少爆击值
	 */
	public final static int CRIT = 14;
	/**
	 * 15	增加或减少幸运值
	 */
	public final static int LUCK = 15;
	/**
	 * 16	增加或减少攻击速度
	 */
	public final static int ATTACKSPEED = 16;
	/**
	 * 17	增加或减少移动速度
	 */
	public final static int SPEED = 17;
	/**
	 * 18	每隔一段时间加满人物的生命，直至剩余容量用完
	 */
	public final static int FILLHP = 18;
	/**
	 * 19	每隔一段时间加满人物的魔法值，直至剩余容量用完
	 */
	public final static int FILLMP = 19;
	/**
	 * 20	每隔一段时间加满人物的体力，直至剩余容量用完
	 */
	public final static int FILLSP = 20;
	/**
	 * 21	打怪获得多倍经验，持续一段时间后消失
	 */
	public final static int MULEXP = 21;
	/**
	 * 22	打怪获得多倍打坐真气，持续一段时间后消失
	 */
	public final static int MULZHENQI = 22;
	/**
	 * 23	在一段时间内享受禁止被PK的保护，持续一段时间后或当玩家主动PK其他玩家时消失
	 */
	public final static int PKPROTECT = 23;
	/**
	 * 24	有一定成功几率将目标定身（不能移动，不能跳跃，不能格挡，不能使用技能，允许吃药），持续一段时间后恢复
	 */
	public final static int STAKME = 24;
	/**
	 * 25	有一定成功几率将目标所有装备附加的攻击力降低一部分比例甚至降为负值，持续一段时间后恢复
	 */
	public final static int EQUIPATTACK = 25;
	/**
	 * 26	有一定成功几率将目标所有装备附加的防御力降低一部分比例甚至降为负值，持续一段时间后恢复
	 */
	public final static int EQUIPDEFENSE = 26;
	/**
	 * 27	有一定成功几率将目标的魔法值值清零，且禁止目标吃魔法值恢复类药品（使用药品时返回消息将禁止使用的消息提示），持续一段时间后恢复
	 */
	public final static int MPEMPTY = 27;
	/**
	 * 28	有一定成功几率将目标的体力值清零，且禁止目标吃体力恢复类药品（使用药品时返回消息将禁止使用的消息提示），持续一段时间后恢复
	 */
	public final static int SPEMPTY = 28;
	/**
	 * 29	有一定成功几率将目标的攻击速度降至其原有攻击速度的一定比例（允许比负100%还低），持续一段时间后恢复
	 */
	public final static int ATTACKSPEEDRECUDE = 29;
	/**
	 * 30	有一定成功几率将目标的移动速度降至其原有移动速度的一定比例（允许比负100%还低），持续一段时间后恢复
	 */
	public final static int SPEEDRECUDE = 30;
	/**
	 * 31	有一定成功几率将目标的闪避值清零，持续一段时间后恢复
	 */
	public final static int DODGEEMPTY = 31;
	/**
	 * 32	有一定成功几率将目标的暴击值清零，持续一段时间后恢复
	 */
	public final static int CRITEMPTY = 32;
	/**
	 * 33	有一定成功几率令目标的方向控制键倒置（上变下，下变上，左变右，右变左），持续一段时间后恢复
	 */
	public final static int BABELISM = 33;
	/**
	 * 34	有一定成功几率将目标从坐骑上击落，并且在一段时间内禁止骑乘，持续一段时间后恢复
	 */
	public final static int FORBIDRIDE = 34;
	/**
	 * 35	有一定成功几率将目标沉睡（不能移动，不能跳跃，不能格挡，不能使用技能，允许吃药）持续一段时间或者被攻击一次后效果消失
	 */
	public final static int SLEEP = 35;
	/**
	 * 36	有一定成功几率对目标施毒，被施毒后在一定时间内每隔一段时间均按比例减血
	 */
	public final static int POISON = 36;
	/**
	 * 37	在一定时间内按比例提高主角的攻击力，效果与丹药相加
	 */
	public final static int ATTACKPERCENT = 37;
	/**
	 * 38	在一定时间内按比例提高主角的防御力，效果与丹药相加
	 */
	public final static int DEFENSEPERCENT = 38;
	/**
	 * 39	在一定时间内按比例提高主角的攻击速度，效果与丹药相加
	 */
	public final static int ATTACKSPEEDPERCENT = 39;
	/**
	 * 40	在一定时间内按比例提高主角的移动速度，效果与丹药相加
	 */
	public final static int SPEEDPERCENT = 40;
	/**
	 * 41	在一定时间内主角跳跃、二次跳跃不减少体力值
	 */
	public final static int NOCOSTFORJUMP = 41;
	/**
	 * 42	在一定时间内主角格挡不减少魔法值值
	 */
	public final static int NOCOSTFORBLOCK = 42;
	/**
	 * 43	在一定时间内使用格挡，每当主角格挡消耗真气时，均按主角生命上限的一定比例恢复主角当前生命值
	 */
	public final static int RECOVERFORBLOCK = 43;
	/**
	 * 44	在一定时间内使用跳跃，每当主角跳跃时，均按主角生命上限的一定比例恢复主角当前生命值
	 */
	public final static int RECOVERFORJUMP = 44;
	/**
	 * 45	在一定时间内无敌，主角免疫所有伤害，持续一段时间后消失
	 */
	public final static int INVINCIBLE = 45;
	/**
	 * 46	在一定时间内若主角死亡则免费原地健康复活一次（不弹出复活提示面板，直接播放复活倒计时），持续一段时间或者死亡复活一次后效果消失
	 */
	public final static int FREERELIVE = 46;
	/**
	 * 47	在一定时间内若主角攻击则必定产生多倍暴击，持续一段时间或者使用三次后效果消失
	 */
	public final static int MULCRIT = 47;
	/**
	 * 48	在一定时间内每次主角发出攻击，若命中，均按其自身魔法值上限的一定比例恢复自身当前魔法值值，按同样比例减少目标魔法值值（吸蓝）
	 */
	public final static int RECOVERMPONATTACK = 48;	
	/**
	 * 49	在一定时间内每次主角发出攻击，若命中，均按其自身体力上限的一定比例恢复自身当前体力值，按同样比例减少目标体力值（吸体）
	 */
	public final static int RECOVERSPONATTACK = 49;	
	/**
	 * 50	在一定时间内每次主角发出攻击，若命中，均按其自身生命上限的一定比例恢复自身当前生命值（吸血）
	 */
	public final static int RECOVERHPONATTACK = 50;
	/**
	 * 51	在一定时间内令目标每次在被攻击时，若被命中，均至少按照其生命上限的一定比例减少其当前生命值（魅惑）
	 */
	public final static int REDUCEHPONBEATTACK = 51;
	/**
	 * 52	有一定成功几率将目标的攻击力清零，持续一段时间后恢复
	 */
	public final static int ATTACKEMPTY = 52;
	/**
	 * 53	有一定成功几率当主角受到负面状态时，予以立刻解除
	 */
	public final static int RELIEFDEBUFF = 53;
	/**
	 * 54	有一定成功几率将目标的防御力清零，持续一段时间后恢复
	 */
	public final static int DEFENSEEMPTY = 54;
	/**
	 * 55	有一定成功几率令目标的主界面中央出现黑影（无法看清自己以及自己周边环境），持续一段时间后恢复
	 */
	public final static int BLIND = 55;
	/**
	 * 56	有一定成功几率令目标每次被攻击命中后（攻击可来自任何人），所受到的伤害均按比例额外加深，持续一段时间后恢复
	 */
	public final static int DEEPENDAMAGE = 56;
	/**
	 * 57	有一定成功几率令目标生命上限下降持续一段时间
	 */
	public final static int REDUCEMAXHP = 57;
	/**
	 * 58	有一定成功几率令目标魔法值上限下降持续一段时间
	 */
	public final static int REDUCEMAXMP = 58;
	/**
	 * 59	有一定成功几率令目标体力上限下降持续一段时间
	 */
	public final static int REDUCEMAXSP = 59;
	/**
	 * 60	有一定成功几率令目标无法使用任何药品，持续一段时间
	 */
	public final static int FROBIDDRUG = 60;
	/**
	 * 61	令魔法值盾新增功能，反弹墨子剑法前三式的无视防御伤害
	 */
	public final static int REBOUND = 61;
	/**
	 * 62	令魔法值盾新增功能，每隔2秒清除一个自己所受的负面BUFF（按所受时间倒序清除）
	 */
	public final static int CLEARDEBUFF = 62;
	/**
	 * 63	直接伤害
	 */
	public final static int DAMAGE = 63;
	/**
	 * 64	减少伤害
	 */
	public final static int REDUCE = 64;
	/**
	 * 66	夜晚挂机保护PK, 当玩家主动PK其他玩家时消失
	 */
	public final static int PROTECTFORNIGHT = 66;
	/**
	 * 67	在一定时间内按比例提高主角的暴击值，效果可与丹药相加
	 */
	public final static int CRITPERCENT = 67;
	/**
	 * 68	在一定时间内按比例提高主角的闪避值，效果可与丹药相加
	 */
	public final static int DODGEPERCENT = 68;
	/**
	 * 69	在一定时间内对所有主角技能等级增加
	 */
	public final static int SKILL = 69;
	/**
	 * 70	令魔法值盾新增功能，每次消耗魔法值时，恢复对应的血量
	 */
	public final static int RECOVERSAMEFORBLOCK = 70;
	/**
	 * 71	属性加成BUFF
	 */
	public final static int POWERUP = 71;
	
	/**
	 * 72       打坐BUFF
	 */
	public final static int SITINMEDITATION=72;
	
//	/**
//	 * 与宠物双修
//	 */
//	public final static int PETSITINMEDITATION=73;
//	
//	/**
//	 * 与玩家双修
//	 */
//	public final static int ROLESITINMEDITATION=74;
	
	/**
	 * 75       连斩BUFF
	 */
	public final static int KILLBUFF=75;
	
	/**
	 * 76       连击BUFF
	 */
	public final static int HITBUFF=76;
	
	/**
	 * 77      显示BUFF
	 */
	public final static int SHOWBUFF=77;
	
	/**
	 * 78       霸权BUFF
	 */
	public final static int BAQUANBUFF=78;
	
	/**
	 * 79      仁德BUFF
	 */
	public final static int RENDEBUFF=79;
	
	/**
	 * 80 状态BUFF 仅用于标识状态 
	 */
	public final static int STATEBUFF=80;
	
	/**
	 * 81 脚本BUFF
	 */
	public final static int SCRIPTBUFF=81;
	
	/**
	 * 82 勾魂BUFF
	 */
	public final static int GOUHUNBUFF=82;
	
	/**
	 * 83 梅花BUFF
	 */
	public final static int MEIHUABUFF=83;
	
	/**
	 * 84 玲珑BUFF
	 */
	public final static int LINGLONGBUFF=84;
	
	/**
	 * 85 遮影BUFF
	 */
	public final static int ZHEYINGBUFF=85;
	
	/**
	 * 86 强行PK BUFF
	 */
	public final static int FORCEPKBUFF=86;
	
	/**
	 * 87 紫芒BUFF
	 */
	public final static int ZIMANGBUFF=87;
	
	/**
	 * 88 跳跃减血
	 */
	public final static int DAMAGEFORJUMP=88;
	
	/**
	 * 89 boss buff（玩家减血,死亡）
	 */
	public final static int DAMAGEANDDIE=89;
	
	/**
	 * 90 boss虚弱
	 */
	public final static int BOSSWEAK=90;
	
	/**
	 * 91 骑战兵器缴械
	 */
	public final static int HORSEWEAPONUNWEAR=91;
	
	/**
	 * 92 防骑战兵器缴械
	 */
	public final static int HORSEWEAPONPROTECT=92;
	
	/**
	 * 93 商城购买指定道具时,降低价格(指定数量的钻石),可叠加
	 */
	public final static int MALLBUYREDUCE=93;
	
	
	//panic add 
	/**
	 * 增加或减少命中值
	 */
	public final static int HIT = 100;
	/**
	 * 在一定时间内按比例提高主角的命中值，效果可与丹药相加
	 */
	public final static int HITPERCENT = 101;
	
	/**
	 *  生命之光 light of life
	 * 技能参数：
职业：剑士
作用中心：自身
判定范围：圆形
作用对象：自己、队友、队友的召唤物
指向类型：固定自身（客户端）

技能描述：施放后对范围内的所有队员及队员召唤物添加一个增加最大生命上限的buff
需新增buff效果类型，效果为百分比增加最大生命上限，效果值与施放者的智力属性有关系
暂定公式为， 增加效果= (10 + 智力/5 )%
	 */
//	public final static int LIGHTOFLIFE = 102;
	
	/*
	 * 103 守护之魂
技能参数：
职业：法师
作用中心：目标
判定范围：单体
作用对象：自己、队友
指向类型：锁定目标（客户端）

技能描述：施放后对目标添加一个可以吸收有限伤害的buff
需新增buff效果类型，效果为减免伤害值，效果值与施放者的敏捷属性有关系
暂定公式为， 增加效果= (10 + 敏捷/7 )%

施放技能时，若未选中目标或者选中目标不符合，则技能对主角自己施放，若目标符合则对目标施放
	 */
	public final static int GUARDIANSPIRIT = 103;
	
	/*
	 * 104战神之力
技能参数：
职业：弓箭手
作用中心：目标
判定范围：单体
作用对象：自己和目标
指向类型：锁定目标（客户端）

技能描述：施放后对目标添加一个增加攻击力的buff
需新增buff效果类型，效果为增加攻击力（固定值），效果值与施放者的之力属性有关系
暂定公式为， 增加效果= 20+智力/7

施放技能时，若未选中目标或者选中目标不符合，则技能对主角自己施放，若目标符合则对目标施放
	 */
	//public final static int BATTLEFORCE = 104;
	
	/*
	 * 105守护之光
技能参数：
职业：弓箭手
作用中心：目标
判定范围：单体
作用对象：自己、队友
指向类型：锁定目标（客户端）

技能描述：施放后对目标添加一个增加防御力的buff
需新增buff效果类型，效果为增加防御力（固定值），效果值与施放者的之力属性有关系
暂定公式为， 增加效果= 20+智力/5

施放技能时，若未选中目标或者选中目标不符合，则技能对主角自己施放，若目标符合则对目标施放
	 */
	//public final static int GUARDIANOFLIGHT = 105;
	
	/*
	 * 106治愈之光
技能参数：
职业：弓箭手
作用中心：目标
判定范围：单体
作用对象：自己、队友
指向类型：锁定目标（客户端）

技能描述：施放后对目标添加一个增加生命值的buff
需新增buff效果类型，效果为增加生命值（固定值），效果值与施放者的之力属性有关系
暂定公式为， 增加效果= 20+智力/10

施放技能时，若未选中目标或者选中目标不符合，则技能对主角自己施放，若目标符合则对目标施放
	 */
	//public final static int HEALINGLIGHT = 106;
/*
 * 107风行者
技能参数：
职业：弓箭手
作用中心：自身
判定范围：单体
作用对象：自己
指向类型：固定自身（客户端）

技能描述：施放后对自己添加一个增加移动速度、闪避率100%的buff
需新增buff效果类型，效果为增加移动速度同时增加闪避率100%，buff表中效果值控制的是移动速度的数值
 */
	//public final static int WINDRUNNER = 107;

	/**
	 * 定时恢复不同比例的血量，如getQ_effect_ratio配置631，则第一次增加血量上限(q_effect_maxvalue比例)60%，第二次30%，第三次10%
	 */
	public final static int SECTION_ADD_HP = 108;

	public final static int SECTION_ADD_MP = 109;
	
	/**
	 * 加固定经验Buff
	 */
	public final static int FIXED_EXP = 110;
	
	/**
	 * 解毒Buff，并在一定时间内不会中毒
	 */
	public final static int UNPOISON = 111;
	
	/**
	 * 定时恢复不同比例的血量，直至血池用完
	**/
	public final static int ADD_HP_BY_TIME = 112;
	
	/**
	 * 定时恢复不同比例的血量，直至魔池用完
	**/
	public final static int ADD_MP_BY_TIME = 113;
	
	/**
	 * 增加固定的力量
	**/
	public final static int FIXED_STRENGTH = 114;
	
	/**
	 * 增加固定的敏捷
	 */
	public final static int FIXED_AGIL = 115;
	
	/**
	 * 增加固定的体力
	 */
	public final static int FIXED_VITALITY = 116;
	
	/**
	 * 增加固定的智力
	 */
	public final static int FIXED_INTELLIGENCE = 117;

	/**
	 * 增加卓越一击触发概率
	 */
	public final static int PERFECT_ATTACK_PERCENT = 118;

	/**
	 * 增加会心一击触发概率
	 */
	public final static int KNOWING_ATTACK_PERCENT = 119;

	/**
	 * 增加无视一击触发概率
	 */
	public final static int IGNORE_ATTACK_PERCENT = 120;

	/**
	 * 增加卓越一击伤害加成
	 */
	public final static int PERFECT_ATTACK_ADDITION = 121;

	/**
	 * 增加会心一击伤害加成
	 */
	public final static int KNOWING_ATTACK_ADDITION = 122;
	
	/**
	 * 放特效使用，无实质用处
	 */
	public final static int SPECIAL_EFFECT = 123;
	
	/**
	 * 变身类型
	 */
	public final static int SHAPE_CHANGE = 124;
	/**
	 * 自爆buff
	 */
	public final static int ZIBAO = 125;
	
	/**
	 * 增加HP上限 比例 计算公式：0.1 + 智力 * 0.0005
	 */
	public final static int MAXHP_PLUS = 126;
	
	/**
	 * 增加吸收伤害比例 计算公式：0.05 + 敏捷 * 0.0001
	 */
	public final static int GUARDIANSPIRIT_PLUS = 127;
	
	/**
	 * 攻击力增加 计算公式: 10 + 智力 * 0.3
	 */
	public final static int ATTACK_PLUS = 128;
	
	/**
	 * 防御力增加 计算公式：10 + 智力 * 0.032
	 */
	public final static int DEFENSE_PLUS = 129;
	
	/**
	 * HP增加 计算公式：0.2 + 智力 * 0.0001
	 */
	public final static int HP_PERCENT = 130;
	
	/**
	 * 全属性增加
	 */
	public final static int ALL_ATTONE = 132;
}
