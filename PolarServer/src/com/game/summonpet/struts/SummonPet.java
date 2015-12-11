package com.game.summonpet.struts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.buff.structs.AttributeBuff;
import com.game.buff.structs.Buff;
import com.game.config.Config;
import com.game.data.bean.Q_monsterBean;
import com.game.data.bean.Q_skill_modelBean;
import com.game.fight.structs.Fighter;
import com.game.fight.structs.FighterState;
import com.game.manager.ManagerPool;
import com.game.map.structs.IMapObject;
import com.game.map.structs.Jump;
import com.game.player.structs.Person;
import com.game.player.structs.Player;
import com.game.skill.structs.Skill;
import com.game.structs.Position;
import com.game.utils.Global;
import com.game.utils.RandomUtils;
/*
 * 弓箭手的召唤怪
 */
import com.game.utils.Symbol;

public class SummonPet extends Person implements IMapObject, Fighter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SummonPet.class);

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	// 攻击对象列表
	private transient List<Fighter> attackTargets = new ArrayList<Fighter>();
	// 攻击对象
	private transient Fighter attTarget;

	// 攻击对象的类型
	private transient int targetType;


	// 宠物状态
	private transient SummonPetState state = SummonPetState.UNSHOW;

	private transient SummonPetJumpState jumpState = SummonPetJumpState.NOMAL;

	private transient SummonPetRunState runState = SummonPetRunState.RUN;

	private transient Jump jump = new Jump();

	private transient Position dest;
	// 默认单攻技能
	private Skill defaultSingleSkill = new Skill();
	// 默认群攻技能
	private Skill defaultMutileSkill = new Skill();

	// 移动耗时时间
	protected transient int cost = 0;
	// 移动上一步时间
	protected transient long prevStep = 0;
	// 上一次寻路的时间
	private transient long lastFindRoadsTime = 0;
	// 最后一次攻击或被攻击时间 用于计算战斗状态
	private transient long lastFightTime = 0;
	// 上一次回血时间
	private transient long lastRecoveryTime = 0;
	// 是否强制收回的
	private transient boolean isForceHide = false;
	// 标志列表
	private HashSet<String> tagSet = new HashSet<String>();

	// 是否显示
	private boolean show = false;

	// 死亡时间
	private long dieTime = 0l;

	private long ownerId;


	private int serverId;

	// 装备列表
	// private Equip[] equips = new Equip[12];

	// 暂时用不到
	private Skill[] skills = new Skill[8];

	//
	private int intelligence = 0;
	
	public SummonPet(Player player, int modelid) {
		setModelId(modelid);
		setId(Config.getId());
		ownerId = player.getId();
		show = true;
		state = SummonPetState.IDEL;	
		intelligence = player.getAttibute_one()[3];
		setLevel(player.getLevel());
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId() + getLevel());
		if(model==null){
			return;
		}
		

		/*
		if (model != null) {
			int q_default_skillId = model.getQ_skill();// TODO 美人技能处理
			Skill skill = new Skill();
			skill.setId(Config.getId());
			skill.setSkillLevel(1);
			skill.setSkillModelId(q_default_skillId);
			skills[0] = skill;
		}
		*/
		if (model.getQ_default_skill() != null) {
			defaultSingleSkill = getUseSkill();
			
//			defaultSingleSkill.setSkillModelId(model.getQ_skill_single());
//			defaultSingleSkill.setSkillLevel(1);
		}
		/*
		if (model.getQ_skill_multi() != 0) {
			defaultMutileSkill.setSkillModelId(model.getQ_skill_multi());
			defaultMutileSkill.setSkillLevel(1);
		}*/
	}

	public SummonPet() {
	}

	public boolean isShow() {
		return show;
	}

	public SummonPetJumpState getJumpState() {
		return jumpState;
	}

	public void setJumpState(SummonPetJumpState jumpState) {
		this.jumpState = jumpState;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public SummonPetState getState() {
		return state;
	}

	public void changeStateTo(SummonPetState state) {
		SummonPetState before = this.state;
		this.state = state;
		logger.debug("召唤怪物" + getId() + "状态从" + before + "切换到" + state);
	}

	/**
	 * 主人ID
	 * 
	 * @return
	 */
	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public long getLastFindRoadsTime() {
		return lastFindRoadsTime;
	}

	public void setLastFindRoadsTime(long time) {
		this.lastFindRoadsTime = time;
	}

	public Skill[] getSkills() {
		return skills;
	}

	public void setSkills(Skill[] skills) {
		this.skills = skills;
	}

	public int getSpeed() {
		double value = 0;
		if(this.speed==0){
			//获得怪物模型
			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId() + getLevel() );
			value = model.getQ_speed();
		}else{
			value = this.speed;
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		int totalPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getSpeed() * aBuff.getOverlay());
				bufPercent += (aBuff.getSpeedPercent() * aBuff.getOverlay());
				totalPercent += (aBuff.getTotalSpeedPercent() * aBuff.getOverlay());
			}
		}
		value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;

		//变身加成
