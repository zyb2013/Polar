package com.game.player.manager;

import com.game.data.bean.Q_characterBean;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.player.structs.PlayerAttributeCalculator;
import com.game.player.structs.PlayerAttributeType;

/**
 * 人物基本属性计算
 * @author 周江华
 *
 */
public class BaseAttributeCalculator implements PlayerAttributeCalculator {

	@Override
	public int getType() {
		return PlayerAttributeType.BASE;
	}

	@Override
	public PlayerAttribute getPlayerAttribute(Player player) {
		PlayerAttribute att = new PlayerAttribute();
		//panic god 暂时修改
		//基本加成
		int key=ManagerPool.dataManager.q_characterContainer.getKey(player.getJob(), player.getLevel());
		Q_characterBean model = ManagerPool.dataManager.q_characterContainer.getMap().get(key);
		// att.setAttack(model.getQ_attack());
		att.setDefense(model.getQ_defense());
		att.setHit(model.getQ_hit());

		att.setDodge(model.getQ_dodge());
		att.setMaxHp(model.getQ_hp());
		att.setMaxMp(model.getQ_mp());
		att.setMaxSp(model.getQ_sp());
		att.setAttackSpeed(model.getQ_attackspeed());
		att.setSpeed(model.getQ_speed());
		// //卓越一击 *暴击 //
		att.setPerfect_attack(model.getQ_crit());

		PlayerAttribute att2=playerBaseCalculator(player);
		//二级属性合并
		att.setPhysic_attackupper(att.getPhysic_attackupper()+att2.getPhysic_attackupper());
		att.setPhysic_attacklower(att.getPhysic_attacklower()+att2.getPhysic_attacklower());
		att.setMagic_attackupper(att.getMagic_attackupper()+att2.getMagic_attackupper());
		att.setMagic_attacklower(att.getMagic_attacklower()+att2.getMagic_attacklower());
		att.setHit(att.getHit() + att2.getHit());
		att.setDefense(att.getDefense()+att2.getDefense());
		att.setDodge(att.getDodge() + att2.getDodge());
		att.setAttackSpeed(att.getAttackSpeed()+att2.getAttackSpeed());
		// att.setAttack(att.getAttack()+att2.getAttack());
		att.setMaxHp(att.getMaxHp()+att2.getMaxHp());
		att.setMaxMp(att.getMaxMp()+att2.getMaxMp());
		return att;
	}
	/** 
	 * 计算玩家基本属性  【力量，体力，敏捷，智力】
	 * @param att
	 * @param player
	 */
	public PlayerAttribute  playerBaseCalculator(Player player){
		PlayerAttribute att = new PlayerAttribute();
		//【力量，体力，敏捷，智力，预留属性】
		int strenth=player.getAttibute_one()[0];//力量
		int attPower=player.getAttibute_one()[1];//体力
		int agility=player.getAttibute_one()[2];//敏捷
		int intelligence=player.getAttibute_one()[3];//智力
		
		int physicAttackUper=0;//物理攻击力（最大）
		int physicAttackLower=0;//物理攻击力（最小）
		int hit = 0;// 攻击成功率
		int magicAttackUpper=0;//魔法攻击力（最大）
		int magicAttackLower=0;//魔法攻击力（最小）	
		int defense=0;//防御力
		int dodge = 0;// 防御成功率
		int attackSpeed=0;//攻击速度
		int attack=0;//技能攻击力
		int maxHp=0;//生命值上限
		int maxMp=0;//魔法值上限
		if(PlayerManager.isWarrior(player.getJob())){//1:剑士
			physicAttackUper = ((int) (strenth * 0.47 * 1.1) + 0);// TODO 增加武器
			physicAttackLower = ((int) (strenth * 0.47 * 0.9) + 0);
			hit = ((int) (player.getLevel() * 5) + (int) (strenth * 0.4));
			defense = ((int) (agility * 0.35) + 0);// TODO 增加武器之和
			dodge = ((int) (agility * 0.35) + 0);// TODO 增加武器防御率之和
			attackSpeed = ((int) (agility * 0.03) + 0 + 0);// TODO +装备的速度 +
															// 卓越装备的属性追加的速度
			attack=0;//
			maxHp = ((int) (player.getLevel() * 8.2) + (int) (attPower * 8.2));
			maxMp = ((int) (player.getLevel() * 1) + (int) (intelligence * 1));
		}else if(PlayerManager.isMagician(player.getJob())){//魔法师
			physicAttackUper=0;// TODO 增加武器
			physicAttackLower=0;
			magicAttackUpper=(int)(intelligence*0.61091*1.1);
			magicAttackLower=(int)(intelligence*0.61091*0.9);
			hit = ((int) (player.getLevel() * 5) + (int) (strenth * 0.16667));
			defense = ((int) (agility * 0.36) + 0);// TODO 增加武器之和
			dodge = ((int) (agility * 0.48) + 0);// TODO 增加武器防御率之和
			attackSpeed = ((int) (agility * 0.048) + 0 + 0);// +装备的速度+卓越装备的属性追加的速度
			attack=0;//
			maxHp = ((int) (player.getLevel() * 6.6) + (int) (attPower * 6.6));
			maxMp = ((int) (player.getLevel() * 2) + (int) (intelligence * 2.18182));
		}else if(PlayerManager.isArcher(player.getJob())){//弓箭手
			physicAttackUper=(int)(agility*0.325*1.1);
			physicAttackLower=(int)(agility*0.325*0.9);
			hit = ((int) (player.getLevel() * 5) + (int) (strenth * 0.25));
			defense = ((int) (agility * 0.13122) + (int) (intelligence * 0.03122) + 0);// 增加武器之和
			dodge = ((int) (agility * 0.4) + 0);// TODO 增加武器防御率之和
			attackSpeed = ((int) (agility * 0.01625) + 0 + 0);// +装备的速度
															// +卓越装备的属性追加的速度
			attack=0;//
			maxHp = ((int) (player.getLevel() * 7.8) + (int) (attPower * 7.8));
			maxMp = ((int) (player.getLevel() * 1.5) + (int) (intelligence * 1.5));
		}
		att.setPhysic_attackupper(physicAttackUper);
		att.setPhysic_attacklower(physicAttackLower);
		att.setMagic_attackupper(magicAttackUpper);
		att.setMagic_attacklower(magicAttackLower);
		att.setHit(hit);
		att.setDefense(defense);
		att.setDodge(dodge);
		att.setAttackSpeed(attackSpeed);
		att.setAttack(attack);
		att.setMaxHp(maxHp);
		att.setMaxMp(maxMp);
		return  att;
	}
	public void doPlayerBaseCalculatorByJob(PlayerAttribute att,Player player){
		
	}


}
