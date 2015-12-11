package com.game.player.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.game.buff.structs.Buff;
import com.game.cooldown.structs.Cooldown;
import com.game.fight.structs.FighterState;
import com.game.object.GameObject;
import com.game.structs.Position;

public abstract class Person extends GameObject {

	private static final long serialVersionUID = -7778568015291171928L;
	//模板Id
	protected int modelId;
	//创建时间
	protected long createTime;
	//所在线服务器
	protected transient int line;
	//地图
	protected int map;
	//地图模板id
	protected int mapModelId;
	//坐标
	protected Position position;
	//方向
	protected byte direction;
	//移动路径
	protected transient List<Position> roads = new ArrayList<Position>();
	//移动耗时时间
	protected transient int cost = 0;
	//移动上一步时间
	protected transient long prevStep = 0;
	//当前生命
	protected int hp;
	//当前魔法
	protected int mp;
	//当前体力
	protected int sp;
	//名字
	protected String name;
	//资源
	protected String res;
	//头像
	protected String icon;
	//等级
	protected int level;
	//BUFF列表
	protected List<Buff> buffs = new ArrayList<Buff>();
	//冷却列表
	protected HashMap<String, Cooldown> cooldowns= new  HashMap<String, Cooldown>();
	//战斗状态
	protected transient int fightState;
	//战斗伤害减免
	protected transient int reduce;
	//攻击
	//protected int attack;
	//防御
	//protected int defense;
	//暴击
	//protected int crit;
	//闪避
	//protected int dodge;
	//最大血量
	protected int maxHp;
	//最大魔法
	protected int maxMp;
	//最大体力
	protected int maxSp;
	//攻击速度
	protected int attackSpeed;
	//速度
	protected int speed;
	//幸运
	protected int luck;
	//无视防御
	private int negDefence;
	//弓箭几率
	private int arrowProbability;
	
	//计算战斗力用攻击
	protected transient int calattack;
	//计算战斗力用防御
	protected transient int caldefense;
	//计算战斗力用暴击
	protected transient int calcrit;	
	//计算战斗力用命中判定
	protected transient int calhit;//
	//计算战斗力用闪避
	protected transient int caldodge;
	//计算战斗力用最大血量
	protected transient int calmaxHp;
	//计算战斗力用最大魔法
	protected transient int calmaxMp;
	//计算战斗力用最大体力
	protected transient int calmaxSp;
	//计算战斗力用攻击速度
	protected transient int calattackSpeed;
	//计算战斗力用速度
	protected transient int calspeed;
	//计算战斗力用幸运
	protected transient int calluck;
	//需要计算战斗力的buff属性
	protected transient PlayerAttribute buffCalAttr = new PlayerAttribute();
	
	//计算无视防御
	private int calnegDefence;
	//计算弓箭几率
	private int calarrowProbability;
	
	//是否死亡
	protected transient boolean die;
	//经验加成
	protected transient int expMultiple;
	//真气加成
	protected transient int zhenQiMultiple;
	
	    //6个元素属性 冰。雷，毒，攻击和防御率 Ice. Ray, poison
	protected transient int ice_attack ;
	protected transient int ray_attack ;
	protected transient int poison_attack;	
	protected transient int ice_def ;
	protected transient int ray_def ;
	protected transient int poison_def ;
			
		//就是攻击成功率
	protected  int hit ;
	protected int hitPercent;// 废用
		//闪避  就是防御成功率
	protected  int dodge;
		//闪避 //防御成功率
	protected int dodgePercent;// 废用
		
        //暴击暂时不用（）
		protected  int crit ;
		protected  int critPercent;
		
		//*攻击  值//物理攻击，魔法攻击 // 最小，最大
		protected  int attack;
		protected  int physic_attackupper;
		protected  int physic_attacklower;
		protected  int magic_attackupper;
		protected  int magic_attacklower;
		
		
		//攻击 百分比//物理攻击，魔法攻击
		protected  int attackPercent;
		protected  int physic_attackPercent;
		protected  int magic_attackPercent;
		
		//*防御
		protected  int defense;
		//防御百分比
		protected  int defensePercent;
		
		 // 无视一击*:触发时无视目标的防御力 概率
		private int Ignore_attackPercent;

		// 会心一击*:触发时攻击力取值为最大攻击
		private int Knowing_attackPercent ;
		
		//卓越一击
		private int Perfect_attackPercent ;