//		if(morph.containsKey(MorphType.SPEED.getValue())){
//			value = value * morph.get(MorphType.SPEED.getValue()).getValue() / Global.MAX_PROBABILITY;
//		}
		
		//总体buff加成
		value = value * (Global.MAX_PROBABILITY + totalPercent) / Global.MAX_PROBABILITY;
		
		return (int)value;
	}

	@Override
	public int getMaxHp() {
		double value = 0;
		if(this.maxHp==0){
			//获得怪物模型
			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
			value = model.getQ_maxhp();
		}else{
			value = this.maxHp;
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getMaxHp() * aBuff.getOverlay());
				bufPercent += (aBuff.getMaxHpPercent() * aBuff.getOverlay());
			}
		}
		value = ((value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY);

		/*
		//变身加成
		if(morph.containsKey(MorphType.HP.getValue())){
			value = value * morph.get(MorphType.HP.getValue()).getValue() / Global.MAX_PROBABILITY;
		}*/
		return (int)value;
	}
	
	@Override
	public int getMaxMp() {
		double value = 0;
		if(this.maxMp==0){
			//获得怪物模型
			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
			value = model.getQ_maxmp();
		}else{
			value = this.maxMp;
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getMaxMp() * aBuff.getOverlay());
				bufPercent += (aBuff.getMaxMpPercent() * aBuff.getOverlay());
			}
		}
		value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;

		return (int)value;
	}
	

	@Override
	public int getMaxSp() {
		return 0;
	}

	@Override
	public int getAttack() {
		return getAttack(null);
	}
	
	@Override
	public int getAttack(Skill skill){
		//攻击清0 Buff
		if(FighterState.GONGJIWEILING.compare(this.getFightState())) return 0;
		
		double value = 0;
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
		if(this.attack==0){
			//获得怪物模型
			value = model.getQ_attack();
		}else{
			value = this.attack;
		}
		
		//玩家的智力加成
		int summonpetType = getModelId() / 1000;
		if (summonpetType == 400001){
//			哥布林攻击加成公式: =智力*0.1
			value += intelligence * 1000 / 10000;
		}else if(summonpetType == 400002){
//			黄金斗士攻击加成公式：=智力*0.2
			value += intelligence * 5000 / 10000;
		}else if(summonpetType == 400003){
//			巴里攻击加成公式：=智力*0.6
			value += intelligence * 6000 / 10000;
		}

//		value = value*(1+ intelligence/1000);
		
		//技能加成
		if(skill!=null){
			Q_skill_modelBean skillModel= ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
			value += skillModel.getQ_attack_addition();
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		int equipPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getAttack() * aBuff.getOverlay());
				bufPercent += (aBuff.getAttackPercent() * aBuff.getOverlay());
				equipPercent += (aBuff.getEquipAttackPercent() * aBuff.getOverlay());
			}
		}
		value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;
		
		//装备加成
		int equipValue = model.getQ_equip_attack();
		value = value + (double)equipValue * (Global.MAX_PROBABILITY + equipPercent) / Global.MAX_PROBABILITY;
		
		//变身加成
