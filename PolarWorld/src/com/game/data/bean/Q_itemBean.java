package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_item Bean
 */
public class Q_itemBean {

	//物品ID
	private int q_id;
	
	//物品名称(最大九个汉字。支持_后缀，后缀部分在客户端不予显示)（物品名称需要唯一，以便在爆率表中使用汉字）
	private String q_name;
	
	// 物品类型 ItemTypeConst
	private int q_type;
	
	//佩戴性别需求（0全性别通用，1男，2女，）
	private int q_sex;
	
	//绑定类型（0不绑定；1获得时绑定；2使用后绑定）
	private int q_bind;
	
	//最大叠加数量（0或1均为不可叠加，最大可以填9999）
	private int q_max;
	
	//商店中的购买价格
	private int q_buy_price;
	
	//商店是否回收（0不回收，1回收）
	private int q_sell;
	
	//商店回收价格
	private int q_sell_price;
	
	//回收时是否弹出二次确认面板（0不弹出，1弹出）
	private int q_sell_confirm;
	
	//是否可丢弃（0不可丢弃，1可丢弃）
	private int q_drop;
	
	//丢弃时是否弹出二次确认面板（0不弹出，1弹出）
	private int q_drop_confirm;
	
	//是否允许被拖放至物品快捷栏（0不允许，1允许）
	private int q_shortcut;
	
	//【左手武器，右手武器，衣服，头盔，项链，手套，裤子，鞋子，左戒指，右戒指，宠物，翅膀，坐骑】
	//佩戴部位
	private int q_kind;
	
	//使用等级需求
	private int q_level;
	
	//装备最大可强化等级
	private int q_max_strengthen;
	
	//装备最大可镶嵌宝石数量
	private int q_max_inlay;
	
	//基础攻击力
	private int q_attack;
	
	//基础防御力
	private int q_defence;
	
	//基础暴击值
	private int q_crit;
	
	
	//基础生命上限
	private int q_max_hp;
	
	//基础体力上限
	private int q_max_sp;
	
	//基础攻击速度
	private int q_attackspeed;
	
	//基础移动速度
	private int q_speed;
	
	//基础幸运值
	private int q_luck;
	
	//使用后关联BUFF编号列表（格式：BUFF编号;BUFF编号;BUFF编号）
	private String q_buff;
	
	//药品使用冷却时间（单位：毫秒）
	private int q_cooldown;
	
	//药品公共冷却层级
	private int q_cooldown_level;
	
	//公共冷却时间
	private int q_cooldown_type;
	
	//传送卷轴类型（1为回城卷，2为随即传送卷，3为地图定点传送卷，4为行会回城卷）
	private int q_transfer;
	
	//传送地图ID
	private int q_transfer_map;
	
	//传送地图坐标X,Y
	private String q_transfer_position;
	
	//使用后触发任务脚本编号
	private int q_task;
	
	//使用后触发脚本编号
	private int q_script;
	
	//使用后学会技能编号
	private int q_skill;
	
	//使用后打开礼包表编号
	private int q_gift;
	
	//使用后打开面板编号
	private int q_ui;
	
	//怪物爆出件数上限阀值（填0为不限制）（格式：件数/天数）例如：2/3 是指：每3天最多爆出2件
	private String q_max_create;
	
	//掉出时发送的公告类型（0不发送公告；1面向个人发送聊天框内公告；2面向个人发送聊天框内与屏幕上方双重公告；3面向全服发送聊天框内公告；4面向全服发送聊天框内与屏幕上方双重公告）
	private int q_notice;
	
	//是否记录产出与操作日志（0不记录；1记录）
	private int q_log;
	
	//物品天生的名字颜色（0白色，1黄色，2绿色，3蓝色，4紫色，5橙色）(讨伐卷轴根据颜色编号关联任务)
	private int q_default;
	
	//物品ICO上附加的光效资源编号
	private String q_append;
	
	//物品描述信息（本处需要支持html标记）
	private String q_describe;
	
	//物品微图标资源编号
	private int q_tiny_icon;
	
	//物品换装资源编号（装备类物品使用）
	private String q_equip_resource;
	
	//物品掉出时播放的音效资源编号
	private String q_drop_music;
	
	//物品使用时播放的音效资源编号
	private String q_use_music;
	
	//物品脱下时播放的音效资源编号
	private String q_unuse_music;
	
