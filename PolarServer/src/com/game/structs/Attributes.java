package com.game.structs;

import java.util.ArrayList;
import java.util.List;

import com.game.backpack.structs.Attribute;

/**
 * 附加属性枚举
 * 
 * @author heyang
 * 
 */

public enum Attributes {

	/**
	 * 1 等级
	 */
	LEVEL(1),
	/**
	 * 2 人物经验
	 */
	EXP(2),
	/**
	 * 3 人物真气
	 */
	ZHENQI(3),
	/**
	 * 4 战场声望
	 */
	BATTLEEXP(4),
	/**
	 * 5 当前生命值
	 */
	HP(5),
	/**
	 * 6 当前魔法值值
	 */
	MP(6),
	/**
	 * 7 当前体力值
	 */
	SP(7),
	/**
	 * 8 生命值上限
	 */
	MAXHP(8),
	/**
	 * 9 魔法值值上限
	 */
	MAXMP(9),
	/**
	 * 10 体力值上限
	 */
	MAXSP(10),
	/**
	 * 11 攻击力
	 */
	ATTACK(11),
	/**
	 * 15 幸运值
	 */
	LUCK(15),
	/**
	 * 16 普通属性 攻击速度
	 */
	ATTACKSPEED(16),
	/**
	 * 18 /物理攻击最大
	 */
	Physic_attackupper(18),
	/**
	 * 19 /物理攻击最小
	 */
	Physic_attacklower(19),
	/**
	 * 20 魔法攻击最大
	 */
	Magic_attackupper(20),
	/**
	 * 21 魔法攻击 最小
	 */
	Magic_attacklower(21),
	/**
	 * 22 攻击命中成功率
	 */
	HIT(22),

	// 新增加属性
	/**
	 * 生命回复 +4% 几率
	 */
	Q_HP_RECOVER(23), 
	/**
	 * 魔法上限 +4% 几率
	 */
	Q_ADD_MPLIMIT(24),
	/**
	 * 生命上限 +4% 几率
	 */
	Q_ADD_HP_LIMIT(25),
	Q_REDUCE_DAMAGE(26), // 伤害减少 -4% 几率,吸收伤害
	Q_REBOUND_DAMAGE(27), // 伤害反射 -4% 几率
	Q_DODGE(28), // 防御成功率 +10% 几率
	Q_ADD_MONEY(29), // 杀怪掉出的金币增加 +40% 几率
	// Q_REMARKBLE_ATTACK(30), // 卓越杀伤力增加 + 10% 几率
	Q_PHYSICATTACK_BYLEVEL(31), // 物理攻击力增加 + (角色等级/20） 几率
	Q_PHYSICATTACK_PERCENT(32), // 物理攻击力增加 + 2%
	Q_MAGICATTACK_BYLEVEL(33), // 魔法攻击力增加 + (角色等级/20） 几率
	Q_MAGICATTACK_PERCENT(34), // 魔法攻击力增加 + 2% 几率
	Q_ATTACKSPEED(35), // 卓越属性 攻击速度 + 7 几率
	Q_ADDHP_WHENKILL(36), // 杀怪时得到的生命值增加 + 生命/8 几率
	Q_ADDMP_WHENKILL(37),//杀怪时得到的魔法值增加 + 魔法/8 几率
	

	ICE_ATTACK(38),//冰
	RAY_ATTACK(39),//雷
	POISON_ATTACK(40),//毒
	ICE_DEF(41),//冰防
	RAY_DEF(42),
	POISON_DEF(43),
	IGNORE_ATTACKPERCENT(44),//无视一击*:触发时无视目标的防御力 概率
	DODGE(45),//闪避
	DODGEPERCENT(46),//防御成功率
	PERFECT_ATTACKPERCENT(47), // 卓越一击 类似暴击
	// PERFECT_ATTACKPERCENTPERCENT(48),//出现卓越一击的概率 //注意48这个字符暂时不用
	KNOWING_ATTACKPERCENT(49),//会心一击*:触发时攻击力取值为最大攻击
	PHYSIC_ATTACKUPPER(50),//物理攻击最大
	PHYSIC_ATTACKLOWER(51),//物理攻击最小
	MAGIC_ATTACKUPPER(52),//魔法攻击最大
	MAGIC_ATTACKLOWER(53),//魔法攻击最小
	ATTACKPERCENT(54),//攻击 百分比
	PHYSIC_ATTACKPERCENT(55),//物理攻击百分比
	MAGIC_ATTACKPERCENT(56),//魔法攻击百分比
	DEFENSE(57),//防御
	DEFENSEPERCENT(58),//防御百分比
	SPEED(59),//移动速度
	PERFECTATK_ADDPERCENT(60), // 卓越一击伤害增加值
	IGNORE_DEFENDPERCENT(61),
	
