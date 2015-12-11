package scripts.zone.country;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.game.batter.message.ResMonsterBatterToClientMessage;
import com.game.country.manager.CountryManager;
import com.game.country.structs.CountryFightStatus;
import com.game.country.structs.SiegeSMS;
import com.game.csys.message.ResPlayerKillMessage;
import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.script.IEnterMapScript;
import com.game.map.structs.Map;
import com.game.monster.script.IMonsterAiScript;
import com.game.monster.script.IMonsterDieScript;
import com.game.monster.structs.Monster;
import com.game.npc.manager.NpcManager;
import com.game.npc.struts.NPC;
import com.game.pet.struts.Pet;
import com.game.player.manager.PlayerManager;
import com.game.player.script.IPlayerDieScript;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.skill.structs.Skill;
import com.game.utils.MessageUtil;
/**
 * 攻城战 怪物死亡
 * @author wzh
 *  
 */
public class CountryScript implements IEnterMapScript ,IPlayerDieScript ,IMonsterDieScript,IMonsterAiScript{


	protected static Logger logx = Logger.getLogger(CountryScript.class);
	
	
	
	@Override
	public int getId() {
		return 55015;
	}

	@Override
	public void onMonsterDie(Monster monster, Fighter killer) {
		Map map = ManagerPool.mapManager.getMap(monster);
		if(map==null ||map.getMapModelid()!=CountryManager.SIEGE_MAPID){
			return;
		}
		
		int index = (int)monster.getParameters().get("cmIndex");
		
		NPC npc= null;
		switch (index) {
//		case 1:
//			 npc = 	NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC1).get(0);
//			NpcManager.getInstance().showNpc(npc);
//			ManagerPool.countryManager.setCm1Status(0);
//			
//			if(ManagerPool.countryManager.getCm2Status()==2){
//				ManagerPool.countryManager.creatMonster(0,2);
//				ManagerPool.countryManager.setCm2Status(1);
//			}
//			ManagerPool.countryManager.stcountryWarInfo(null, true);
//			
//			break;
//		case 2:
//			 npc = 	NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC2).get(0);
//			NpcManager.getInstance().showNpc(npc);
//			ManagerPool.countryManager.setCm2Status(0);
//			if(ManagerPool.countryManager.getCm3Status()==2){
//				ManagerPool.countryManager.creatMonster(0,3);
//				ManagerPool.countryManager.setCm3Status(1);
//			}
//			ManagerPool.countryManager.stcountryWarInfo(null, true);
//		
//			break;
//		case 3:
//			 npc = 	NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC3).get(0);
//			NpcManager.getInstance().showNpc(npc);
//			ManagerPool.countryManager.setCm3Status(0);
//			
//			if(ManagerPool.countryManager.getKingStatus()==2){
//				ManagerPool.countryManager.creatMonster(0,4);
//				ManagerPool.countryManager.setKingStatus(1);
//			}
//			ManagerPool.countryManager.stcountryWarInfo(null, true);
//			
//			break;
//		case 4:
//			ManagerPool.countryManager.setCm1Status(1);
//			ManagerPool.countryManager.setCm2Status(2);
//			ManagerPool.countryManager.setCm3Status(2);
//			ManagerPool.countryManager.setKingStatus(2);
//			ManagerPool.countryManager.creatMonster(0,1);
//			ManagerPool.countryManager.stcountryWarInfo(null, true);
//			ManagerPool.countryManager.reLoadKing(monster);
//			break;
		case 1:
			 npc = 	NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC1).get(0);
			NpcManager.getInstance().showNpc(npc);
			ManagerPool.countryManager.setCm1Status(0);
			
			if(ManagerPool.countryManager.getSx2Status()==2){
				ManagerPool.countryManager.setSx2Status(1);
				ManagerPool.countryManager.creatMonster(0,6);
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}击破外墙城门，浩浩荡荡的大军正杀向王座。"),killer.getName());
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("{1}击破外墙城门，浩浩荡荡的大军正杀向王座。"),killer.getName());
			
			}
