package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_equip_append Bean
 * 
 */
public class Q_equip_appendBean {

	//物品ID
	private int q_id;
	
	//攻击力附加产生几率
	private int q_attack;
	
	//攻击力min
	private int q_attack_min;
	
	//攻击力max
	private int q_attack_max;
	
	//防御力附加产生几率
	private int q_defence;
	
	//防御力min
	private int q_defence_min;
	
	//防御力max
	private int q_defence_max;
	
	//暴击附加产生几率
	private int q_crit;
	
	//暴击min
	private int q_crit_min;
	
	//暴击max
	private int q_crit_max;
	
//	//闪避附加产生几率
//	private int q_dodge;
//	
//	//闪避min
//	private int q_dodge_min;
//	
//	//闪避max
//	private int q_dodge_max;
	
	//生命上限附加产生几率
	private int q_hp;
	
	//生命上限min
	private int q_hp_min;
	
	//生命上限max
	private int q_hp_max;
	
	//魔法值上限附加产生几率
	private int q_mp;
	
	//魔法值上限min
	private int q_mp_min;
	
	//魔法值上限max
	private int q_mp_max;
	
	//体力上限附加产生几率
	private int q_sp;
	
	//体力上限min
	private int q_sp_min;
	
	//体力上限max
	private int q_sp_max;
	
//	//攻速附加产生几率
//	private int q_attackspeed;
//	
//	//攻速min
//	private int q_attackspeed_min;
//	
//	//攻速max
//	private int q_attackspeed_max;
	
	//移速附加产生几率
	private int q_speed;
	
	//移速min
	private int q_speed_min;
	
	//移速max
	private int q_speed_max;
	
	//幸运附加产生几率
	private int q_luck;
	
	//幸运min
	private int q_luck_min;
	
	//幸运max
	private int q_luck_max;
	
	
	//生命回复 +4% 几率
	private int q_hp_recover;
	//最小回复
	private int q_hp_recover_min;
	//最大回复
	private int q_hp_recover_max;
	
	//魔法上限 +4% 几率
	private int q_add_mplimit;
	//最小魔法上限min
	private int q_add_mplimit_min;
	//魔法上限增加max
	private int q_add_mplimit_max;
	
	//生命上限 +4% 几率
	private int q_add_hp_limit;
	//生命上限增加min
	private int q_add_hp_limit_min;
	//生命上限增加max
	private int q_add_hp_limit_max;
	
	//伤害减少 -4% 几率
	private int q_reduce_damage;
	//伤害减少min
	private int q_reduce_damage_min;
	//伤害减少max
	private int q_reduce_damage_max;
	
	//伤害反射 -4% 几率
	private int q_rebound_damage;
	//伤害反射min
	private int q_rebound_damage_min;
	//伤害反射max
	private int q_rebound_damage_max;
	
	//防御成功率 +10% 几率
	private int q_dodge;
	//防御成功率min
	private int q_dodge_min;
	//防御成功率max
	private int q_dodge_max;
	
	//杀怪掉出的金币增加 +40% 几率
	private int q_add_money;
	//杀怪掉出的金币增加min
	private int q_add_money_min;
	//杀怪掉出的金币增加max
	private int q_add_money_max;
	
	// 卓越一击出现概率 增加 + 10% 几率
	private int q_remarkble_attack;
	// 卓越一击概率增加min
	private int q_remarkble_attack_min;
	// 卓越一击出现概率增加max
	private int q_remarkble_attack_max;
	
	// 攻击力增加 + (角色等级/（min~max）） 几率
	private int q_physicattack_bylevel;
	//攻击力增加min
	private int q_physicattack_bylevel_min;
	//攻击力增加max
	private int q_physicattack_bylevel_max;
	
	//物理攻击力增加 + 2%
	private int q_physicattack_Percent;
	//物理攻击力增加min
	private int q_physicattack_Percent_min;
	//物理攻击力增加max
	private int q_physicattack_Percent_max;
	
	//魔法攻击力增加 + (角色等级/20） 几率
	private int q_magicattack_bylevel;
	//魔法攻击力增加min
	private int q_magicattack_bylevel_min;
	//魔法攻击力增加max
	private int q_magicattack_bylevel_max;
	
	//魔法攻击力增加 + 2% 几率
	private int q_magicattack_Percent;
	//魔法攻击力增加min
	private int q_magicattack_Percent_min;
	//魔法攻击力增加max
	private int q_magicattack_Percent_max;
	
