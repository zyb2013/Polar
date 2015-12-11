package com.game.data.manager;


import com.game.data.container.*;

/**
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 *          数据管理
 */
public class DataManager {

	// job
	public Q_jobContainer q_jobContainer = new Q_jobContainer();

	public Q_backpack_gridContainer q_backpack_gridContainer = new Q_backpack_gridContainer();
	public Q_characterContainer q_characterContainer = new Q_characterContainer();
	public Q_itemContainer q_itemContainer = new Q_itemContainer();
	public Q_shopContainer q_shopContainer = new Q_shopContainer();
	public Q_npcContainer q_npcContainer = new Q_npcContainer();
	public Q_equip_appendContainer q_equip_appendContainer = new Q_equip_appendContainer();
	public Q_skill_modelContainer q_skill_modelContainer = new Q_skill_modelContainer();
	public Q_globalContainer q_globalContainer = new Q_globalContainer();
	public Q_versionContainer q_versionContainer = new Q_versionContainer();
	public Q_mapContainer q_mapContainer = new Q_mapContainer();
	public Q_map_blockContainer q_map_blockContainer = new Q_map_blockContainer();
	public Q_monster_dropgroupContainer q_monster_dropgroupContainer = new Q_monster_dropgroupContainer();
	public Q_monster_dropprobContainer q_monster_dropprobContainer = new Q_monster_dropprobContainer();
	public Q_buffContainer q_buffContainer = new Q_buffContainer();
	public Q_monsterContainer q_monsterContainer = new Q_monsterContainer();
	public Q_scene_monsterContainer q_scene_monsterContainer = new Q_scene_monsterContainer();
	public Q_skill_realmContainer q_skill_realmContainer = new Q_skill_realmContainer();
	public Q_filterwordContainer q_filterwordContainer = new Q_filterwordContainer();
	public Q_boss_dropContainer q_boss_dropContainer = new Q_boss_dropContainer();
	public Q_rankContainer q_rankContainer = new Q_rankContainer();
	public Q_transferContainer q_transferContainer = new Q_transferContainer();
	public Q_task_mainContainer q_task_mainContainer = new Q_task_mainContainer();
	public Q_task_conquerContainer q_task_conquerContainer = new Q_task_conquerContainer();
	public Q_task_daily_condContainer q_task_daily_condContainer = new Q_task_daily_condContainer();
	public Q_task_daily_rewardsContainer q_task_daily_rewardsContainer = new Q_task_daily_rewardsContainer();
	public Q_task_extra_rewardsContainer q_task_extra_rewardsContainer = new Q_task_extra_rewardsContainer();
	public Q_schedulerContainer q_schedulerContainer = new Q_schedulerContainer();
	public Q_scriptContainer q_scriptContainer = new Q_scriptContainer();
	public Q_item_strengthContainer q_item_strengthContainer = new Q_item_strengthContainer();
	public Q_role_random_nameContainer q_role_random_nameContainer = new Q_role_random_nameContainer();
	public Q_clone_activityContainer q_clone_activityContainer = new Q_clone_activityContainer();
	public Q_longyuan_expContainer q_longyuan_expContainer = new Q_longyuan_expContainer();
	public Q_longyuanContainer q_longyuanContainer = new Q_longyuanContainer();
	public Q_minimapshowContainer q_minimapshowContainer = new Q_minimapshowContainer();
	public Q_scene_monster_areaContainer q_scene_monster_areaContainer = new Q_scene_monster_areaContainer();
	public Q_petsContainer q_petsContainer = new Q_petsContainer();
	public Q_horse_basicContainer q_horse_basicContainer = new Q_horse_basicContainer();
	public Q_horse_skillsContainer q_horse_skillsContainer = new Q_horse_skillsContainer();
	public Q_horse_additionContainer q_horse_additionContainer = new Q_horse_additionContainer();
	public Q_horse_skill_expContainer q_horse_skill_expContainer = new Q_horse_skill_expContainer();
	public Q_chapter_appendContainer q_chapter_appendContainer = new Q_chapter_appendContainer();
	public Q_rangevalueContainer q_rangevalueContainer = new Q_rangevalueContainer();
	public Q_guildbannerContainer q_guildbannerContainer = new Q_guildbannerContainer();
	public Q_gem_activationContainer q_gem_activationContainer = new Q_gem_activationContainer();
	public Q_gem_upContainer q_gem_upContainer = new Q_gem_upContainer();
	public Q_newrole_defaultvalueContainer q_newrole_defaultvalueContainer = new Q_newrole_defaultvalueContainer();
	public Q_spirittree_packetContainer q_spirittree_packetContainer = new Q_spirittree_packetContainer();
	public Q_spirittree_kiwiContainer q_spirittree_kiwiContainer = new Q_spirittree_kiwiContainer();
	public Q_spirittree_pack_conContainer q_spirittree_pack_conContainer = new Q_spirittree_pack_conContainer();
	public Q_newfuncContainer q_newfuncContainer = new Q_newfuncContainer();
	public Q_tipconfigContainer q_tipconfigContainer = new Q_tipconfigContainer();
	public Q_giftContainer q_giftContainer = new Q_giftContainer();
	public Q_bulletinContainer q_bulletinContainer = new Q_bulletinContainer();
	public Q_petinfoContainer q_petinfoContainer = new Q_petinfoContainer();
	public Q_petattributeContainer q_petattributeContainer = new Q_petattributeContainer();
	public Q_evencutContainer q_evencutContainer = new Q_evencutContainer();
	public Q_task_explorepalaceContainer q_task_explorepalaceContainer = new Q_task_explorepalaceContainer();
	public Q_explorepalace_mapContainer q_explorepalace_mapContainer = new Q_explorepalace_mapContainer();
	public Q_explorepalace_rewardsContainer q_explorepalace_rewardsContainer = new Q_explorepalace_rewardsContainer();
	public Q_titleContainer q_titleContainer = new Q_titleContainer();
	public Q_jiaochangContainer q_jiaochangContainer = new Q_jiaochangContainer();
	public Q_toplistchestContainer q_toplistchestContainer = new Q_toplistchestContainer();
	public Q_vipContainer q_vipContainer = new Q_vipContainer();
	public Q_scene_objContainer q_scene_objContainer = new Q_scene_objContainer();
	public Q_special_eventContainer q_special_eventContainer = new Q_special_eventContainer();
	public Q_activitiesContainer q_activitiesContainer = new Q_activitiesContainer();
	public Q_new_activityContainer q_newActivityContainer = new Q_new_activityContainer();
	public Q_fourjinsuoContainer q_fourjinsuoContainer = new Q_fourjinsuoContainer();
	public Q_qingfengguyunContainer q_qingfengguyunContainer = new Q_qingfengguyunContainer();
	public Q_arrowContainer q_arrowContainer = new Q_arrowContainer();
	public Q_arrow_starContainer q_arrow_starContainer = new Q_arrow_starContainer();
	public Q_arrow_bowContainer q_arrow_bowContainer = new Q_arrow_bowContainer();
	public Q_task_rankContainer q_task_rankContainer = new Q_task_rankContainer();
	public Q_task_daily_monsterContainer q_task_daily_monsterContainer = new Q_task_daily_monsterContainer();
	public Q_sign_wageContainer q_sign_wageContainer = new Q_sign_wageContainer();
	public Q_fightspiritContainer q_fightspiritContainer = new Q_fightspiritContainer();
	public Q_panel_dataContainer q_panel_dataContainer = new Q_panel_dataContainer();
	public Q_collectContainer q_collectContainer = new Q_collectContainer();
	public Q_meting_randomContainer q_meting_randomContainer = new Q_meting_randomContainer();
	public Q_meihuaxuanwuContainer q_meihuaxuanwuContainer = new Q_meihuaxuanwuContainer();
	public Q_horseweapon_attrContainer q_horseweapon_attrContainer = new Q_horseweapon_attrContainer();
	public Q_horseweaponContainer q_horseweaponContainer = new Q_horseweaponContainer();
	public Q_horseweapon_skillContainer q_horseweapon_skillContainer = new Q_horseweapon_skillContainer();
	public Q_platformgiftContainer q_platformgiftContainer = new Q_platformgiftContainer();
	public Q_restwContainer q_restwContainer = new Q_restwContainer();
	public Q_activities_dropContainer q_activities_dropContainer = new Q_activities_dropContainer();
	public Q_activation_attributeContainer q_activation_attributeContainer = new Q_activation_attributeContainer();
	public Q_realm_breakContainer q_realm_breakContainer = new Q_realm_breakContainer();
	public Q_realm_basicContainer q_realm_basicContainer = new Q_realm_basicContainer();
	public Q_hiddenweapon_attrContainer q_hiddenweapon_attrContainer = new Q_hiddenweapon_attrContainer();
	public Q_hiddenweaponContainer q_hiddenweaponContainer = new Q_hiddenweaponContainer();
	public Q_hiddenweapon_skillContainer q_hiddenweapon_skillContainer = new Q_hiddenweapon_skillContainer();
	
