package com.game.fightpower.manager;

import org.apache.log4j.Logger;

import com.game.data.bean.Q_skill_modelBean;
import com.game.data.manager.DataManager;
import com.game.fightpower.message.ReqFightPowerToServerMessage;
import com.game.fightpower.message.ResFightPowerToClientMessage;
import com.game.guild.message.ReqInnerGuildUpdateFightPowerToWorld;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.skill.structs.Skill;
import com.game.utils.Global;
import com.game.utils.MessageUtil;

/**
 * 战斗力系统
 *
 * @author 杨洪岚
 */
public class FightPowerManager {

	private Logger logger = Logger.getLogger(FightPowerManager.class);
	
	private static Object obj = new Object();
	//战斗力系统类实例
	private static FightPowerManager manager;

	private FightPowerManager() {
	}

	public static FightPowerManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new FightPowerManager();
			}
		}
		return manager;
	}
	//分母为10
	private int MaxMaxHpCof = 2;			//最大生命	2
	private int MaxMaxMpCof = 1;			//最大魔法值	1
	private int MaxDefCof = 10;			//防御		10
	private int MaxDodgeCof = 40;			//闪避		40
	private int MaxCritCof = 40;			//暴击		40
	private int MaxAttackSpeedCOF = 25;		//攻击速度	25
	private int MaxMoveSpeedCof = 15;		//移动速度	15
	private int MaxLuckyCof = 8;			//幸运		8
	private int MaxMaxSpCof = 1;			//体力		1
	private int MaxAttackCof = 14;			//攻击		14
	
	public void Update(Player player){
		int fightPower = calAllFightPower(player);
		if(fightPower != player.getFightPower()) {
			ReqInnerGuildUpdateFightPowerToWorld reqFightPowerToWorldMessage = new ReqInnerGuildUpdateFightPowerToWorld();
			reqFightPowerToWorldMessage.setPlayerId(player.getId());
			reqFightPowerToWorldMessage.setFightPower(fightPower);
			MessageUtil.send_to_world(reqFightPowerToWorldMessage);
		}
		
		ResFightPowerToClientMessage sendMessage = new ResFightPowerToClientMessage();
		player.setFightPower(fightPower);
		sendMessage.setFightPower(player.getFightPower());
		MessageUtil.tell_player_message(player, sendMessage);
		if (player.getLevel() >= Global.SYNC_PLAYER_LEVEL) {
			PlayerManager.getInstance().syncPlayerOrderInfo(player);
		}
	}

	public int calAllFightPower(Player player) {
		if (player != null) {
			int fightPower = calPlayerFightPower(player);
			// + calSkillFightPower(player);
			if (fightPower > player.getMaxFightPower()) {
				player.setMaxFightPower(fightPower);
			}
			return fightPower;
		}
		return 0;
	}

	public int calPlayerFightPower(Player player) {
		if (player != null) {
			// 战斗力=ROUNDUP(生命+攻击*12+防御*12+魔法值*0.1+0.95*防率/(防率+1662)*584+（1-182.4/(192+攻率)）*584+（攻速-100）*5.84+会心一击率*卓越一击率*685*（1.2+卓越一击伤害提高）+无视防御比例*192+（冰抗+雷抗+毒抗）*10
			//+（增加伤害比例+吸收伤害比例）*584+无视一击率*1752+（伤害减少+伤害反射）*584,0)
			/**
			 * 战斗力公式=ROUNDUP(生命+攻击*12+防御*12+魔法值*0.1+防率/(防率+1660)*9120+
			 * 攻速*96+会心一击率*卓越一击率*960*（1.4+卓越一击伤害提高）*12+无视防御比例*9600+（冰抗+雷抗+毒抗）*10+
			 * （增加伤害比例+吸收伤害比例）*800*12+无视一击率*48000+（伤害减少+伤害反射）*9600,0)
			 */
			double attack = 0;
			// 攻击:按职业区分,战士/弓箭手用物理攻击上下限的平均值,魔法师用魔法攻击力的平均值
			if (PlayerManager.isMagician(player.getJob())) {
				attack = (player.getMagic_attacklower() + player.getMagic_attackupper()) / 2.0d;
			} else {
				attack = (player.getPhysic_attacklower() + player.getPhysic_attackupper()) / 2.0d;
			}
			double fightPower = player.getMaxHp() 
					+ attack * 12 + player.getDefense() * 12 
					+ player.getMaxMp() * 0.1d 
					+ (double)player.getDodge() / (player.getDodge() + 1660) * 9120
					+(player.getAttackSpeed()-100)* 96
					+ ((double)player.getKnowing_attackPercent() / Global.MAX_PROBABILITY) * ((double)player.getPerfect_attackPercent() / Global.MAX_PROBABILITY) * 960 * (1.4d + (double)player.getPerfect_addattackPercent() / Global.MAX_PROBABILITY)*12
					+ (double)player.getignore_defendPercent() / Global.MAX_PROBABILITY * 9600 
					+ (player.getIce_def() + player.getPoison_def() + player.getRay_def()) * 10
					//增加伤害比例+吸收伤害比例 = 总增加伤害比例-总吸收伤害比例
					+ ((double)player.getAddInjure() / Global.MAX_PROBABILITY - (double)player.getReduceInjure() / Global.MAX_PROBABILITY ) * 800*12 
					+ (double)player.getIgnore_attackPercent() / Global.MAX_PROBABILITY * 48000 
					+ (double)(player.getReduce() + player.getRebound_damage())/ Global.MAX_PROBABILITY *9600 
					+ player.getSkillFightPower();
			if(logger.isDebugEnabled()) {
				logger.error("-----player-------");
				logger.error(player.getMaxHp());
				logger.error(attack*12);
				logger.error(player.getDefense()*12);
				logger.error(player.getMaxMp()*0.1d);
				logger.error(player.getDodge() / (player.getDodge() + 1660) * 9120);
				logger.error(player.getHit());
				logger.error((player.getAttackSpeed()-100)* 96);
				logger.error(player.getKnowing_attackPercent());
				logger.error(player.getPerfect_attackPercent());
				logger.error(player.getPerfect_addattackPercent());
				logger.error(player.getignore_defendPercent());
				logger.error(player.getIce_def());
				logger.error(player.getPoison_def());
				logger.error(player.getRay_def());
				logger.error(player.getAddInjure());
				logger.error(player.getReduceInjure());
				logger.error(player.getIgnore_attackPercent());
				logger.error(player.getReduce());
				logger.error(player.getRebound_damage());
				logger.error(player.getSkillFightPower());
				logger.error((int) Math.ceil(fightPower));
			}
			return (int) Math.ceil(fightPower);// 零舍一入
			// return (player.getCalattack() * MaxAttackCof
			// + player.getCaldefense() * MaxDefCof
			// + player.getCalcrit() * MaxCritCof
			// + player.getCaldodge() * MaxDodgeCof
			// + player.getCalmaxHp() * MaxMaxHpCof
			// + player.getCalmaxMp() * MaxMaxMpCof
			// + player.getCalmaxSp() * MaxMaxSpCof
			// + player.getCalattackSpeed() * MaxAttackSpeedCOF
			// + player.getCalspeed() * MaxMoveSpeedCof
			// + player.getCalluck() * MaxLuckyCof) / 10;
		}
		return 0;
	}

