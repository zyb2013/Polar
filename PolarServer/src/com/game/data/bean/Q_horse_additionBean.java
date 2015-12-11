package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_horse_addition Bean
 */
public class Q_horse_additionBean {

	//坐骑编号= 阶数_等级
	private String q_id;
	
	//坐骑阶数
	private int q_layer;
	
	//坐骑等级
	private int q_level;
	
	//攻击
	private int q_attack;

	//最大物理攻击力加成
	private int horse_max_phyatk;
	
	//最小物理攻击力加成
	private int horse_min_phyatk;
	
	//最大魔法攻击力加成
	private int horse_max_magicatk;

	//最小魔法攻击力加成
	private int horse_min_magicatk;
	
	//防御
	private int q_defense;
	
	//无视防御（万分比）
	private int horse_ignoredefense;
	
	//无视防御产生的值（万分比）
	private int horse_ignore_add_defense;
	
	//卓越一击伤害（万分比）
	private int horse_perfectatk_addpercent;
	
	//暴击
	private int q_crit;
	
	//闪避
	private int q_dodge;
	
	//血量
	private int q_maxHp;
	
	//魔法值
	private int q_maxMp;
	
	//体力
	private int q_maxSp;
	
	//攻速
	private int q_attackSpeed;
	
	//移速
	private int q_speed;
	
	
	/**
	 * get 坐骑编号= 阶数_等级
	 * @return 
	 */
	public String getQ_id(){
		return q_id;
	}
	
	/**
	 * set 坐骑编号= 阶数_等级
	 */
	public void setQ_id(String q_id){
		this.q_id = q_id;
	}
	
	/**
	 * get 坐骑阶数
	 * @return 
	 */
	public int getQ_layer(){
		return q_layer;
	}
	
	/**
	 * set 坐骑阶数
	 */
	public void setQ_layer(int q_layer){
		this.q_layer = q_layer;
	}
	
	/**
	 * get 坐骑等级
	 * @return 
	 */
	public int getQ_level(){
		return q_level;
	}
	
	/**
	 * set 坐骑等级
	 */
	public void setQ_level(int q_level){
		this.q_level = q_level;
	}
	
	/**
	 * get 攻击
	 * @return 
	 */
	public int getQ_attack(){
		return q_attack;
	}
	
	/**
	 * set 攻击
	 */
	public void setQ_attack(int q_attack){
		this.q_attack = q_attack;
	}
	
	/**
	 * get 最大物理攻击力加成
	 * @return
	 */
	public int getHorse_max_phyatk() {
		return horse_max_phyatk;
	}

	/**
	 * set 最大物理攻击力加成
	 */
	public void setHorse_max_phyatk(int horse_max_phyatk) {
		this.horse_max_phyatk = horse_max_phyatk;
	}

	/**
	 * get 最小物理攻击力加成
	 * @return
	 */
	public int getHorse_min_phyatk() {
		return horse_min_phyatk;
	}

	/**
	 * set 最小物理攻击力加成
	 */
	public void setHorse_min_phyatk(int horse_min_phyatk) {
		this.horse_min_phyatk = horse_min_phyatk;
	}

	/**
	 * get 最大魔法攻击力加成
	 * @return
	 */
	public int getHorse_max_magicatk() {
		return horse_max_magicatk;
	}

	/**
	 * set 最大魔法攻击力加成
	 */
	public void setHorse_max_magicatk(int horse_max_magicatk) {
		this.horse_max_magicatk = horse_max_magicatk;
	}

	/**
	 * get 最小魔法攻击力加成
	 * @return
	 */
	public int getHorse_min_magicatk() {
		return horse_min_magicatk;
	}

	/**
	 * set 最小魔法攻击力加成
	 */
	public void setHorse_min_magicatk(int horse_min_magicatk) {
		this.horse_min_magicatk = horse_min_magicatk;
	}

	/**
	 * get 防御
	 * @return 
	 */
	public int getQ_defense(){
		return q_defense;
	}
	
	/**
	 * set 防御
	 */
	public void setQ_defense(int q_defense){
		this.q_defense = q_defense;
	}
	
	/**
	 * get 无视防御（万分比）
	 * @return
	 */
	public int getHorse_ignoredefense() {
		return horse_ignoredefense;
	}

	/**
	 * set 无视防御（万分比）
	 */
	public void setHorse_ignoredefense(int horse_ignoredefense) {
		this.horse_ignoredefense = horse_ignoredefense;
	}

	/**
	 * get 无视防御产生的值（万分比）
	 * @return
	 */
	public int getHorse_ignore_add_defense() {
		return horse_ignore_add_defense;
	}

	/**
	 * set 无视防御产生的值（万分比）
	 */
	public void setHorse_ignore_add_defense(int horse_ignore_add_defense) {
		this.horse_ignore_add_defense = horse_ignore_add_defense;
	}

	/**
	 * get 卓越一击伤害（万分比）
	 * @return
	 */
	public int getHorse_perfectatk_addpercent() {
		return horse_perfectatk_addpercent;
	}

	/**
	 * set 卓越一击伤害（万分比）
	 */
	public void setHorse_perfectatk_addpercent(int horse_perfectatk_addpercent) {
		this.horse_perfectatk_addpercent = horse_perfectatk_addpercent;
	}

	/**
	 * get 暴击
	 * @return 
	 */
	public int getQ_crit(){
		return q_crit;
	}
	
	/**
	 * set 暴击
	 */
	public void setQ_crit(int q_crit){
		this.q_crit = q_crit;
	}
	
	/**
	 * get 闪避
	 * @return 
	 */
	public int getQ_dodge(){
		return q_dodge;
	}
	
	/**
	 * set 闪避
	 */
	public void setQ_dodge(int q_dodge){
		this.q_dodge = q_dodge;
	}
	
	/**
	 * get 血量
	 * @return 
	 */
	public int getQ_maxHp(){
		return q_maxHp;
	}
	
	/**
	 * set 血量
	 */
	public void setQ_maxHp(int q_maxHp){
		this.q_maxHp = q_maxHp;
	}
	
	/**
	 * get 魔法值
	 * @return 
	 */
	public int getQ_maxMp(){
		return q_maxMp;
	}
	
	/**
	 * set 魔法值
	 */
	public void setQ_maxMp(int q_maxMp){
		this.q_maxMp = q_maxMp;
	}
	
	/**
	 * get 体力
	 * @return 
	 */
	public int getQ_maxSp(){
		return q_maxSp;
	}
	
	/**
	 * set 体力
	 */
	public void setQ_maxSp(int q_maxSp){
		this.q_maxSp = q_maxSp;
	}
	
	/**
	 * get 攻速
	 * @return 
	 */
	public int getQ_attackSpeed(){
		return q_attackSpeed;
	}
	
	/**
	 * set 攻速
	 */
	public void setQ_attackSpeed(int q_attackSpeed){
		this.q_attackSpeed = q_attackSpeed;
	}
	
	/**
	 * get 移速
	 * @return 
	 */
	public int getQ_speed(){
		return q_speed;
	}
	
	/**
	 * set 移速
	 */
	public void setQ_speed(int q_speed){
		this.q_speed = q_speed;
	}
	
}