		//加成卓越一击 *暴击 伤害加成值120% +%  
		private int Perfect_addattackPercent;
	// 触发会心一击后，伤害加成100%+x%
	private int Knowing_addattackPercent;

	    // 增加伤害
	   private int addInjure;
	   // 吸收伤害
	   private int reduceInjure;
			
		
		//进入  忽视防御百分比 的概率的机会
		protected  int ignore_defendPercent;
		//进入  忽视防御百分比 的概率的后， 忽视防御百分比的值
		protected int ignore_defend_add_Percent;

	// 生命回复 +x%
	private int hp_recover;
	// 伤害反射 -x%
	private int rebound_damage;

	// 杀怪掉出的金币增加 +x%
	private int addmoney_whenkill;
	// 杀怪时得到的生命值增加 + 生命/x
	private int addhp_whenkill;
	// 杀怪时得到的魔法值增加 + 魔法/x
	private int addmp_whenkill;


	/**
	 * @return the hp_recover
	 */
	public int getHp_recover() {
		return hp_recover;
	}

	/**
	 * @param hp_recover
	 *            the hp_recover to set
	 */
	public void setHp_recover(int hp_recover) {
		this.hp_recover = hp_recover;
	}

	/**
	 * @return the rebound_damage
	 */
	public int getRebound_damage() {
		return rebound_damage;
	}

	/**
	 * @param rebound_damage
	 *            the rebound_damage to set
	 */
	public void setRebound_damage(int rebound_damage) {
		this.rebound_damage = rebound_damage;
	}

	/**
	 * @return the addmoney_whenkill
	 */
	public int getAddmoney_whenkill() {
		return addmoney_whenkill;
	}

	/**
	 * @param addmoney_whenkill
	 *            the addmoney_whenkill to set
	 */
	public void setAddmoney_whenkill(int addmoney_whenkill) {
		this.addmoney_whenkill = addmoney_whenkill;
	}

	/**
	 * @return the addhp_whenkill
	 */
	public int getAddhp_whenkill() {
		return addhp_whenkill;
	}

	/**
	 * @param addhp_whenkill
	 *            the addhp_whenkill to set
	 */
	public void setAddhp_whenkill(int addhp_whenkill) {
		this.addhp_whenkill = addhp_whenkill;
	}

	/**
	 * @return the addmp_whenkill
	 */
	public int getAddmp_whenkill() {
		return addmp_whenkill;
	}

	/**
	 * @param addmp_whenkill
	 *            the addmp_whenkill to set
	 */
	public void setAddmp_whenkill(int addmp_whenkill) {
		this.addmp_whenkill = addmp_whenkill;
	}
		public int getIgnore_attackPercent(){
			return Ignore_attackPercent;
		}
		public void setIgnore_attackPercent(int ignore_attackPercent) {
			this.Ignore_attackPercent = ignore_attackPercent;
		}
		// 会心一击*:触发时攻击力取值为最大攻击
		public int getKnowing_attackPercent(){
			return Knowing_attackPercent;
		}
		public void setKnowing_attackPercent(int Knowing_attackPercent) {
			this.Knowing_attackPercent = Knowing_attackPercent;
		}

		//卓越一击*暴击是1.2倍
		public int getPerfect_attackPercent(){
			return Perfect_attackPercent;
		}
		public void setPerfect_attackPercent(int Perfect_attackPercent) {
			this.Perfect_attackPercent = Perfect_attackPercent;
		}
		public int getignore_defend_add_Percent() {
			return ignore_defend_add_Percent;
		}

		public void setignore_defend_add_Percent(int ignore_defend_add_Percent) {
			this.ignore_defend_add_Percent = ignore_defend_add_Percent;
		}
		
		public void setignore_defendPercent(int ignore_defendPercent) {
			this.ignore_defendPercent = ignore_defendPercent;
		}
		public int getignore_defendPercent() {
			return this.ignore_defendPercent;
		}
		public int getIce_attack() {
			return ice_attack;
		}

		public void setIce_attack(int ice_attack) {
			this.ice_attack = ice_attack;
		}

		public int getRay_attack() {
			return ray_attack;
		}

		public void setRay_attack(int ray_attack) {
			this.ray_attack = ray_attack;
		}

		public int getPoison_attack() {
			return poison_attack;
		}
		/**
		 * @return the addInjure
		 */
		public int getAddInjure() {
			return addInjure;
		}

		/**
		 * @param addInjure
		 *            the addInjure to set
		 */
		public void setAddInjure(int addInjure) {
			this.addInjure = addInjure;
		}