/*	
 * luminghua hide
 * public int calSkillFightPower(Player player) {
		if (player != null) {
			int ret = 0;
			for (int i = 0; i < player.getSkills().size(); i++) {
				Skill skill = player.getSkills().get(i);
				if (skill != null) {
					Q_skill_modelBean q_skill_modelBean = DataManager.getInstance().q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
					if (q_skill_modelBean != null) {
						ret = ret + q_skill_modelBean.getQ_fight_bonus();
					}
				}
			}
			return ret;
		}
		return 0;
	}*/
	
	public int calAttrFightPower(PlayerAttribute playerAttribute) {
		// if (playerAttribute != null) {
		// return (playerAttribute.getAttack() * MaxAttackCof
		// + playerAttribute.getDefense() * MaxDefCof
		// + playerAttribute.getCrit() * MaxCritCof
		// + playerAttribute.getDodge() * MaxDodgeCof
		// + playerAttribute.getMaxHp() * MaxMaxHpCof
		// + playerAttribute.getMaxMp() * MaxMaxMpCof
		// + playerAttribute.getMaxSp() * MaxMaxSpCof
		// + playerAttribute.getAttackSpeed() * MaxAttackSpeedCOF
		// + playerAttribute.getSpeed() * MaxMoveSpeedCof
		// + playerAttribute.getLuck() * MaxLuckyCof) / 10;
		// }
		return 0;
	}
	
	public void reqFightPowerToServer(Player player, ReqFightPowerToServerMessage message){
		Update(player);
	}
}
