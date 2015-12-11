package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_vip Bean
 */
public class Q_vipBean {

	// vip等级
	private int q_level;

	// 名字
	private String q_name;

	// 描述
	private String q_desc;

	// 升级需要经验
	private int q_exp;

	// 交易税比例,万分比
	private int q_transaction;

	// 是否可以循环挂机：0不可以，1可以
	private int q_hangUp;
	
	// 使用远程商店需要消耗的钻石数(无vip是2，其他是0)
	private int q_shop;

	// 使用远程仓库需要消耗的钻石数(无vip是2，其他是0)
	private int q_storage;

	// 是否可以使用聊天表情：0不可以，1可以
	private int q_chat;

	// 恶魔广场/血色城堡 可增加次数
	private int q_devil;

	// 是否可以签到补签：0不可以，1可以
	private int q_signIn;

	// 活跃值加成点数
	private int q_active;

	// 副本翻牌免费次数
	private int q_lottery;

	// 恶魔广场、血色城堡是否可以使用高级鼓舞功能：0不可以，1可以
	private int q_inspire;

	// 是否可以进入vip商城：0不可以，1可以
	private int q_store;

	// 是否可以免费传送：0不可以，1可以
	private int q_transfer;

	// 是否可以钻石提升坐骑：0不可以，1可以
	private int q_horse;

	// 是否拥有高级强化功能：0不可以，1可以
	private int q_strength;

	// 杀怪加成经验，万分比
	private int q_monsterExp;

	// 多种属性加成，格式：[生命上限:X,魔法值上限:X,攻击:X,防御:X]
	private String q_attribute;

	// 增加祈愿次数
	private int q_pray;
	
	// 增加爬塔次数
	private int q_pt;
	
	//高级坐骑强化：0不可以，1可以
	private int q_advanced_horse_strengthen;
	
	//超级坐骑强化：0不可以，1可以
	private int q_super_horse_strengthen;

	public int getQ_pray() {
		return q_pray;
	}

	public void setQ_pray(int q_pray) {
		this.q_pray = q_pray;
	}

	public int getQ_level() {
		return q_level;
	}

	public void setQ_level(int q_level) {
		this.q_level = q_level;
	}

	public String getQ_name() {
		return q_name;
	}

	public void setQ_name(String q_name) {
		this.q_name = q_name;
	}

	public String getQ_desc() {
		return q_desc;
	}

	public void setQ_desc(String q_desc) {
		this.q_desc = q_desc;
	}

	public int getQ_exp() {
		return q_exp;
	}

	public void setQ_exp(int q_exp) {
		this.q_exp = q_exp;
	}

	public int getQ_transaction() {
		return q_transaction;
	}

	public void setQ_transaction(int q_transaction) {
		this.q_transaction = q_transaction;
	}

	public int getQ_hangUp() {
		return q_hangUp;
	}

	public void setQ_hangUp(int q_hangUp) {
		this.q_hangUp = q_hangUp;
	}

	public int getQ_shop() {
		return q_shop;
	}

	public void setQ_shop(int q_shop) {
		this.q_shop = q_shop;
	}

	public int getQ_storage() {
		return q_storage;
	}

	public void setQ_storage(int q_storage) {
		this.q_storage = q_storage;
	}

	public int getQ_chat() {
		return q_chat;
	}

	public void setQ_chat(int q_chat) {
		this.q_chat = q_chat;
	}

	public int getQ_devil() {
		return q_devil;
	}

	public void setQ_devil(int q_devil) {
		this.q_devil = q_devil;
	}

	public int getQ_signIn() {
		return q_signIn;
	}

	public void setQ_signIn(int q_signIn) {
		this.q_signIn = q_signIn;
	}

	public int getQ_active() {
		return q_active;
	}

	public void setQ_active(int q_active) {
		this.q_active = q_active;
	}

	public int getQ_lottery() {
		return q_lottery;
	}

	public void setQ_lottery(int q_lottery) {
		this.q_lottery = q_lottery;
	}

	public int getQ_inspire() {
		return q_inspire;
	}

	public void setQ_inspire(int q_inspire) {
		this.q_inspire = q_inspire;
	}

	public int getQ_store() {
		return q_store;
	}

	public void setQ_store(int q_store) {
		this.q_store = q_store;
	}

	public int getQ_transfer() {
		return q_transfer;
	}

	public void setQ_transfer(int q_transfer) {
		this.q_transfer = q_transfer;
	}

	public int getQ_horse() {
		return q_horse;
	}

	public void setQ_horse(int q_horse) {
		this.q_horse = q_horse;
	}

	public int getQ_strength() {
		return q_strength;
	}

	public void setQ_strength(int q_strength) {
		this.q_strength = q_strength;
	}

	public int getQ_monsterExp() {
		return q_monsterExp;
	}

	public void setQ_monsterExp(int q_monsterExp) {
		this.q_monsterExp = q_monsterExp;
	}

	public String getQ_attribute() {
		return q_attribute;
	}

	public void setQ_attribute(String q_attribute) {
		this.q_attribute = q_attribute;
	}

	public int getQ_pt() {
		return q_pt;
	}

	public void setQ_pt(int q_pt) {
		this.q_pt = q_pt;
	}

	public int getQ_advanced_horse_strengthen() {
		return q_advanced_horse_strengthen;
	}

	public void setQ_advanced_horse_strengthen(int q_advanced_horse_strengthen) {
		this.q_advanced_horse_strengthen = q_advanced_horse_strengthen;
	}

	public int getQ_super_horse_strengthen() {
		return q_super_horse_strengthen;
	}

	public void setQ_super_horse_strengthen(int q_super_horse_strengthen) {
		this.q_super_horse_strengthen = q_super_horse_strengthen;
	}
	
}