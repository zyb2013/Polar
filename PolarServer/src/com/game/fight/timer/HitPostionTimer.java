package com.game.fight.timer;

import java.util.List;

import org.apache.log4j.Logger;

import com.game.fight.structs.Fighter;
import com.game.manager.ManagerPool;
import com.game.skill.structs.Skill;
import com.game.structs.Position;
import com.game.timer.TimerEvent;

/**
 * 击中事件
 * @author heyang
 *
 */
public class HitPostionTimer extends TimerEvent {
	
	protected Logger log = Logger.getLogger(HitTimer.class);
	//攻击Id
	private long fightId;
	//攻击者
	private Fighter attacker;
	//使用技能
	private Skill skill;
	//防御者
	//private Fighter defender;
	
	private int mapModelId;
	
	private int line;
	private   Position Dpostion;
	
	//方向
	private int direction;
	//是否触发技能或buff
	private boolean trigger;
	
	private List<Long> fightTargets;
	private List<Byte> fightTypes;
	
	public HitPostionTimer(long fightId, Fighter attacker,int mapModelId, int line, Position Dpostion,Skill skill, int direction, long delay, boolean trigger, List<Long> fightTargets, List<Byte> fightTypes) {
		super(1, delay);
		this.fightId = fightId;
		this.attacker = attacker;
        this.mapModelId = mapModelId;
        this.line = line;
        this.Dpostion = Dpostion;
		this.skill = skill;
		this.direction = direction;
		this.trigger = trigger;
		this.fightTargets = fightTargets;
		this.fightTypes = fightTypes;
	}

	@Override
	public void action() {
		try{
			if(attacker==null){
				return;
			}
//			if(defender!=null){
//				if(attacker.getServerId()!=defender.getServerId() || attacker.getLine()!=defender.getLine() || attacker.getMap()!=defender.getMap()) return;
//			}
			ManagerPool.fightManager.attackPostion(fightId, attacker, mapModelId,  line, Dpostion, skill, direction, trigger, fightTargets, fightTypes);
		}catch (Exception e) {
			log.error(e, e);
		}
	}

}
