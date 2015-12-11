package com.game.horse.manager;

import com.game.data.bean.Q_horse_additionBean;
//import com.game.data.bean.Q_horse_basicBean;
import com.game.horse.struts.Horse;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.player.structs.PlayerAttributeCalculator;
import com.game.player.structs.PlayerAttributeType;

/**
 * 
 * @author xiaozhuoming
 * 
 * @since 2013-11-16
 *
 */
public class HorseAttributeCalculator implements PlayerAttributeCalculator {

	@Override
	public int getType() {
		return PlayerAttributeType.HORSE;
	}
	
	//潜能点额外加成
//		100攻击
//		50防御
//		320血量
//		20暴击
//		20闪避

	@Override
	public PlayerAttribute getPlayerAttribute(Player player) {
		PlayerAttribute att = new PlayerAttribute();
		att.setMaxHp(0);
		att.setMaxMp(0);
		/*
		att.setMaxSp(0);
		att.setCrit(0);
		att.setLuck(0);
		att.setAttack(0);
		*/
		att.setPhysic_attackupper(0);
		att.setPhysic_attacklower(0);
		att.setMagic_attackupper(0);
		att.setMagic_attacklower(0);
		att.setIgnore_defendPercent(0);
		att.setPerfect_attackPercent(0);
		att.setDefense(0);
		att.setDodge(0);
		att.setAttackSpeed(0);
		att.setSpeed(0);

		Horse horse = ManagerPool.horseManager.getHorse(player);
		if (horse != null && horse.getCurlayer() > 0) {
			if (horse.getCurlayer() > 0) {	//骑乘
//				Q_horse_basicBean horsebasic = ManagerPool.dataManager.q_horse_basicContainer.getMap().get((int)horse.getCurlayer());
//				int lv = player.getLevel();
//				if (lv > horsebasic.getQ_level_max()) {
//					lv = horsebasic.getQ_level_max();
//				}
//				Q_horse_additionBean horsedata = ManagerPool.dataManager.q_horse_additionContainer.getMap().get(horse.getCurlayer()+"_"+lv);
				
				Q_horse_additionBean horsedata = ManagerPool.dataManager.q_horse_additionContainer.getQ_horse_additionBeanByCurLayer(horse.getCurlayer());
				if (horsedata != null) {
					/*xiaozhuoming: 暂时不要
					int attack = 100;
					int defense = 50;
					int hp = 320;
					int crit = 20;
					int dodge = 20;
					
					//潜能点额外加成
						
					attack = attack * horse.getPotential();
					defense = defense * horse.getPotential();
					hp = hp *  horse.getPotential();
					crit = crit * horse.getPotential();
					dodge = dodge * horse.getPotential();
					
					//锻骨草

					double mixing= (double)(horse.getMixingbone()*0.02);
					int mix_attack = (int) (horsedata.getQ_attack() * mixing);
					int mix_defense = (int) (horsedata.getQ_defense() *  mixing);
					int mix_hp = (int) (horsedata.getQ_maxHp() *   mixing);
					int mix_crit = (int) (horsedata.getQ_crit() *  mixing);
					int mix_dodge = (int) (horsedata.getQ_dodge() *  mixing);
					int mix_sp =(int) (horsedata.getQ_maxSp()*  mixing);
					int mix_mp = (int) (horsedata.getQ_maxMp()*  mixing);
					
					att.setAttack(horsedata.getQ_attack());
					att.setCrit(horsedata.getQ_crit());
					att.setMaxSp(horsedata.getQ_maxSp());
					*/
					
					att.setMaxHp(horsedata.getQ_maxHp());
					att.setMaxMp(horsedata.getQ_maxMp());
					att.setPhysic_attackupper(horsedata.getHorse_max_phyatk());
					att.setPhysic_attacklower(horsedata.getHorse_min_phyatk());
					att.setMagic_attackupper(horsedata.getHorse_max_magicatk());
					att.setMagic_attacklower(horsedata.getHorse_min_magicatk());
					att.setIgnore_defendPercent(horsedata.getHorse_ignoredefense());
					att.setIgnore_defend_add_Percent(horsedata.getHorse_ignore_add_defense());

					att.setPerfect_attack(horsedata.getQ_crit());
					att.setPerfectatk_addpercent(horsedata.getHorse_perfectatk_addpercent());

					att.setDefense(horsedata.getQ_defense());
					att.setDodge(horsedata.getQ_dodge());
					att.setAttackSpeed(horsedata.getQ_attackSpeed());
					att.setSpeed(horsedata.getQ_speed());
				}
			}
		}
		return att;
	}

}
	
	
	
	
