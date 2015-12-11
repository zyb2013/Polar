package com.game.summonpet.manager;


import java.util.ArrayList;
import java.util.List;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.skill.bean.SkillInfo;
import com.game.skill.structs.Skill;
import com.game.structs.Attributes;
import com.game.summonpet.bean.SummonPetAttribute;
import com.game.summonpet.bean.SummonPetDetailInfo;
import com.game.summonpet.message.ResSummonPetAttributeChangeMessage;
import com.game.summonpet.message.ResSummonPetListMessage;
import com.game.summonpet.struts.SummonPet;
import com.game.utils.MessageUtil;

/**
 * 召唤怪信息管理器
 * @author  panic
 * @2013-11-21 下午1:27:48
 */
public class SummonPetInfoManager {
	private static SummonPetInfoManager  instance=new SummonPetInfoManager ();
	public static SummonPetInfoManager  getInstance(){
		return instance;
	}
	private SummonPetInfoManager() {
		
	}
	public String getKey(Player player,int summonpetModel){
		return summonpetModel+"_"+player.getLevel();
	}
	public boolean isFullHp(SummonPet summonpet){
		return summonpet.getHp()>=summonpet.getMaxHp();
	}
	
	
	/**
	 * 获取当前出战召唤怪
	 * @param player
	 * @return
	 */
	public SummonPet getShowSummonPet(Player player){
		for (SummonPet summonpet : player.getSummonPetList()) {
			if(summonpet.isShow()){
				return summonpet;
			}
		}
		return null;
	}
	
	/**
	 * 按ID查找召唤怪
	 * @param player
	 * @param petId
	 * @return
	 */
	public SummonPet getSummonPet(Player player, long petId){
		for (SummonPet summonpet:player.getSummonPetList()) {
			if(summonpet.getId()==petId){
				return summonpet;
			}
		}
		return null;
	}
	
	public SummonPet getSummonPetByModelId(Player player,int modelId){
		List<SummonPet> petList = player.getSummonPetList();
		for (SummonPet summonpet : petList) {
			if(summonpet.getModelId()==modelId){
				return summonpet;
			}
		}
		return null;
	}
	
//	/** 多美人出战属于 后期需求  暂时不考虑
//	 * 获取玩家出战的召唤怪列表
//	 * @param player
//	 * @return
//	 */
//	public List<Pet> getShowPets(Player player){
//		ArrayList<Pet> list=new ArrayList<Pet>();
//		List<Pet> petList = player.getPetList();
//		if(petList!=null&&petList.size()>0){
//			for (Pet pet : petList) {
//				if(pet.isShow()){
//					list.add(pet);
//				}
//			}
//		}
//		return list;
//	}
	
	public SummonPetDetailInfo getDetailInfo(SummonPet pet){
		SummonPetDetailInfo info = new SummonPetDetailInfo();
		/*
		if(pet.getDieTime()!=0&&pet.isDie()){
			Q_petinfoBean model = DataManager.getInstance().q_petinfoContainer.getMap().get(pet.getModelId());
			int revive_time = model.getQ_revive_time();
			
			
			info.setDieTime((int) ((revive_time-(System.currentTimeMillis()-pet.getDieTime()))/1000));	
//			System.out.println("复活时间"+info.getDieTime());
		}else{
			pet.setDieTime(0);
		}
		*/
		pet.setDieTime(0);
		info.setHp(pet.getHp());
		info.setMp(pet.getMp());
		info.setSp(pet.getSp());
		info.setMaxHp(pet.getMaxHp());
		info.setMaxMp(pet.getMaxMp());
		info.setMaxSp(pet.getMaxSp());
		info.setSpeed(pet.getSpeed());
		
		info.setLevel(pet.getLevel());
		info.setPetId(pet.getId());
		info.setPetModelId(pet.getModelId());
		for (Skill skill : pet.getSkills()) {
			if(skill!=null)
			info.getSkillInfos().add(getSkillInfo(skill));
		}
		return info;
	}

	
	/**
	 * 获取技能信息
	 * @param skill 技能
	 * @return
	 */
	public SkillInfo getSkillInfo(Skill skill){
		SkillInfo info = new SkillInfo();
		info.setSkillId(skill.getId());
		info.setSkillModelId(skill.getSkillModelId());
		info.setSkillLevel(skill.getSkillLevel());
//		info.setLongyuanLevel(LongYuanManager.getInstance().getLongYuanSkillLevel(player, info.getSkillModelId()));
		return info;
	}
	