	//add start
	public Q_item_add_attributeContainer q_item_add_attributeContainer = new Q_item_add_attributeContainer();
	public Q_equip_composeContainer q_equip_composeContainer = new Q_equip_composeContainer();
	public Q_pandoraContainer q_pandoraContainer = new Q_pandoraContainer();
	
	/*活跃度宝箱原型库   add：hongxiao.z*/
	public Q_liveness_boxContainer q_liveness_boxContainer = new Q_liveness_boxContainer();
	/*活跃度事件原型库   add：hongxiao.z*/
	public Q_liveness_eventContainer q_liveness_eventContainer = new Q_liveness_eventContainer();
	
	//爬塔领奖消耗数据
	public Q_pt_reward_consumeContainer q_pt_reward_consumeContainer = new Q_pt_reward_consumeContainer();
	public Q_pt_awardContainer q_pt_awardContainer = new Q_pt_awardContainer();
	
	//等级礼包
	public Q_gradegiftContainer q_gradegiftContainer = new Q_gradegiftContainer();
	//遗落技能
	public Q_lost_skillContainer q_lostskillContainer = new Q_lost_skillContainer();
	
	public Q_bank_rateContainer q_bank_rateContainer = new Q_bank_rateContainer();
	
	//强化优化
	public Q_strenghten_vipContainer q_strenghten_vipContainer = new Q_strenghten_vipContainer();
	//合成数据表
	public Q_equip_compose_appendContainer q_equip_compose_appendContainer = new Q_equip_compose_appendContainer();
	