	// 1恢复生命值,2恢复魔法值,3恢复体力值,4增加经验,5增加真气储量,6增加等级,7增加攻击力,8增加防御力,9增加暴击,10增加闪避,11增加攻击速度,12增加移动速度,13增加生命上限值,14增加魔法值上限值,15增加体力上限值,16增加坐骑经验值，17解除冰冻效果，18解除中毒效果 19力量加成 20敏捷加成 21体力加成 22智力加成
	// 23分配点增加 24全部点数清洗 格式示例[1,2,4]
	private String q_effict_type;
	
	//是否自动使用（1是，0否）
	private int q_auto_use;
	
	//道具是否可批量使用(0否，1是)
	private int q_whether_batch;
	
	//道具是否可进入仓库（1否，0是）
	private int q_save_warehouse;
	
	//道具tips中的天生颜色
	private int q_tips_effect;
	
	//物品使用上限
	private int q_item_limit;
	
	// 装备等级，计算战斗力用
	private int q_equip_level;
	
	
	private int q_dodge;//基础防御成功率值
	private int q_max_mp;//基础魔法上限
//	private int q_large_icon;//物品大图标资源编号
//	private int q_equip_icon;//装备穿戴状态图标编号
//	private int q_drop_animation;//物品掉落动画资源编号
	private String q_effict_value;//永久性药效值
	private String q_effict_value_Percent;//百分比永久性药效值
	private int q_can_die_drop;//死亡是否掉落
	private int q_die_drop_probability;//死亡掉落几率
	private int q_can_break_moving;//使用是否打断寻路
	private int q_effective_time;//物品有效时间，分钟
	private int q_when_noeffective;//物品有效时间到期是否消失
	private int q_use_confirm;//物品使用是否弹出提示框
	private String q_job_limit;// 使用职业需求,三位数 0通用，100是只有战士能用 110是战士法师都能用
							// 111是战士法师弓箭手都能用 011（11）法师弓箭手能用。
	private int q_str_limit;//所需力量点数
	private int q_dex_limit;//所需敏捷点数
	private int q_int_limit;//所需智力点数
	private int q_vit_limit;//所需体力点数
	private int q_max_physicattack;//物理最大攻击力
	private int q_min_physicattack;//物理最小攻击力
	private int q_max_magicattack;//魔法最大攻击力
	private int q_min_magicattack;//魔法最小攻击力
	private int q_physicattack_Percent;//物理攻击力百分比
	private int q_magicattack_Percent;//魔法攻击力百分比
	private String  q_element_defence;//元素属性抗性
	private String q_element_attack;//元素属性攻击
	private int q_hit;//攻击成功值
	private int q_attach_skill;//附带技能编号
	private int q_quality_lv;//物品品质
	private int q_remarkable;//是否卓越0否，1是 
	private int q_max_damage_hit;//会心一击率
	private int q_secondary_type;// 物品类型次级编号，在物品大种类的划分下，再次进行细致的二次划分，没有便填0；（装备：是单双手。1：单手，2：双手）（药品：1只能在战盟使用）
	private int q_third_type;//三级分类如：刀，盾牌，法杖。。。等
	
	private int q_add_injure;// 增加伤害
	private int q_reduce_injure;// 吸收伤害
	private int q_max_damage_percent;// 会心一击生成概率（物品生成时触发）

	private int q_ignore_attack_percent;// 无视一击生成概率（物品生成时触发）
	private int q_ignore_attack;// 战斗时无视一击触发概率

	public int getQ_secondary_type() {
		return q_secondary_type;
	}

	public void setQ_secondary_type(int q_secondary_type) {
		this.q_secondary_type = q_secondary_type;
	}

	public int getQ_third_type() {
		return q_third_type;
	}

	public void setQ_third_type(int q_third_type) {
		this.q_third_type = q_third_type;
	}

	public int getQ_remarkable() {
		return q_remarkable;
	}

	public void setQ_remarkable(int q_remarkable) {
		this.q_remarkable = q_remarkable;
	}

	public int getQ_max_damage_hit() {
		return q_max_damage_hit;
	}

	public void setQ_max_damage_hit(int q_max_damage_hit) {
		this.q_max_damage_hit = q_max_damage_hit;
	}

	public String getQ_effict_value() {
		return q_effict_value;
	}

	public void setQ_effict_value(String q_effict_value) {
		this.q_effict_value = q_effict_value;
	}