	public void sendSummonPetInfo(Player player){
		List<SummonPet> summonpetList = player.getSummonPetList();
		List<SummonPetDetailInfo> summonpetInfos=new ArrayList<SummonPetDetailInfo>();
		for (SummonPet pet : summonpetList) {
//			if(pet.isShow()&&pet.isDie()){
//				Q_petinfoBean model = DataManager.getInstance().q_petinfoContainer.getMap().get(pet.getModelId());
//				int revive_time = model.getQ_revive_time();
//				if(System.currentTimeMillis()-pet.getDieTime()>=revive_time){
//					pet.resetPet();	
//				}	
//			}
			summonpetInfos.add(getDetailInfo(pet));
		}
		ResSummonPetListMessage resSummonPetListMessage=new ResSummonPetListMessage();
		resSummonPetListMessage.setPets(summonpetInfos);
		MessageUtil.tell_player_message(player, resSummonPetListMessage);
	}
	
	public void onHpChange(SummonPet summonpet){
		ResSummonPetAttributeChangeMessage msg=new ResSummonPetAttributeChangeMessage();
		msg.setSummonPetId(summonpet.getId());
		SummonPetAttribute attribute=new SummonPetAttribute();
		attribute.setType(Attributes.HP.getValue());
		attribute.setValue(summonpet.getHp());
		msg.setAttributeChange(attribute);
		MessageUtil.tell_round_message(summonpet, msg);
		//PetScriptManager.getInstance().hpChange(summonpet);
	}
	
	public void onMpChange(SummonPet summonpet){
		ResSummonPetAttributeChangeMessage msg=new ResSummonPetAttributeChangeMessage();
		msg.setSummonPetId(summonpet.getId());
		SummonPetAttribute attribute=new SummonPetAttribute();
		attribute.setType(Attributes.MP.getValue());
		attribute.setValue(summonpet.getHp());
		msg.setAttributeChange(attribute);
		MessageUtil.tell_round_message(summonpet, msg);
	}
	
	public void onSpChange(SummonPet summonpet){
		ResSummonPetAttributeChangeMessage msg=new ResSummonPetAttributeChangeMessage();
		msg.setSummonPetId(summonpet.getId());
		SummonPetAttribute attribute=new SummonPetAttribute();
		attribute.setType(Attributes.SP.getValue());
		attribute.setValue(summonpet.getHp());
		msg.setAttributeChange(attribute);
		MessageUtil.tell_round_message(summonpet, msg);
	}
	
	public void onSpeedChange(SummonPet summonpet){
		ResSummonPetAttributeChangeMessage msg=new ResSummonPetAttributeChangeMessage();
		msg.setSummonPetId(summonpet.getId());
		SummonPetAttribute attribute=new SummonPetAttribute();
		attribute.setType(Attributes.SPEED.getValue());
		attribute.setValue(summonpet.getHp());
		msg.setAttributeChange(attribute);
		MessageUtil.tell_round_message(summonpet, msg);
	}
	/**
	 * 血量变化
	 * @param summonpet
	 * @param recover
	 */
	public void addHp(SummonPet pet,int recover) {
		if(pet.getHp() == pet.getMaxHp()) return;
		int hp=pet.getHp()+recover;
		if(hp>=pet.getMaxHp()){
			pet.setHp(pet.getMaxHp());
		}else{
			pet.setHp(hp);
		}
		onHpChange(pet);
	}
	
	
	
	/**获取召唤怪主人
	 * 
	 * @param fighter
	 * @return
	 */
	public Player getSummonPetHost(SummonPet summonpet){
		long attackplayerid = summonpet.getOwnerId();
		return  ManagerPool.playerManager.getPlayer(attackplayerid);
	}

}
