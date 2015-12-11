package com.game.data.manager;

import com.game.data.container.*;


/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * 数据管理
 */
public class DataManager {

	public Q_itemContainer q_itemContainer = new Q_itemContainer();
	public Q_equip_appendContainer q_equip_appendContainer = new Q_equip_appendContainer();
	public Q_spirittree_kiwiContainer q_spirittree_kiwiContainer = new Q_spirittree_kiwiContainer();
	public Q_spirittree_pack_conContainer q_spirittree_pack_conContainer = new Q_spirittree_pack_conContainer();
	public Q_spirittree_packetContainer q_spirittree_packetContainer = new Q_spirittree_packetContainer();
	public Q_globalContainer q_globalContainer = new Q_globalContainer();
	public Q_versionContainer q_versionContainer = new Q_versionContainer();
	public Q_characterContainer q_characterContainer = new Q_characterContainer();
	public Q_cardContainer q_cardContainer = new Q_cardContainer();
	public Q_scriptContainer q_scriptContainer = new Q_scriptContainer();
	public Q_bulletinContainer q_bulletinContainer = new Q_bulletinContainer();
	public Q_gem_activationContainer q_gem_activationContainer = new Q_gem_activationContainer();
	public Q_titleContainer q_titleContainer = new Q_titleContainer();
	public Q_vipContainer q_vipContainer = new Q_vipContainer();
	public Q_schedulerContainer q_schedulerContainer = new Q_schedulerContainer();
	public Q_skill_modelContainer q_skill_modelContainer = new  Q_skill_modelContainer();
	public Q_item_strengthContainer q_item_strengthContainer = new Q_item_strengthContainer();
	public Q_activitiesContainer q_activitiesContainer = new Q_activitiesContainer();
	public Q_gem_upContainer q_gem_upContainer = new Q_gem_upContainer();
	public Q_questionContainer q_questionContainer = new Q_questionContainer();
	public Q_item_add_attributeContainer q_item_add_attributeContainer = new Q_item_add_attributeContainer();
	
	private static Object obj = new Object();
	
	private static DataManager manager;
	
	private DataManager(){
		q_itemContainer.load();
		q_equip_appendContainer.load();
		q_spirittree_kiwiContainer.load();
		q_spirittree_pack_conContainer.load();
		q_spirittree_packetContainer.load();
		q_globalContainer.load();
		q_versionContainer.load();
		q_characterContainer.load();
		q_cardContainer.load();
		q_scriptContainer.load();
		q_bulletinContainer.load();
		q_gem_activationContainer.load();
		q_titleContainer.load();
		q_vipContainer.load();
		q_schedulerContainer.load();
		q_skill_modelContainer.load();
		q_item_strengthContainer.load();
		q_activitiesContainer.load();
		q_gem_upContainer.load();
		q_questionContainer.load();
		q_item_add_attributeContainer.load();
	}
	
	public static DataManager getInstance(){
		synchronized (obj) {
			if(manager == null){
				manager = new DataManager();
			}
		}
		return manager;
	}
	
	
	public void setData(){
		DataManager newdata = new DataManager();
		manager = newdata;
	}
	
}