	public String getQ_effict_value_Percent() {
		return q_effict_value_Percent;
	}

	public void setQ_effict_value_Percent(String q_effict_value_Percent) {
		this.q_effict_value_Percent = q_effict_value_Percent;
	}

	public int getQ_can_die_drop() {
		return q_can_die_drop;
	}

	public void setQ_can_die_drop(int q_can_die_drop) {
		this.q_can_die_drop = q_can_die_drop;
	}

	public int getQ_die_drop_probability() {
		return q_die_drop_probability;
	}

	public void setQ_die_drop_probability(int q_die_drop_probability) {
		this.q_die_drop_probability = q_die_drop_probability;
	}

	public int getQ_can_break_moving() {
		return q_can_break_moving;
	}

	public void setQ_can_break_moving(int q_can_break_moving) {
		this.q_can_break_moving = q_can_break_moving;
	}

	public int getQ_effective_time() {
		return q_effective_time;
	}

	public void setQ_effective_time(int q_effective_time) {
		this.q_effective_time = q_effective_time;
	}

	public int getQ_when_noeffective() {
		return q_when_noeffective;
	}

	public void setQ_when_noeffective(int q_when_noeffective) {
		this.q_when_noeffective = q_when_noeffective;
	}

	public int getQ_use_confirm() {
		return q_use_confirm;
	}

	public void setQ_use_confirm(int q_use_confirm) {
		this.q_use_confirm = q_use_confirm;
	}

	public String getQ_job_limit() {
		return q_job_limit;
	}

	public void setQ_job_limit(String q_job_limit) {
		this.q_job_limit = q_job_limit;
	}

	public int getQ_str_limit() {
		return q_str_limit;
	}

	public void setQ_str_limit(int q_str_limit) {
		this.q_str_limit = q_str_limit;
	}

	public int getQ_dex_limit() {
		return q_dex_limit;
	}

	public void setQ_dex_limit(int q_dex_limit) {
		this.q_dex_limit = q_dex_limit;
	}

	public int getQ_int_limit() {
		return q_int_limit;
	}

	public void setQ_int_limit(int q_int_limit) {
		this.q_int_limit = q_int_limit;
	}

	public int getQ_vit_limit() {
		return q_vit_limit;
	}

	public void setQ_vit_limit(int q_vit_limit) {
		this.q_vit_limit = q_vit_limit;
	}

	public int getQ_max_physicattack() {
		return q_max_physicattack;
	}

	public void setQ_max_physicattack(int q_max_physicattack) {
		this.q_max_physicattack = q_max_physicattack;
	}

	public int getQ_min_physicattack() {
		return q_min_physicattack;
	}

	public void setQ_min_physicattack(int q_min_physicattack) {
		this.q_min_physicattack = q_min_physicattack;
	}

	public int getQ_max_magicattack() {
		return q_max_magicattack;
	}

	public void setQ_max_magicattack(int q_max_magicattack) {
		this.q_max_magicattack = q_max_magicattack;
	}

	public int getQ_min_magicattack() {
		return q_min_magicattack;
	}

	public void setQ_min_magicattack(int q_min_magicattack) {
		this.q_min_magicattack = q_min_magicattack;
	}

	public int getQ_physicattack_Percent() {
		return q_physicattack_Percent;
	}

	public void setQ_physicattack_Percent(int q_physicattack_Percent) {
		this.q_physicattack_Percent = q_physicattack_Percent;
	}

	public int getQ_magicattack_Percent() {
		return q_magicattack_Percent;
	}

	public void setQ_magicattack_Percent(int q_magicattack_Percent) {
		this.q_magicattack_Percent = q_magicattack_Percent;
	}
	

	public String getQ_element_defence() {
		return q_element_defence;
	}

	public void setQ_element_defence(String q_element_defence) {
		this.q_element_defence = q_element_defence;
	}

	public String getQ_element_attack() {
		return q_element_attack;
	}

	public void setQ_element_attack(String q_element_attack) {
		this.q_element_attack = q_element_attack;
	}

	public int getQ_hit() {
		return q_hit;
	}

	public void setQ_hit(int q_hit) {
		this.q_hit = q_hit;
	}

	public int getQ_attach_skill() {
		return q_attach_skill;
	}

	public void setQ_attach_skill(int q_attach_skill) {
		this.q_attach_skill = q_attach_skill;
	}

	public int getQ_quality_lv() {
		return q_quality_lv;
	}