	//攻击速度 + 7 几率
	private int q_attackspeed;
	//攻击速度min
	private int q_attackspeed_min;
	//攻击速度max
	private int q_attackspeed_max;
	
	//杀怪时得到的生命值增加 + 生命/8 几率
	private int q_addhp_whenkill;
	//杀怪时得到的生命值增加min
	private int q_addhp_whenkill_min;
	//杀怪时得到的生命值增加max
	private int q_addhp_whenkill_max;
	
	//杀怪时得到的魔法值增加 + 魔法/8 几率
	private int q_addmp_whenkill;
	//杀怪时得到的魔法值增加min
	private int q_addmp_whenkill_min;
	//杀怪时得到的魔法值增加max
	private int q_addmp_whenkill_max;
	
	//随机最少属性个数
	private int attribute_min;
	//随机最多属性个数
	private int attribute_max;
	

	//攻击力增加 + (角色等级/20） 几率
	private int q_attack_bylevel;
	//攻击力增加min
	private int q_attack_bylevel_min;
	//攻击力增加max
	private int q_attack_bylevel_max;
		
	//攻击力增加 + 2% 几率
	private int q_attack_percent;
	//攻击力增加min
	private int q_attack_percent_min;
	//攻击力增加max
	private int q_attack_percent_max;
	
	
	
	public int getQ_attack_bylevel() {
		return q_attack_bylevel;
	}

	public void setQ_attack_bylevel(int q_attack_bylevel) {
		this.q_attack_bylevel = q_attack_bylevel;
	}

	public int getQ_attack_bylevel_min() {
		return q_attack_bylevel_min;
	}

	public void setQ_attack_bylevel_min(int q_attack_bylevel_min) {
		this.q_attack_bylevel_min = q_attack_bylevel_min;
	}

	public int getQ_attack_bylevel_max() {
		return q_attack_bylevel_max;
	}

	public void setQ_attack_bylevel_max(int q_attack_bylevel_max) {
		this.q_attack_bylevel_max = q_attack_bylevel_max;
	}

	public int getQ_attack_percent() {
		return q_attack_percent;
	}

	public void setQ_attack_percent(int q_attack_percent) {
		this.q_attack_percent = q_attack_percent;
	}

	public int getQ_attack_percent_min() {
		return q_attack_percent_min;
	}

	public void setQ_attack_percent_min(int q_attack_percent_min) {
		this.q_attack_percent_min = q_attack_percent_min;
	}

	public int getQ_attack_percent_max() {
		return q_attack_percent_max;
	}

	public void setQ_attack_percent_max(int q_attack_percent_max) {
		this.q_attack_percent_max = q_attack_percent_max;
	}

	public int getQ_hp_recover() {
		return q_hp_recover;
	}

	public void setQ_hp_recover(int q_hp_recover) {
		this.q_hp_recover = q_hp_recover;
	}

	public int getQ_hp_recover_min() {
		return q_hp_recover_min;
	}

	public void setQ_hp_recover_min(int q_hp_recover_min) {
		this.q_hp_recover_min = q_hp_recover_min;
	}

	public int getQ_hp_recover_max() {
		return q_hp_recover_max;
	}

	public void setQ_hp_recover_max(int q_hp_recover_max) {
		this.q_hp_recover_max = q_hp_recover_max;
	}

	public int getQ_add_mplimit() {
		return q_add_mplimit;
	}

	public void setQ_add_mplimit(int q_add_mplimit) {
		this.q_add_mplimit = q_add_mplimit;
	}

	public int getQ_add_mplimit_min() {
		return q_add_mplimit_min;
	}

	public void setQ_add_mplimit_min(int q_add_mplimit_min) {
		this.q_add_mplimit_min = q_add_mplimit_min;
	}

	public int getQ_add_mplimit_max() {
		return q_add_mplimit_max;
	}

	public void setQ_add_mplimit_max(int q_add_mplimit_max) {
		this.q_add_mplimit_max = q_add_mplimit_max;
	}

	public int getQ_add_hp_limit() {
		return q_add_hp_limit;
	}

	public void setQ_add_hp_limit(int q_add_hp_limit) {
		this.q_add_hp_limit = q_add_hp_limit;
	}

	public int getQ_add_hp_limit_min() {
		return q_add_hp_limit_min;
	}