		/**
		 * @return the reduceInjure
		 */
		public int getReduceInjure() {
			return reduceInjure;
		}

		/**
		 * @param reduceInjure
		 *            the reduceInjure to set
		 */
		public void setReduceInjure(int reduceInjure) {
			this.reduceInjure = reduceInjure;
		}

		public void setPoison_attack(int poison_attack) {
			this.poison_attack = poison_attack;
		}

		public int getIce_def() {
			return ice_def;
		}

		public void setIce_def(int ice_def) {
			this.ice_def = ice_def;
		}

		public int getRay_def() {
			return ray_def;
		}

		public void setRay_def(int ray_def) {
			this.ray_def = ray_def;
		}

		public int getPoison_def() {
			return poison_def;
		}

		public void setPoison_def(int poison_def) {
			this.poison_def = poison_def;
		}

		public int getDodgePercent() {
			return dodgePercent;
		}

		public void setDodgePercent(int dodgePercent) {
			this.dodgePercent = dodgePercent;
		}

		public int getCritPercent() {
			return critPercent;
		}

		public void setCritPercent(int critPercent) {
			this.critPercent = critPercent;
		}

		public int getPhysic_attackupper() {
			return physic_attackupper;
		}

		public void setPhysic_attackupper(int physic_attackupper) {
			this.physic_attackupper = physic_attackupper;
		}

		public int getPhysic_attacklower() {
			return physic_attacklower;
		}

		public void setPhysic_attacklower(int physic_attacklower) {
			this.physic_attacklower = physic_attacklower;
		}

		public int getMagic_attackupper() {
			return magic_attackupper;
		}

		public void setMagic_attackupper(int magic_attackupper) {
			this.magic_attackupper = magic_attackupper;
		}

		public int getMagic_attacklower() {
			return magic_attacklower;
		}

		public void setMagic_attacklower(int magic_attacklower) {
			this.magic_attacklower = magic_attacklower;
		}

		public int getAttackPercent() {
			return attackPercent;
		}

		public void setAttackPercent(int attackPercent) {
			this.attackPercent = attackPercent;
		}

		public int getPhysic_attackPercent() {
			return physic_attackPercent;
		}

		public void setPhysic_attackPercent(int physic_attackPercent) {
			this.physic_attackPercent = physic_attackPercent;
		}

		public int getMagic_attackPercent() {
			return magic_attackPercent;
		}

		public void setMagic_attackPercent(int magic_attackPercent) {
			this.magic_attackPercent = magic_attackPercent;
		}

		public int getDefensePercent() {
			return defensePercent;
		}

		public void setDefensePercent(int defensePercent) {
			this.defensePercent = defensePercent;
		}


		
	/**
	 * 技能加成HashMap
	 * key为技能id（key为-1的时候为所有技能加成）
	 */
	protected transient HashMap<Integer, Integer> skillLevelUp = new HashMap<Integer, Integer>();

	abstract public int getServerId();
	
	public int getMap() {
		return map;
	}

	public void setMap(int map) {
		this.map = map;
	}

	public int getMapModelId() {
		return mapModelId;
	}

