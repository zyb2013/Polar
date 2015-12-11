/**
 * 
 */
package com.game.buff.structs;

import com.game.data.bean.Q_buffBean;
import com.game.fight.structs.Fighter;
import com.game.fight.structs.FighterState;
import com.game.manager.ManagerPool;
import com.game.monster.structs.Monster;
import com.game.player.structs.Player;
import com.game.summonpet.struts.SummonPet;
import com.game.utils.Global;

/**
 * @author luminghua
 * 
 * @date 2013年12月11日 下午8:03:13
 * 
 *       增加一个每次按照不同比例增益/减益buff，用于加血和加蓝,如getQ_effect_ratio配置631，则第一次增加血量上限60%，第二次30%，第三次10%
 * 
 *       （废用）
 */
@Deprecated
public class SectionTimeBuff extends TimeBuff {

	/**
	 * 
	 */
	private static final long serialVersionUID = -73126183815685802L;

	private int times = 0;// 触发次数

	/**
	 * buff作用
	 * 
	 * @param source
	 *            来源
	 * @param target
	 *            对象
	 * @return 作用数值 0-失败 1-成功 2-移除 3-涉及别的buff移除，需要终止计算
	 */
	public int action(Fighter source, Fighter target) {
		Q_buffBean buff = ManagerPool.dataManager.q_buffContainer.getMap().get(this.getModelId());
		if (buff == null)
			return 2;
		int q_effect_ratio = buff.getQ_effect_ratio();
		String valueOfRatio = String.valueOf(q_effect_ratio);
		boolean isReduce = false;// 是否减益
		if (q_effect_ratio < 0) {
			valueOfRatio = valueOfRatio.substring(1);
			isReduce = true;
		}
		if (times++ >= valueOfRatio.length()) {
			return 2;
		}
		// 小数位，如0.6
		double ratio = Integer.parseInt(((Character) valueOfRatio.charAt(times - 1)).toString()) / 10.0d;
		if (isReduce) {
			ratio = -1 * ratio;
		}
		switch (this.getActionType()) {
		case BuffType.SECTION_ADD_HP:
			int maxHp = source.getMaxHp();
			// 人物血量上限的比例值
			int totalHp = maxHp * buff.getQ_effect_maxvalue() / Global.MAX_PROBABILITY;
			// 增加的hp
			int hp = (int) (totalHp * ratio);
			// 无敌不受到伤害
			if (hp < 0 && FighterState.WUDI.compare(source.getFightState()))
				return 0;
			source.setHp(source.getHp() + hp);
			if (source.getHp() > maxHp)
				source.setHp(maxHp);
			else if (source.getHp() < 1) {
				source.setHp(1);
			}
			if (source instanceof Player)
				ManagerPool.playerManager.onHpChange((Player) source);
			else if (source instanceof Monster)
				ManagerPool.monsterManager.onHpChange((Monster) source);
			else if (source instanceof SummonPet){
				ManagerPool.summonpetInfoManager.onHpChange((SummonPet) source);
			}
			return 1;
		case BuffType.SECTION_ADD_MP:
			int maxMp = source.getMaxMp();
			// 人物魔力上限的比例值
			int totalMp = maxMp * buff.getQ_effect_maxvalue() / Global.MAX_PROBABILITY;
			// 增加的Mp
			int mp = (int) (totalMp * ratio);
			// 无敌不受到伤害
			if (mp < 0 && FighterState.WUDI.compare(source.getFightState()))
				return 0;
			source.setMp(source.getMp() + mp);
			if (source.getMp() > maxMp)
				source.setMp(maxMp);
			else if (source.getMp() < 1) {
				source.setMp(1);
			}
			if (source instanceof Player)
				ManagerPool.playerManager.onMpChange((Player) source);
			else if (source instanceof Monster)
				ManagerPool.monsterManager.onMpChange((Monster) source);
			return 1;
		default:
			break;
		}
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "123";
		Character charAt = str.charAt(0);
		System.out.println(charAt.toString());
		double ratio = Integer.parseInt(((Character) str.charAt(1 - 1)).toString()) / 10.0d;
		System.out.println(ratio);
		int add = (int) (1000 * ratio);
		System.out.println(add);
	}

}