	public void setQ_add_hp_limit_min(int q_add_hp_limit_min) {
		this.q_add_hp_limit_min = q_add_hp_limit_min;
	}

	public int getQ_add_hp_limit_max() {
		return q_add_hp_limit_max;
	}

	public void setQ_add_hp_limit_max(int q_add_hp_limit_max) {
		this.q_add_hp_limit_max = q_add_hp_limit_max;
	}

	public int getQ_reduce_damage() {
		return q_reduce_damage;
	}

	public void setQ_reduce_damage(int q_reduce_damage) {
		this.q_reduce_damage = q_reduce_damage;
	}

	public int getQ_reduce_damage_min() {
		return q_reduce_damage_min;
	}

	public void setQ_reduce_damage_min(int q_reduce_damage_min) {
		this.q_reduce_damage_min = q_reduce_damage_min;
	}

	public int getQ_reduce_damage_max() {
		return q_reduce_damage_max;
	}

	public void setQ_reduce_damage_max(int q_reduce_damage_max) {
		this.q_reduce_damage_max = q_reduce_damage_max;
	}

	public int getQ_rebound_damage() {
		return q_rebound_damage;
	}

	public void setQ_rebound_damage(int q_rebound_damage) {
		this.q_rebound_damage = q_rebound_damage;
	}

	public int getQ_rebound_damage_min() {
		return q_rebound_damage_min;
	}

	public void setQ_rebound_damage_min(int q_rebound_damage_min) {
		this.q_rebound_damage_min = q_rebound_damage_min;
	}

	public int getQ_rebound_damage_max() {
		return q_rebound_damage_max;
	}

	public void setQ_rebound_damage_max(int q_rebound_damage_max) {
		this.q_rebound_damage_max = q_rebound_damage_max;
	}

	public int getQ_add_money() {
		return q_add_money;
	}

	public void setQ_add_money(int q_add_money) {
		this.q_add_money = q_add_money;
	}

	public int getQ_add_money_min() {
		return q_add_money_min;
	}

	public void setQ_add_money_min(int q_add_money_min) {
		this.q_add_money_min = q_add_money_min;
	}

	public int getQ_add_money_max() {
		return q_add_money_max;
	}

	public void setQ_add_money_max(int q_add_money_max) {
		this.q_add_money_max = q_add_money_max;
	}

	public int getQ_remarkble_attack() {
		return q_remarkble_attack;
	}

	public void setQ_remarkble_attack(int q_remarkble_attack) {
		this.q_remarkble_attack = q_remarkble_attack;
	}

	public int getQ_remarkble_attack_min() {
		return q_remarkble_attack_min;
	}

	public void setQ_remarkble_attack_min(int q_remarkble_attack_min) {
		this.q_remarkble_attack_min = q_remarkble_attack_min;
	}

	public int getQ_remarkble_attack_max() {
		return q_remarkble_attack_max;
	}

	public void setQ_remarkble_attack_max(int q_remarkble_attack_max) {
		this.q_remarkble_attack_max = q_remarkble_attack_max;
	}

	public int getQ_physicattack_bylevel() {
		return q_physicattack_bylevel;
	}

	public void setQ_physicattack_bylevel(int q_physicattack_bylevel) {
		this.q_physicattack_bylevel = q_physicattack_bylevel;
	}

	public int getQ_physicattack_bylevel_min() {
		return q_physicattack_bylevel_min;
	}

	public void setQ_physicattack_bylevel_min(int q_physicattack_bylevel_min) {
		this.q_physicattack_bylevel_min = q_physicattack_bylevel_min;
	}

	public int getQ_physicattack_bylevel_max() {
		return q_physicattack_bylevel_max;
	}

	public void setQ_physicattack_bylevel_max(int q_physicattack_bylevel_max) {
		this.q_physicattack_bylevel_max = q_physicattack_bylevel_max;
	}

	public int getQ_physicattack_Percent() {
		return q_physicattack_Percent;
	}

	public void setQ_physicattack_Percent(int q_physicattack_Percent) {
		this.q_physicattack_Percent = q_physicattack_Percent;
	}

	public int getQ_physicattack_Percent_min() {
		return q_physicattack_Percent_min;
	}

	public void setQ_physicattack_Percent_min(int q_physicattack_Percent_min) {
		this.q_physicattack_Percent_min = q_physicattack_Percent_min;
	}