	public void setMapModelId(int mapModelId) {
		this.mapModelId = mapModelId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	
	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	public List<Position> getRoads() {
		return roads;
	}

	public void setRoads(List<Position> roads) {
		this.roads = roads;
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

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}

	public int getSp() {
		return sp;
	}

	public void setSp(int sp) {
		this.sp = sp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public List<Buff> getBuffs() {
		return buffs;
	}

	public void setBuffs(List<Buff> buffs) {
		this.buffs = buffs;
	}

	public HashMap<String, Cooldown> getCooldowns() {
		return cooldowns;
	}

	public void setCooldowns(HashMap<String, Cooldown> cooldowns) {
		this.cooldowns = cooldowns;
	}
	

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public int getDodge() {
		return dodge;
	}

	public void setDodge(int dodge) {
		this.dodge = dodge;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getMaxMp() {
		return maxMp;
	}

	public void setMaxMp(int maxMp) {
		this.maxMp = maxMp;
	}

	public int getMaxSp() {
		return maxSp;
	}

	public void setMaxSp(int maxSp) {
		this.maxSp = maxSp;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public void setAttackSpeed(int attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getLuck() {
		return luck;
	}

	public void setLuck(int luck) {
		this.luck = luck;
	}

	public int getCalattack() {
		return calattack;
	}

	public void setCalattack(int calattack) {
		this.calattack = calattack;
	}

	public int getCalattackSpeed() {
		return calattackSpeed;
	}

	public void setCalattackSpeed(int calattackSpeed) {
		this.calattackSpeed = calattackSpeed;
	}

	public int getCalcrit() {
		return calcrit;
	}

	public void setCalcrit(int calcrit) {
		this.calcrit = calcrit;
	}

	public int getCaldefense() {
		return caldefense;
	}

	public void setCaldefense(int caldefense) {
		this.caldefense = caldefense;
	}

	public int getCaldodge() {
		return caldodge;
	}

	public void setCaldodge(int caldodge) {
		this.caldodge = caldodge;
	}

	public int getCalluck() {
		return calluck;
	}

	public void setCalluck(int calluck) {
		this.calluck = calluck;
	}

	public int getCalmaxHp() {
		return calmaxHp;
	}

	public void setCalmaxHp(int calmaxHp) {
		this.calmaxHp = calmaxHp;
	}

	public int getCalmaxMp() {
		return calmaxMp;
	}

	public void setCalmaxMp(int calmaxMp) {
		this.calmaxMp = calmaxMp;
	}

	public int getCalmaxSp() {
		return calmaxSp;
	}

	public void setCalmaxSp(int calmaxSp) {
		this.calmaxSp = calmaxSp;
	}

	public int getCalspeed() {
		return calspeed;
	}

	public void setCalspeed(int calspeed) {
		this.calspeed = calspeed;
	}

	public int getExpMultiple() {
		return expMultiple;
	}

	public void setExpMultiple(int expMultiple) {
		this.expMultiple = expMultiple;
	}
	
	public int getFightState() {
		return fightState;
	}

	public void setFightState(int fightState) {
		this.fightState = fightState;
	}

	public void addFightState(FighterState state){
		this.fightState = this.fightState | state.getValue();
	}
	
	public void removeFightState(FighterState state){
		this.fightState = this.fightState & (~state.getValue());
	}

	public int getReduce() {
		return reduce;
	}

	public void setReduce(int reduce) {
		this.reduce = reduce;
	}

	public HashMap<Integer, Integer> getSkillLevelUp() {
		return skillLevelUp;
	}

	public void setSkillLevelUp(HashMap<Integer, Integer> skillLevelUp) {
		this.skillLevelUp = skillLevelUp;
	}

	public int getZhenQiMultiple() {
		return zhenQiMultiple;
	}

	public void setZhenQiMultiple(int zhenQiMultiple) {
		this.zhenQiMultiple = zhenQiMultiple;
	}

	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}

	public int getNegDefence() {
		return negDefence;
	}

	public void setNegDefence(int negDefence) {
		this.negDefence = negDefence;
	}

	public int getArrowProbability() {
		return arrowProbability;
	}

	public void setArrowProbability(int arrowProbability) {
		this.arrowProbability = arrowProbability;
	}

	public int getCalnegDefence() {
		return calnegDefence;
	}

	public void setCalnegDefence(int calnegDefence) {
		this.calnegDefence = calnegDefence;
	}

	public int getCalarrowProbability() {
		return calarrowProbability;
	}

	public void setCalarrowProbability(int calarrowProbability) {
		this.calarrowProbability = calarrowProbability;
	}

	public PlayerAttribute getBuffCalAttr() {
		return buffCalAttr;
	}

	public void setBuffCalAttr(PlayerAttribute buffCalAttr) {
		this.buffCalAttr = buffCalAttr;
	}
	public int getCalhit() {
		return calhit;
	}

	public void setCalhit(int calhit) {
		this.calhit = calhit;
	}

	public boolean isDie() {
		return die;
	}

	public void setDie(boolean die) {
		this.die = die;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int getHitPercent() {
		return hitPercent;
	}

	public void setHitPercent(int hitPercent) {
		this.hitPercent = hitPercent;
	}
	public int getPerfect_addattackPercent() {
		return Perfect_addattackPercent;
	}

	public void setPerfect_addattackPercent(int Perfect_addattackPercent) {
		this.Perfect_addattackPercent = Perfect_addattackPercent;
	}

	public int getKnowing_addattackPercent() {
		return Knowing_addattackPercent;
	}

	public void setKnowing_addattackPercent(int knowing_addattackPercent) {
		Knowing_addattackPercent = knowing_addattackPercent;
	}

}