//			　　攻击方击破第x道城门，浩浩荡荡的大军正杀向王座。
			ManagerPool.countryManager.opendoor1(2);
			
			ManagerPool.countryManager.stcountryWarInfo(null, true);
			
			break;
		case 2:
			 npc = 	NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC2).get(0);
			NpcManager.getInstance().showNpc(npc);
			ManagerPool.countryManager.setCm2Status(0);
			if(ManagerPool.countryManager.getSx3Status()==2){
				ManagerPool.countryManager.setSx3Status(1);
				ManagerPool.countryManager.creatMonster(0,7);
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}击破中墙城门，浩浩荡荡的大军正杀向王座。"),killer.getName());
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("{1}击破中墙城门，浩浩荡荡的大军正杀向王座。"),killer.getName());
				
			}
			ManagerPool.countryManager.opendoor2(2);
			ManagerPool.countryManager.stcountryWarInfo(null, true);
		
			break;
		case 3:
			 npc = 	NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC3).get(0);
			NpcManager.getInstance().showNpc(npc);
			ManagerPool.countryManager.setCm3Status(0);
			
			if(ManagerPool.countryManager.getKingStatus()==2){
				ManagerPool.countryManager.creatMonster(0,4);
				ManagerPool.countryManager.setKingStatus(1);
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}击破内墙城门，浩浩荡荡的大军正杀向王座。"),killer.getName());
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("{1}击破内墙城门，浩浩荡荡的大军正杀向王座。"),killer.getName());
				
			}
			ManagerPool.countryManager.opendoor3(2);
			ManagerPool.countryManager.stcountryWarInfo(null, true);
			
			break;
		case 4:
			ManagerPool.countryManager.setSx1Status(1);
			ManagerPool.countryManager.setCm1Status(2);
			ManagerPool.countryManager.setCm2Status(2);
			ManagerPool.countryManager.setCm3Status(2);
			ManagerPool.countryManager.setSx2Status(2);
			ManagerPool.countryManager.setSx3Status(2);
			ManagerPool.countryManager.setKingStatus(2);
			ManagerPool.countryManager.opendoor1(0);
			ManagerPool.countryManager.opendoor2(0);
			ManagerPool.countryManager.opendoor3(0);
			
			
			//隐藏NPC
			NPC npc1 = NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC1).get(0);
			NPC npc2 = NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC2).get(0);
			NPC npc3 = NpcManager.getInstance().findNpc(map, CountryFightStatus.NPC3).get(0);
			
			NpcManager.getInstance().hideNpc(npc1);
			NpcManager.getInstance().hideNpc(npc2);
			NpcManager.getInstance().hideNpc(npc3);
		
			ManagerPool.countryManager.reLoadKing(monster);
			ManagerPool.monsterManager.removeMonster(ManagerPool.countryManager.getSiegeMap());
			ManagerPool.countryManager.creatMonster(0,5);
			break;
			
		case 5:
			ManagerPool.countryManager.setSx1Status(0);
			ManagerPool.countryManager.creatMonster(0,1);
			ManagerPool.countryManager.setCm1Status(1);
			ManagerPool.countryManager.stcountryWarInfo(null, true);
			MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}摧毁了外墙机关，外墙城门失去无敌护盾的保护!"),killer.getName());
			
			
		
			break;
		case 6:
			ManagerPool.countryManager.setSx2Status(0);
			ManagerPool.countryManager.creatMonster(0,2);
			ManagerPool.countryManager.setCm2Status(1);
			ManagerPool.countryManager.stcountryWarInfo(null, true);
			MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}摧毁了中墙机关，中墙城门失去无敌护盾的保护!"),killer.getName());
		
			break;
		case 7:
			ManagerPool.countryManager.setSx3Status(0);
			ManagerPool.countryManager.creatMonster(0,3);
			ManagerPool.countryManager.setCm3Status(1);
			ManagerPool.countryManager.stcountryWarInfo(null, true);
			MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}摧毁了内墙机关，内墙城门失去无敌护盾的保护!"),killer.getName());
			break;
		default:
			break;
		}
		
		
		
//		Player player = PlayerManager.getInstance().getPlayer(killer.getId());
//		CsysManger.getInstance().changeCsysSMSTopData(player, CsysManger.KILLMONSTER, monster.getModelId(),null);
	}





	@Override
	public void onPlayerDie(Player player, Fighter killer) {
		// TODO Auto-generated method stub
		
	}

	



	@Override
	public void onEnterMap(Player player, Map map) {
		// 进入圣盟争夺战
				if (ManagerPool.countryManager.getSiegestate() == 1
						&& map.getMapModelid() == ManagerPool.countryManager.SIEGE_MAPID) {
					ManagerPool.countryManager.stcountryWarInfo(player, false);
					
					if(player.getGuildId() == ManagerPool.countryManager.getKingcity().getGuildid()){
						player.setGroupmark(1);
					}else{
						player.setGroupmark(2);
					}
			
					SiegeSMS siegeSMS = ManagerPool.countryManager.getSMSTopData(player);
					
					if(siegeSMS.getConKill()>0){
						ResPlayerKillMessage cmsg2 = new ResPlayerKillMessage();
						cmsg2.setPlayerId(player.getId());
						cmsg2.setCount(siegeSMS.getConKill());
						MessageUtil.tell_round_message(player, cmsg2);
						
						ResMonsterBatterToClientMessage cmsg = new ResMonsterBatterToClientMessage();
						cmsg.setId(0);
						cmsg.setNum(siegeSMS.getConKill());
						cmsg.setCountdowntime(0);
						cmsg.setType((byte) 1);
						MessageUtil.tell_player_message(player, cmsg);
					}
				}
		
	}

	@Override
	public boolean wasHit(Monster monster, Fighter attacker, long damage) {
		if(monster.getModelId()==CountryFightStatus.KING){
			long  guild =0l;
			
			//记录玩家 公会的ID
			if(attacker instanceof Player){
				  guild = ((Player)attacker).getGuildId();
			}
			//记录宠物主人的 公会ID
			if(attacker instanceof Pet){
				Player play =  PlayerManager.getInstance().getPlayer(((Pet) attacker).getOwnerId());
				guild = play.getGuildId();
			}
			
			if (monster.getParameters().containsKey("kingDamages")) {
				
				HashMap<Long, Long> kingDamages = (HashMap<Long, Long>)monster.getParameters().get("kingDamages");
				if(kingDamages.containsKey(guild)){
					kingDamages.put(guild, kingDamages.get(guild) + damage);
				}else{
					kingDamages.put(guild,damage);
				}
			} else {
				HashMap<Long, Long> kingDamages = new HashMap<Long, Long>();
				kingDamages.put(guild, damage);
				monster.getParameters().put("kingDamages", kingDamages);
			}
		}
		return false;
	}

	@Override
	public Fighter getAttackTarget(Monster monster) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Skill getSkill(Monster monster) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	
	
	
	

}