	public int getQ_physicattack_Percent_max() {
		return q_physicattack_Percent_max;
	}

	public void setQ_physicattack_Percent_max(int q_physicattack_Percent_max) {
		this.q_physicattack_Percent_max = q_physicattack_Percent_max;
	}

	public int getQ_magicattack_bylevel() {
		return q_magicattack_bylevel;
	}

	public void setQ_magicattack_bylevel(int q_magicattack_bylevel) {
		this.q_magicattack_bylevel = q_magicattack_bylevel;
	}

	public int getQ_magicattack_bylevel_min() {
		return q_magicattack_bylevel_min;
	}

	public void setQ_magicattack_bylevel_min(int q_magicattack_bylevel_min) {
		this.q_magicattack_bylevel_min = q_magicattack_bylevel_min;
	}

	public int getQ_magicattack_bylevel_max() {
		return q_magicattack_bylevel_max;
	}

	public void setQ_magicattack_bylevel_max(int q_magicattack_bylevel_max) {
		this.q_magicattack_bylevel_max = q_magicattack_bylevel_max;
	}

	public int getQ_magicattack_Percent() {
		return q_magicattack_Percent;
	}

	public void setQ_magicattack_Percent(int q_magicattack_Percent) {
		this.q_magicattack_Percent = q_magicattack_Percent;
	}

	public int getQ_magicattack_Percent_min() {
		return q_magicattack_Percent_min;
	}

	public void setQ_magicattack_Percent_min(int q_magicattack_Percent_min) {
		this.q_magicattack_Percent_min = q_magicattack_Percent_min;
	}

	public int getQ_magicattack_Percent_max() {
		return q_magicattack_Percent_max;
	}

	public void setQ_magicattack_Percent_max(int q_magicattack_Percent_max) {
		this.q_magicattack_Percent_max = q_magicattack_Percent_max;
	}

	public int getQ_addhp_whenkill() {
		return q_addhp_whenkill;
	}

	public void setQ_addhp_whenkill(int q_addhp_whenkill) {
		this.q_addhp_whenkill = q_addhp_whenkill;
	}

	public int getQ_addhp_whenkill_min() {
		return q_addhp_whenkill_min;
	}

	public void setQ_addhp_whenkill_min(int q_addhp_whenkill_min) {
		this.q_addhp_whenkill_min = q_addhp_whenkill_min;
	}

	public int getQ_addhp_whenkill_max() {
		return q_addhp_whenkill_max;
	}

	public void setQ_addhp_whenkill_max(int q_addhp_whenkill_max) {
		this.q_addhp_whenkill_max = q_addhp_whenkill_max;
	}

	public int getQ_addmp_whenkill() {
		return q_addmp_whenkill;
	}

	public void setQ_addmp_whenkill(int q_addmp_whenkill) {
		this.q_addmp_whenkill = q_addmp_whenkill;
	}

	public int getQ_addmp_whenkill_min() {
		return q_addmp_whenkill_min;
	}

	public void setQ_addmp_whenkill_min(int q_addmp_whenkill_min) {
		this.q_addmp_whenkill_min = q_addmp_whenkill_min;
	}

	public int getQ_addmp_whenkill_max() {
		return q_addmp_whenkill_max;
	}

	public void setQ_addmp_whenkill_max(int q_addmp_whenkill_max) {
		this.q_addmp_whenkill_max = q_addmp_whenkill_max;
	}

	/**
	 * get 物品ID
	 * @return 
	 */
	public int getQ_id(){
		return q_id;
	}
	
	/**
	 * set 物品ID
	 */
	public void setQ_id(int q_id){
		this.q_id = q_id;
	}
	
	/**
	 * get 攻击力附加产生几率
	 * @return 
	 */
	public int getQ_attack(){
		return q_attack;
	}
	
	/**
	 * set 攻击力附加产生几率
	 */
	public void setQ_attack(int q_attack){
		this.q_attack = q_attack;
	}
	
	/**
	 * get 攻击力min
	 * @return 
	 */
	public int getQ_attack_min(){
		return q_attack_min;
	}
	
	/**
	 * set 攻击力min
	 */
	public void setQ_attack_min(int q_attack_min){
		this.q_attack_min = q_attack_min;
	}
	
	/**
	 * get 攻击力max
	 * @return 
	 */
	public int getQ_attack_max(){
		return q_attack_max;
	}
	