//		if(morph.containsKey(MorphType.ATTACK.getValue())){
//			value = value * morph.get(MorphType.ATTACK.getValue()).getValue() / Global.MAX_PROBABILITY;
//		}
		return (int)value;
	}

	@Override
	public int getDefense() {
		//防御清0 Buff
		if(FighterState.FANGYUWEILING.compare(this.getFightState())) return 0;
				
		double value = 0;
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
		if(this.defense==0){
			//获得怪物模型
			value = model.getQ_defense();
		}else{
			value = this.defense;
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		int equipPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getDefense() * aBuff.getOverlay());
				bufPercent += (aBuff.getDefensePercent() * aBuff.getOverlay());
				equipPercent += (aBuff.getEquipDefensePercent() * aBuff.getOverlay());
			}
		}
		value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;
		
		//装备加成
		int equipValue = model.getQ_equip_defense();
		value = value + (double)equipValue * (Global.MAX_PROBABILITY + equipPercent) / Global.MAX_PROBABILITY;
		
		//变身加成
//		if(morph.containsKey(MorphType.DEFENSE.getValue())){
//			value = value * morph.get(MorphType.DEFENSE.getValue()).getValue() / Global.MAX_PROBABILITY;
//		}
		return (int)value;
	}

	@Override
	public int getAttackSpeed() {
		double value = 0;
		if(this.attackSpeed==0){
			//获得怪物模型
			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
			value = 1000 + model.getQ_attack_speed();
		}else{
			value = this.attackSpeed;
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		int totalPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getAttackSpeed() * aBuff.getOverlay());
				bufPercent += (aBuff.getAttackSpeedPercent() * aBuff.getOverlay());
				totalPercent += (aBuff.getTotalAttackSpeedPercent() * aBuff.getOverlay());
			}
		}
		value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;

		//变身加成
//		if(morph.containsKey(MorphType.ATTACKSPEED.getValue())){
//			value = value * morph.get(MorphType.ATTACKSPEED.getValue()).getValue() / Global.MAX_PROBABILITY;
//		}
		
		//总体buff加成
		value = value * (Global.MAX_PROBABILITY + totalPercent) / Global.MAX_PROBABILITY;
		
		return (int)value;
	}

	@Override
	public int getLuck() {
		double value = 0;
		if(this.luck==0){
			//获得怪物模型
			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
			value = model.getQ_luck();
		}else{
			value = this.luck;
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getLuck() * aBuff.getOverlay());
				bufPercent += (aBuff.getLuckPercent() * aBuff.getOverlay());
			}
		}
		value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;

		return (int)value;
	}


	@Override
	public int getCrit() {
		//暴击清0 Buff
		if(FighterState.BAOJIWEILING.compare(this.getFightState())) return 0;
				
		double value = 0;
		if(this.crit==0){
			//获得怪物模型
			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
			value = model.getQ_crt();
		}else{
			value = this.crit;
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getCrit() * aBuff.getOverlay());
				bufPercent += (aBuff.getCritPercent() * aBuff.getOverlay());
			}
		}
		value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;

		//变身加成