	ATTRIBUTE_ONE_REST_PLUS_VALUE(62),//预留点
	IGNORE_ADD_DEFENSE(63), // 无视防御产生的值（万分比）

	Q_ATTACK_BYLEVEL(64), // 物理魔法攻击力增加 + (角色等级/20） 几率
	Q_ATTACK_PERCENT(65), // 物理魔法攻击力增加 + 2%
	;
	private int value;

	Attributes(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public boolean compare(int value) {
		return this.value == value;
	}
	
	//卓越
	private static List<Integer> superiors = new ArrayList<>();
	//会心一击
	private static List<Integer> lucks = new ArrayList<>();
	//无视一击
	private static List<Integer> ignorings = new ArrayList<>();
	
	static
	{
		//卓越分类
		superiors.add(Q_REDUCE_DAMAGE.getValue());// 伤害减少 -4% 几率,吸收伤害
		superiors.add(Q_REBOUND_DAMAGE.getValue());//  伤害反射 -4% 几率
		superiors.add(Q_DODGE.getValue());	//防御成功率 +10% 几率
		superiors.add(Q_ADD_MONEY.getValue());// 杀怪掉出的金币增加 +40% 几率
		superiors.add(Q_PHYSICATTACK_BYLEVEL.getValue()); // 物理攻击力增加 + (角色等级/20） 几率
		superiors.add(Q_PHYSICATTACK_PERCENT.getValue()); // 物理攻击力增加 + 2%
		superiors.add(Q_MAGICATTACK_BYLEVEL.getValue()); // 魔法攻击力增加 + (角色等级/20） 几率
		superiors.add(Q_MAGICATTACK_PERCENT.getValue()); // 魔法攻击力增加 + 2% 几率
		superiors.add(Q_ATTACKSPEED.getValue());		// 卓越属性 攻击速度 + 7 几率
		superiors.add(Q_ADDHP_WHENKILL.getValue());		// 杀怪时得到的生命值增加 + 生命/8 几率
		superiors.add(Q_ADDMP_WHENKILL.getValue());		//杀怪时得到的魔法值增加 + 魔法/8 几率
		superiors.add(PERFECT_ATTACKPERCENT.getValue());	// 卓越一击 类似暴击
		
		//会心一击分类
		lucks.add(KNOWING_ATTACKPERCENT.getValue());		//会心一击*:触发时攻击力取值为最大攻击
		
		//无视一击
		ignorings.add(IGNORE_ATTACKPERCENT.getValue());   //无视一击*:触发时无视目标的防御力 概率
	}
	
	/**
	 * 是否卓越
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午9:41:17
	 */
	private static boolean _isSuperior(int type)
	{
		return superiors.contains(type);
	}
	
	/**
	 * 是否会心一击
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午9:41:24
	 */
	private static boolean _isLuck(int type)
	{
		return lucks.contains(type);
	}
	
	/**
	 * 是否无视一击
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午9:41:30
	 */
	private static boolean _isIgnoring(int type)
	{
		return ignorings.contains(type);
	}
	
	/**
	 * 是否卓越
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午9:41:17
	 */
	public static boolean inSuperior(Attribute type)
	{
		return _isSuperior(type.getType());
	}
	
	/**
	 * 是否会心一击
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午9:41:24
	 */
	public static boolean inLuck(Attribute type)
	{
		return _isLuck(type.getType());
	}
	
	/**
	 * 是否无视一击
	 * @return
	 * @create	hongxiao.z      2014-2-25 上午9:41:30
	 */
	public static boolean inIgnoring(Attribute type)
	{
		return _isIgnoring(type.getType());
	}
}