	public Q_csysContainer q_csysContainer = new Q_csysContainer();
	
	public Q_activity_monstersContainer q_activity_monstersContainer = new Q_activity_monstersContainer();
	
	public Q_prayContainer q_prayContainer = new Q_prayContainer();
	public Q_lotteryContainer q_lotteryContainer = new Q_lotteryContainer();
	public Q_fractionContainer q_fractionContainer = new Q_fractionContainer();
	public Q_casting_costContainer q_casting_costContainer = new Q_casting_costContainer();
	public Q_casting_rewardContainer q_casting_rewardContainer = new Q_casting_rewardContainer();
	public Q_casting_exchangeContainer q_casting_exchangeContainer = new Q_casting_exchangeContainer();
	public Q_casting_add_attributeContainer q_casting_add_attributeContainer = new Q_casting_add_attributeContainer();
	public Q_casting_strengthContainer q_casting_strengthContainer = new Q_casting_strengthContainer();
	//add end
	
	
	public Q_hold_rewardContainer q_hold_rewardContainer = new Q_hold_rewardContainer();
	
	public Q_countryContainer q_countryContainer = new Q_countryContainer();
	
	private static Object obj = new Object();

	private static DataManager manager;

	private DataManager() {
		q_jobContainer.load();
		q_backpack_gridContainer.load();
		q_characterContainer.load();
		q_itemContainer.load();
		q_shopContainer.load();
		q_npcContainer.load();
		q_equip_appendContainer.load();
		q_skill_modelContainer.load();
		q_globalContainer.load();
		q_versionContainer.load();
		q_mapContainer.load();
		q_map_blockContainer.load();
		q_monster_dropgroupContainer.load();
		q_monster_dropprobContainer.load();
		q_buffContainer.load();
		q_monsterContainer.load();
		q_scene_monsterContainer.load();
		q_skill_realmContainer.load();
		q_filterwordContainer.load();
		q_boss_dropContainer.load();
		q_rankContainer.load();
		q_transferContainer.load();
		q_task_mainContainer.load();
		q_task_conquerContainer.load();
		q_task_daily_condContainer.load();
		q_task_daily_rewardsContainer.load();
		q_task_extra_rewardsContainer.load();
		q_schedulerContainer.load();
		q_scriptContainer.load();
		q_item_strengthContainer.load();
		q_role_random_nameContainer.load();
		q_clone_activityContainer.load();
		q_longyuan_expContainer.load();
		q_longyuanContainer.load();
		q_minimapshowContainer.load();
		q_scene_monster_areaContainer.load();
		q_petsContainer.load();
		q_horse_basicContainer.load();
		q_horse_skillsContainer.load();
		q_horse_additionContainer.load();
		q_horse_skill_expContainer.load();
		q_chapter_appendContainer.load();
		q_rangevalueContainer.load();
		q_guildbannerContainer.load();
		q_gem_activationContainer.load();
		q_gem_upContainer.load();
		q_newrole_defaultvalueContainer.load();
		q_spirittree_packetContainer.load();
		q_spirittree_kiwiContainer.load();
		q_spirittree_pack_conContainer.load();
		q_newfuncContainer.load();
		q_tipconfigContainer.load();
		q_giftContainer.load();
		q_bulletinContainer.load();
		q_petinfoContainer.load();
		q_petattributeContainer.load();
		q_evencutContainer.load();
		q_task_explorepalaceContainer.load();
		q_explorepalace_mapContainer.load();
		q_explorepalace_rewardsContainer.load();
		q_titleContainer.load();
		q_jiaochangContainer.load();
		q_toplistchestContainer.load();
		q_vipContainer.load();
		q_scene_objContainer.load();
		q_special_eventContainer.load();
		q_activitiesContainer.load();
		q_newActivityContainer.load();
		q_fourjinsuoContainer.load();
		q_qingfengguyunContainer.load();
		q_arrowContainer.load();
		q_arrow_starContainer.load();
		q_arrow_bowContainer.load();
		q_task_rankContainer.load();
		q_task_daily_monsterContainer.load();
		q_sign_wageContainer.load();
		q_fightspiritContainer.load();
		q_panel_dataContainer.load();
		q_collectContainer.load();
		q_meting_randomContainer.load();
		q_meihuaxuanwuContainer.load();
		q_horseweapon_attrContainer.load();
		q_horseweaponContainer.load();
		q_horseweapon_skillContainer.load();
		q_platformgiftContainer.load();
		q_restwContainer.load();
		q_activities_dropContainer.load();
		q_activation_attributeContainer.load();
		q_realm_breakContainer.load();
		q_realm_basicContainer.load();
		q_hiddenweapon_attrContainer.load();
		q_hiddenweaponContainer.load();
		q_hiddenweapon_skillContainer.load();
		//add start
		q_item_add_attributeContainer.load();
		q_equip_composeContainer.load();
		q_pandoraContainer.load();
		
		q_liveness_boxContainer.load();
		q_liveness_eventContainer.load();
		
		q_pt_reward_consumeContainer.load();
		q_pt_awardContainer.load();
		
		q_gradegiftContainer.load();
		q_lostskillContainer.load();
		
		q_prayContainer.load();
		
		q_bank_rateContainer.load();
		q_lotteryContainer.load();
		q_fractionContainer.load();
		q_casting_costContainer.load();
		q_casting_rewardContainer.load();
		q_casting_exchangeContainer.load();
		q_casting_add_attributeContainer.load();
		q_casting_strengthContainer.load();
		
		q_strenghten_vipContainer.load();
		q_equip_compose_appendContainer.load();
		
//		q_csysContainer.load();
		q_csysContainer.load();
		q_countryContainer.load();
		q_hold_rewardContainer.load();
		q_activity_monstersContainer.load();
		//add end
		
		
	}

	/**
	 * 获取数据管理实例
	 * 
	 * @return
	 */
	public static DataManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new DataManager();
			}
		}
		return manager;
	}

	/**
	 * 重新加载数据
	 */
	public void setData() {
		DataManager newdata = new DataManager();
		manager = newdata;
	}
}