//		if(morph.containsKey(MorphType.CRIT.getValue())){
//			value = value * morph.get(MorphType.CRIT.getValue()).getValue() / Global.MAX_PROBABILITY;
//		}
		return (int)value;
	}

	@Override
	public int getHit() {      
	double value = 0;
	if(this.hit==0){
		//获得怪物模型
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
		value = model.getQ_hit();
	}else{
		value = this.hit;
	}
	
	//Buff加成
	int bufValue = 0;
	int bufPercent = 0;
	for (int i = 0; i < this.getBuffs().size(); i++) {
		Buff buff = this.getBuffs().get(i);
		if(buff instanceof AttributeBuff){
			AttributeBuff aBuff = (AttributeBuff)buff;
			bufValue += (aBuff.getHit() * aBuff.getOverlay());
			bufPercent += (aBuff.getHitPercent() * aBuff.getOverlay());
		}
	}
	value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;

	//变身加成
	/*
	if(morph.containsKey(MorphType.DODGE.getValue())){
		value = value * morph.get(MorphType.DODGE.getValue()).getValue() / Global.MAX_PROBABILITY;
	}
	*/
	return (int)value;
	}
	
	
	@Override
	public int getDodge() {
		//闪避清0 Buff
		if(FighterState.SHANBIWEILING.compare(this.getFightState())) return 0;
				
		double value = 0;
		if(this.dodge==0){
			//获得怪物模型
			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
			value = model.getQ_dodge();
		}else{
			value = this.dodge;
		}
		
		//Buff加成
		int bufValue = 0;
		int bufPercent = 0;
		for (int i = 0; i < this.getBuffs().size(); i++) {
			Buff buff = this.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				bufValue += (aBuff.getDodge() * aBuff.getOverlay());
				bufPercent += (aBuff.getDodgePercent() * aBuff.getOverlay());
			}
		}
		value = (value + bufValue) * (Global.MAX_PROBABILITY + bufPercent) / Global.MAX_PROBABILITY;

		//变身加成
