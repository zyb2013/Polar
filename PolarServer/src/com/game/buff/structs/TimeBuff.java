package com.game.buff.structs;

import java.util.List;

import com.game.csys.manager.CsysManger;
import com.game.data.bean.Q_buffBean;
import com.game.data.bean.Q_characterBean;
import com.game.dazuo.manager.PlayerDaZuoManager;
import com.game.fight.structs.Fighter;
import com.game.fight.structs.FighterState;
import com.game.manager.ManagerPool;
import com.game.map.structs.Map;
import com.game.monster.structs.Monster;
import com.game.pet.struts.Pet;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.structs.Reasons;
import com.game.summonpet.struts.SummonPet;
import com.game.utils.Global;

public class TimeBuff extends Buff implements IBuff {

	private static final long serialVersionUID = 77215350304432935L;

	@Override
	public int add(Fighter source, Fighter target) {
		return 0;
	}

	@Override
	public int action(Fighter source, Fighter target) {
		Q_buffBean buff = ManagerPool.dataManager.q_buffContainer.getMap().get(this.getModelId());
		if(buff==null) return 0;
		
		int maxHp = 0;
		int maxMp = 0;
		int maxSp = 0;
		
		int hp = 0;
		int mp = 0;
		int sp = 0;
		
		int min = 0;
		int cost = 0;
		int result = 0;
		long num = ((long)buff.getQ_effect_value() + this.getValue()) * this.getOverlay();
		//计算效果
		switch(this.getActionType()){
				//1	增加等级
			case BuffType.LEVEL:
				for (int i = 1; i <= num; i++) {
					ManagerPool.playerManager.setLevel((Player)source, source.getLevel() + 1, true, Reasons.LEVELUPBUFF);
				}
				ManagerPool.playerManager.levelUpSyn((Player)source);
				return 1;//ManagerPool.playerManager.setLevel((Player)source, source.getLevel() + buff.getQ_effect_value() + this.getValue(), true, Reasons.LEVELUPBUFF);
				//2	增加人物经验
			case BuffType.EXP:
			int key = ManagerPool.dataManager.q_characterContainer.getKey(((Player) source).getJob(), ((Player) source).getLevel());
			Q_characterBean model = ManagerPool.dataManager.q_characterContainer.getMap().get(key);
				num += (long)model.getQ_exp() * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY  * this.getOverlay();
				ManagerPool.playerManager.addExp((Player)source, num, AttributeChangeReason.BUFF);
				
				return 1;
			case BuffType.FIXED_EXP:
				num = (long)buff.getQ_effect_value() * this.getOverlay();
				ManagerPool.playerManager.addExp((Player)source, num, AttributeChangeReason.BUFF);
				
				return 1;
				//3	增加人物真气
			case BuffType.ZHENQI:
				return ManagerPool.playerManager.addZhenqi((Player)source, (int)num,AttributeChangeReason.BUFF);
				//4	增加战场声望
			case BuffType.BATTLEEXP:
				return ManagerPool.playerManager.addBattleExp((Player)source, (int)num, AttributeChangeReason.BUFF);
				//5	增加或减少当前生命值
			case BuffType.HP:
				maxHp = source.getMaxHp();
				hp = buff.getQ_effect_value() + this.getValue();
				hp += (long)maxHp * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY;
				//无敌不受到伤害
				if(hp<0 && FighterState.WUDI.compare(source.getFightState())) return 0;
				if(source.getHp() >= maxHp) return 0;
				source.setHp(source.getHp() + hp);
				if(source.getHp() > maxHp) source.setHp(maxHp);
				else if(source.getHp() < 1){
					source.setHp(1);
				}
				min = (int) ((long)maxHp * 2000/ Global.MAX_PROBABILITY);
				if(hp >= min){
					log.debug("玩家：" + source.getName() + "(" + source.getId() + ")回复生命" + hp + ",因为自动恢复BUFF");
				}
				if(source instanceof Player) ManagerPool.playerManager.onHpChange((Player)source);
				else if(source instanceof Monster) ManagerPool.monsterManager.onHpChange((Monster)source);
				else if(source instanceof SummonPet) ManagerPool.summonpetInfoManager.onHpChange((SummonPet)source);
				return 1;
				//6	增加或减少当前魔法值值
			case BuffType.MP:
				maxMp = source.getMaxMp();
				mp = buff.getQ_effect_value() + this.getValue();
				mp += (long)maxMp * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY;
				//禁止魔法值恢复
				if(mp>0 && FighterState.JINZHINEILIHUIFU.compare(source.getFightState())) return 0;
				//无敌不受到伤害
				if(mp<0 && FighterState.WUDI.compare(source.getFightState())) return 0;
				if(source.getMp() >= maxMp) return 0;
				source.setMp(source.getMp() + mp);
				if(source.getMp() > maxMp) source.setMp(maxMp);
				else if(source.getMp() < 0) source.setMp(0);
				if(source instanceof Player) ManagerPool.playerManager.onMpChange((Player)source);
				else if(source instanceof Monster) ManagerPool.monsterManager.onMpChange((Monster)source);
				return 1;
				//7	增加或减少当前体力值
			case BuffType.SP:
				maxSp = source.getMaxSp();
				sp = buff.getQ_effect_value() + this.getValue();
				sp += (long)maxSp * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY;
				//禁止体力恢复
				if(sp>0 && FighterState.JINZHITILIHUIFU.compare(source.getFightState())) return 0;
				//无敌不受到伤害
				if(sp<0 && FighterState.WUDI.compare(source.getFightState())) return 0;
				if(source.getSp() >= maxSp) return 0;
				source.setSp(source.getSp() + sp);
				if(source.getSp() > maxSp) source.setSp(maxSp);
				else if(source.getSp() < 0) source.setSp(0);
				if(source instanceof Player) ManagerPool.playerManager.onSpChange((Player)source);
				else if(source instanceof Monster) ManagerPool.monsterManager.onSpChange((Monster)source);
				return 1;
				//18  每隔一段时间加满人物的生命，直至剩余容量用完
			case BuffType.FILLHP:
				if(source.isDie()) return 0;
				if(source instanceof Player){
					//战斗中不恢复
					if(PlayerState.FIGHT.compare(((Player) source).getState())){
						return 0;
					}
				}
				maxHp = source.getMaxHp();
				if(source.getHp() >= maxHp) return 0;
				cost = maxHp - source.getHp();
				if(this.getParameter() > cost){
					int num1 = (int)Math.ceil((double)this.getParameter() / buff.getQ_effect_value());
					this.setParameter(this.getParameter() - cost);
					int num2 = (int)Math.ceil((double)this.getParameter() / buff.getQ_effect_value());
					if(num1!=num2 && num2>0){
						this.setOverlay(num2);
						ManagerPool.buffManager.sendChangeBuffMessage(source, source, this);
					}
					source.setHp(maxHp);
					result = 1;
					min = (int) ((long)maxHp * 2000/ Global.MAX_PROBABILITY);
					if(cost >= min){
						log.debug("玩家：" + source.getName() + "(" + source.getId() + ")回复生命" + cost + ",因为自动恢复生命池");
					}
				}else{
					source.setHp(source.getHp() + this.getParameter());
					min = (int) ((long)maxHp * 2000/ Global.MAX_PROBABILITY);
					if(this.getParameter() >= min){
						log.debug("玩家：" + source.getName() + "(" + source.getId() + ")回复生命" + this.getParameter() + ",因为自动恢复生命池");
					}
					this.setParameter(0);
					result = 2;
				}
				if(source instanceof Player) ManagerPool.playerManager.onHpChange((Player)source);
				else if(source instanceof Monster) ManagerPool.monsterManager.onHpChange((Monster)source);
				else if(source instanceof SummonPet) ManagerPool.summonpetInfoManager.onHpChange((SummonPet)source);
				return result;
				//19  每隔一段时间加满人物的魔法值，直至剩余容量用完
			case BuffType.FILLMP:
				if(source.isDie()) return 0;
				if(source instanceof Player){
					//战斗中不恢复
					if(PlayerState.FIGHT.compare(((Player) source).getState())){
						return 0;
					}
				}
				//禁止魔法值恢复
				if(FighterState.JINZHINEILIHUIFU.compare(source.getFightState())) return 0;
				maxMp = source.getMaxMp();
				if(source.getMp() >= maxMp) return 0;
				cost = maxMp - source.getMp();
				if(this.getParameter() > cost){
					int num1 = (int)Math.ceil((double)this.getParameter() / buff.getQ_effect_value());
					this.setParameter(this.getParameter() - cost);
					int num2 = (int)Math.ceil((double)this.getParameter() / buff.getQ_effect_value());
					if(num1!=num2 && num2>0){
						this.setOverlay(num2);
						ManagerPool.buffManager.sendChangeBuffMessage(source, source, this);
					}
					source.setMp(maxMp);
					result = 1;
				}else{
					source.setMp(source.getMp() + this.getParameter());
					this.setParameter(0);
					result = 2;
				}
				if(source instanceof Player) ManagerPool.playerManager.onMpChange((Player)source);
				else if(source instanceof Monster) ManagerPool.monsterManager.onMpChange((Monster)source);
				return result;
				//20  每隔一段时间加满人物的体力，直至剩余容量用完
			case BuffType.FILLSP:
				if(source.isDie()) return 0;
				if(source instanceof Player){
					//战斗中不恢复
					if(PlayerState.FIGHT.compare(((Player) source).getState())){
						return 0;
					}
				}
				//禁止体力恢复
				Map smap = ManagerPool.mapManager.getMap((Player)source);
				if(FighterState.JINZHITILIHUIFU.compare(source.getFightState()) || smap.getBanusesp() != 0) return 0;
				maxSp = source.getMaxSp();
				if(source.getSp() >= maxSp) return 0;
				cost = maxSp - source.getSp();
				if(this.getParameter() > cost){
					int num1 = (int)Math.ceil((double)this.getParameter() / buff.getQ_effect_value());
					this.setParameter(this.getParameter() - cost);
					int num2 = (int)Math.ceil((double)this.getParameter() / buff.getQ_effect_value());
					if(num1!=num2 && num2>0){
						this.setOverlay(num2);
						ManagerPool.buffManager.sendChangeBuffMessage(source, source, this);
					}
					source.setSp(maxSp);
					result = 1;
				}else{
					source.setSp(source.getSp() + this.getParameter());
					this.setParameter(0);
					result = 2;
				}
				if(source instanceof Player) ManagerPool.playerManager.onSpChange((Player)source);
				else if(source instanceof Monster) ManagerPool.monsterManager.onSpChange((Monster)source);
				return result;
				//36  有一定成功几率对目标施毒，被施毒后在一定时间内每隔一段时间均按比例减血
			case BuffType.POISON:
				if(FighterState.WUDI.compare(source.getFightState())) return 0;
				if(source.isDie()) return 0;
				maxHp = source.getMaxHp();
				hp = buff.getQ_effect_value() + this.getValue();
				hp += (long)maxHp * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY;
				source.setHp(source.getHp() + hp);
				if(source.getHp() < 1) source.setHp(1);
				min = (int) ((long)maxHp * 2000/ Global.MAX_PROBABILITY);
				if(hp >= min){
					log.debug("玩家：" + source.getName() + "(" + source.getId() + ")回复生命" + hp + ",因为毒药(or自爆)");
				}
				if(source instanceof Player) ManagerPool.playerManager.onHpChange((Player)source);
				else if(source instanceof Monster) ManagerPool.monsterManager.onHpChange((Monster)source);
				else if(source instanceof SummonPet) ManagerPool.summonpetInfoManager.onHpChange((SummonPet)source);
				return 1;
				//63  触发对所选目标造成额外N点伤害
			case BuffType.DAMAGE:
				int damage = buff.getQ_effect_value() + this.getValue();
				damage += (long)damage * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY;
				
				ManagerPool.fightManager.damage(source, target, damage, 0, 0);
				return 1;
				//64  触发减少额外N点伤害
			case BuffType.REDUCE:
				int reduce = buff.getQ_effect_value() + this.getValue();
				reduce += (long)reduce * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY;
				source.setReduce(reduce);
				return 1;
				//72  打坐BUFF
			case BuffType.SITINMEDITATION:
				return PlayerDaZuoManager.getInstacne().buffAction((Player)source);
				//TODO 添加打坐增加真气
				//return 1;
				//89 boss buff（玩家减血,死亡）
			case BuffType.DAMAGEANDDIE:
				if(source.isDie()) return 0;
				if(!(source instanceof Player)) return 0;
				
				Map map = ManagerPool.mapManager.getMap((Player)source);
				if(map==null) return 0;
				
				List<Monster> monsters = ManagerPool.monsterManager.getMonsterByModel(map, 14006);
				Monster monster = null;
				if(monsters.size() > 0){
					monster = monsters.get(0);
				}
				if(monster==null) return 0;
				
				maxHp = source.getMaxHp();
				hp = buff.getQ_effect_value() + this.getValue();
				hp += (long)maxHp * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY;
				source.setHp(source.getHp() + hp);
				if(source.getHp() < 0) source.setHp(0);
				min = (int) ((long)maxHp * 2000/ Global.MAX_PROBABILITY);
				if(hp >= min){
					log.debug("玩家：" + source.getName() + "(" + source.getId() + ")回复生命" + hp + ",因为血崩buff");
				}
				if(source.getHp() < 1) {
					ManagerPool.playerManager.onHpChange((Player)source);
					ManagerPool.playerManager.die((Player)source, monster);
					ManagerPool.buffManager.addBuff(monster, monster, 24009, 0, 0, 0);
					return 3;
				}else{
					ManagerPool.playerManager.onHpChange((Player)source);
					ManagerPool.buffManager.addBuff(monster, monster, 24009, 0, 0, 0);
					return 1;
				}
		case BuffType.SECTION_ADD_HP:
			// 一个每次按照不同比例增益/减益buff，用于加血和加蓝,如getQ_effect_ratio配置631，则第一次增加血量上限60%，第二次30%，第三次10%			
			int q_effect_ratio = buff.getQ_effect_ratio();
			String valueOfRatio = String.valueOf(q_effect_ratio);
			boolean isReduce = false;// 是否减益
			if (q_effect_ratio < 0) {
				valueOfRatio = valueOfRatio.substring(1);
				isReduce = true;
			}
			if (this.getParameter() >= valueOfRatio.length()) {
				return 2;
			}
			this.setParameter(this.getParameter() + 1);
			// 比例值小数位，如0.6
			double ratio = Integer.parseInt(((Character) valueOfRatio.charAt(this.getParameter() - 1)).toString()) / 10.0d;
			if (isReduce) {
				ratio = -1 * ratio;
			}
			maxHp = source.getMaxHp();
			// 人物血量上限的比例值
			int totalHp = maxHp * buff.getQ_effect_maxvalue() / Global.MAX_PROBABILITY;
			// 增加的hp
			int addhp = (int) (totalHp * ratio);
			// 无敌不受到伤害
			if (addhp < 0 && FighterState.WUDI.compare(source.getFightState()))
				return 0;
			source.setHp(source.getHp() + addhp);
			if (source.getHp() > maxHp)
				source.setHp(maxHp);
			else if (source.getHp() < 1) {
				source.setHp(1);
			}
			if (source instanceof Player)
				ManagerPool.playerManager.onHpChange((Player) source);
			else if (source instanceof Monster)
				ManagerPool.monsterManager.onHpChange((Monster) source);
			else if(source instanceof SummonPet)
				ManagerPool.summonpetInfoManager.onHpChange((SummonPet)source);
			return 1;
		case BuffType.SECTION_ADD_MP:
			// 一个每次按照不同比例增益/减益buff，用于加血和加蓝,如getQ_effect_ratio配置631，则第一次增加血量上限60%，第二次30%，第三次10%			
			int q_effect_ratio2 = buff.getQ_effect_ratio();
			String valueOfRatio2 = String.valueOf(q_effect_ratio2);
			boolean isReduce2 = false;// 是否减益
			if (q_effect_ratio2 < 0) {
				valueOfRatio = valueOfRatio2.substring(1);
				isReduce2 = true;
			}
			if (this.getParameter() >= valueOfRatio2.length()) {
				return 2;
			}
			this.setParameter(this.getParameter() + 1);
			// 比例值小数位，如0.6
			double ratio2 = Integer.parseInt(((Character) valueOfRatio2.charAt(this.getParameter() - 1)).toString()) / 10.0d;
			if (isReduce2) {
				ratio2 = -1 * ratio2;
			}
			maxMp = source.getMaxMp();
			// 人物魔力上限的比例值
			int totalMp = maxMp * buff.getQ_effect_maxvalue() / Global.MAX_PROBABILITY;
			// 增加的Mp
			int addmp = (int) (totalMp * ratio2);
			// 无敌不受到伤害
			if (addmp < 0 && FighterState.WUDI.compare(source.getFightState()))
				return 0;
			source.setMp(source.getMp() + addmp);
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
		case BuffType.ADD_HP_BY_TIME:
			int nTimes = 1;
			int nValuePool = this.getParameter();
			
			if (this.getBackups().containsKey("times")){
				nTimes = Integer.valueOf(this.getBackups().get("times"));
			}
			
			// 一个每次按照不同比例增益/减益buff，用于加血和加蓝,如getQ_effect_ratio配置631，则第一次增加血量上限60%，第二次30%，第三次10%，直至固定的血池用完
			if (source.getHp() == source.getMaxHp()){
				return 1;
			}
			
			int nEffect_ratio = buff.getQ_effect_ratio();
			String szRatio = String.valueOf(nEffect_ratio);

			if (nTimes > szRatio.length()) {
				return 2;
			}
			// 比例值小数位，如0.6
			double fRatio = Integer.parseInt(((Character) szRatio.charAt(nTimes - 1)).toString()) / 10.0d;

			maxHp = source.getMaxHp();
			// 增加的hp
			int nAddhp = (int) (maxHp * fRatio) + buff.getQ_effect_value() + this.getValue();
			nAddhp = Math.min(nAddhp, source.getMaxHp() - source.getHp());
			
			if (nAddhp < nValuePool){
				nValuePool -= nAddhp;
				this.setParameter(nValuePool);
				ManagerPool.buffManager.sendChangeBuffMessage(source, source, this);
			}else{
				nAddhp = nValuePool;
				nValuePool = 0;
				this.setParameter(0);
			}
			
			source.setHp(source.getHp() + nAddhp);
			if (source.getHp() > maxHp)
				source.setHp(maxHp);
			else if (source.getHp() < 1) {
				source.setHp(1);
			}
			if (source instanceof Player)
				ManagerPool.playerManager.onHpChange((Player) source);
			else if (source instanceof Monster)
				ManagerPool.monsterManager.onHpChange((Monster) source);
			else if (source instanceof SummonPet)
				ManagerPool.summonpetInfoManager.onHpChange((SummonPet) source);
			
			if (nValuePool <= 0) return 2;
			
			nTimes = nTimes % szRatio.length() + 1;
			
			this.getBackups().put("timer", String.valueOf(nTimes));
			
			return 1;
		case BuffType.ADD_MP_BY_TIME:
			int nTimes2 = 1;
			int nValuePool2 = this.getParameter();
			
			if (this.getBackups().containsKey("times")){
				nTimes2 = Integer.valueOf(this.getBackups().get("times"));
			}
			
			// 一个每次按照不同比例增益/减益buff，用于加血和加蓝,如getQ_effect_ratio配置631，则第一次增加血量上限60%，第二次30%，第三次10%，直至固定的血池用完
			if (source.getMp() == source.getMaxMp()){
				return 1;
			}
			
			int nEffect_ratio2 = buff.getQ_effect_ratio();
			String szRatio2 = String.valueOf(nEffect_ratio2);

			if (nTimes2 > szRatio2.length()) {
				return 2;
			}
			// 比例值小数位，如0.6
			double fRatio2 = Integer.parseInt(((Character) szRatio2.charAt(nTimes2 - 1)).toString()) / 10.0d;

			maxMp = source.getMaxMp();
			// 增加的mp
			int nAddmp = (int) (maxMp * fRatio2) + buff.getQ_effect_value() + this.getValue();
			nAddmp = Math.min(nAddmp, source.getMaxMp() - source.getMp());
			
			if (nAddmp < nValuePool2){
				nValuePool2 -= nAddmp;
				this.setParameter(nValuePool2);
				ManagerPool.buffManager.sendChangeBuffMessage(source, source, this);
			}else{
				nAddmp = nValuePool2;
				nValuePool2 = 0;
				this.setParameter(0);
			}
			
			source.setMp(source.getMp() + nAddmp);
			if (source.getMp() > maxMp)
				source.setMp(maxMp);
			else if (source.getMp() < 1) {
				source.setMp(1);
			}
			if (source instanceof Player)
				ManagerPool.playerManager.onMpChange((Player) source);
			else if (source instanceof Monster)
				ManagerPool.monsterManager.onMpChange((Monster) source);
			
			if (nValuePool2 <= 0) return 2;
			
			nTimes2 = nTimes2 % szRatio2.length() + 1;
			
			this.getBackups().put("timer", String.valueOf(nTimes2));
			
			return 1;
		
		case BuffType.SPECIAL_EFFECT:
			return 1;
		case BuffType.ZIBAO:
			if(FighterState.WUDI.compare(source.getFightState())) return 0;
			if(source.isDie()) return 0;
			maxHp = source.getMaxHp();
			hp = buff.getQ_effect_value() + this.getValue();
			hp += (long)maxHp * (buff.getQ_effect_ratio() + this.getPercent()) / Global.MAX_PROBABILITY;
			source.setHp(source.getHp() - hp);
			if(source.getHp() < 1){
				source.setHp(0);
				//怪物或玩家死亡
				if (source instanceof Monster) {
					ManagerPool.monsterManager.die((Monster) source, null, -1);
				} else if (source instanceof Player) {
					ManagerPool.playerManager.die((Player) source, null);
				} else if (source instanceof Pet) {
					ManagerPool.petOptManager.die((Pet) source, null);
				}else if (source instanceof SummonPet) {
					ManagerPool.summonpetOptManager.die((SummonPet) source, null);
				}
			}else{
				if(source instanceof Player) ManagerPool.playerManager.onHpChange((Player)source);
				else if(source instanceof Monster) ManagerPool.monsterManager.onHpChange((Monster)source);
			}
			return 1;
		case BuffType.HP_PERCENT:
			Player player = (Player)target;			
			
			hp = player.getHp();
			hp += buff.getQ_effect_value() + this.getValue();
			
			if (hp >= player.getMaxHp()){
				player.setHp(player.getMaxHp());
			}else{
				player.setHp(hp);
			}
			
			ManagerPool.playerManager.onHpChange(player);
			return 1;
		}
		return 0;
	}

	@Override
	public int remove(Fighter source) {
		return 0;
	}

}