	/**
	 * set 攻击力max
	 */
	public void setQ_attack_max(int q_attack_max){
		this.q_attack_max = q_attack_max;
	}
	
	/**
	 * get 防御力附加产生几率
	 * @return 
	 */
	public int getQ_defence(){
		return q_defence;
	}
	
	/**
	 * set 防御力附加产生几率
	 */
	public void setQ_defence(int q_defence){
		this.q_defence = q_defence;
	}
	
	/**
	 * get 防御力min
	 * @return 
	 */
	public int getQ_defence_min(){
		return q_defence_min;
	}
	
	/**
	 * set 防御力min
	 */
	public void setQ_defence_min(int q_defence_min){
		this.q_defence_min = q_defence_min;
	}
	
	/**
	 * get 防御力max
	 * @return 
	 */
	public int getQ_defence_max(){
		return q_defence_max;
	}
	
	/**
	 * set 防御力max
	 */
	public void setQ_defence_max(int q_defence_max){
		this.q_defence_max = q_defence_max;
	}
	
	/**
	 * get 暴击附加产生几率
	 * @return 
	 */
	public int getQ_crit(){
		return q_crit;
	}
	
	/**
	 * set 暴击附加产生几率
	 */
	public void setQ_crit(int q_crit){
		this.q_crit = q_crit;
	}
	
	/**
	 * get 暴击min
	 * @return 
	 */
	public int getQ_crit_min(){
		return q_crit_min;
	}
	
	/**
	 * set 暴击min
	 */
	public void setQ_crit_min(int q_crit_min){
		this.q_crit_min = q_crit_min;
	}
	
	/**
	 * get 暴击max
	 * @return 
	 */
	public int getQ_crit_max(){
		return q_crit_max;
	}
	
	/**
	 * set 暴击max
	 */
	public void setQ_crit_max(int q_crit_max){
		this.q_crit_max = q_crit_max;
	}
	
	/**
	 * get 闪避附加产生几率
	 * @return 
	 */
	public int getQ_dodge(){
		return q_dodge;
	}
	
	/**
	 * set 闪避附加产生几率
	 */
	public void setQ_dodge(int q_dodge){
		this.q_dodge = q_dodge;
	}
	
	/**
	 * get 闪避min
	 * @return 
	 */
	public int getQ_dodge_min(){
		return q_dodge_min;
	}
	
	/**
	 * set 闪避min
	 */
	public void setQ_dodge_min(int q_dodge_min){
		this.q_dodge_min = q_dodge_min;
	}
	
	/**
	 * get 闪避max
	 * @return 
	 */
	public int getQ_dodge_max(){
		return q_dodge_max;
	}
	
	/**
	 * set 闪避max
	 */
	public void setQ_dodge_max(int q_dodge_max){
		this.q_dodge_max = q_dodge_max;
	}
	
	/**
	 * get 生命上限附加产生几率
	 * @return 
	 */
	public int getQ_hp(){
		return q_hp;
	}
	
	/**
	 * set 生命上限附加产生几率
	 */
	public void setQ_hp(int q_hp){
		this.q_hp = q_hp;
	}
	
	/**
	 * get 生命上限min
	 * @return 
	 */
	public int getQ_hp_min(){
		return q_hp_min;
	}
	
	/**
	 * set 生命上限min
	 */
	public void setQ_hp_min(int q_hp_min){
		this.q_hp_min = q_hp_min;
	}
	
	/**
	 * get 生命上限max
	 * @return 
	 */
	public int getQ_hp_max(){
		return q_hp_max;
	}
	
	/**
	 * set 生命上限max
	 */
	public void setQ_hp_max(int q_hp_max){
		this.q_hp_max = q_hp_max;
	}
	
	/**
	 * get 魔法值上限附加产生几率
	 * @return 
	 */
	public int getQ_mp(){
		return q_mp;
	}
	
	/**
	 * set 魔法值上限附加产生几率
	 */
	public void setQ_mp(int q_mp){
		this.q_mp = q_mp;
	}
	
	/**
	 * get 魔法值上限min
	 * @return 
	 */
	public int getQ_mp_min(){
		return q_mp_min;
	}
	
	/**
	 * set 魔法值上限min
	 */
	public void setQ_mp_min(int q_mp_min){
		this.q_mp_min = q_mp_min;
	}
	
	/**
	 * get 魔法值上限max
	 * @return 
	 */
	public int getQ_mp_max(){
		return q_mp_max;
	}
	