//		if(morph.containsKey(MorphType.DODGE.getValue())){
//			value = value * morph.get(MorphType.DODGE.getValue()).getValue() / Global.MAX_PROBABILITY;
//		}
		return (int)value;
	}
	
	public int getLevel(){
		if(this.level > 0){
			return this.level;
		}else{
			return 1;
			//获得怪物模型
//			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
//			return model.getQ_grade();
		}
	}
	
	/**
	 * 获取无视伤害防御值
	 * @return
	 */
	public int getIgnoreDamage(){
		//获得怪物模型
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
		return model.getQ_ignore_damage();
	}
	

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	@Override
	public boolean isDie() {
		return getState().getValue() == SummonPetState.DIE.getValue() || getState().getValue() == SummonPetState.DIEWAIT.getValue();
	}

	
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	@Override
	public int getServerId() {
		return this.serverId;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public long getPrevStep() {
		return prevStep;
	}

	public void setPrevStep(long prevStep) {
		this.prevStep = prevStep;
	}

	public List<Fighter> getAttackTargets() {
		return attackTargets;
	}

	public void setAttackTargets(List<Fighter> attackTargets) {
		this.attackTargets = attackTargets;
	}


	/**
	 * 获取默认技能
	 * @return
	 */
	public Skill getUseSkill() {
		//确定攻击技能
		try{
			//获得怪物模型
			Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(getModelId()  + getLevel() );
			//计算特殊技能
			if(model.getQ_special_skill()!=null && !("").equals(model.getQ_special_skill())){
				//技能ID_技能等级|触发几率分子/触发几率分母;技能ID_技能等级|触发几率分子/触发几率分母;
				//怪物技能
				String[] skills = model.getQ_special_skill().split(Symbol.FENHAO_REG);
				for (int i = 0; i < skills.length; i++) {
					String[] paras = skills[i].split(Symbol.SHUXIAN_REG);
					String[] probabilitys = paras[1].split(Symbol.XIEGANG_REG);
			    	//随机变身概率
			    	int pro = RandomUtils.random(Integer.parseInt(probabilitys[0]));
			    	if(pro < Integer.parseInt(probabilitys[1])){
			    		String[] skillParas = paras[0].split(Symbol.XIAHUAXIAN_REG);
			    		defaultSingleSkill.setSkillModelId(Integer.parseInt(skillParas[0]));
			    		defaultSingleSkill.setSkillLevel(Integer.parseInt(skillParas[1]));
			    		return defaultSingleSkill;
			    	}
				}
			}
			//计算默认技能
			return getDefaultSkill(model);
		}catch (NumberFormatException e) {
			logger.error(e, e);
		}
		
		return null;
	}
	
	/**
	 * 获取默认技能
	 * @return
	 */
	public Skill getDefaultSkill(Q_monsterBean model){
		try{
			//计算默认技能
			if(model.getQ_default_skill()!=null && !("").equals(model.getQ_default_skill())){
				String[] skills = model.getQ_default_skill().split(Symbol.XIAHUAXIAN_REG);
				defaultSingleSkill.setSkillModelId(Integer.parseInt(skills[0]));
				defaultSingleSkill.setSkillLevel(Integer.parseInt(skills[1]));
	    		return defaultSingleSkill;
			}
		}catch (NumberFormatException e) {
			logger.error(e,e);
		}
		
		return null;
	}
	
	public void setDefaultSingleSkill(Skill defaultSingleSkill) {
		this.defaultSingleSkill = defaultSingleSkill;
	}

	public Skill getDefaultSingleSkill(){
		return getUseSkill();
	}
	
	//范围攻击暂时没用
	public Skill getDefaultMutileSkill() {
		return null;
	}
    
	//范围攻击暂时没用
	public void setDefaultMutileSkill(Skill defaultMutileSkill) {
		this.defaultMutileSkill = defaultMutileSkill;
	}

	public long getDieTime() {
		return dieTime;
	}

	public void setDieTime(long dieTime) {
		this.dieTime = dieTime;
	}

	public long getLastFightTime() {
		return lastFightTime;
	}

	public void setLastFightTime(long lastFightTime) {
		this.lastFightTime = lastFightTime;
	}

	public long getLastRecoveryTime() {
		return lastRecoveryTime;
	}

	public void setLastRecoveryTime(long lastRecoveryTime) {
		this.lastRecoveryTime = lastRecoveryTime;
	}

	public int getTargetType() {
		return targetType;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public Jump getJump() {
		return jump;
	}

	public void setJump(Jump jump) {
		this.jump = jump;
	}
	
	public HashSet<String> getTagSet() {
		return tagSet;
	}

	public void setTagSet(HashSet<String> tagSet) {
		this.tagSet = tagSet;
	}

	public void resetPet() {
		// BUFF清除
		this.getBuffs().clear();
		// 清除目标
		this.attackTargets.clear();
		// 设置血量
		this.setHp((int) (this.getMaxHp() * .1));
		// 设置魔法值
		this.setMp(this.getMaxMp());
		// 设置体力
		this.setSp(this.getMaxSp());
		// 设置正常
		this.changeStateTo(SummonPetState.IDEL);
		// 死亡时间清0
		this.setDieTime(0);
		// 设置战斗状态正常
		this.setFightState(0);
		// 攻击目标清除
		this.setTargetType(0);
	}

	@Override
	public boolean canSee(Player player) {
		return !this.isDie();
	}

	public Position getDest() {
		return dest;
	}

	public void setDest(Position dest) {
		this.dest = dest;
	}

	public Fighter getAttTarget() {
		return attTarget;
	}

	public void setAttTarget(Fighter attTarget) {
		this.attTarget = attTarget;
	}

	public SummonPetRunState getRunState() {
		return runState;
	}

	public void setRunState(SummonPetRunState runState) {
		this.runState = runState;
	}

	public boolean isForceHide() {
		return isForceHide;
	}

	public void setForceHide(boolean isForceHide) {
		this.isForceHide = isForceHide;
	}
	
	/**
	 * 冰，雷，毒，玩家和小怪都要用
	 * @return
	 */
	public int getIce_def(){
		double value = 0;
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(this.getModelId() + getLevel());
		if(this.ice_def==0){
			//获得怪物模型
			value = model.getQ_element_defence_ice();
		}else{
			value = this.ice_def;
		}
		return (int)value;
	};
	
	public int getRay_def(){
		double value = 0;
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(this.getModelId() + getLevel());
		if(this.ray_def==0){
			//获得怪物模型
			value = model.getQ_element_defence_bolt();
		}else{
			value = this.ray_def;
		}
		return (int)value;
		
	};
	
	public int getPoison_def(){
		double value = 0;
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(this.getModelId() + getLevel());
		if(this.poison_def==0){
			//获得怪物模型
			value = model.getQ_element_defence_bane();
		}else{
			value = this.poison_def;
		}
		return (int)value;
	}
}
