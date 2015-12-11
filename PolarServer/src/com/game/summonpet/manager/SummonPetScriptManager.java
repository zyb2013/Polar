package com.game.summonpet.manager;

import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.game.count.manager.CountManager;
import com.game.count.structs.CountTypes;
import com.game.data.bean.Q_monsterBean;
import com.game.data.bean.Q_petinfoBean;
import com.game.data.manager.DataManager;
import com.game.manager.ManagerPool;
import com.game.pet.message.ResPetChatMessage;
import com.game.pet.struts.Pet;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Person;
import com.game.player.structs.Player;
import com.game.skill.structs.Skill;
import com.game.summonpet.struts.SummonPet;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.StringUtil;

/**
 * AI字符串配置 互动对话
 * @author  
 * @2012-8-31 下午9:38:18
 */
public class SummonPetScriptManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SummonPetScriptManager.class);

	private static SummonPetScriptManager instance=new SummonPetScriptManager();
	public static SummonPetScriptManager getInstance(){
		return instance;
	}
	public static final String CHAT_AI_PROB="发言万分比概率";
	public static final String CHAT_AI_SHOWPET="召唤怪出战";
	public static final String CHAT_AI_USESKILL="召唤怪施放特殊技能";
	public static final String CHAT_AI_ARTICULO="召唤怪生命垂危";
	public static final String CHAT_AI_SHUANGXIU="玩家与召唤怪双修";
	public static final String CHAT_AI_HIDEPET="召唤怪休息";
	public static final String CHAT_AI_ONWER_FINSHTASK="主角完成任务";
	public static final String CHAT_AI_SHOWTIME_ONEHOUR="出战时间超过1小时";
	public static final String CHAT_AI_PET_KILLTARGET="召唤怪击杀目标";
	public static final String CHAT_AI_ONWER_KILLTARGET="主角击杀目标";
	public static final String CHAT_AI_PET_FIRST_SHOW="召唤怪第一次出战";
	public static final String CHAT_AI_PET_HUNDRED_SHOW="出战一百次";
	
	public void summonpetChatAI(String key,SummonPet summonpet){
		try{
			Player player = PlayerManager.getInstance().getOnLinePlayer(summonpet.getOwnerId());
			JSONObject aiValueByAction = getAiValueByAction(key, summonpet);
			if(aiValueByAction==null||aiValueByAction.get(key)==null||player==null){
				return;
			}
			Object probObj= aiValueByAction.get(CHAT_AI_PROB);
			if(probObj==null){
				return;
			}
			int probability = Integer.parseInt(probObj.toString());
			if(RandomUtils.defaultIsGenerate(probability)){
				ResPetChatMessage msg=new ResPetChatMessage();
				msg.setPetId(summonpet.getId());
				msg.setSaycontent(aiValueByAction.get(key)+"");
				MessageUtil.tell_player_message(player,msg);
			}	
		}catch (Exception e) {
			logger.error(e,e);
		}
	}
	
//	
	/**
	 * 召唤怪出战
	 * @param pet
	 */
	public void showPet(SummonPet pet){
		summonpetChatAI(CHAT_AI_SHOWPET, pet);
		Player player = PlayerManager.getInstance().getPlayer(pet.getOwnerId());
		if(player!=null){
			long count = CountManager.getInstance().getCount(player, CountTypes.PET_SHOW, pet.getModelId()+"");
			if(count==0){
				summonpetChatAI(CHAT_AI_PET_FIRST_SHOW, pet);
			}
			if(count==100){
				summonpetChatAI(CHAT_AI_PET_HUNDRED_SHOW, pet);
			}
			//超过一万次就不记了
			if(count<=10000){
				CountManager.getInstance().addCount(player, CountTypes.PET_SHOW,pet.getModelId()+"",CountManager.COUNT_PERSISTENT,1,0);
			}
		}
	}
	
	/**
	 * 召唤怪休息
	 * @param pet
	 */
//	public void hidePet(Pet pet){
//		pet.getTagSet().remove(CHAT_AI_SHOWTIME_ONEHOUR);
//		summonpetChatAI(CHAT_AI_HIDEPET, pet);
//		//召唤怪休息
//		
//	}
	
	/**
	 * 召唤怪血量变化
	 * @param pet
	 */
	public void hpChange(SummonPet summonpet){
		if(summonpet.getMaxHp()*0.3<=summonpet.getHp()&&!summonpet.getTagSet().contains(CHAT_AI_ARTICULO)){
			summonpetChatAI(CHAT_AI_ARTICULO, summonpet);
		}else{
			summonpet.getTagSet().remove(CHAT_AI_ARTICULO);
		}
		//召唤怪生命垂危
	}
	/**
	 * 召唤怪使用技能
	 * @param pet
	 * @param skill
	 */
	public void useSkill(SummonPet summonpet,Skill skill){
		//召唤怪使用特殊技能
		if(skill.getSkillModelId()!=3){
			summonpetChatAI(CHAT_AI_USESKILL, summonpet);	
		}
	}
	

	
	/**
	 * 召唤怪时间AI
	 * @param pet
	 */
//	public void petTimmerAction(SummonPet summonpet){
//		//出战时间超过1小时
//		if(System.currentTimeMillis()-summonpet.getShowTime()>60*60*1000&&!summonpet.getTagSet().contains(CHAT_AI_SHOWTIME_ONEHOUR)){
//			summonpetChatAI(CHAT_AI_SHOWTIME_ONEHOUR, summonpet);
//			summonpet.getTagSet().add(CHAT_AI_SHOWTIME_ONEHOUR);
//		}
//	}
//	
	/**
	 * 召唤怪击杀目标
	 * @param pet
	 * @param persion
	 */
	public void summonpetKillTarget(SummonPet summonpet,Person persion){
		//召唤怪击杀目标
		summonpetChatAI(CHAT_AI_PET_KILLTARGET, summonpet);
		
	}
	
	/**
	 * 玩家击杀目标
	 * @param player
	 * @param person
	 */
	public void playerKillTarget(Player player,Person person){
		//主人击杀目标
		SummonPet showPet = SummonPetInfoManager.getInstance().getShowSummonPet(player);
		if(showPet==null){
			return;
		}
		summonpetChatAI(CHAT_AI_ONWER_KILLTARGET, showPet);
	}

	private JSONObject getAiValueByAction(String action,SummonPet summonpet){
		Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(summonpet.getModelId() + summonpet.getLevel() );
		String objString = model.getQ_say_condition();
		if(StringUtil.isBlank(objString)){
			return null;
		}
		net.sf.json.JSONArray aiarray = net.sf.json.JSONArray.fromObject(objString);
		for (int i = 0; i < aiarray.size(); i++) {
			net.sf.json.JSONObject chatAI= aiarray.getJSONObject(i);
			if(chatAI.containsKey(action)){
				return chatAI;
			}
		}
		return null;
	}
	
	public static void main(String args[]){
		JSONArray fromObject = net.sf.json.JSONArray.fromObject("[{}]");
		JSONObject jsonObject = fromObject.getJSONObject(0);
		System.out.println(jsonObject.get("1")+"");
		
	}
	
	
}