	/**
	 * set 魔法值上限max
	 */
	public void setQ_mp_max(int q_mp_max){
		this.q_mp_max = q_mp_max;
	}
	
	/**
	 * get 体力上限附加产生几率
	 * @return 
	 */
	public int getQ_sp(){
		return q_sp;
	}
	
	/**
	 * set 体力上限附加产生几率
	 */
	public void setQ_sp(int q_sp){
		this.q_sp = q_sp;
	}
	
	/**
	 * get 体力上限min
	 * @return 
	 */
	public int getQ_sp_min(){
		return q_sp_min;
	}
	
	/**
	 * set 体力上限min
	 */
	public void setQ_sp_min(int q_sp_min){
		this.q_sp_min = q_sp_min;
	}
	
	/**
	 * get 体力上限max
	 * @return 
	 */
	public int getQ_sp_max(){
		return q_sp_max;
	}
	
	/**
	 * set 体力上限max
	 */
	public void setQ_sp_max(int q_sp_max){
		this.q_sp_max = q_sp_max;
	}
	
	/**
	 * get 攻速附加产生几率
	 * @return 
	 */
	public int getQ_attackspeed(){
		return q_attackspeed;
	}
	
	/**
	 * set 攻速附加产生几率
	 */
	public void setQ_attackspeed(int q_attackspeed){
		this.q_attackspeed = q_attackspeed;
	}
	
	/**
	 * get 攻速min
	 * @return 
	 */
	public int getQ_attackspeed_min(){
		return q_attackspeed_min;
	}
	
	/**
	 * set 攻速min
	 */
	public void setQ_attackspeed_min(int q_attackspeed_min){
		this.q_attackspeed_min = q_attackspeed_min;
	}
	
	/**
	 * get 攻速max
	 * @return 
	 */
	public int getQ_attackspeed_max(){
		return q_attackspeed_max;
	}
	
	/**
	 * set 攻速max
	 */
	public void setQ_attackspeed_max(int q_attackspeed_max){
		this.q_attackspeed_max = q_attackspeed_max;
	}
	
	/**
	 * get 移速附加产生几率
	 * @return 
	 */
	public int getQ_speed(){
		return q_speed;
	}
	
	/**
	 * set 移速附加产生几率
	 */
	public void setQ_speed(int q_speed){
		this.q_speed = q_speed;
	}
	
	/**
	 * get 移速min
	 * @return 
	 */
	public int getQ_speed_min(){
		return q_speed_min;
	}
	
	/**
	 * set 移速min
	 */
	public void setQ_speed_min(int q_speed_min){
		this.q_speed_min = q_speed_min;
	}
	
	/**
	 * get 移速max
	 * @return 
	 */
	public int getQ_speed_max(){
		return q_speed_max;
	}
	
	/**
	 * set 移速max
	 */
	public void setQ_speed_max(int q_speed_max){
		this.q_speed_max = q_speed_max;
	}
	
	/**
	 * get 幸运附加产生几率
	 * @return 
	 */
	public int getQ_luck(){
		return q_luck;
	}
	
	/**
	 * set 幸运附加产生几率
	 */
	public void setQ_luck(int q_luck){
		this.q_luck = q_luck;
	}
	
	/**
	 * get 幸运min
	 * @return 
	 */
	public int getQ_luck_min(){
		return q_luck_min;
	}
	
	/**
	 * set 幸运min
	 */
	public void setQ_luck_min(int q_luck_min){
		this.q_luck_min = q_luck_min;
	}
	
	/**
	 * get 幸运max
	 * @return 
	 */
	public int getQ_luck_max(){
		return q_luck_max;
	}
	
	/**
	 * set 幸运max
	 */
	public void setQ_luck_max(int q_luck_max){
		this.q_luck_max = q_luck_max;
	}

	/**
	 * @return the attribute_min
	 */
	public int getAttribute_min() {
		return attribute_min;
	}

	/**
	 * @param attribute_min the attribute_min to set
	 */
	public void setAttribute_min(int attribute_min) {
		this.attribute_min = attribute_min;
	}

	/**
	 * @return the attribute_max
	 */
	public int getAttribute_max() {
		return attribute_max;
	}

	/**
	 * @param attribute_max the attribute_max to set
	 */
	public void setAttribute_max(int attribute_max) {
		this.attribute_max = attribute_max;
	}
	
}