	public void setQ_quality_lv(int q_quality_lv) {
		this.q_quality_lv = q_quality_lv;
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
	 * get 物品名称(最大九个汉字。支持_后缀，后缀部分在客户端不予显示)（物品名称需要唯一，以便在爆率表中使用汉字）
	 * @return 
	 */
	public String getQ_name(){
		return q_name;
	}
	
	/**
	 * set 物品名称(最大九个汉字。支持_后缀，后缀部分在客户端不予显示)（物品名称需要唯一，以便在爆率表中使用汉字）
	 */
	public void setQ_name(String q_name){
		this.q_name = q_name;
	}
	
	/**
	 * get 物品类型
	 * @return 
	 */
	public int getQ_type(){
		return q_type;
	}
	
	/**
	 * set 物品类型
	 */
	public void setQ_type(int q_type){
		this.q_type = q_type;
	}
	
	/**
	 * get 佩戴性别需求（0全性别通用，1男，2女，）
	 * @return 
	 */
	public int getQ_sex(){
		return q_sex;
	}
	
	/**
	 * set 佩戴性别需求（0全性别通用，1男，2女，）
	 */
	public void setQ_sex(int q_sex){
		this.q_sex = q_sex;
	}
	
	/**
	 * get 绑定类型（0不绑定；1获得时绑定；2使用后绑定）
	 * @return 
	 */
	public int getQ_bind(){
		return q_bind;
	}
	
	/**
	 * set 绑定类型（0不绑定；1获得时绑定；2使用后绑定）
	 */
	public void setQ_bind(int q_bind){
		this.q_bind = q_bind;
	}
	
	/**
	 * get 最大叠加数量（0或1均为不可叠加，最大可以填9999）
	 * @return 
	 */
	public int getQ_max(){
		return q_max;
	}
	
	/**
	 * set 最大叠加数量（0或1均为不可叠加，最大可以填9999）
	 */
	public void setQ_max(int q_max){
		this.q_max = q_max;
	}
	
	/**
	 * get 商店中的购买价格
	 * @return 
	 */
	public int getQ_buy_price(){
		return q_buy_price;
	}
	
	/**
	 * set 商店中的购买价格
	 */
	public void setQ_buy_price(int q_buy_price){
		this.q_buy_price = q_buy_price;
	}
	
	/**
	 * get 商店是否回收（0不回收，1回收）
	 * @return 
	 */
	public int getQ_sell(){
		return q_sell;
	}
	
	/**
	 * set 商店是否回收（0不回收，1回收）
	 */
	public void setQ_sell(int q_sell){
		this.q_sell = q_sell;
	}
	
	/**
	 * get 商店回收价格
	 * @return 
	 */
	public int getQ_sell_price(){
		return q_sell_price;
	}
	
	/**
	 * set 商店回收价格
	 */
	public void setQ_sell_price(int q_sell_price){
		this.q_sell_price = q_sell_price;
	}
	
	/**
	 * get 回收时是否弹出二次确认面板（0不弹出，1弹出）
	 * @return 
	 */
	public int getQ_sell_confirm(){
		return q_sell_confirm;
	}
	
	/**
	 * set 回收时是否弹出二次确认面板（0不弹出，1弹出）
	 */
	public void setQ_sell_confirm(int q_sell_confirm){
		this.q_sell_confirm = q_sell_confirm;
	}
	
	/**
	 * get 是否可丢弃（0不可丢弃，1可丢弃）
	 * @return 
	 */
	public int getQ_drop(){
		return q_drop;
	}
	
	/**
	 * set 是否可丢弃（0不可丢弃，1可丢弃）
	 */
	public void setQ_drop(int q_drop){
		this.q_drop = q_drop;
	}
	
	/**
	 * get 丢弃时是否弹出二次确认面板（0不弹出，1弹出）
	 * @return 
	 */
	public int getQ_drop_confirm(){
		return q_drop_confirm;
	}
	
	/**
	 * set 丢弃时是否弹出二次确认面板（0不弹出，1弹出）
	 */
	public void setQ_drop_confirm(int q_drop_confirm){
		this.q_drop_confirm = q_drop_confirm;
	}
	
	/**
	 * get 是否允许被拖放至物品快捷栏（0不允许，1允许）
	 * @return 
	 */
	public int getQ_shortcut(){
		return q_shortcut;
	}
	
	/**
	 * set 是否允许被拖放至物品快捷栏（0不允许，1允许）
	 */
	public void setQ_shortcut(int q_shortcut){
		this.q_shortcut = q_shortcut;
	}
	
	/**
	 * get 佩戴部位
	 * @return 
	 */
	public int getQ_kind(){
		return q_kind;
	}
	
	/**
	 * set 佩戴部位
	 */
	public void setQ_kind(int q_kind){
		this.q_kind = q_kind;
	}
	
	/**
	 * get 使用等级需求
	 * @return 
	 */
	public int getQ_level(){
		return q_level;
	}
	
	/**
	 * set 使用等级需求
	 */
	public void setQ_level(int q_level){
		this.q_level = q_level;
	}
	
	/**
	 * get 装备最大可强化等级
	 * @return 
	 */
	public int getQ_max_strengthen(){
		return q_max_strengthen;
	}
	
	/**
	 * set 装备最大可强化等级
	 */
	public void setQ_max_strengthen(int q_max_strengthen){
		this.q_max_strengthen = q_max_strengthen;
	}
	
	/**
	 * get 装备最大可镶嵌宝石数量
	 * @return 
	 */
	public int getQ_max_inlay(){
		return q_max_inlay;
	}
	
	/**
	 * set 装备最大可镶嵌宝石数量
	 */
	public void setQ_max_inlay(int q_max_inlay){
		this.q_max_inlay = q_max_inlay;
	}
	
	/**
	 * get 基础攻击力
	 * @return 
	 */
	public int getQ_attack(){
		return q_attack;
	}
	
	/**
	 * set 基础攻击力
	 */
	public void setQ_attack(int q_attack){
		this.q_attack = q_attack;
	}
	
	/**
	 * get 基础防御力
	 * @return 
	 */
	public int getQ_defence(){
		return q_defence;
	}
	
	/**
	 * set 基础防御力
	 */
	public void setQ_defence(int q_defence){
		this.q_defence = q_defence;
	}
	
	/**
	 * get 基础暴击值
	 * @return 
	 */
	public int getQ_crit(){
		return q_crit;
	}
	
	/**
	 * set 基础暴击值
	 */
	public void setQ_crit(int q_crit){
		this.q_crit = q_crit;
	}
	
	/**
	 * get 基础闪避值
	 * @return 
	 */
	public int getQ_dodge(){
		return q_dodge;
	}
	
	/**
	 * set 基础闪避值
	 */
	public void setQ_dodge(int q_dodge){
		this.q_dodge = q_dodge;
	}
	
	/**
	 * get 基础生命上限
	 * @return 
	 */
	public int getQ_max_hp(){
		return q_max_hp;
	}
	
	/**
	 * set 基础生命上限
	 */
	public void setQ_max_hp(int q_max_hp){
		this.q_max_hp = q_max_hp;
	}
	
	/**
	 * get 基础魔法值上限
	 * @return 
	 */
	public int getQ_max_mp(){
		return q_max_mp;
	}
	
	/**
	 * set 基础魔法值上限
	 */
	public void setQ_max_mp(int q_max_mp){
		this.q_max_mp = q_max_mp;
	}
	
	/**
	 * get 基础体力上限
	 * @return 
	 */
	public int getQ_max_sp(){
		return q_max_sp;
	}
	
	/**
	 * set 基础体力上限
	 */
	public void setQ_max_sp(int q_max_sp){
		this.q_max_sp = q_max_sp;
	}
	
	/**
	 * get 基础攻击速度
	 * @return 
	 */
	public int getQ_attackspeed(){
		return q_attackspeed;
	}
	
	/**
	 * set 基础攻击速度
	 */
	public void setQ_attackspeed(int q_attackspeed){
		this.q_attackspeed = q_attackspeed;
	}
	
	/**
	 * get 基础移动速度
	 * @return 
	 */
	public int getQ_speed(){
		return q_speed;
	}
	
	/**
	 * set 基础移动速度
	 */
	public void setQ_speed(int q_speed){
		this.q_speed = q_speed;
	}
	
	/**
	 * get 基础幸运值
	 * @return 
	 */
	public int getQ_luck(){
		return q_luck;
	}
	
	/**
	 * set 基础幸运值
	 */
	public void setQ_luck(int q_luck){
		this.q_luck = q_luck;
	}
	
	/**
	 * get 使用后关联BUFF编号列表（格式：BUFF编号;BUFF编号;BUFF编号）
	 * @return 
	 */
	public String getQ_buff(){
		return q_buff;
	}
	
	/**
	 * set 使用后关联BUFF编号列表（格式：BUFF编号;BUFF编号;BUFF编号）
	 */
	public void setQ_buff(String q_buff){
		this.q_buff = q_buff;
	}
	
	/**
	 * get 药品使用冷却时间（单位：毫秒）
	 * @return 
	 */
	public int getQ_cooldown(){
		return q_cooldown;
	}
	
	/**
	 * set 药品使用冷却时间（单位：毫秒）
	 */
	public void setQ_cooldown(int q_cooldown){
		this.q_cooldown = q_cooldown;
	}
	
	/**
	 * get 药品公共冷却层级
	 * @return 
	 */
	public int getQ_cooldown_level(){
		return q_cooldown_level;
	}
	
	/**
	 * set 药品公共冷却层级
	 */
	public void setQ_cooldown_level(int q_cooldown_level){
		this.q_cooldown_level = q_cooldown_level;
	}
	
	/**
	 * get 公共冷却时间
	 * @return 
	 */
	public int getQ_cooldown_type(){
		return q_cooldown_type;
	}
	
	/**
	 * set 公共冷却时间
	 */
	public void setQ_cooldown_type(int q_cooldown_type){
		this.q_cooldown_type = q_cooldown_type;
	}
	
	/**
	 * get 传送卷轴类型（1为回城卷，2为随即传送卷，3为地图定点传送卷，4为行会回城卷）
	 * @return 
	 */
	public int getQ_transfer(){
		return q_transfer;
	}
	
	/**
	 * set 传送卷轴类型（1为回城卷，2为随即传送卷，3为地图定点传送卷，4为行会回城卷）
	 */
	public void setQ_transfer(int q_transfer){
		this.q_transfer = q_transfer;
	}
	
	/**
	 * get 传送地图ID
	 * @return 
	 */
	public int getQ_transfer_map(){
		return q_transfer_map;
	}
	
	/**
	 * set 传送地图ID
	 */
	public void setQ_transfer_map(int q_transfer_map){
		this.q_transfer_map = q_transfer_map;
	}
	
	/**
	 * get 传送地图坐标X,Y
	 * @return 
	 */
	public String getQ_transfer_position(){
		return q_transfer_position;
	}
	
	/**
	 * set 传送地图坐标X,Y
	 */
	public void setQ_transfer_position(String q_transfer_position){
		this.q_transfer_position = q_transfer_position;
	}
	
	/**
	 * get 使用后触发任务脚本编号
	 * @return 
	 */
	public int getQ_task(){
		return q_task;
	}
	
	/**
	 * set 使用后触发任务脚本编号
	 */
	public void setQ_task(int q_task){
		this.q_task = q_task;
	}
	
	/**
	 * get 使用后触发脚本编号
	 * @return 
	 */
	public int getQ_script(){
		return q_script;
	}
	
	/**
	 * set 使用后触发脚本编号
	 */
	public void setQ_script(int q_script){
		this.q_script = q_script;
	}
	
	/**
	 * get 使用后学会技能编号
	 * @return 
	 */
	public int getQ_skill(){
		return q_skill;
	}
	
	/**
	 * set 使用后学会技能编号
	 */
	public void setQ_skill(int q_skill){
		this.q_skill = q_skill;
	}
	
	/**
	 * get 使用后打开礼包表编号
	 * @return 
	 */
	public int getQ_gift(){
		return q_gift;
	}
	
	/**
	 * set 使用后打开礼包表编号
	 */
	public void setQ_gift(int q_gift){
		this.q_gift = q_gift;
	}
	
	/**
	 * get 使用后打开面板编号
	 * @return 
	 */
	public int getQ_ui(){
		return q_ui;
	}
	
	/**
	 * set 使用后打开面板编号
	 */
	public void setQ_ui(int q_ui){
		this.q_ui = q_ui;
	}
	
	/**
	 * get 怪物爆出件数上限阀值（填0为不限制）（格式：件数/天数）例如：2/3 是指：每3天最多爆出2件
	 * @return 
	 */
	public String getQ_max_create(){
		return q_max_create;
	}
	
	/**
	 * set 怪物爆出件数上限阀值（填0为不限制）（格式：件数/天数）例如：2/3 是指：每3天最多爆出2件
	 */
	public void setQ_max_create(String q_max_create){
		this.q_max_create = q_max_create;
	}
	
	/**
	 * get 掉出时发送的公告类型（0不发送公告；1面向个人发送聊天框内公告；2面向个人发送聊天框内与屏幕上方双重公告；3面向全服发送聊天框内公告；4面向全服发送聊天框内与屏幕上方双重公告）
	 * @return 
	 */
	public int getQ_notice(){
		return q_notice;
	}
	
	/**
	 * set 掉出时发送的公告类型（0不发送公告；1面向个人发送聊天框内公告；2面向个人发送聊天框内与屏幕上方双重公告；3面向全服发送聊天框内公告；4面向全服发送聊天框内与屏幕上方双重公告）
	 */
	public void setQ_notice(int q_notice){
		this.q_notice = q_notice;
	}
	
	/**
	 * get 是否记录产出与操作日志（0不记录；1记录）
	 * @return 
	 */
	public int getQ_log(){
		return q_log;
	}
	
	/**
	 * set 是否记录产出与操作日志（0不记录；1记录）
	 */
	public void setQ_log(int q_log){
		this.q_log = q_log;
	}
	
	/**
	 * get 物品天生的名字颜色（0白色，1黄色，2绿色，3蓝色，4紫色，5橙色）(讨伐卷轴根据颜色编号关联任务)
	 * @return 
	 */
	public int getQ_default(){
		return q_default;
	}
	
	/**
	 * set 物品天生的名字颜色（0白色，1黄色，2绿色，3蓝色，4紫色，5橙色）(讨伐卷轴根据颜色编号关联任务)
	 */
	public void setQ_default(int q_default){
		this.q_default = q_default;
	}
	
	/**
	 * get 物品ICO上附加的光效资源编号
	 * @return 
	 */
	public String getQ_append(){
		return q_append;
	}
	
	/**
	 * set 物品ICO上附加的光效资源编号
	 */
	public void setQ_append(String q_append){
		this.q_append = q_append;
	}
	
	/**
	 * get 物品描述信息（本处需要支持html标记）
	 * @return 
	 */
	public String getQ_describe(){
		return q_describe;
	}
	
	/**
	 * set 物品描述信息（本处需要支持html标记）
	 */
	public void setQ_describe(String q_describe){
		this.q_describe = q_describe;
	}
	
	/**
	 * get 物品微图标资源编号
	 * @return 
	 */
	public int getQ_tiny_icon(){
		return q_tiny_icon;
	}
	
	/**
	 * set 物品微图标资源编号
	 */
	public void setQ_tiny_icon(int q_tiny_icon){
		this.q_tiny_icon = q_tiny_icon;
	}
	
	/**
	 * get 物品换装资源编号（装备类物品使用）
	 * @return 
	 */
	public String getQ_equip_resource(){
		return q_equip_resource;
	}
	
	/**
	 * set 物品换装资源编号（装备类物品使用）
	 */
	public void setQ_equip_resource(String q_equip_resource){
		this.q_equip_resource = q_equip_resource;
	}
	
	/**
	 * get 物品掉出时播放的音效资源编号
	 * @return 
	 */
	public String getQ_drop_music(){
		return q_drop_music;
	}
	
	/**
	 * set 物品掉出时播放的音效资源编号
	 */
	public void setQ_drop_music(String q_drop_music){
		this.q_drop_music = q_drop_music;
	}
	
	/**
	 * get 物品使用时播放的音效资源编号
	 * @return 
	 */
	public String getQ_use_music(){
		return q_use_music;
	}
	
	/**
	 * set 物品使用时播放的音效资源编号
	 */
	public void setQ_use_music(String q_use_music){
		this.q_use_music = q_use_music;
	}
	
	/**
	 * get 物品脱下时播放的音效资源编号
	 * @return 
	 */
	public String getQ_unuse_music(){
		return q_unuse_music;
	}
	
	/**
	 * set 物品脱下时播放的音效资源编号
	 */
	public void setQ_unuse_music(String q_unuse_music){
		this.q_unuse_music = q_unuse_music;
	}
	
	/**
	 * get 药效编号(1恢复生命值,2恢复魔法值值,3恢复体力值,4增加经验,5增加真气储量,6增加等级,7增加攻击力,8增加防御力,9增加暴击,10增加闪避,11增加攻击速度,12增加移动速度,13增加生命上限值,14增加魔法值上限值,15增加体力上限值） 格式示例[1,2,4]
	 * @return 
	 */
	public String getQ_effict_type(){
		return q_effict_type;
	}
	
	/**
	 * set 药效编号(1恢复生命值,2恢复魔法值值,3恢复体力值,4增加经验,5增加真气储量,6增加等级,7增加攻击力,8增加防御力,9增加暴击,10增加闪避,11增加攻击速度,12增加移动速度,13增加生命上限值,14增加魔法值上限值,15增加体力上限值） 格式示例[1,2,4]
	 */
	public void setQ_effict_type(String q_effict_type){
		this.q_effict_type = q_effict_type;
	}
	
	/**
	 * get 是否自动使用（1是，0否）
	 * @return 
	 */
	public int getQ_auto_use(){
		return q_auto_use;
	}
	
	/**
	 * set 是否自动使用（1是，0否）
	 */
	public void setQ_auto_use(int q_auto_use){
		this.q_auto_use = q_auto_use;
	}
	
	/**
	 * get 道具是否可批量使用(0否，1是)
	 * @return 
	 */
	public int getQ_whether_batch(){
		return q_whether_batch;
	}
	
	/**
	 * set 道具是否可批量使用(0否，1是)
	 */
	public void setQ_whether_batch(int q_whether_batch){
		this.q_whether_batch = q_whether_batch;
	}
	
	/**
	 * get 道具是否可进入仓库（1否，0是）
	 * @return 
	 */
	public int getQ_save_warehouse(){
		return q_save_warehouse;
	}
	
	/**
	 * set 道具是否可进入仓库（1否，0是）
	 */
	public void setQ_save_warehouse(int q_save_warehouse){
		this.q_save_warehouse = q_save_warehouse;
	}
	
	/**
	 * get 道具tips中的天生颜色
	 * @return 
	 */
	public int getQ_tips_effect(){
		return q_tips_effect;
	}
	
	/**
	 * set 道具tips中的天生颜色
	 */
	public void setQ_tips_effect(int q_tips_effect){
		this.q_tips_effect = q_tips_effect;
	}
	
	/**
	 * get 物品使用上限
	 * @return 
	 */
	public int getQ_item_limit(){
		return q_item_limit;
	}
	
	/**
	 * set 物品使用上限
	 */
	public void setQ_item_limit(int q_item_limit){
		this.q_item_limit = q_item_limit;
	}
	
	/**
	 * @return the q_add_injure
	 */
	public int getQ_add_injure() {
		return q_add_injure;
	}

	/**
	 * @param q_add_injure
	 *            the q_add_injure to set
	 */
	public void setQ_add_injure(int q_add_injure) {
		this.q_add_injure = q_add_injure;
	}

	/**
	 * @return the q_reduce_injure
	 */
	public int getQ_reduce_injure() {
		return q_reduce_injure;
	}

	/**
	 * @param q_reduce_injure
	 *            the q_reduce_injure to set
	 */
	public void setQ_reduce_injure(int q_reduce_injure) {
		this.q_reduce_injure = q_reduce_injure;
	}

	/**
	 * @return the q_max_damage_percent
	 */
	public int getQ_max_damage_percent() {
		return q_max_damage_percent;
	}

	/**
	 * @param q_max_damage_percent
	 *            the q_max_damage_percent to set
	 */
	public void setQ_max_damage_percent(int q_max_damage_percent) {
		this.q_max_damage_percent = q_max_damage_percent;
	}

	/**
	 * @return the q_ignore_attack_percent
	 */
	public int getQ_ignore_attack_percent() {
		return q_ignore_attack_percent;
	}

	/**
	 * @param q_ignore_attack_percent
	 *            the q_ignore_attack_percent to set
	 */
	public void setQ_ignore_attack_percent(int q_ignore_attack_percent) {
		this.q_ignore_attack_percent = q_ignore_attack_percent;
	}

	/**
	 * @return the q_ignore_attack
	 */
	public int getQ_ignore_attack() {
		return q_ignore_attack;
	}

	/**
	 * @param q_ignore_attack
	 *            the q_ignore_attack to set
	 */
	public void setQ_ignore_attack(int q_ignore_attack) {
		this.q_ignore_attack = q_ignore_attack;
	}

	public int getQ_equip_level() {
		return q_equip_level;
	}

	public void setQ_equip_level(int q_equip_level) {
		this.q_equip_level = q_equip_level;
	}

}